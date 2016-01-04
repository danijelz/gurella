package com.gurella.engine.base.model;

import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.Method;
import com.gurella.engine.asset.AssetId;
import com.gurella.engine.base.model.ValueRange.ByteRange;
import com.gurella.engine.base.model.ValueRange.CharRange;
import com.gurella.engine.base.model.ValueRange.DoubleRange;
import com.gurella.engine.base.model.ValueRange.FloatRange;
import com.gurella.engine.base.model.ValueRange.IntegerRange;
import com.gurella.engine.base.model.ValueRange.LongRange;
import com.gurella.engine.base.model.ValueRange.ShortRange;
import com.gurella.engine.base.registry.InitializationContext;
import com.gurella.engine.base.registry.ManagedObject;
import com.gurella.engine.base.registry.ObjectReference;
import com.gurella.engine.base.registry.Objects;
import com.gurella.engine.utils.Range;
import com.gurella.engine.utils.ReflectionUtils;
import com.gurella.engine.utils.ValueUtils;

public class ReflectionProperty<T> implements Property<T> {
	private String name;
	private String descriptiveName;
	private String description;
	private String group;
	private Class<T> type;
	private Range<?> range;
	private boolean nullable;
	private boolean applyDefaultValueOnInit;
	private T defaultValue;

	private Field field;
	private Method getter;
	private Method setter;

	public ReflectionProperty(Field field) {
		this(field, null, null);
	}

	public ReflectionProperty(Field field, Method getter, Method setter) {
		this.name = field.getName();
		this.field = field;
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

		init();
	}

	private void init() {
		init(ReflectionUtils.getDeclaredAnnotation(field, PropertyDescriptor.class));
		range = initRange(ReflectionUtils.getDeclaredAnnotation(field, ValueRange.class));
		@SuppressWarnings("unchecked")
		T casted = (T) initDefaultValue(ReflectionUtils.getDeclaredAnnotation(field, DefaultValue.class));
		defaultValue = casted;
	}

	private void init(PropertyDescriptor propertyDescriptor) {
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
	}

	private boolean isDefaultNullable() {
		return !(type.isPrimitive() || (field != null && field.isFinal()));
	}

	private Range<?> initRange(ValueRange valueRange) {
		if (valueRange == null) {
			return null;
		}

		if (Integer.class.equals(type) || Integer.TYPE.equals(type)) {
			IntegerRange integerRange = valueRange.integerRange();
			return integerRange == null ? null
					: new Range<Integer>(Integer.valueOf(integerRange.min()), Integer.valueOf(integerRange.max()));
		} else if (Float.class.equals(type) || Float.TYPE.equals(type)) {
			FloatRange floatRange = valueRange.floatRange();
			return floatRange == null ? null
					: new Range<Float>(Float.valueOf(floatRange.min()), Float.valueOf(floatRange.max()));
		} else if (Long.class.equals(type) || Long.TYPE.equals(type)) {
			LongRange longRange = valueRange.longRange();
			return longRange == null ? null
					: new Range<Long>(Long.valueOf(longRange.min()), Long.valueOf(longRange.max()));
		} else if (Double.class.equals(type) || Double.TYPE.equals(type)) {
			DoubleRange doubleRange = valueRange.doubleRange();
			return doubleRange == null ? null
					: new Range<Double>(Double.valueOf(doubleRange.min()), Double.valueOf(doubleRange.max()));
		} else if (Short.class.equals(type) || Short.TYPE.equals(type)) {
			ShortRange shortRange = valueRange.shortRange();
			return shortRange == null ? null
					: new Range<Short>(Short.valueOf(shortRange.min()), Short.valueOf(shortRange.max()));
		} else if (Byte.class.equals(type) || Byte.TYPE.equals(type)) {
			ByteRange byteRange = valueRange.byteRange();
			return byteRange == null ? null
					: new Range<Byte>(Byte.valueOf(byteRange.min()), Byte.valueOf(byteRange.max()));
		} else if (Character.class.equals(type) || Character.TYPE.equals(type)) {
			CharRange charRange = valueRange.charRange();
			return charRange == null ? null
					: new Range<Character>(Character.valueOf(charRange.min()), Character.valueOf(charRange.max()));
		} else {
			return null;
		}
	}

	private Object initDefaultValue(DefaultValue defaultValue) {
		if (defaultValue == null) {
			return null;
		}

		applyDefaultValueOnInit = defaultValue.applyOnInit();

		if (Integer.class == type || int.class == type) {
			return Integer.valueOf(defaultValue.integerValue());
		} else if (Boolean.class.equals(type) || Boolean.TYPE.equals(type)) {
			return Boolean.valueOf(defaultValue.booleanValue());
		} else if (Float.class.equals(type) || Float.TYPE.equals(type)) {
			return Float.valueOf(defaultValue.floatValue());
		} else if (Long.class.equals(type) || Long.TYPE.equals(type)) {
			return Long.valueOf(defaultValue.longValue());
		} else if (Double.class.equals(type) || Double.TYPE.equals(type)) {
			return Double.valueOf(defaultValue.doubleValue());
		} else if (Short.class.equals(type) || Short.TYPE.equals(type)) {
			return Short.valueOf(defaultValue.shortValue());
		} else if (Byte.class.equals(type) || Byte.TYPE.equals(type)) {
			return Byte.valueOf(defaultValue.byteValue());
		} else if (Character.class.equals(type) || Character.TYPE.equals(type)) {
			return Character.valueOf(defaultValue.charValue());
		} else if (String.class.equals(type)) {
			return defaultValue.stringValue();
		} else if (type.isEnum()) {
			return type.getEnumConstants()[defaultValue.enumOrdinal()];
		} else {
			return createCompositeDefaultValue(defaultValue);
		}
	}

