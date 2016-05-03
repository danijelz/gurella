package com.gurella.studio.editor.model.property;

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
import com.gurella.studio.GurellaStudioPlugin;

public class AssetPropertyEditor<T> extends SimplePropertyEditor<T> {
	private Text text;
	private Button selectAssetButton;

	private Class<T> assetType;

	public AssetPropertyEditor(Composite parent, PropertyEditorContext<?, T> context, Class<T> assetType) {
		super(parent, context);
		this.assetType = assetType;

		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		body.setLayout(layout);
		text = GurellaStudioPlugin.getToolkit().createText(body, "", SWT.BORDER);
		text.setEditable(false);
		GridData layoutData = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
		layoutData.widthHint = 50;
		text.setLayoutData(layoutData);
		selectAssetButton = GurellaStudioPlugin.getToolkit().createButton(body, "Browse", SWT.PUSH);
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
		FileDialog dialog = new FileDialog(getBody().getShell());
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
			setValue(path);
		}
	}

	private void setValue(final String path) {
		T oldValue = getValue();
		if(oldValue != null) {
			ResourceService.unload(oldValue);
		}
		T asset = ResourceService.load(path);
		setValue(asset);
		text.setText(path);
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
				setValue(item.getLocation().toString());
			}
		}
	}
}
