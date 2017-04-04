package com.gurella.studio.editor.ui;

import static com.gurella.engine.asset.descriptor.AssetDescriptors.getAssetDescriptor;
import static java.util.stream.Collectors.joining;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiConsumer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ResourceTransfer;

import com.gurella.engine.asset.descriptor.AssetDescriptor;
import com.gurella.engine.asset.descriptor.AssetDescriptors;
import com.gurella.engine.utils.Values;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.utils.UiUtils;
import com.gurella.studio.gdx.GdxContext;

public class AssetSelectionWidget<T> extends Composite {
	private static final String emptySelectionMessage = "select";

	private final Text text;
	private final Button selectButton;

	private final int gdxContextId;
	private final Class<T> assetType;
	private final IFolder assetsFolder;

	private BiConsumer<T, T> selectionListener;

	private T asset;
	private T lastLoaded;

	public AssetSelectionWidget(Composite parent, int gdxContextId, Class<T> assetType) {
		super(parent, SWT.NONE);
		this.assetType = assetType;
		this.gdxContextId = gdxContextId;
		this.assetsFolder = GdxContext.getAssetsFolder(gdxContextId);

		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		layout.verticalSpacing = 0;
		setLayout(layout);
		addDisposeListener(e -> unloadLastAsset());

		text = UiUtils.createText(this);
		text.setMessage(emptySelectionMessage);
		text.setEditable(false);
		text.addListener(SWT.KeyUp, e -> onKeyUp(e.character));
		GridDataFactory.defaultsFor(text).align(SWT.FILL, SWT.CENTER).grab(true, false).hint(100, 14).applyTo(text);

		selectButton = GurellaStudioPlugin.getToolkit().createButton(this, "", SWT.PUSH);
		selectButton.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER));
		GridDataFactory.defaultsFor(selectButton).align(SWT.BEGINNING, SWT.BEGINNING).grab(false, false).hint(32, 22)
				.applyTo(selectButton);
		selectButton.addListener(SWT.Selection, e -> showFileDialg());
		selectButton.setEnabled(assetType != null && getAssetDescriptor(assetType) != null);

		DropTarget target = new DropTarget(text, DND.DROP_DEFAULT | DND.DROP_COPY);
		target.setTransfer(new Transfer[] { ResourceTransfer.getInstance() });
		target.addDropListener(new AssetDropTarget());

		UiUtils.adapt(this);
		UiUtils.paintBordersFor(this);
	}

	private void onKeyUp(int character) {
		if (SWT.DEL == character || SWT.BS == character) {
			assetSelected(null);
		}
	}

	private void showFileDialg() {
		FileDialog dialog = new FileDialog(getShell());
		AssetDescriptor<?> descriptor = getAssetDescriptor(assetType);
		Object[] extensionsArr = descriptor.extensions.toArray(String.class);
		String extensions = Arrays.stream(extensionsArr).map(e -> "*." + e).collect(joining(";"));
		dialog.setFilterExtensions(new String[] { extensions });
		dialog.setFilterPath(assetsFolder.getLocation().toString());
		Optional.ofNullable(dialog.open()).ifPresent(path -> assetSelected(path));
	}

	// TODO copy asset if not in project
	private void assetSelected(final String assetPath) {
		T oldAsset = asset;
		setAsset(toRelativePath(assetPath));
		Optional.ofNullable(selectionListener).ifPresent(l -> l.accept(oldAsset, asset));
		unloadLastAsset();
		lastLoaded = asset;
	}

	private String toRelativePath(final String assetPath) {
		return Values.isBlank(assetPath) ? null
				: new Path(assetPath).makeRelativeTo(assetsFolder.getLocation()).toString();
	}

	private void unloadLastAsset() {
		if (lastLoaded != null) {
			GdxContext.unload(gdxContextId, lastLoaded);
		}
	}

	public void setSelectionListener(BiConsumer<T, T> selectionListener) {
		this.selectionListener = selectionListener;
	}

	public T getAsset() {
		return asset;
	}

	public void setAsset(final String assetPath) {
		setAsset(Values.isBlank(assetPath) ? null : GdxContext.load(gdxContextId, assetPath, assetType));
	}

	public void setAsset(final T asset) {
		this.asset = asset;
		if (asset == null) {
			text.setText("");
			text.setMessage(emptySelectionMessage);
		} else {
			String path = GdxContext.getFileName(gdxContextId, asset);
			text.setText(extractFileName(path));
			text.setMessage("");
		}
	}

	public void setSelection(final String assetPath) {
		setAsset(assetPath);
		unloadLastAsset();
		lastLoaded = asset;
	}

	private static String extractFileName(String path) {
		int index = path.lastIndexOf('/');
		return index < 0 ? path : path.substring(index + 1);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		text.setEnabled(enabled);
		selectButton.setEnabled(enabled);
	}

	private final class AssetDropTarget extends DropTargetAdapter {
		@Override
		public void dragEnter(DropTargetEvent event) {
			if (isEnabled() && (event.operations & DND.DROP_COPY) != 0) {
				event.detail = DND.DROP_COPY;
			} else {
				event.detail = DND.DROP_NONE;
			}
		}

		@Override
		public void drop(DropTargetEvent event) {
			event.detail = DND.DROP_NONE;
			Optional.ofNullable((IResource[]) event.data).filter(d -> d.length == 1).map(d -> d[0])
					.filter(r -> isValidResource(r)).ifPresent(r -> assetSelected(r.getLocation().toString()));
		}

		private boolean isValidResource(IResource item) {
			return (item instanceof IFile) && AssetDescriptors.isValidExtension(item.getFileExtension(), assetType);
		}
	}
}
