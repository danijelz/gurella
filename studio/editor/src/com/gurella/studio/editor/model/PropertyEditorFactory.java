package com.gurella.studio.editor.model;

import static com.gurella.engine.utils.Reflection.getDeclaredAnnotation;
import static com.gurella.engine.utils.Reflection.newInstance;
import static com.gurella.engine.utils.Values.cast;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;

import org.eclipse.swt.widgets.Composite;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Bits;
import com.gurella.engine.asset.Assets;
import com.gurella.engine.base.model.DefaultModels.SimpleModel;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.base.model.Property;
import com.gurella.engine.base.model.ReflectionProperty;
import com.gurella.engine.editor.property.PropertyEditorDescriptor;
import com.gurella.engine.scene.bullet.shapes.BulletCollisionShape;
import com.gurella.engine.scene.layer.Layer;
import com.gurella.engine.utils.BitsExt;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.Values;
import com.gurella.studio.editor.model.property.ArrayPropertyEditor;
import com.gurella.studio.editor.model.property.AssetPropertyEditor;
import com.gurella.studio.editor.model.property.BigDecimalPropertyEditor;
import com.gurella.studio.editor.model.property.BigIntegerPropertyEditor;
import com.gurella.studio.editor.model.property.BitsPropertyEditor;
import com.gurella.studio.editor.model.property.BooleanPropertyEditor;
import com.gurella.studio.editor.model.property.BytePropertyEditor;
import com.gurella.studio.editor.model.property.CharacterPropertyEditor;
import com.gurella.studio.editor.model.property.CollectionPropertyEditor;
import com.gurella.studio.editor.model.property.ColorPropertyEditor;
import com.gurella.studio.editor.model.property.CustomComplexPropertyEditor;
import com.gurella.studio.editor.model.property.CustomSimplePropertyEditor;
import com.gurella.studio.editor.model.property.DatePropertyEditor;
import com.gurella.studio.editor.model.property.DoublePropertyEditor;
import com.gurella.studio.editor.model.property.EnumPropertyEditor;
import com.gurella.studio.editor.model.property.FloatPropertyEditor;
import com.gurella.studio.editor.model.property.GdxArrayPropertyEditor;
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
import com.gurella.studio.editor.model.property.SimpleObjectPropertyEditor;
import com.gurella.studio.editor.model.property.StringPropertyEditor;
import com.gurella.studio.editor.model.property.Vector2PropertyEditor;
import com.gurella.studio.editor.model.property.Vector3PropertyEditor;
import com.gurella.studio.editor.model.property.bullet.BulletCollisionShapePropertyEditor;

public class PropertyEditorFactory {
	public static <T> PropertyEditor<T> createEditor(Composite parent, PropertyEditorContext<?, T> context) {
		PropertyEditorDescriptor editorDescriptor = getEditorDescriptor(context);
		if (editorDescriptor == null) {
			Class<T> propertyType = context.property.getType();
			return createEditor(parent, context, propertyType);
		} else {
			return editorDescriptor.complex()
					? new CustomComplexPropertyEditor<>(parent, context, cast(newInstance(editorDescriptor.factory())))
					: new CustomSimplePropertyEditor<>(parent, context, cast(newInstance(editorDescriptor.factory())));
		}
	}

	private static <T> PropertyEditorDescriptor getEditorDescriptor(PropertyEditorContext<?, T> context) {
		Property<T> property = context.property;
		if (!(property instanceof ReflectionProperty)) {
			return null;
		}

		ReflectionProperty<T> reflectionProperty = cast(property);
		return getDeclaredAnnotation(reflectionProperty.getField(), PropertyEditorDescriptor.class);
	}

