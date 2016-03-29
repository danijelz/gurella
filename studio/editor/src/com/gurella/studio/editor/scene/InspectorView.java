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
	private Object current;
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
				Inspectable inspectable = (Inspectable) seclection;
				if (current != inspectable.getTarget()) {
					if (currentContainer != null) {
						currentContainer.dispose();
						currentContainer = null;
					}

					current = inspectable.getTarget();
					if (current != null) {
						currentContainer = Values
								.cast(inspectable.createPropertiesContainer(editor, this, editor.getToolkit()));
						if (currentContainer != null) {
							currentContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
							currentContainer.init(editor.getToolkit(), current);
							layout(true, true);
							currentContainer.layout(true, true);
						}
					}
				}
			}
		}
	}

	public interface Inspectable {
		Object getTarget();

		PropertiesContainer<?> createPropertiesContainer(GurellaEditor editor, Composite parent, FormToolkit toolkit);
	}

	public static abstract class PropertiesContainer<T> extends ScrolledForm {
		protected GurellaEditor editor;
		protected InspectorView inspectorView;

		public PropertiesContainer(GurellaEditor editor, Composite parent, int style) {
			super(parent, style);
			this.editor = editor;
		}

		protected final void postMessage(Object message, Object... additionalData) {
			editor.postMessage(inspectorView, message, additionalData);
		}
		
		@SuppressWarnings("unused")
		public void handleMessage(SceneEditorView source, Object message, Object... additionalData) {
		}

		protected abstract void init(FormToolkit toolkit, T object);
	}
}
