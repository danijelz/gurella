package com.gurella.studio.editor.model;

import com.gurella.engine.base.model.Property;

public abstract class ComplexPropertyEditor<T> extends PropertyEditor<T> {
	public ComplexPropertyEditor(ModelPropertiesContainer<?> parent, Property<T> property) {
		super(parent, property);
	}
}
