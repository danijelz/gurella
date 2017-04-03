package com.gurella.engine.metatype;

import static com.badlogic.gdx.utils.reflect.ClassReflection.isAssignableFrom;

import java.lang.annotation.Annotation;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.reflect.ArrayReflection;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.Method;
import com.gurella.engine.asset.descriptor.AssetDescriptors;
import com.gurella.engine.editor.property.PropertyEditorDescriptor;
import com.gurella.engine.metatype.serialization.Input;
import com.gurella.engine.metatype.serialization.Output;
import com.gurella.engine.utils.DefaultInstances;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.Range;
import com.gurella.engine.utils.Reflection;
import com.gurella.engine.utils.Values;

public class ReflectionProperty<T> implements Property<T> {
	private Class<?> declaringClass;
	private String name;
	private boolean editable;
	private Class<T> type;
	private Range<?> range;
	private boolean asset;
	private boolean nullable;
	private boolean finalProperty;
	private boolean copyable;
	private boolean flatSerialization;
	private T defaultValue;

	private Field field;
	private Method getter;
	private Method setter;

	public static <T> ReflectionProperty<T> newInstance(MetaType<?> metaType, String name) {
		Class<?> type = metaType.getType();
		Field field = Reflection.getDeclaredFieldSilently(type, name);
		String upperCaseName = name.substring(0, 1).toUpperCase() + name.substring(1);
		BeanPropertyMethods beanPropertyMethods = BeanPropertyMethods.getInstance(type, upperCaseName);

		if (field == null || isIgnoredProperty(type, field, beanPropertyMethods)) {
			if (beanPropertyMethods == null) {
				throw new GdxRuntimeException(name + " is not a property of " + type.getSimpleName());
			}

			Method getter = beanPropertyMethods.getter;
			Method setter = beanPropertyMethods.setter;
			Field resolvedField = field == null || field.getType() != getter.getReturnType() ? null : field;
			return new ReflectionProperty<T>(type, name, resolvedField, getter, setter, metaType);
		} else {
			return new ReflectionProperty<T>(type, field, metaType);
		}
	}

	// TODO unify with ReflectionMetaType.isIgnoredProperty
	private static boolean isIgnoredProperty(Class<?> type, Field field, BeanPropertyMethods beanPropertyMethods) {
		if (field.isStatic() || field.isTransient() || field.isSynthetic()
				|| field.getDeclaredAnnotation(TransientProperty.class) != null) {
			return true;
		}

		boolean hasPropertyAnnotation = Reflection.getDeclaredAnnotation(field, PropertyDescriptor.class) != null;
		if (field.isPrivate() && !hasPropertyAnnotation && beanPropertyMethods == null) {
			return true;
		}

		if (!field.isFinal() || hasPropertyAnnotation) {
			return false;
		}

		Class<?> fieldType = field.getType();
		if (fieldType.isPrimitive()) {
			return true;
		}

		field.setAccessible(true);
		Object defaultInstance = DefaultInstances.getDefault(type);
		if (defaultInstance != null) {
			Object fieldValue = Reflection.getFieldValue(field, defaultInstance);
			if (fieldValue == null) {
				return true;
			}

			fieldType = fieldValue.getClass();
			if (fieldType.isArray()) {
				return ArrayReflection.getLength(fieldValue) == 0;
			}
		}

		if (ClassReflection.isAssignableFrom(type, fieldType)) {
			return false;
		}

		if (AssetDescriptors.isAssetType(fieldType)) {
			AssetProperty assetProperty = Reflection.getDeclaredAnnotation(field, AssetProperty.class);
			return assetProperty != null && assetProperty.value();
		}

		ImmutableArray<Property<?>> properties = MetaTypes.getMetaType(fieldType).getProperties();
		return properties == null || properties.size() == 0;
	}

	public ReflectionProperty(Class<?> declaringClass, Field field, MetaType<?> metaType) {
		this(declaringClass, field.getName(), field, null, null, metaType);
	}

	public ReflectionProperty(Class<?> declaringClass, String name, Field field, Method getter, Method setter,
			MetaType<?> metaType) {
		this.declaringClass = declaringClass;
		this.name = name;

		this.field = field;
		if (this.field == null) {
			@SuppressWarnings("unchecked")
			Class<T> castedType = getter.getReturnType();
			type = castedType;
		} else {
			this.field.setAccessible(true);
			this.name = field.getName();
			finalProperty = field.isFinal();
			@SuppressWarnings("unchecked")
			Class<T> castedType = field.getType();
			type = castedType;
		}

		this.getter = getter;
		if (this.getter != null) {
			this.getter.setAccessible(true);
		}

		this.setter = setter;
		if (this.setter != null) {
			this.setter.setAccessible(true);
		}

		PropertyDescriptor propertyDescriptor = findAnnotation(PropertyDescriptor.class);
		if (propertyDescriptor == null) {
			nullable = isDefaultNullable();
			copyable = true;
			flatSerialization = isDefaultFlatSerialization();
		} else {
			nullable = isDefaultNullable() ? propertyDescriptor.nullable() : false;
			copyable = propertyDescriptor.copyable();
			flatSerialization = isDefaultFlatSerialization() ? true : propertyDescriptor.flatSerialization();
		}

		AssetProperty assetProperty = findAnnotation(AssetProperty.class);
		asset = (assetProperty == null || assetProperty.value()) && AssetDescriptors.isAssetType(type);

		range = Range.valueOf(findAnnotation(ValueRange.class), type);

		PropertyEditorDescriptor editorDescriptor = findAnnotation(PropertyEditorDescriptor.class);
		if (editorDescriptor == null) {
			editable = true;
		} else {
			editable = editorDescriptor.editable();
		}

		defaultValue = getValue(DefaultInstances.getDefault(metaType.getType()));
	}

