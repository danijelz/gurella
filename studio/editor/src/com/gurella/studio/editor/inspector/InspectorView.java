package com.gurella.studio.editor.inspector;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.gurella.engine.utils.Values;
import com.gurella.studio.editor.GurellaEditor;
import com.gurella.studio.editor.GurellaStudioPlugin;
import com.gurella.studio.editor.scene.SceneEditorView;
import com.gurella.studio.editor.scene.SelectionMessage;

public class InspectorView extends SceneEditorView {
	private Object currentTarget;
	private InspectableContainer<?> currentContainer;

	public InspectorView(GurellaEditor editor, int style) {
		super(editor, "Inspector", GurellaStudioPlugin.createImage("icons/showproperties_obj.gif"), style | SWT.BORDER);
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
		if (currentTarget != inspectable.getTarget()) {
			try {
				replaceSelectable(inspectable);
			} catch (Exception e) {
				Arrays.stream(getChildren()).forEach(c -> c.dispose());
				currentTarget = null;
				currentContainer = new ErrorInspectableContainer(this, e);
				layout(true, true);
			}
		}
	}

	private <T> void replaceSelectable(Inspectable<T> inspectable) {
		clearCurrentSelection();
		currentTarget = inspectable.getTarget();
		if (currentTarget == null) {
			return;
		}

		currentContainer = Values.cast(inspectable.createContainer(this, inspectable.getTarget()));
		if (currentContainer != null) {
			currentContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
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
