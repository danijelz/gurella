package com.gurella.studio.editor.scene;

import org.eclipse.swt.SWT;
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
		setLayout(new GridLayout(1, true));
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
					}

					current = inspectable.getTarget();
					if (current != null) {
						currentContainer = Values.cast(inspectable.createPropertiesContainer(this, editor.getToolkit()));
						if (currentContainer != null) {
							currentContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
							currentContainer.init(editor.getToolkit(), current);
							layout(true, true);
						}
					}
				}
			}
		}
	}

	public interface Inspectable {
		Object getTarget();

		PropertiesContainer<?> createPropertiesContainer(Composite parent, FormToolkit toolkit);
	}

	public static abstract class PropertiesContainer<T> extends ScrolledForm {
		public PropertiesContainer(Composite parent, int style) {
			super(parent, style);
		}

		protected abstract void init(FormToolkit toolkit, T object);
	}
}