	private <A extends Annotation> A findAnnotation(Class<A> type) {
		if (field != null) {
			A annotation = Reflection.getDeclaredAnnotation(field, type);
			if (annotation != null) {
				return annotation;
			}
		}

		if (getter != null) {
			A annotation = Reflection.getDeclaredAnnotation(getter, type);
			if (annotation != null) {
				return annotation;
			}
		}

		if (setter != null) {
			A annotation = Reflection.getDeclaredAnnotation(setter, type);
			if (annotation != null) {
				return annotation;
			}
		}

		return null;
	}

	private boolean isDefaultNullable() {
		return !(type.isPrimitive() || (field != null && field.isFinal()));
	}

	private boolean isDefaultFlatSerialization() {
		return (type.isPrimitive() || (field != null && field.isFinal() && getter == null));
	}

	public Class<?> getDeclaringClass() {
		return declaringClass;
	}

	public Field getField() {
		return field;
	}

	public Method getGetter() {
		return getter;
	}

	public Method getSetter() {
		return setter;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Class<T> getType() {
		return type;
	}

	@Override
	public Range<?> getRange() {
		return range;
	}

	@Override
	public boolean isAsset() {
		return asset;
	}

	@Override
	public boolean isNullable() {
		return nullable;
	}

	@Override
	public boolean isFinal() {
		return finalProperty;
	}

	@Override
	public boolean isCopyable() {
		return copyable;
	}

	@Override
	public boolean isFlatSerialization() {
		return flatSerialization;
	}

	@Override
	public boolean isEditable() {
		return editable;
	}

	@Override
	public Property<T> newInstance(MetaType<?> owner) {
		T overriden = getValue(DefaultInstances.getDefault(owner.getType()));
		return Values.isEqual(defaultValue, overriden, true) ? this
				: new ReflectionProperty<T>(declaringClass, name, field, getter, setter, owner);
	}

	@Override
	public T getValue(Object object) {
		if (object == null) {
			return null;
		} else if (getter == null) {
			return Reflection.getFieldValue(field, object);
		} else {
			return Reflection.invokeMethod(getter, object);
		}
	}

	@Override
	public void setValue(Object object, T value) {
		if (setter != null) {
			Reflection.invokeMethod(setter, object, value);
		} else if (field.isFinal()) {
			T fieldValue = Reflection.getFieldValue(field, object);
			updateFinalValueProperties(value, fieldValue);
		} else {
			Reflection.setFieldValue(field, object, value);
		}
	}

	private static <T> void updateFinalValueProperties(T source, T target) {
		if (source == null || target == null) {
			return;
		}

		Class<? extends Object> sourceType = source.getClass();
		Class<? extends Object> targetType = target.getClass();
		if (targetType.isArray() && isAssignableFrom(targetType, sourceType)) {
			int length = Math.min(ArrayReflection.getLength(source), ArrayReflection.getLength(target));
			System.arraycopy(source, 0, target, 0, length);
		} else {
			MetaType<Object> metaType = MetaTypes.getCommonMetaType(source, target);
			ImmutableArray<Property<?>> properties = metaType.getProperties();
			for (int i = 0; i < properties.size(); i++) {
				@SuppressWarnings("unchecked")
				Property<Object> property = (Property<Object>) properties.get(i);
				Object value = property.getValue(source);
				property.setValue(target, value);
			}
		}
	}

	@Override
	public void serialize(Object object, Object template, Output output) {
		T value = getValue(object);
		Object resolvedTemplate = MetaTypes.resolveTemplate(object, template);
		Object templateValue = resolvedTemplate == null ? defaultValue : getValue(resolvedTemplate);

		if (!Values.isEqual(value, templateValue)) {
			if (value == null) {
				output.writeNullProperty(name);
			} else {
				output.writeObjectProperty(name, type, flatSerialization, value, templateValue);
			}
		}
	}

	@Override
	public void deserialize(Object object, Object template, Input input) {
		Object resolvedTemplate = MetaTypes.resolveTemplate(object, template);
		if (input.hasProperty(name)) {
			T value = getValue(object);
			Object templateValue = resolvedTemplate == null ? value : getValue(resolvedTemplate);
			setValue(object, input.readObjectProperty(name, type, templateValue));
		} else if (resolvedTemplate != null) {
			T value = getValue(object);
			T templateValue = getValue(resolvedTemplate);
			if (!Values.isEqual(value, templateValue)) {
				setValue(object, field.isFinal() ? templateValue : input.copyObject(templateValue));
			}
		}
	}

	@Override
	public void copy(Object original, Object duplicate, CopyContext context) {
		setValue(duplicate, context.copy(getValue(original)));
	}
}
