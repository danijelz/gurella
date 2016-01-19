package com.gurella.engine.base.model;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.reflect.ArrayReflection;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.Method;
import com.gurella.engine.base.model.ValueRange.ByteRange;
import com.gurella.engine.base.model.ValueRange.CharRange;
import com.gurella.engine.base.model.ValueRange.DoubleRange;
import com.gurella.engine.base.model.ValueRange.FloatRange;
import com.gurella.engine.base.model.ValueRange.IntegerRange;
import com.gurella.engine.base.model.ValueRange.LongRange;
import com.gurella.engine.base.model.ValueRange.ShortRange;
import com.gurella.engine.base.registry.InitializationContext;
import com.gurella.engine.base.registry.Objects;
import com.gurella.engine.base.serialization.Archive;
import com.gurella.engine.base.serialization.ArrayType;
import com.gurella.engine.base.serialization.AssetReference;
import com.gurella.engine.base.serialization.ObjectReference;
import com.gurella.engine.base.serialization.Serialization;
import com.gurella.engine.utils.Range;
import com.gurella.engine.utils.ReflectionUtils;
import com.gurella.engine.utils.SynchronizedPools;
import com.gurella.engine.utils.ValueUtils;

public class ReflectionProperty<T> implements Property<T> {
	private String name;
	private String descriptiveName;
	private String description;
	private String group;
	private Class<T> type;
	private Range<?> range;
	private boolean nullable;
	private T defaultValue;

	private Field field;
	private Method getter;
	private Method setter;
	private Model<?> model;

	public ReflectionProperty(Field field, Model<?> model) {
		this(field, null, null, model);
	}

	public ReflectionProperty(Field field, Method getter, Method setter, Model<?> model) {
		this.name = field.getName();
		this.field = field;
		this.model = model;
		this.field.setAccessible(true);
		@SuppressWarnings("unchecked")
		Class<T> castedType = field.getType();
		type = castedType;

		this.getter = getter;
		if (this.getter != null) {
			this.getter.setAccessible(true);
		}

		this.setter = setter;
		if (this.setter != null) {
			this.setter.setAccessible(true);
		}

		PropertyDescriptor propertyDescriptor = ReflectionUtils.getDeclaredAnnotation(field, PropertyDescriptor.class);
		if (propertyDescriptor == null) {
			descriptiveName = name;
			description = "";
			group = "";
			nullable = isDefaultNullable();
		} else {
			descriptiveName = propertyDescriptor.descriptiveName();
			if (ValueUtils.isEmpty(descriptiveName)) {
				descriptiveName = name;
			}
			description = propertyDescriptor.description();
			group = propertyDescriptor.group();
			nullable = isDefaultNullable() ? propertyDescriptor.nullable() : false;
		}

		range = extractRange(ReflectionUtils.getDeclaredAnnotation(field, ValueRange.class));
		defaultValue = getValue(Defaults.getDefault(model.getType()));
	}

	private boolean isDefaultNullable() {
		return !(type.isPrimitive() || (field != null && field.isFinal()));
	}

