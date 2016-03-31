package com.gurella.studio.editor.model;

import com.gurella.engine.base.model.Property;

public abstract class SimplePropertyEditor<T> extends PropertyEditor<T> {
	public SimplePropertyEditor(ModelPropertiesContainer<?> parent, Property<T> property) {
		super(parent, property);
	}
}
