package com.gurella.studio.editor.model;

import com.gurella.engine.base.model.Property;
import com.gurella.engine.utils.Values;
import com.gurella.studio.editor.model.property.BooleanPropertyEditor;
import com.gurella.studio.editor.model.property.EnumPropertyEditor;
import com.gurella.studio.editor.model.property.FloatPropertyEditor;
import com.gurella.studio.editor.model.property.IntegerPropertyEditor;
import com.gurella.studio.editor.model.property.StringPropertyEditor;

public class PropertyEditorFactory {
	public static <T> PropertyEditor<T> createEditor(ModelPropertiesContainer<?> parent, Property<T> property) {
		Class<?> propertyType = property.getType();

		if (propertyType == Boolean.class || propertyType == boolean.class) {
			return Values.cast(new BooleanPropertyEditor(parent, Values.<Property<Boolean>> cast(property)));
		} else if (propertyType == Integer.class || propertyType == int.class) {
			return Values.cast(new IntegerPropertyEditor(parent, Values.<Property<Integer>> cast(property)));
		} else if (propertyType == Float.class || propertyType == float.class) {
			return Values.cast(new FloatPropertyEditor(parent, Values.<Property<Float>> cast(property)));
		} else if (propertyType == String.class) {
			return Values.cast(new StringPropertyEditor(parent, Values.<Property<String>> cast(property)));
		}

		/////

		else if (propertyType.isEnum()) {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			PropertyEditor<T> editor = Values.cast(new EnumPropertyEditor(parent, Values.<Property> cast(property)));
			return editor;
		}

		return new DefaultPropertyEditor<>(parent, property);
	}
}
