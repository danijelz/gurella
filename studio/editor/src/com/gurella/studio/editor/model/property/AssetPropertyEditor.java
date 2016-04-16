package com.gurella.studio.editor.model.property;

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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ResourceTransfer;

import com.gurella.studio.editor.GurellaStudioPlugin;

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
		text.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false));
		selectAssetButton = GurellaStudioPlugin.getToolkit().createButton(body, "add", SWT.PUSH);
		selectAssetButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));

		DropTarget target = new DropTarget(text, DND.DROP_MOVE );
		target.setTransfer(new Transfer[] { ResourceTransfer.getInstance() });
		target.addDropListener(new DropTargetAdapter() {
			@Override
			public void dragOver(DropTargetEvent event) {
				event.feedback = DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL;
				if (event.data != null) {
					IResource item = (IResource) event.data;
					if (isValidResource(item)) {
						event.feedback = DND.FEEDBACK_SELECT;
					}
				}
			}

			private boolean isValidResource(IResource item) {
				// TODO Auto-generated method stub
				return true;
			}
			
			@Override
			public void dropAccept(DropTargetEvent event) {
		    	if (!isValidResource((IResource) event.data)) {
		            event.detail = DND.DROP_NONE;
		        }
		    }

			@Override
			public void drop(DropTargetEvent event) {
				IResource item = (IResource) event.data;
				if (item == null || !isValidResource(item)) {
					event.detail = DND.DROP_NONE;
					return;
				}

				int i = 0;
			}
		});
		// TODO Auto-generated constructor stub
	}

}
