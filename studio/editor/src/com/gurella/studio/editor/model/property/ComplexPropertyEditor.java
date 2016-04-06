package com.gurella.studio.editor.model.property;

import org.eclipse.swt.widgets.Composite;

import com.gurella.engine.base.model.Property;

public abstract class ComplexPropertyEditor<T> extends PropertyEditor<T> {
	public ComplexPropertyEditor(Composite parent, ModelPropertiesContainer<?> propertiesContainer, Property<T> property, Object modelInstance) {
		super(parent, propertiesContainer, property, modelInstance);
	}
}