	private Object createCompositeDefaultValue(DefaultValue defaultValue) {
		Model<T> model = Models.getModel(type);
		T resolvedDefaultValue = model.createInstance();
		PropertyValue[] values = defaultValue.compositeValues();

		if (ValueUtils.isNotEmpty(values)) {
			for (int i = 0; i < values.length; i++) {
				PropertyValue propertyValue = values[i];
				String propertyName = propertyValue.name();
				Property<Object> resourceProperty = model.getProperty(propertyName);
				Object value = getDefaultValue(propertyValue, resourceProperty.getType());
				resourceProperty.setValue(resolvedDefaultValue, value);
			}
		}

		return resolvedDefaultValue;
	}

	private static Object getDefaultValue(PropertyValue propertyValue, Class<?> valueType) {
		if (Integer.class == valueType || int.class == valueType) {
			return Integer.valueOf(propertyValue.integerValue());
		} else if (Boolean.class == valueType || boolean.class.equals(valueType)) {
			return Boolean.valueOf(propertyValue.booleanValue());
		} else if (Float.class.equals(valueType) || Float.TYPE.equals(valueType)) {
			return Float.valueOf(propertyValue.floatValue());
		} else if (Long.class.equals(valueType) || Long.TYPE.equals(valueType)) {
			return Long.valueOf(propertyValue.longValue());
		} else if (Double.class.equals(valueType) || Double.TYPE.equals(valueType)) {
			return Double.valueOf(propertyValue.doubleValue());
		} else if (Short.class.equals(valueType) || Short.TYPE.equals(valueType)) {
			return Short.valueOf(propertyValue.shortValue());
		} else if (Byte.class.equals(valueType) || Byte.TYPE.equals(valueType)) {
			return Byte.valueOf(propertyValue.byteValue());
		} else if (Character.class.equals(valueType) || Character.TYPE.equals(valueType)) {
			return Character.valueOf(propertyValue.charValue());
		} else if (String.class.equals(valueType)) {
			return propertyValue.stringValue();
		} else if (valueType.isEnum()) {
			return valueType.getEnumConstants()[propertyValue.enumOrdinal()];
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

	public boolean isApplyDefaultValueOnInit() {
		return applyDefaultValueOnInit;
	}

	@Override
	public void init(InitializationContext<?> context) {
		Object initializingObject = context.initializingObject;
		JsonValue serializedPropertyValue = context.serializedValue == null ? null : context.serializedValue.get(name);

		if (serializedPropertyValue == null) {
			Object template = context.template;
			T value;
			if (template != null) {
				value = getValue(template);
			} else if (!applyDefaultValueOnInit) {
				return;
			} else {
				value = defaultValue;
			}

			T resolvedValue = field.isFinal() ? value : copyValue(context, value);
			setValue(initializingObject, resolvedValue);
		} else {
			T resolvedValue;
			T value = context.json.readValue(type, null, serializedPropertyValue);
			if (value instanceof ObjectReference) {
				ObjectReference objectReference = (ObjectReference) value;
				@SuppressWarnings("unchecked")
				T instance = (T) context.getInstance(objectReference.getId());
				resolvedValue = instance;
			} else if (value instanceof AssetId) {
				AssetId assetId = (AssetId) value;
				@SuppressWarnings("unchecked")
				T instance = (T) context.getInstance(objectReference.getId());
				resolvedValue = instance;
			} else {
				resolvedValue = value;
			}
			setValue(initializingObject, resolvedValue);
		}
	}

	private T copyValue(InitializationContext<?> context, T value) {
		if (value == null || type.isPrimitive() || type.isEnum() || Integer.class == type || Long.class == type
				|| Short.class == type || Byte.class == type || Character.class == type || Boolean.class == type
				|| Double.class == type || Float.class == type || String.class == type) {
			return value;
		} else if (value instanceof ManagedObject) {
			ManagedObject object = (ManagedObject) value;
			@SuppressWarnings("unchecked")
			T instance = (T) context.getInstance(object);
			return instance;
		} else {
			return Objects.duplicate(value, context);
		}
	}

	public Property<T> copy(PropertyValue propertyValue, boolean applyDefaultValueOnInit) {
		@SuppressWarnings("unchecked")
		T overridenValue = (T) getDefaultValue(propertyValue, type);
		if (ValueUtils.isEqual(defaultValue, overridenValue)) {
			return this;
		} else {
			ReflectionProperty<T> copy = new ReflectionProperty<T>(field, getter, setter);
			copy.name = name;
			copy.descriptiveName = descriptiveName;
			copy.description = description;
			copy.group = group;
			copy.type = type;
			copy.range = range;
			copy.nullable = nullable;
			copy.applyDefaultValueOnInit = applyDefaultValueOnInit;
			copy.defaultValue = overridenValue;
			return copy;
		}
	}

	@Override
	public T getValue(Object object) {
		if (getter == null) {
			@SuppressWarnings("unchecked")
			T casted = (T) ReflectionUtils.getFieldValue(field, object);
			return casted;
		} else {
			@SuppressWarnings("unchecked")
			T casted = (T) ReflectionUtils.invokeMethod(getter, object);
			return casted;
		}
	}

	@Override
	public void setValue(Object object, T value) {
		if (setter != null) {
			ReflectionUtils.invokeMethod(setter, object, value);
		} else if (field.isFinal()) {
			Object fieldValue = ReflectionUtils.getFieldValue(field, object);
			Objects.copyProperties(value, fieldValue);
		} else {
			ReflectionUtils.setFieldValue(field, object, value);
		}
	}
}