	public static <T> PropertyEditor<T> createEditor(Composite parent, PropertyEditorContext<?, T> context,
			Class<T> propertyType) {
		if (propertyType == Boolean.class || propertyType == boolean.class) {
			return Values.cast(new BooleanPropertyEditor(parent, cast(context)));
		} else if (propertyType == Integer.class || propertyType == int.class) {
			return cast(new IntegerPropertyEditor(parent, cast(context)));
		} else if (propertyType == Long.class || propertyType == long.class) {
			return cast(new LongPropertyEditor(parent, cast(context)));
		} else if (propertyType == Float.class || propertyType == float.class) {
			return cast(new FloatPropertyEditor(parent, cast(context)));
		} else if (propertyType == Byte.class || propertyType == byte.class) {
			return cast(new BytePropertyEditor(parent, cast(context)));
		} else if (propertyType == Short.class || propertyType == short.class) {
			return cast(new ShortPropertyEditor(parent, cast(context)));
		} else if (propertyType == Character.class || propertyType == char.class) {
			return cast(new CharacterPropertyEditor(parent, cast(context)));
		} else if (propertyType == Double.class || propertyType == double.class) {
			return cast(new DoublePropertyEditor(parent, cast(context)));
		} else if (propertyType == String.class) {
			return cast(new StringPropertyEditor(parent, cast(context)));
		} else if (propertyType == Date.class) {
			return cast(new DatePropertyEditor(parent, cast(context)));
		} else if (propertyType == Vector3.class) {
			return cast(new Vector3PropertyEditor(parent, cast(context)));
		} else if (propertyType == Vector2.class) {
			return cast(new Vector2PropertyEditor(parent, cast(context)));
		} else if (propertyType == Quaternion.class) {
			return cast(new QuaternionPropertyEditor(parent, cast(context)));
		} else if (propertyType == GridPoint2.class) {
			return cast(new GridPoint2PropertyEditor(parent, cast(context)));
		} else if (propertyType == GridPoint3.class) {
			return cast(new GridPoint3PropertyEditor(parent, cast(context)));
		} else if (propertyType == Matrix3.class) {
			return cast(new Matrix3PropertyEditor(parent, cast(context)));
		} else if (propertyType == Matrix4.class) {
			return cast(new Matrix4PropertyEditor(parent, cast(context)));
		} else if (propertyType == Color.class) {
			return cast(new ColorPropertyEditor(parent, cast(context)));
		} else if (propertyType == Layer.class) {
			return cast(new LayerPropertyEditor(parent, cast(context)));
		} else if (propertyType == Bits.class || propertyType == BitsExt.class) {
			return cast(new BitsPropertyEditor(parent, cast(context)));
		} else if (propertyType == BigInteger.class) {
			return cast(new BigIntegerPropertyEditor(parent, cast(context)));
		} else if (propertyType == BigDecimal.class) {
			return cast(new BigDecimalPropertyEditor(parent, cast(context)));
		} else if (propertyType.isArray()) {
			return cast(new ArrayPropertyEditor<>(parent, context));
		} else if (propertyType.isEnum()) {
			return cast(new EnumPropertyEditor<>(parent, cast(context)));
		} else if (Assets.isAssetType(propertyType)) {
			return cast(new AssetPropertyEditor<>(parent, context, propertyType));
		} else if (context.property.isFinal() && context.modelInstance != null && isSimpleProperty(propertyType)) {
			// TODO handle in ReflectionPropertyEditor
			return cast(new SimpleObjectPropertyEditor<>(parent, context));
		}

		/////

		else if (BulletCollisionShape.class.isAssignableFrom(propertyType)) {
			return cast(new BulletCollisionShapePropertyEditor(parent, cast(context)));
		}

		///// custom models for collections...

		else if (Array.class.isAssignableFrom(propertyType)) {
			return cast(new GdxArrayPropertyEditor<>(parent, cast(context)));
		} else if (Collection.class.isAssignableFrom(propertyType)) {
			return cast(new CollectionPropertyEditor<>(parent, cast(context)));
		}

		else {
			return new ReflectionPropertyEditor<T>(parent, context);
		}
	}

	public static boolean isSimpleProperty(Class<?> propertyType) {
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

		return editableProperty != null && Models.getModel(editableProperty.getType()) instanceof SimpleModel;
	}
}
