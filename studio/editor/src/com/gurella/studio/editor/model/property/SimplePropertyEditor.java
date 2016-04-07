package com.gurella.studio.editor.model.property;

import org.eclipse.swt.widgets.Composite;

import com.gurella.studio.editor.model.ModelEditorContainer;

public abstract class SimplePropertyEditor<T> extends PropertyEditor<T> {
	public SimplePropertyEditor(Composite parent, PropertyEditorContext<T> context,
			ModelEditorContainer<?> propertiesContainer) {
		super(parent, context, propertiesContainer);
	}
}
