package com.gurella.studio.editor.scene;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.gurella.studio.editor.GurellaEditor;

public class InspectorView extends SceneEditorView {
	private Inspectable current;

	public InspectorView(GurellaEditor editor, int style) {
		super(editor, "Inspector", null, style);
	}

	public interface Inspectable {
		Object getTarget();

		Composite createPropertiesContainer(Composite parent, FormToolkit toolkit);
	}

	public static abstract class PropertiesContainer<T> extends ScrolledForm {
		public PropertiesContainer(Composite parent, int style) {
			super(parent, style);
		}

		protected abstract void init(FormToolkit toolkit, T object);
	}
}