	private Range<?> extractRange(ValueRange valueRange) {
		if (valueRange == null) {
			return null;
		}

		if (Integer.class == type || int.class == type || Integer[].class == type || int[].class == type) {
			IntegerRange integerRange = valueRange.integerRange();
			return integerRange == null ? null
					: new Range<Integer>(Integer.valueOf(integerRange.min()), Integer.valueOf(integerRange.max()));
		} else if (Float.class == type || float.class == type || Float[].class == type || float[].class == type) {
			FloatRange floatRange = valueRange.floatRange();
			return floatRange == null ? null
					: new Range<Float>(Float.valueOf(floatRange.min()), Float.valueOf(floatRange.max()));
		} else if (Long.class == type || long.class == type || Long[].class == type || long[].class == type) {
			LongRange longRange = valueRange.longRange();
			return longRange == null ? null
					: new Range<Long>(Long.valueOf(longRange.min()), Long.valueOf(longRange.max()));
		} else if (Double.class == type || double.class == type || Double[].class == type || double[].class == type) {
			DoubleRange doubleRange = valueRange.doubleRange();
			return doubleRange == null ? null
					: new Range<Double>(Double.valueOf(doubleRange.min()), Double.valueOf(doubleRange.max()));
		} else if (Short.class == type || short.class == type || Short[].class == type || short[].class == type) {
			ShortRange shortRange = valueRange.shortRange();
			return shortRange == null ? null
					: new Range<Short>(Short.valueOf(shortRange.min()), Short.valueOf(shortRange.max()));
		} else if (Byte.class == type || byte.class == type || Byte[].class == type || byte[].class == type) {
			ByteRange byteRange = valueRange.byteRange();
			return byteRange == null ? null
					: new Range<Byte>(Byte.valueOf(byteRange.min()), Byte.valueOf(byteRange.max()));
		} else if (Character.class == type || char.class == type || Character[].class == type || char[].class == type) {
			CharRange charRange = valueRange.charRange();
			return charRange == null ? null
					: new Range<Character>(Character.valueOf(charRange.min()), Character.valueOf(charRange.max()));
		} else {
			return null;
		}
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
	public Model<?> getModel() {
		return model;
	}

	@Override
	public Range<?> getRange() {
		return range;
	}

	@Override
	public boolean isNullable() {
		return nullable;
	}

	@Override
	public String getDescriptiveName() {
		return descriptiveName;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getGroup() {
		return group;
	}

	@Override
	public Property<T> copy(Model<?> newModel) {
		return new ReflectionProperty<T>(field, getter, setter, newModel);
	}

	@Override
	public void init(InitializationContext context) {
		JsonValue serializedObject = context.serializedValue();
		JsonValue serializedValue = serializedObject == null ? null : serializedObject.get(name);
		if (serializedValue == null) {
			initFromTemplate(context);
		} else {
			initFromSerializedValue(context, serializedValue);
		}
	}

	private void initFromTemplate(InitializationContext context) {
		Object template = context.template();
		if (template == null) {
			return;
		}

		T value = getValue(template);
		if (ValueUtils.isEqual(value, defaultValue)) {
			return;
		}

		Object initializingObject = context.initializingObject();
		if (value == null || type.isPrimitive()) {
			setValue(initializingObject, value);
		} else {
			setValue(initializingObject, field.isFinal() ? value : Objects.copyValue(value, context));
		}
	}

	private void initFromTemplate1(InitializationContext context) {
		Object template = context.template();
		if (template == null) {
			return;
		}

		T value = getValue(template);
		if (ValueUtils.isEqual(value, defaultValue)) {
			return;
		}

		Object initializingObject = context.initializingObject();
		if (value == null) {
			setValue(initializingObject, null);
		} else if (value.getClass().isArray()) {
			int length = ArrayReflection.getLength(template);
			@SuppressWarnings("unchecked")
			T array = (T) ArrayReflection.newInstance(type, length);
			for (int i = 0; i < length; i++) {
				Object item = ArrayReflection.get(template, i);
				ArrayReflection.set(array, i, Objects.copyValue(item, context));
			}
			setValue(initializingObject, array);
		} else {
			setValue(initializingObject, field.isFinal() ? value : Objects.copyValue(value, context));
		}
	}

	private void initFromSerializedValue(InitializationContext context, JsonValue serializedValue) {
		Object initializingObject = context.initializingObject();
		if (serializedValue.isNull()) {
			if (!field.isFinal()) {
				setValue(initializingObject, null);
			}
			return;
		}

		Class<T> resolvedType = type.isPrimitive() ? type : Serialization.resolveObjectType(type, serializedValue);
		Model<T> model = Models.getModel(resolvedType);
		if (field.isFinal()) {
			T value = getValue(initializingObject);
			if (value == null) {
				return;
			}

			Class<? extends Object> targetType = value.getClass();
			if (!type.isPrimitive() && targetType != resolvedType) {
				throw new GdxRuntimeException("Unequal types.");
			}

			context.push(value, null, serializedValue);
			model.initInstance(context);
			context.pop();
		} else {
			context.push(null, null, serializedValue);
			T value = model.createInstance(context);
			context.setInitializingObject(value);
			model.initInstance(context);
			context.pop();
			setValue(initializingObject, value);
		}
	}

	private void initFromSerializedValue1(InitializationContext context, JsonValue serializedValue) {
		Object initializingObject = context.initializingObject();
		if (serializedValue.isNull()) {
			setValue(initializingObject, null);
			return;
		}

		Class<T> resolvedType = Serialization.resolveObjectType(type, serializedValue);
		if (resolvedType.isArray()) {
			int size = serializedValue.size;
			Class<?> componentType = resolvedType.getComponentType();
			JsonValue item = serializedValue.child;
			Class<?> itemType = Serialization.resolveObjectType(Object.class, item);
			if (itemType == ArrayType.class) {
				item = item.next;
				size--;
			}

			@SuppressWarnings("unchecked")
			T array = (T) ArrayReflection.newInstance(componentType, size);

			int i = 0;
			for (; item != null; item = item.next) {
				if (item.isNull()) {
					ArrayReflection.set(array, i++, null);
				} else {
					Class<?> resolvedItemType = Serialization.resolveObjectType(componentType, item);
					if (Serialization.isSimpleType(resolvedItemType)) {
						ArrayReflection.set(array, i++, context.json.readValue(resolvedItemType, null, item));
					} else if (ClassReflection.isAssignableFrom(AssetReference.class, resolvedItemType)) {
						AssetReference assetReference = context.json.readValue(AssetReference.class, null, item);
						ArrayReflection.set(array, i++, context.<T> getAsset(assetReference));
					} else if (ClassReflection.isAssignableFrom(ObjectReference.class, resolvedItemType)) {
						ObjectReference objectReference = context.json.readValue(ObjectReference.class, null, item);
						@SuppressWarnings("unchecked")
						T instance = (T) context.getInstance(objectReference.getId());
						ArrayReflection.set(array, i++, instance);
					} else {
						ArrayReflection.set(array, i++, Objects.deserialize(item, resolvedItemType, context));
					}
				}
			}

			setValue(initializingObject, array);
		} else {
			if (Serialization.isSimpleType(resolvedType)) {
				setValue(initializingObject, context.json.readValue(resolvedType, null, serializedValue));
			} else if (ClassReflection.isAssignableFrom(AssetReference.class, resolvedType)) {
				AssetReference assetReference = context.json.readValue(AssetReference.class, null, serializedValue);
				setValue(initializingObject, context.<T> getAsset(assetReference));
			} else if (ClassReflection.isAssignableFrom(ObjectReference.class, resolvedType)) {
				ObjectReference objectReference = context.json.readValue(ObjectReference.class, null, serializedValue);
				@SuppressWarnings("unchecked")
				T instance = (T) context.getInstance(objectReference.getId());
				setValue(initializingObject, instance);
			} else if (field.isFinal()) {
				initProperties(getValue(initializingObject), serializedValue, context);
			} else {
				setValue(initializingObject, Objects.deserialize(serializedValue, resolvedType, context));
			}
		}
	}

	private void initProperties(T target, JsonValue serializedValue, InitializationContext context) {
		if (target == null || serializedValue.isNull()) {
			return;
		}

		Class<? extends Object> targetType = target.getClass();
		Class<? extends Object> resolvedType = Serialization.resolveObjectType(targetType, serializedValue);
		if (targetType != resolvedType) {
			throw new GdxRuntimeException("Unequal types.");
		}

		Model<T> model = Models.getModel(target);
		context.push(target, null, serializedValue);
		model.initInstance(context);
		context.pop();
	}

	@Override
	public T getValue(Object object) {
		if (object == null) {
			return null;
		} else if (getter == null) {
			return ReflectionUtils.getFieldValue(field, object);
		} else {
			return ReflectionUtils.invokeMethod(getter, object);
		}
	}

	@Override
	public void setValue(Object object, T value) {
		if (setter != null) {
			ReflectionUtils.invokeMethod(setter, object, value);
		} else if (field.isFinal()) {
			Object fieldValue = ReflectionUtils.getFieldValue(field, object);
			copyProperties(value, fieldValue);
		} else {
			ReflectionUtils.setFieldValue(field, object, value);
		}
	}

	public static <T> void copyProperties(T source, T target) {
		if (source == null || target == null) {
			return;
		}

		Model<T> model = Models.getModel(source);
		InitializationContext context = SynchronizedPools.obtain(InitializationContext.class);
		context.push(target, source, null);
		model.initInstance(context);
		SynchronizedPools.free(context);
	}

	@Override
	public void serialize(Object object, Archive archive) {
		T value = getValue(object);
		if (Objects.isEqual(value, defaultValue)) {
			return;
		}

		if (value == null || !value.getClass().isArray()) {
			archive.writeValue(name, value, type);
		} else {
			archive.writeArrayStart(name);
			Class<?> actualType = value.getClass();
			if (actualType != type) {
				ArrayType arrayType = new ArrayType();
				arrayType.typeName = actualType.getName();
				archive.writeValue(arrayType, null);
			}

			Class<?> componentType = actualType.getComponentType();
			int length = ArrayReflection.getLength(value);
			for (int i = 0; i < length; i++) {
				Object item = ArrayReflection.get(value, i);
				archive.writeValue(item, componentType);
			}
			archive.writeArrayEnd();
		}
	}
}
