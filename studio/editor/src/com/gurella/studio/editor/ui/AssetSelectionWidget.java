package com.gurella.studio.editor.ui;

import static java.util.stream.Collectors.joining;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiConsumer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
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
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ResourceTransfer;

import com.gurella.engine.asset.AssetType;
import com.gurella.engine.asset.Assets;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.utils.UiUtils;
import com.gurella.studio.gdx.GdxContext;

public class AssetSelectionWidget<T> extends Composite {
	private final Text text;
	private final Button selectAssetButton;

	private final int gdxContextId;
	private final Class<T> assetType;
	private final IFolder assetsFolder;

	private BiConsumer<T, T> selectionListener;

	private T asset;
	private T lastLoaded;

	// TODO unload loaded assets
	public AssetSelectionWidget(Composite parent, int gdxContextId, Class<T> assetType) {
		super(parent, SWT.NONE);
		this.assetType = assetType;
		this.gdxContextId = gdxContextId;
		this.assetsFolder = GdxContext.getAssetsFolder(gdxContextId);

		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();

		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		layout.verticalSpacing = 0;
		setLayout(layout);
		addDisposeListener(e -> unloadLastAsset());

		text = UiUtils.createText(this);
		text.setEditable(false);
		GridDataFactory.defaultsFor(text).align(SWT.FILL, SWT.CENTER).grab(true, false).hint(100, 14).applyTo(text);

		selectAssetButton = toolkit.createButton(this, "", SWT.PUSH);
		selectAssetButton.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER));
		GridDataFactory.defaultsFor(selectAssetButton).align(SWT.BEGINNING, SWT.BEGINNING).grab(false, false)
				.hint(32, 22).applyTo(selectAssetButton);
		selectAssetButton.addListener(SWT.Selection, e -> showFileDialg());

		DropTarget target = new DropTarget(text, DND.DROP_DEFAULT | DND.DROP_COPY);
		target.setTransfer(new Transfer[] { ResourceTransfer.getInstance() });
		target.addDropListener(new AssetDropTarget());

		toolkit.adapt(this);
		UiUtils.paintBordersFor(this);
	}

	private void showFileDialg() {
		FileDialog dialog = new FileDialog(getShell());
		AssetType value = Assets.getAssetType(assetType);
		String extensions = Arrays.stream(value.extensions).map(e -> "*." + e).collect(joining(";"));
		dialog.setFilterExtensions(new String[] { extensions });
		dialog.setFilterPath(assetsFolder.getLocation().toString());
		Optional.ofNullable(dialog.open()).ifPresent(path -> assetSelected(path));
	}

	// TODO copy asset if not in project
	private void assetSelected(final String path) {
		T oldAsset = asset;
		IPath assetPath = new Path(path).makeRelativeTo(assetsFolder.getLocation());
		asset = GdxContext.load(gdxContextId, assetPath.toString(), assetType);
		text.setText(assetPath.lastSegment());
		text.setMessage("");
		Optional.ofNullable(selectionListener).ifPresent(l -> l.accept(oldAsset, asset));
		unloadLastAsset();
		lastLoaded = asset;
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

	public void setAsset(final T asset) {
		this.asset = asset;
		if (asset == null) {
			text.setText("");
			text.setMessage("null");
		} else {
			String path = GdxContext.getFileName(gdxContextId, asset);
			text.setText(extractFileName(path));
			text.setMessage("");
		}
	}

	private static String extractFileName(String path) {
		int index = path.lastIndexOf('/');
		return index < 0 ? path : path.substring(index + 1);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		text.setEnabled(enabled);
		selectAssetButton.setEnabled(enabled);
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
			return (item instanceof IFile) && Assets.isValidExtension(assetType, item.getFileExtension());
		}
	}
}
