package com.gurella.studio.editor.scene;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.gurella.engine.utils.Values;
import com.gurella.studio.editor.EditorMessageListener;
import com.gurella.studio.editor.GurellaEditor;
import com.gurella.studio.editor.GurellaStudioPlugin;

public class InspectorView extends SceneEditorView {
	private Object currentTarget;
	private PropertiesContainer<Object> currentContainer;

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
			clearCurrentSelection();
			currentTarget = inspectable.getTarget();
			if (currentTarget == null) {
				return;
			}

			currentContainer = Values.cast(inspectable.createPropertiesContainer(this, inspectable.getTarget()));
			if (currentContainer != null) {
				currentContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
				layout(true, true);
			}
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

		PropertiesContainer<T> createPropertiesContainer(InspectorView parent, T target);
	}

	public static abstract class PropertiesContainer<T> extends ScrolledForm implements EditorMessageListener {
		protected T target;

		public PropertiesContainer(InspectorView parent, T target) {
			super(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
			this.target = target;
			setExpandHorizontal(true);
			setExpandVertical(true);
			setMinWidth(200);
			addListener(SWT.Resize, (e) -> reflow(true));
			addDisposeListener(e -> parent.editor.removeEditorMessageListener(this));
			parent.editor.addEditorMessageListener(this);
		}

		@Override
		public InspectorView getParent() {
			return (InspectorView) super.getParent();
		}

		@Override
		public boolean setParent(Composite parent) {
			if (parent instanceof InspectorView) {
				return super.setParent(parent);
			} else {
				throw new IllegalArgumentException();
			}
		}

		public GurellaEditor getGurellaEditor() {
			return getParent().editor;
		}

		protected final void postMessage(Object message) {
			getParent().postMessage(message);
		}

		@Override
		public void handleMessage(Object source, Object message) {
		}

		protected void setDirty() {
			getGurellaEditor().setDirty();
		}
	}
}
