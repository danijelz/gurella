package com.gurella.studio.editor.model;

import org.eclipse.swt.widgets.Composite;

import com.gurella.engine.base.model.Property;
import com.gurella.studio.editor.model.property.ModelPropertiesContainer;
import com.gurella.studio.editor.model.property.PropertyEditor;

public abstract class ComplexPropertyEditor<T> extends PropertyEditor<T> {
	public ComplexPropertyEditor(Composite parent, ModelPropertiesContainer<?> propertiesContainer, Property<T> property) {
		super(parent, propertiesContainer, property);
	}
}
