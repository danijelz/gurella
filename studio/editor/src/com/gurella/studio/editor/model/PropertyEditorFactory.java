package com.gurella.studio.editor.model;

import org.eclipse.swt.widgets.Composite;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.gurella.engine.asset.Assets;
import com.gurella.engine.base.model.DefaultModels.SimpleModel;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.base.model.Property;
import com.gurella.engine.scene.layer.Layer;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.Values;
import com.gurella.studio.editor.model.property.ArrayPropertyEditor;
import com.gurella.studio.editor.model.property.AssetPropertyEditor;
import com.gurella.studio.editor.model.property.BooleanPropertyEditor;
import com.gurella.studio.editor.model.property.BytePropertyEditor;
import com.gurella.studio.editor.model.property.CharacterPropertyEditor;
import com.gurella.studio.editor.model.property.ColorPropertyEditor;
import com.gurella.studio.editor.model.property.DoublePropertyEditor;
import com.gurella.studio.editor.model.property.EnumPropertyEditor;
import com.gurella.studio.editor.model.property.FloatPropertyEditor;
import com.gurella.studio.editor.model.property.GridPoint2PropertyEditor;
import com.gurella.studio.editor.model.property.GridPoint3PropertyEditor;
import com.gurella.studio.editor.model.property.IntegerPropertyEditor;
import com.gurella.studio.editor.model.property.LayerPropertyEditor;
import com.gurella.studio.editor.model.property.LongPropertyEditor;
import com.gurella.studio.editor.model.property.Matrix3PropertyEditor;
import com.gurella.studio.editor.model.property.Matrix4PropertyEditor;
import com.gurella.studio.editor.model.property.PropertyEditor;
import com.gurella.studio.editor.model.property.PropertyEditorContext;
import com.gurella.studio.editor.model.property.QuaternionPropertyEditor;
import com.gurella.studio.editor.model.property.ReflectionPropertyEditor;
import com.gurella.studio.editor.model.property.ShortPropertyEditor;
import com.gurella.studio.editor.model.property.StringPropertyEditor;
import com.gurella.studio.editor.model.property.Vector2PropertyEditor;
import com.gurella.studio.editor.model.property.Vector3PropertyEditor;

public class PropertyEditorFactory {
	public static <T> PropertyEditor<T> createEditor(Composite parent, PropertyEditorContext<?, T> context) {
		Class<T> propertyType = context.property.getType();
		return createEditor(parent, context, propertyType);
	}

	public static <T> PropertyEditor<T> createEditor(Composite parent, PropertyEditorContext<?, T> context,
			Class<T> propertyType) {
		if (propertyType == Boolean.class || propertyType == boolean.class) {
			return Values.cast(new BooleanPropertyEditor(parent, Values.cast(context)));
		} else if (propertyType == Integer.class || propertyType == int.class) {
			return Values.cast(new IntegerPropertyEditor(parent, Values.cast(context)));
		} else if (propertyType == Long.class || propertyType == long.class) {
			return Values.cast(new LongPropertyEditor(parent, Values.cast(context)));
		} else if (propertyType == Float.class || propertyType == float.class) {
			return Values.cast(new FloatPropertyEditor(parent, Values.cast(context)));
		} else if (propertyType == Byte.class || propertyType == byte.class) {
			return Values.cast(new BytePropertyEditor(parent, Values.cast(context)));
		} else if (propertyType == Short.class || propertyType == short.class) {
			return Values.cast(new ShortPropertyEditor(parent, Values.cast(context)));
		} else if (propertyType == Character.class || propertyType == char.class) {
			return Values.cast(new CharacterPropertyEditor(parent, Values.cast(context)));
		} else if (propertyType == Double.class || propertyType == double.class) {
			return Values.cast(new DoublePropertyEditor(parent, Values.cast(context)));
		} else if (propertyType == String.class) {
			return Values.cast(new StringPropertyEditor(parent, Values.cast(context)));
		} else if (propertyType == Vector3.class) {
			return Values.cast(new Vector3PropertyEditor(parent, Values.cast(context)));
		} else if (propertyType == Vector2.class) {
			return Values.cast(new Vector2PropertyEditor(parent, Values.cast(context)));
		} else if (propertyType == Quaternion.class) {
			return Values.cast(new QuaternionPropertyEditor(parent, Values.cast(context)));
		} else if (propertyType == GridPoint2.class) {
			return Values.cast(new GridPoint2PropertyEditor(parent, Values.cast(context)));
		} else if (propertyType == GridPoint3.class) {
			return Values.cast(new GridPoint3PropertyEditor(parent, Values.cast(context)));
		} else if (propertyType == Matrix3.class) {
			return Values.cast(new Matrix3PropertyEditor(parent, Values.cast(context)));
		} else if (propertyType == Matrix4.class) {
			return Values.cast(new Matrix4PropertyEditor(parent, Values.cast(context)));
		} else if (propertyType == Color.class) {
			return Values.cast(new ColorPropertyEditor(parent, Values.cast(context)));
		} else if (propertyType == Layer.class) {
			return Values.cast(new LayerPropertyEditor(parent, Values.cast(context)));
		}

		///// custom models for collections...

		else if (propertyType.isArray()) {
			return Values.cast(new ArrayPropertyEditor<>(parent, context));
		} else if (propertyType.isEnum()) {
			return Values.cast(new EnumPropertyEditor<>(parent, Values.cast(context)));
		} else if (Assets.isAssetType(propertyType)) {
			return Values.cast(new AssetPropertyEditor<>(parent, context, propertyType));
		} /*else if (isSimpleProperty(propertyType)) {
			//TODO handle in ReflectionPropertyEditor
			return Values.cast(new SimpleObjectPropertyEditor<>(parent, context));}*/
		else {
			return new ReflectionPropertyEditor<T>(parent, context);
		}
	}

	public static boolean isSimpleProperty(Class<?> propertyType) {
		if (Models.getModel(propertyType) instanceof SimpleModel) {
			return true;
		}

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
			return false;
		} else {
			return Models.getModel(editableProperty.getType()) instanceof SimpleModel;
		}
	}
}
