package com.gurella.studio.editor.model;

import com.badlogic.gdx.math.Vector3;
import com.gurella.engine.base.model.Property;
import com.gurella.engine.utils.Values;
import com.gurella.studio.editor.model.property.ArrayPropertyEditor;
import com.gurella.studio.editor.model.property.BooleanPropertyEditor;
import com.gurella.studio.editor.model.property.BytePropertyEditor;
import com.gurella.studio.editor.model.property.CharacterPropertyEditor;
import com.gurella.studio.editor.model.property.DoublePropertyEditor;
import com.gurella.studio.editor.model.property.EnumPropertyEditor;
import com.gurella.studio.editor.model.property.FloatPropertyEditor;
import com.gurella.studio.editor.model.property.IntegerPropertyEditor;
import com.gurella.studio.editor.model.property.LongPropertyEditor;
import com.gurella.studio.editor.model.property.ReflectionPropertyEditor;
import com.gurella.studio.editor.model.property.ShortPropertyEditor;
import com.gurella.studio.editor.model.property.StringPropertyEditor;
import com.gurella.studio.editor.model.property.Vector3PropertyEditor;

public class PropertyEditorFactory {
	public static <T> PropertyEditor<T> createEditor(ModelPropertiesContainer<?> parent, Property<T> property) {
		Class<?> propertyType = property.getType();

		if (propertyType == Boolean.class || propertyType == boolean.class) {
			return Values.cast(new BooleanPropertyEditor(parent, Values.cast(property)));
		} else if (propertyType == Integer.class || propertyType == int.class) {
			return Values.cast(new IntegerPropertyEditor(parent, Values.cast(property)));
		} else if (propertyType == Long.class || propertyType == long.class) {
			return Values.cast(new LongPropertyEditor(parent, Values.cast(property)));
		} else if (propertyType == Float.class || propertyType == float.class) {
			return Values.cast(new FloatPropertyEditor(parent, Values.cast(property)));
		} else if (propertyType == Byte.class || propertyType == byte.class) {
			return Values.cast(new BytePropertyEditor(parent, Values.cast(property)));
		} else if (propertyType == Short.class || propertyType == short.class) {
			return Values.cast(new ShortPropertyEditor(parent, Values.cast(property)));
		} else if (propertyType == Character.class || propertyType == char.class) {
			return Values.cast(new CharacterPropertyEditor(parent, Values.cast(property)));
		} else if (propertyType == Double.class || propertyType == double.class) {
			return Values.cast(new DoublePropertyEditor(parent, Values.cast(property)));
		} else if (propertyType == String.class) {
			return Values.cast(new StringPropertyEditor(parent, Values.cast(property)));
		} else if (propertyType == Vector3.class) {
			return Values.cast(new Vector3PropertyEditor(parent, Values.cast(property)));
		} else if (propertyType.isArray()) {
			return Values.cast(new ArrayPropertyEditor<>(parent, Values.cast(property)));
		}

		/////

		else if (propertyType.isEnum()) {
			return Values.cast(new EnumPropertyEditor<>(parent, Values.cast(property)));
		} else {
			return new ReflectionPropertyEditor<T>(parent, property);
		}

		// return new DefaultPropertyEditor<>(parent, property);
	}
}
