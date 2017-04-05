package com.gurella.studio.editor.ui;

import static com.gurella.engine.asset.descriptor.AssetDescriptors.getAssetDescriptor;
import static com.gurella.studio.editor.utils.FileDialogUtils.selectNewFileName;
import static java.util.stream.Collectors.joining;

import java.io.File;
import java.io.FileInputStream;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
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

import com.gurella.engine.asset.Assets;
import com.gurella.engine.asset.descriptor.AssetDescriptor;
import com.gurella.engine.asset.descriptor.AssetDescriptors;
import com.gurella.engine.utils.Values;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.utils.Try;
import com.gurella.studio.editor.utils.UiUtils;
import com.gurella.studio.gdx.GdxContext;

public class AssetSelectionWidget<T> extends Composite {
	private static final String emptySelectionMessage = "select";

	private final Text text;
	private final Button selectButton;

	private final int gdxContextId;
	private final Class<T> assetType;
	private final IFolder assetsFolder;

	private Consumer<String> selectionListener;

	private String selection;

	public AssetSelectionWidget(Composite parent, int gdxContextId, Class<T> assetType) {
		super(parent, SWT.NONE);
		this.assetType = assetType;
		this.gdxContextId = gdxContextId;
		assetsFolder = GdxContext.getAssetsFolder(gdxContextId);

		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		layout.verticalSpacing = 0;
		setLayout(layout);

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
			setSelection(null);
		}
	}

	private void showFileDialg() {
		FileDialog dialog = new FileDialog(getShell());
		dialog.setFilterExtensions(new String[] { getValidExtensions() });
		dialog.setFilterPath(assetsFolder.getLocation().toString());
		Optional.ofNullable(dialog.open()).flatMap(this::getRelativePath).ifPresent(this::assetSelected);
	}

	private String getValidExtensions() {
		AssetDescriptor<?> descriptor = getAssetDescriptor(assetType);
		Object[] extensionsArr = descriptor.extensions.toArray(String.class);
		return Stream.of(extensionsArr).map(e -> "*." + e).collect(joining(";"));
	}

	private Optional<String> getRelativePath(String absolutePath) {
		if (assetsFolder.getLocation().isPrefixOf(new Path(absolutePath))) {
			return Optional.of(getAssetFolderRelativePath(absolutePath));
		}

		boolean importAsset = MessageDialog.openQuestion(getShell(), "Import asset",
				"Selection is not part of project. Do you want to import asset.");
		return importAsset ? importAsset(absolutePath) : Optional.empty();
	}

	private Optional<String> importAsset(String absolutePath) {
		Optional<String> relativePath = selectNewFileName(assetsFolder, Assets.getFileName(absolutePath),
				getAssetDescriptor(assetType)).map(this::getAssetFolderRelativePath);
		if (relativePath.isPresent()) {
			Path importPath = new Path(relativePath.get());
			IFolder importFolder = assetsFolder.getFolder(importPath.removeLastSegments(1));
			IFile newFile = importFolder.getFile(importPath.lastSegment());
			Try.unchecked(() -> newFile.create(new FileInputStream(new File(absolutePath)), true, null));
		}

		return relativePath;
	}

	private String getAssetFolderRelativePath(String absolutePath) {
		return new Path(absolutePath).makeRelativeTo(assetsFolder.getLocation()).toString();
	}

	private void assetSelected(final String newSelection) {
		setSelection(newSelection);
		Optional.ofNullable(selectionListener).ifPresent(l -> l.accept(newSelection));
	}

	public void setSelectionListener(Consumer<String> selectionListener) {
		this.selectionListener = selectionListener;
	}

	public void setAsset(final T asset) {
		String newSelection = asset == null ? null : GdxContext.getFileName(gdxContextId, asset);
		setSelection(newSelection);
	}

	public String getSelection() {
		return selection;
	}

	public void setSelection(final String selection) {
		this.selection = selection;
		if (Values.isBlank(selection)) {
			text.setText("");
			text.setMessage(emptySelectionMessage);
		} else {
			text.setText(Assets.getFileName(selection));
			text.setMessage("");
		}
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
