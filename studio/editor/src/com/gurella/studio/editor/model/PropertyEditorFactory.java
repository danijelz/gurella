package com.gurella.studio.editor.model;

import org.eclipse.swt.widgets.Composite;

import com.badlogic.gdx.math.Vector3;
import com.gurella.engine.base.model.DefaultModels.SimpleModel;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.base.model.Property;
import com.gurella.engine.utils.ImmutableArray;
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
import com.gurella.studio.editor.model.property.ModelPropertiesContainer;
import com.gurella.studio.editor.model.property.PropertyEditor;
import com.gurella.studio.editor.model.property.ReflectionPropertyEditor;
import com.gurella.studio.editor.model.property.ShortPropertyEditor;
import com.gurella.studio.editor.model.property.SimpleObjectPropertyEditor;
import com.gurella.studio.editor.model.property.StringPropertyEditor;
import com.gurella.studio.editor.model.property.Vector3PropertyEditor;

public class PropertyEditorFactory {
	public static <T> PropertyEditor<T> createEditor(Composite parent, ModelPropertiesContainer<?> propertiesContainer,
			Property<T> property, Object modelInstance) {
		Class<?> propertyType = property.getType();

		if (propertyType == Boolean.class || propertyType == boolean.class) {
			return Values
					.cast(new BooleanPropertyEditor(parent, propertiesContainer, Values.cast(property), modelInstance));
		} else if (propertyType == Integer.class || propertyType == int.class) {
			return Values
					.cast(new IntegerPropertyEditor(parent, propertiesContainer, Values.cast(property), modelInstance));
		} else if (propertyType == Long.class || propertyType == long.class) {
			return Values
					.cast(new LongPropertyEditor(parent, propertiesContainer, Values.cast(property), modelInstance));
		} else if (propertyType == Float.class || propertyType == float.class) {
			return Values
					.cast(new FloatPropertyEditor(parent, propertiesContainer, Values.cast(property), modelInstance));
		} else if (propertyType == Byte.class || propertyType == byte.class) {
			return Values
					.cast(new BytePropertyEditor(parent, propertiesContainer, Values.cast(property), modelInstance));
		} else if (propertyType == Short.class || propertyType == short.class) {
			return Values
					.cast(new ShortPropertyEditor(parent, propertiesContainer, Values.cast(property), modelInstance));
		} else if (propertyType == Character.class || propertyType == char.class) {
			return Values.cast(
					new CharacterPropertyEditor(parent, propertiesContainer, Values.cast(property), modelInstance));
		} else if (propertyType == Double.class || propertyType == double.class) {
			return Values
					.cast(new DoublePropertyEditor(parent, propertiesContainer, Values.cast(property), modelInstance));
		} else if (propertyType == String.class) {
			return Values
					.cast(new StringPropertyEditor(parent, propertiesContainer, Values.cast(property), modelInstance));
		} else if (propertyType == Vector3.class) {
			return Values
					.cast(new Vector3PropertyEditor(parent, propertiesContainer, Values.cast(property), modelInstance));
		} else if (propertyType.isArray()) {
			return Values
					.cast(new ArrayPropertyEditor<>(parent, propertiesContainer, Values.cast(property), modelInstance));
		}

		/////

		else if (propertyType.isEnum()) {
			return Values
					.cast(new EnumPropertyEditor<>(parent, propertiesContainer, Values.cast(property), modelInstance));
		} else if (isSimpleProperty(propertyType)) {
			return Values.cast(new SimpleObjectPropertyEditor<>(parent, propertiesContainer, Values.cast(property),
					modelInstance));
		} else {
			return new ReflectionPropertyEditor<T>(parent, propertiesContainer, property, modelInstance);
		}

		// return new DefaultPropertyEditor<>(parent, property);
	}

	private static boolean isSimpleProperty(Class<?> propertyType) {
		ImmutableArray<Property<?>> properties = Models.getModel(propertyType).getProperties();
		Property<?> editableProperty = null;
		for (Property<?> property : properties) {
			if (property.isEditable()) {
				if (editableProperty == null) {
					editableProperty = property;
				} else {
					return false;
				}
			}
		}

		if (editableProperty == null) {
			return true;
		} else {
			return Models.getModel(editableProperty.getType()) instanceof SimpleModel;
		}
	}
}
