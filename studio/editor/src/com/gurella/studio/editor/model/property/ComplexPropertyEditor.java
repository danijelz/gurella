package com.gurella.studio.editor.model.property;

import org.eclipse.swt.widgets.Composite;

import com.gurella.studio.editor.model.ModelEditorContainer;

public abstract class ComplexPropertyEditor<T> extends PropertyEditor<T> {
	public ComplexPropertyEditor(Composite parent, PropertyEditorContext<T> context,
			ModelEditorContainer<?> propertiesContainer) {
		super(parent, context, propertiesContainer);
	}
}
