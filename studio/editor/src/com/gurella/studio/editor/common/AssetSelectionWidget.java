package com.gurella.studio.editor.common;

import java.util.Arrays;

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
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ResourceTransfer;

import com.gurella.engine.asset.AssetType;
import com.gurella.engine.base.resource.ResourceService;
import com.gurella.studio.editor.GurellaStudioPlugin;

public abstract class AssetSelectionWidget<T> extends Composite {
	private Text text;
	private Button selectAssetButton;

	private Class<T> assetType;
	private T asset;

	public AssetSelectionWidget(Composite parent, Class<T> assetType) {
		super(parent, SWT.NONE);
		this.assetType = assetType;

		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		setLayout(layout);
		text = GurellaStudioPlugin.getToolkit().createText(this, "", SWT.BORDER);
		text.setEditable(false);
		text.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));
		selectAssetButton = GurellaStudioPlugin.getToolkit().createButton(this, "add", SWT.PUSH);
		selectAssetButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));
		selectAssetButton.addListener(SWT.Selection, e -> showFileDialg());

		DropTarget target = new DropTarget(text, DND.DROP_MOVE);
		target.setTransfer(new Transfer[] { ResourceTransfer.getInstance() });
		target.addDropListener(new AssetDropTarget());
	}

	private boolean isValidResource(IResource item) {
		return (item instanceof IFile) && AssetType.isValidExtension(assetType, item.getFileExtension());
	}

	private void showFileDialg() {
		FileDialog dialog = new FileDialog(getShell());
		AssetType value = AssetType.value(assetType);
		dialog.setFilterExtensions(Arrays.stream(value.extensions).map(e -> "*." + e).toArray(s -> new String[s]));

		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchWindow window = wb.getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		IEditorPart editor = page.getActiveEditor();
		IFileEditorInput input = (IFileEditorInput) editor.getEditorInput();
		dialog.setFilterPath(input.getFile().getLocation().toString());

		final String path = dialog.open();
		if (path != null) {
			setAsset(path);
		}
	}

	private void setAsset(final String path) {
		asset = ResourceService.load(path);
		text.setText(path);
		assetSelected(asset);
	}
	
	public T getAsset() {
		return asset;
	}

	protected abstract void assetSelected(T asset);

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
				setAsset(item.getLocation().toString());
			}
		}
	}
}
