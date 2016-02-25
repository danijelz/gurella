package com.gurella.engine.resource.model;

import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.Method;
import com.gurella.engine.resource.DependencyMap;
import com.gurella.engine.resource.factory.ModelResourceFactory;
import com.gurella.engine.resource.model.ValueRange.ByteRange;
import com.gurella.engine.resource.model.ValueRange.CharRange;
import com.gurella.engine.resource.model.ValueRange.DoubleRange;
import com.gurella.engine.resource.model.ValueRange.FloatRange;
import com.gurella.engine.resource.model.ValueRange.IntegerRange;
import com.gurella.engine.resource.model.ValueRange.LongRange;
import com.gurella.engine.resource.model.ValueRange.ShortRange;
import com.gurella.engine.utils.Range;
import com.gurella.engine.utils.Reflection;
import com.gurella.engine.utils.Values;

public class ReflectionResourceModelProperty extends AbstractResourceModelProperty {
	private String name;
	private String descriptiveName;
	private String description;
	private String group;
	private Class<?> propertyType;
	private Range<?> range;
	private boolean nullable;
	private boolean initByDefaultValue;
	private boolean defaultValueResolved;
	private Object resolvedDefaultValue;

	private Field field;

	private Method getter;
	private Method setter;

	public ReflectionResourceModelProperty(Field field) {
		this.name = field.getName();
		this.field = field;
		propertyType = field.getType();
		init();
	}

	public ReflectionResourceModelProperty(Field field, Method getter, Method setter) {
		this.name = field.getName();
		this.field = field;
		this.getter = getter;
		this.setter = setter;
		propertyType = this.getter.getReturnType();
		init();
	}

	private void init() {
		init(Reflection.getDeclaredAnnotation(field, ResourceProperty.class));
		range = initRange(Reflection.getDeclaredAnnotation(field, ValueRange.class));
		resolvedDefaultValue = initDefaultValue(Reflection.getDeclaredAnnotation(field, DefaultValue.class));
	}

	private void init(ResourceProperty resourceProperty) {
		if (resourceProperty == null) {
			descriptiveName = name;
			description = "";
			group = "";
			nullable = isDefaultNullable();
		} else {
			descriptiveName = resourceProperty.descriptiveName();
			description = resourceProperty.description();
			group = resourceProperty.group();
			nullable = isDefaultNullable()
					? resourceProperty.nullable()
					: false;
		}
	}

	private boolean isDefaultNullable() {
		return !(propertyType.isPrimitive() || (field != null && field.isFinal()));
	}

