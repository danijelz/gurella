package com.gurella.studio.editor.scene;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.gurella.engine.utils.Values;
import com.gurella.studio.editor.GurellaEditor;

public class InspectorView extends SceneEditorView {
	private Object currentTarget;
	private PropertiesContainer<Object> currentContainer;

	public InspectorView(GurellaEditor editor, int style) {
		super(editor, "Inspector", null, style);
		setLayout(new GridLayout());
		setBackground(new Color(getDisplay(), 0, 100, 100));
	}

	@Override
	public void handleMessage(SceneEditorView source, Object message, Object... additionalData) {
		if (message instanceof SelectionMessage) {
			Object seclection = ((SelectionMessage) message).seclection;
			if (seclection instanceof Inspectable) {
				presentInspectable(Values.cast(seclection));
			} else {
				clearCurrentSelection();
			}
		}
	}

	private <T> void presentInspectable(Inspectable<T> inspectable) {
		if (currentTarget != inspectable.getTarget()) {
			clearCurrentSelection();
			currentTarget = inspectable.getTarget();
			
			if (currentTarget != null) {
				currentContainer = Values.cast(inspectable.createPropertiesContainer(this, inspectable.getTarget()));
				if (currentContainer != null) {
					currentContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
					layout(true, true);
					currentContainer.layout(true, true);
				}
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

	public static abstract class PropertiesContainer<T> extends ScrolledForm {
		protected T target;

		public PropertiesContainer(InspectorView parent, T target) {
			super(parent, SWT.NONE);
			this.target = target;
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

		protected final void postMessage(Object message, Object... additionalData) {
			getParent().postMessage(message, additionalData);
		}

		@SuppressWarnings("unused")
		public void handleMessage(SceneEditorView source, Object message, Object... additionalData) {
		}

		protected void setDirty() {
			getGurellaEditor().setDirty();
		}

		protected FormToolkit getToolkit() {
			return getGurellaEditor().getToolkit();
		}
	}
}
