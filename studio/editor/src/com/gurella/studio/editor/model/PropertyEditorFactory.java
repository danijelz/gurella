package com.gurella.studio.editor.model;

import com.gurella.engine.base.model.Property;
import com.gurella.engine.utils.Values;

public class PropertyEditorFactory {
	public static <T> PropertyEditor<T> createEditor(ModelPropertiesContainer<?> parent, Property<T> property) {
		Class<?> propertyType = property.getType();

		if (propertyType == Boolean.class || propertyType == Boolean.TYPE) {
			return Values.cast(new BooleanPropertyEditor(parent, Values.<Property<Boolean>> cast(property)));
		}

		return new DefaultPropertyEditor<>(parent, property);
	}
}
