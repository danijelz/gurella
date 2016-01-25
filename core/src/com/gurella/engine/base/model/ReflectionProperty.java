package com.gurella.engine.base.model;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonValue;
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
import com.gurella.engine.base.serialization.Input;
import com.gurella.engine.base.serialization.Output;
import com.gurella.engine.base.serialization.JsonSerialization;
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
	public Property<T> newInstance(Model<?> newModel) {
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

	private void initFromSerializedValue(InitializationContext context, JsonValue serializedValue) {
		Object initializingObject = context.initializingObject();
		if (serializedValue.isNull()) {
			if (!field.isFinal()) {
				setValue(initializingObject, null);
			}
			return;
		}

		Class<T> resolvedType = type.isPrimitive() ? type : JsonSerialization.resolveObjectType(type, serializedValue);
		Model<T> model = Models.getModel(resolvedType);
		Object template = context.template();
		T templateValue = template == null ? null : getValue(template);
		if (field.isFinal()) {
			T value = getValue(initializingObject);
			if (value == null) {
				return;
			}

			Class<? extends Object> targetType = value.getClass();
			if (!type.isPrimitive() && targetType != resolvedType) {
				throw new GdxRuntimeException("Unequal types.");
			}

			context.push(value, templateValue, serializedValue);
			model.initInstance(context);
			context.pop();
		} else {
			context.push(null, templateValue, serializedValue);
			T value = model.createInstance(context);
			context.setInitializingObject(value);
			model.initInstance(context);
			context.pop();
			setValue(initializingObject, value);
		}
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
			CopyContext context = SynchronizedPools.obtain(CopyContext.class);
			context.copyProperties(value, fieldValue);
			SynchronizedPools.free(context);
		} else {
			ReflectionUtils.setFieldValue(field, object, value);
		}
	}

	@Override
	public void serialize(Object object, Object template, Output output) {
		T value = getValue(object);
		Object templateValue = template == null ? defaultValue : getValue(template);

		if (!ValueUtils.isEqual(value, templateValue)) {
			if (value == null) {
				output.writeNullProperty(name);
			} else {
				output.writeObjectProperty(name, type, templateValue, value);
			}
		}
	}

	@Override
	public void deserialize(Object object, Input input) {
		if (input.hasProperty(name)) {
			setValue(object, input.readObjectProperty(name, type));
		}
	}

	@Override
	public void copy(Object original, Object duplicate, CopyContext context) {
		setValue(duplicate, context.copy(getValue(original)));
	}
}
