package com.gurella.studio.editor.inspector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.gurella.engine.utils.Values;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.GurellaSceneEditor;
import com.gurella.studio.editor.scene.SceneEditorView;
import com.gurella.studio.editor.scene.SelectionMessage;
import com.gurella.studio.editor.utils.UiUtils;

public class InspectorView extends SceneEditorView {
	private Object currentTarget;
	private InspectableContainer<?> currentContainer;

	public InspectorView(GurellaSceneEditor editor, int style) {
		super(editor, "Inspector", GurellaStudioPlugin.getImage("icons/showproperties_obj.gif"), style | SWT.BORDER);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		setLayout(layout);

		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		toolkit.adapt(this);
	}

	@Override
	public void handleMessage(Object source, Object message) {
		if (message instanceof SelectionMessage) {
			Object seclection = ((SelectionMessage) message).seclection;
			if (seclection instanceof Inspectable) {
				presentInspectable(Values.<Inspectable<Object>> cast(seclection));
			} else {
				clearCurrentSelection();
			}
		}
	}

	private <T> void presentInspectable(Inspectable<T> inspectable) {
		if (Values.isNotEqual(currentTarget, inspectable.getTarget(), true)) {
			try {
				replaceInspectable(inspectable);
			} catch (Exception e) {
				UiUtils.disposeChildren(this);
				currentTarget = null;
				currentContainer = new ErrorInspectableContainer(this, e);
				currentContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
				layout(true, true);
			}
		}
	}

	private <T> void replaceInspectable(Inspectable<T> inspectable) {
		clearCurrentSelection();
		currentTarget = inspectable.getTarget();
		if (currentTarget == null) {
			return;
		}

		currentContainer = Values.cast(inspectable.createContainer(this, inspectable.getTarget()));
		if (currentContainer != null) {
			GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
			gridData.widthHint = 200;
			currentContainer.setLayoutData(gridData);
			layout(true, true);
		}
	}

	private void clearCurrentSelection() {
		currentTarget = null;
		if (currentContainer != null) {
			currentContainer.dispose();
			currentContainer = null;
		}
	}

	public interface Inspectable<T> {
		T getTarget();

		InspectableContainer<T> createContainer(InspectorView parent, T target);
	}
}