	private Range<?> initRange(ValueRange valueRange) {
		if (valueRange == null) {
			return null;
		}

		if (Integer.class.equals(propertyType) || Integer.TYPE.equals(propertyType)) {
			IntegerRange integerRange = valueRange.integerRange();
			return integerRange == null
					? null
					: new Range<Integer>(Integer.valueOf(integerRange.min()), Integer.valueOf(integerRange.max()));
		} else if (Float.class.equals(propertyType) || Float.TYPE.equals(propertyType)) {
			FloatRange floatRange = valueRange.floatRange();
			return floatRange == null
					? null
					: new Range<Float>(Float.valueOf(floatRange.min()), Float.valueOf(floatRange.max()));
		} else if (Long.class.equals(propertyType) || Long.TYPE.equals(propertyType)) {
			LongRange longRange = valueRange.longRange();
			return longRange == null
					? null
					: new Range<Long>(Long.valueOf(longRange.min()), Long.valueOf(longRange.max()));
		} else if (Double.class.equals(propertyType) || Double.TYPE.equals(propertyType)) {
			DoubleRange doubleRange = valueRange.doubleRange();
			return doubleRange == null
					? null
					: new Range<Double>(Double.valueOf(doubleRange.min()), Double.valueOf(doubleRange.max()));
		} else if (Short.class.equals(propertyType) || Short.TYPE.equals(propertyType)) {
			ShortRange shortRange = valueRange.shortRange();
			return shortRange == null
					? null
					: new Range<Short>(Short.valueOf(shortRange.min()), Short.valueOf(shortRange.max()));
		} else if (Byte.class.equals(propertyType) || Byte.TYPE.equals(propertyType)) {
			ByteRange byteRange = valueRange.byteRange();
			return byteRange == null
					? null
					: new Range<Byte>(Byte.valueOf(byteRange.min()), Byte.valueOf(byteRange.max()));
		} else if (Character.class.equals(propertyType) || Character.TYPE.equals(propertyType)) {
			CharRange charRange = valueRange.charRange();
			return charRange == null
					? null
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
		defaultValueResolved = true;

		if (Integer.class.equals(propertyType) || Integer.TYPE.equals(propertyType)) {
			return Integer.valueOf(defaultValue.integerValue());
		} else if (Boolean.class.equals(propertyType) || Boolean.TYPE.equals(propertyType)) {
			return Boolean.valueOf(defaultValue.booleanValue());
		} else if (Float.class.equals(propertyType) || Float.TYPE.equals(propertyType)) {
			return Float.valueOf(defaultValue.floatValue());
		} else if (Long.class.equals(propertyType) || Long.TYPE.equals(propertyType)) {
			return Long.valueOf(defaultValue.longValue());
		} else if (Double.class.equals(propertyType) || Double.TYPE.equals(propertyType)) {
			return Double.valueOf(defaultValue.doubleValue());
		} else if (Short.class.equals(propertyType) || Short.TYPE.equals(propertyType)) {
			return Short.valueOf(defaultValue.shortValue());
		} else if (Byte.class.equals(propertyType) || Byte.TYPE.equals(propertyType)) {
			return Byte.valueOf(defaultValue.byteValue());
		} else if (Character.class.equals(propertyType) || Character.TYPE.equals(propertyType)) {
			return Character.valueOf(defaultValue.charValue());
		} else if (String.class.equals(propertyType)) {
			return defaultValue.stringValue();
		} else if (propertyType.isEnum()) {
			return propertyType.getEnumConstants()[defaultValue.enumOrdinal()];
		} else {
			return createCompositeDefaultValue(defaultValue);
		}
	}

	private Object createCompositeDefaultValue(DefaultValue defaultValue) {
		@SuppressWarnings({ "rawtypes", "unchecked" })
		ModelResourceFactory<?> factory = new ModelResourceFactory(propertyType);

		PropertyValue[] values = defaultValue.compositeValues();
		if (Values.isNotEmpty(values)) {
			for (int i = 0; i < values.length; i++) {
				PropertyValue propertyValue = values[i];
				String propertyName = propertyValue.name();
				ResourceModelProperty resourceProperty = factory.getProperty(propertyName);
				Object value = getDefaultValue(propertyValue, resourceProperty.getPropertyType());
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
	public void initFromSerializableValue(Object resource, Object serializableValue, DependencyMap dependencies) {
		if (setter != null) {
			initFromSerializableValueBySetter(resource, serializableValue, dependencies);
		} else if (field != null) {
			initFromSerializableValueByField(resource, serializableValue, dependencies);
		}
	}

	private void initFromSerializableValueBySetter(Object resource, Object serializableValue, DependencyMap dependencies) {
		boolean accessible = setter.isAccessible();
		try {
			if (!accessible) {
				setter.setAccessible(true);
			}
			Object resolvedValue = ResourceModelUtils.resolvePropertyValue(serializableValue, dependencies);
			Reflection.invokeMethod(setter, resource, resolvedValue);
		} finally {
			if (!accessible) {
				setter.setAccessible(false);
			}
		}
	}

	private void initFromSerializableValueByField(Object resource, Object serializableValue, DependencyMap dependencies) {
		boolean accessible = field.isAccessible();
		try {
			if (!accessible) {
				field.setAccessible(true);
			}
			if (field.isFinal()) {
				initFinalProperty(resource, field, serializableValue, dependencies);
			} else {
				initProperty(resource, field, serializableValue, dependencies);
			}
		} finally {
			if (!accessible) {
				field.setAccessible(false);
			}
		}
	}

	private static void initFinalProperty(Object resource, Field field, Object serializableValue,
			DependencyMap dependencies) {
		if (serializableValue instanceof ModelResourceFactory) {
			Object fieldValue = Reflection.getFieldValue(field, resource);
			@SuppressWarnings("unchecked")
			ModelResourceFactory<Object> factory = (ModelResourceFactory<Object>) serializableValue;
			factory.init(fieldValue, dependencies);
		}
	}

	private static void initProperty(Object resource, Field field, Object serializableValue, DependencyMap dependencies) {
		Object resolvedValue = ResourceModelUtils.resolvePropertyValue(serializableValue, dependencies);
		Reflection.setFieldValue(field, resource, resolvedValue);
	}

	@Override
	public void initFromDefaultValue(Object resource) {
		if (initByDefaultValue) {
			initFromSerializableValue(resource, resolvedDefaultValue, null);
		}
	}

	@Override
	public Object getDefaultValue() {
		if (!defaultValueResolved) {
			resolvedDefaultValue = resolveDefaultValue();
			defaultValueResolved = true;
		}
		return resolvedDefaultValue;
	}

	private Object resolveDefaultValue() {
		if (propertyType.isPrimitive()) {
			return getDefaulPrimitive();
		} else if (field.isFinal()) {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			ModelResourceFactory<?> factory = new ModelResourceFactory(propertyType);
			return factory;
		} else {
			return null;
		}
	}

	private Object getDefaulPrimitive() {
		if (Integer.TYPE.equals(propertyType)) {
			return Integer.valueOf(0);
		} else if (Boolean.TYPE.equals(propertyType)) {
			return Boolean.FALSE;
		} else if (Float.TYPE.equals(propertyType)) {
			return Float.valueOf(0);
		} else if (Long.TYPE.equals(propertyType)) {
			return Long.valueOf(0);
		} else if (Double.TYPE.equals(propertyType)) {
			return Double.valueOf(0);
		} else if (Short.TYPE.equals(propertyType)) {
			return Short.valueOf((short) 0);
		} else if (Byte.TYPE.equals(propertyType)) {
			return Byte.valueOf((byte) 0);
		} else if (Character.TYPE.equals(propertyType)) {
			return Character.valueOf((char) 0);
		} else {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public Class<?> getPropertyType() {
		return propertyType;
	}

	@Override
	protected Class<?> getSerializableValueType() {
		if (propertyType.isPrimitive() || String.class.equals(propertyType)
				|| ClassReflection.isAssignableFrom(Enum.class, propertyType)) {
			return propertyType;
		} else {
			return ModelResourceFactory.class;
		}
	}

	ResourceModelProperty copy(PropertyValue override, boolean initByDefaultValueOverride) {
		Object overridenValue = getDefaultValue(override, propertyType);
		if (Values.isEqual(getDefaultValue(), overridenValue)) {
			return this;
		} else {
			ReflectionResourceModelProperty copy = setter == null
					? new ReflectionResourceModelProperty(field)
					: new ReflectionResourceModelProperty(field, getter, setter);
			copy.name = name;
			copy.descriptiveName = descriptiveName;
			copy.description = description;
			copy.group = group;
			copy.propertyType = propertyType;
			copy.range = range;
			copy.nullable = nullable;
			copy.initByDefaultValue = initByDefaultValueOverride;
			copy.defaultValueResolved = true;
			copy.resolvedDefaultValue = overridenValue;
			return copy;
		}
	}

	@Override
	public boolean isNullable() {
		return nullable;
	}

	@Override
	public Range<?> getRange() {
		return range;
	}

	@Override
	public String getDescriptiveName() {
		return Values.isBlank(descriptiveName)
				? name
				: descriptiveName;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getGroup() {
		return group;
	}
}
