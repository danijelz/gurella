package com.gurella.studio.editor.common.property;

import static com.gurella.engine.utils.Values.cast;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;

import org.eclipse.jdt.core.IJavaProject;
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
import com.gurella.engine.utils.BitsExt;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.Values;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.engine.property.CustomCompositePropertyEditor;
import com.gurella.studio.editor.engine.property.CustomPropertyEditor;
import com.gurella.studio.editor.engine.property.CustomSimplePropertyEditor;

public class PropertyEditorFactory {
	public static <T> PropertyEditor<T> createEditor(Composite parent, PropertyEditorContext<?, T> context) {
		PropertyEditor<T> customEditor = createCustomEditor(parent, context);
		if (customEditor == null) {
			Class<T> propertyType = context.property.getType();
			return createEditor(parent, context, propertyType);
		} else {
			return customEditor;
		}
	}

	private static <T> PropertyEditor<T> createCustomEditor(Composite parent, PropertyEditorContext<?, T> context) {
		try {
			IJavaProject javaProject = context.sceneEditorContext.javaProject;
			Class<?> modelClass = context.modelInstance.getClass();
			Property<?> property = context.property;
			PropertyEditorData data = PropertyEditorData.get(javaProject, modelClass, property);
			if (data == null || !data.isValidFactoryClass()) {
				return null;
			}

			ClassLoader classLoader = context.sceneEditorContext.classLoader;
			Class<?> factoryClass = classLoader.loadClass(data.factoryClass);
			Constructor<?> constructor = factoryClass.getDeclaredConstructor(new Class[0]);
			constructor.setAccessible(true);
			Object factory = constructor.newInstance(new Object[0]);
			switch (data.type) {
			case composite:
				return new CustomCompositePropertyEditor<>(parent, context, cast(factory));
			case simple:
				return new CustomSimplePropertyEditor<>(parent, context, cast(factory));
			case custom:
				return new CustomPropertyEditor<>(parent, context, cast(factory));
			default:
				return new CustomCompositePropertyEditor<>(parent, context, cast(factory));
			}
		} catch (Exception e) {
			GurellaStudioPlugin.log(e, "Error creating editor.");
			return null;
		}
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

		///// custom editors for collections...

		else if (Array.class.isAssignableFrom(propertyType)) {
			return cast(new GdxArrayPropertyEditor<>(parent, cast(context)));
		} else if (Collection.class.isAssignableFrom(propertyType)) {
			return cast(new CollectionPropertyEditor<>(parent, cast(context)));
		} else {
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
