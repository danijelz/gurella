package com.gurella.engine.base.model;

import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.Method;
import com.gurella.engine.base.container.InitializationContext;
import com.gurella.engine.base.container.ManagedObject;
import com.gurella.engine.base.model.ValueRange.ByteRange;
import com.gurella.engine.base.model.ValueRange.CharRange;
import com.gurella.engine.base.model.ValueRange.DoubleRange;
import com.gurella.engine.base.model.ValueRange.FloatRange;
import com.gurella.engine.base.model.ValueRange.IntegerRange;
import com.gurella.engine.base.model.ValueRange.LongRange;
import com.gurella.engine.base.model.ValueRange.ShortRange;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.Range;
import com.gurella.engine.utils.ReflectionUtils;
import com.gurella.engine.utils.ValueUtils;

public class ReflectionMetaProperty<T> implements MetaProperty<T> {
	private String name;
	private String descriptiveName;
	private String description;
	private String group;
	private Class<T> type;
	private Range<?> range;
	private boolean nullable;
	private boolean initByDefaultValue;
	private Object resolvedDefaultValue;

	private Field field;

	private Method getter;
	private Method setter;

	public ReflectionMetaProperty(Field field) {
		this.name = field.getName();
		this.field = field;
		type = field.getType();
		init();
	}

	public ReflectionMetaProperty(Field field, Method getter, Method setter) {
		this.name = field.getName();
		this.field = field;
		this.getter = getter;
		this.setter = setter;
		type = this.getter.getReturnType();
		init();
	}

	private void init() {
		init(ReflectionUtils.getDeclaredAnnotation(field, Property.class));
		range = initRange(ReflectionUtils.getDeclaredAnnotation(field, ValueRange.class));
		resolvedDefaultValue = initDefaultValue(ReflectionUtils.getDeclaredAnnotation(field, DefaultValue.class));
	}

	private void init(Property property) {
		if (property == null) {
			descriptiveName = name;
			description = "";
			group = "";
			nullable = isDefaultNullable();
		} else {
			descriptiveName = property.descriptiveName();
			if (ValueUtils.isEmpty(descriptiveName)) {
				descriptiveName = name;
			}
			description = property.description();
			group = property.group();
			nullable = isDefaultNullable() ? property.nullable() : false;
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

		initByDefaultValue = defaultValue.updateResourceOnInit();

		if (Integer.class.equals(type) || Integer.TYPE.equals(type)) {
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
		@SuppressWarnings({ "rawtypes", "unchecked" })
		ModelResourceFactory<?> factory = new ModelResourceFactory(type);

		PropertyValue[] values = defaultValue.compositeValues();
		if (ValueUtils.isNotEmpty(values)) {
			for (int i = 0; i < values.length; i++) {
				PropertyValue propertyValue = values[i];
				String propertyName = propertyValue.name();
				MetaProperty<?> resourceProperty = factory.getProperty(propertyName);
				Object value = getDefaultValue(propertyValue, resourceProperty.getType());
				factory.setPropertyValue(propertyName, value);
			}
		}

		return factory;
	}

	private static Object getDefaultValue(PropertyValue propertyValue, Class<?> valueType) {
		if (Integer.class.equals(valueType) || Integer.TYPE.equals(valueType)) {
			return Integer.valueOf(propertyValue.integerValue());
		} else if (Boolean.class.equals(valueType) || Boolean.TYPE.equals(valueType)) {
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

	@Override
	public void init(InitializationContext<?> context) {
		JsonValue serializedValue = context.serializedValue;
		if (serializedValue == null) {
			Object template = context.template;
			if (template != null) {
				MetaModel<? extends Object> model = ModelUtils.getModel(template.getClass());
				initValue(context.initializingObject, getValue(template));
			} else if (initByDefaultValue) {
				initValue(context.initializingObject, getDefaultValue());
			}
		} else {
			JsonValue serializedPropertyValue = serializedValue.get(name);
			if (serializedPropertyValue != null) {

			} else if (initByDefaultValue) {
				initValue(context.initializingObject, getDefaultValue());
			}
		}
		// TODO Auto-generated method stub
	}

	private void initValue(Object initializingObject, Object value) {
		if (setter != null) {
			initBySetter(initializingObject, value);
		} else if (field != null) {
			initByField(initializingObject, value);
		}
	}

	private void initBySetter(Object initializingObject, Object value) {
		boolean accessible = setter.isAccessible();
		try {
			if (!accessible) {
				setter.setAccessible(true);
			}
			ReflectionUtils.invokeMethod(setter, initializingObject, value);
		} finally {
			if (!accessible) {
				setter.setAccessible(false);
			}
		}
	}

	private void initByField(Object initializingObject, Object value) {
		boolean accessible = field.isAccessible();
		try {
			if (!accessible) {
				field.setAccessible(true);
			}
			if (field.isFinal()) {
				initFinalProperty(initializingObject, value);
			} else {
				ReflectionUtils.setFieldValue(field, initializingObject, value);
			}
		} finally {
			if (!accessible) {
				field.setAccessible(false);
			}
		}
	}

	private void initFinalProperty(Object initializingObject, Object value) {
		Object fieldValue = ReflectionUtils.getFieldValue(field, initializingObject);
		MetaModel<?> model = ModelUtils.getModel(fieldValue.getClass());
		// TODO garbage
		InitializationContext<Object> context = new InitializationContext<Object>();
		context.template = fieldValue;

		ImmutableArray<MetaProperty<?>> properties = model.getProperties();
		for (int i = 0; i < properties.size(); i++) {
			properties.get(i).init(context);
		}
	}
}
