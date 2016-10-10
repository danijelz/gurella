package com.gurella.studio.editor.common;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ResourceTransfer;

import com.gurella.engine.asset.AssetService;
import com.gurella.engine.asset.AssetType;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.utils.UiUtils;

public class AssetSelectionWidget<T> extends Composite {
	private Text text;
	private Button selectAssetButton;

	private Class<T> assetType;
	private T asset;

	private BiConsumer<T, T> selectionChangedListener;

	public AssetSelectionWidget(Composite parent, Class<T> assetType) {
		super(parent, SWT.NONE);
		this.assetType = assetType;

		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();

		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 2;
		layout.marginHeight = 0;
		layout.verticalSpacing = 0;
		setLayout(layout);

		text = UiUtils.createText(this);
		text.setEditable(false);
		GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		layoutData.widthHint = 100;
		layoutData.heightHint = 14;
		text.setLayoutData(layoutData);

		selectAssetButton = toolkit.createButton(this, "", SWT.PUSH);
		selectAssetButton.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER));
		selectAssetButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		selectAssetButton.addListener(SWT.Selection, e -> showFileDialg());

		DropTarget target = new DropTarget(text, DND.DROP_MOVE);
		target.setTransfer(new Transfer[] { ResourceTransfer.getInstance() });
		target.addDropListener(new AssetDropTarget());

		toolkit.adapt(this);
		UiUtils.paintBordersFor(this);
	}

	private boolean isValidResource(IResource item) {
		return (item instanceof IFile) && AssetType.isValidExtension(assetType, item.getFileExtension());
	}

	private void showFileDialg() {
		FileDialog dialog = new FileDialog(getShell());
		AssetType value = AssetType.value(assetType);
		dialog.setFilterExtensions(
				new String[] { Arrays.stream(value.extensions).map(e -> "*." + e).collect(Collectors.joining(";")) });

		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchWindow window = wb.getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		IEditorPart editor = page.getActiveEditor();
		IFileEditorInput input = (IFileEditorInput) editor.getEditorInput();
		dialog.setFilterPath(input.getFile().getLocation().removeLastSegments(1).toString());

		final String path = dialog.open();
		if (path != null) {
			assetSelected(path);
		}
	}

	private void assetSelected(final String path) {
		T oldAsset = asset;
		asset = AssetService.load(path, assetType);
		text.setText(extractFileName(path));
		text.setMessage("");
		if (selectionChangedListener != null) {
			selectionChangedListener.accept(oldAsset, asset);
		}
	}

	private static String extractFileName(String path) {
		int index = path.lastIndexOf('/');
		return index < 0 ? path : path.substring(index + 1);
	}

	public void setSelectionChangedListener(BiConsumer<T, T> selectionChangedListener) {
		this.selectionChangedListener = selectionChangedListener;
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
			String path = AssetService.getFileName(asset);
			text.setText(extractFileName(path));
			text.setMessage("");
		}
	}

	private final class AssetDropTarget extends DropTargetAdapter {
		@Override
		public void dragEnter(DropTargetEvent event) {
			if (event.detail == DND.DROP_DEFAULT) {
				if ((event.operations & DND.DROP_MOVE) != 0) {
					event.detail = DND.DROP_MOVE;
				} else {
					event.detail = DND.DROP_NONE;
				}
			}
		}

		@Override
		public void drop(DropTargetEvent event) {
			event.detail = DND.DROP_NONE;
			IResource[] data = (IResource[]) event.data;
			if (data == null || data.length != 1) {
				return;
			}

			IResource item = data[0];
			if (isValidResource(item)) {
				assetSelected(item.getLocation().toString());
			}
		}
	}
}
