package com.gurella.engine.base.model;

import java.util.Arrays;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Constructor;
import com.gurella.engine.base.registry.InitializationContext;
import com.gurella.engine.base.registry.Objects;
import com.gurella.engine.base.serialization.Input;
import com.gurella.engine.base.serialization.Output;
import com.gurella.engine.base.serialization.JsonSerialization;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.Range;
import com.gurella.engine.utils.ReflectionUtils;
import com.gurella.engine.utils.ValueUtils;

public class GdxArrayModelFactory implements ModelFactory {
	public static final GdxArrayModelFactory instance = new GdxArrayModelFactory();

	private GdxArrayModelFactory() {
	}

	@Override
	public <T> Model<T> create(Class<T> type) {
		if (ClassReflection.isAssignableFrom(Array.class, type)) {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			GdxArrayModel raw = new GdxArrayModel(type);
			@SuppressWarnings("unchecked")
			Model<T> casted = raw;
			return casted;
		} else {
			return null;
		}
	}

	public static class GdxArrayModel<T extends Array<?>> implements Model<T> {
		private Class<T> type;
		private ArrayExt<Property<?>> properties;

		public GdxArrayModel(Class<T> type) {
			this.type = type;
			properties = new ArrayExt<Property<?>>();
			properties.add(new ArrayOrderedProperty(this));
			properties.add(new ArrayItemsProperty(this));
		}

		@Override
		public String getName() {
			return type.getName();
		}

		@Override
		public Class<T> getType() {
			return type;
		}

		@Override
		public T createInstance(InitializationContext context) {
			JsonValue serializedValue = context.serializedValue();
			if (serializedValue == null) {
				T template = context.template();
				if (template == null) {
					return null;
				}

				Class<?> componentType = template.items.getClass().getComponentType();
				if (Object.class != componentType) {
					Constructor constructor = ReflectionUtils.getDeclaredConstructorSilently(template.getClass(),
							Class.class);
					if (constructor != null) {
						return ReflectionUtils.invokeConstructor(constructor, componentType);
					}
				}

				@SuppressWarnings("unchecked")
				T instance = (T) ReflectionUtils.newInstance(template.getClass());
				return instance;
			} else {
				if (serializedValue.isNull()) {
					return null;
				}

				Class<T> resolvedType = JsonSerialization.resolveObjectType(type, serializedValue);
				JsonValue componentTypeValue = serializedValue.get("componentType");
				if (componentTypeValue != null) {
					Class<?> componentType = ReflectionUtils.forNameSilently(componentTypeValue.asString());
					Constructor constructor = ReflectionUtils.getDeclaredConstructorSilently(resolvedType, Class.class);
					if (constructor != null) {
						return ReflectionUtils.invokeConstructor(constructor, componentType);
					}
				}

				return ReflectionUtils.newInstance(resolvedType);
			}
		}

		@Override
		public void initInstance(InitializationContext context) {
			Array<?> initializingObject = context.initializingObject();
			if (initializingObject != null) {
				properties.get(0).init(context);
				properties.get(1).init(context);
			}
		}

		@Override
		public ImmutableArray<Property<?>> getProperties() {
			return properties.immutable();
		}

		@Override
		@SuppressWarnings("unchecked")
		public <P> Property<P> getProperty(String name) {
			if (ArrayOrderedProperty.name.equals(name)) {
				return (Property<P>) properties.get(0);
			} else if (ArrayItemsProperty.name.equals(name)) {
				return (Property<P>) properties.get(1);
			} else {
				return null;
			}
		}

		@Override
		public void serialize(T value, Object template, Output output) {
			if (ValueUtils.isEqual(template, value)) {
				return;
			} else if (value == null) {
				output.writeNull();
			} else {
				Class<?> componentType = value.items.getClass().getComponentType();
				if (Object.class != componentType) {
					output.writeStringProperty("componentType", componentType.getName());
				}

				T templateArray = resolveTemplate(value, template);
				properties.get(0).serialize(value, templateArray, output);
				properties.get(1).serialize(value, templateArray, output);
			}
		}

		private T resolveTemplate(T value, Object template) {
			if (template == null || template.getClass() != value.getClass()) {
				return null;
			}

			@SuppressWarnings("unchecked")
			T templateArray = (T) template;
			return value.ordered == templateArray.ordered && value.items.getClass() == templateArray.items.getClass()
					? templateArray : null;
		}

		@Override
		public T deserialize(Object template, Input input) {
			T array = createArray(input);
			input.pushObject(array);
			properties.get(0).deserialize(array, dddd, input);
			properties.get(1).deserialize(array, dddd, input);
			input.popObject();
			return array;
		}

		private T createArray(Input input) {
			if (input.hasProperty("componentType")) {
				Class<?> componentType = ReflectionUtils.forNameSilently(input.readStringProperty("componentType"));
				Constructor constructor = ReflectionUtils.getDeclaredConstructorSilently(type, Class.class);
				if (constructor != null) {
					return ReflectionUtils.invokeConstructor(constructor, componentType);
				}
			}

			return ReflectionUtils.newInstance(type);
		}

		@Override
		public T copy(T original, CopyContext context) {
			Class<?> componentType = original.items.getClass().getComponentType();
			Constructor constructor = ReflectionUtils.getDeclaredConstructorSilently(type, Class.class);
			T array;
			if (constructor != null) {
				array = ReflectionUtils.invokeConstructor(constructor, componentType);
			} else {
				array = ReflectionUtils.newInstance(type);
			}

			context.pushObject(array);
			properties.get(0).copy(original, array, context);
			properties.get(1).copy(original, array, context);
			context.popObject();
			return array;
		}
	}

	private static class ArrayOrderedProperty implements Property<Boolean> {
		private static final String name = "ordered";

		private Model<?> model;

		public ArrayOrderedProperty(Model<?> model) {
			this.model = model;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public Class<Boolean> getType() {
			return boolean.class;
		}

		@Override
		public Model<?> getModel() {
			return model;
		}

		@Override
		public Range<?> getRange() {
			return null;
		}

		@Override
		public boolean isNullable() {
			return false;
		}

		@Override
		public String getDescriptiveName() {
			return name;
		}

		@Override
		public String getDescription() {
			return name;
		}

		@Override
		public String getGroup() {
			return null;
		}

		@Override
		public Property<Boolean> newInstance(Model<?> newModel) {
			return new ArrayOrderedProperty(newModel);
		}

		@Override
		public void init(InitializationContext context) {
			Array<?> array = (Array<?>) context.initializingObject();
			if (array == null) {
				return;
			}

			JsonValue serializedObject = context.serializedValue();
			JsonValue serializedValue = serializedObject == null ? null : serializedObject.get(name);
			if (serializedValue == null) {
				Array<?> template = (Array<?>) context.template();
				if (template == null) {
					return;
				}
				array.ordered = template.ordered;
			} else {
				array.ordered = serializedValue.asBoolean();
			}
		}

		@Override
		public Boolean getValue(Object object) {
			return Boolean.valueOf(((Array<?>) object).ordered);
		}

		@Override
		public void setValue(Object object, Boolean value) {
			((Array<?>) object).ordered = Boolean.TRUE.equals(value);
		}

		@Override
		public void serialize(Object object, Object template, Output output) {
			Array<?> array = (Array<?>) object;
			Array<?> templateArray = (Array<?>) template;
			if (templateArray == null ? !array.ordered : array.ordered != templateArray.ordered) {
				output.writeBooleanProperty(name, array.ordered);
			}
		}

		@Override
		public void deserialize(Object object, Object template, Input input) {
			if (input.hasProperty(name)) {
				((Array<?>) object).ordered = input.readBooleanProperty(name);
			}
		}

		@Override
		public void copy(Object original, Object duplicate, CopyContext context) {
			((Array<?>) duplicate).ordered = ((Array<?>) original).ordered;
		}
	}

	private static class ArrayItemsProperty implements Property<Object[]> {
		private static final String name = "items";

		private Model<?> model;

		public ArrayItemsProperty(Model<?> model) {
			this.model = model;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public Class<Object[]> getType() {
			return Object[].class;
		}

		@Override
		public Model<?> getModel() {
			return model;
		}

		@Override
		public Property<Object[]> newInstance(Model<?> model) {
			return new ArrayItemsProperty(model);
		}

		@Override
		public Range<?> getRange() {
			return null;
		}

		@Override
		public boolean isNullable() {
			return false;
		}

		@Override
		public String getDescriptiveName() {
			return name;
		}

		@Override
		public String getDescription() {
			return null;
		}

		@Override
		public String getGroup() {
			return null;
		}

		@Override
		public void init(InitializationContext context) {
			@SuppressWarnings("unchecked")
			Array<Object> array = (Array<Object>) context.initializingObject();
			if (array == null) {
				return;
			}

			JsonValue serializedObject = context.serializedValue();
			JsonValue serializedValue = serializedObject == null ? null : serializedObject.get(name);
			if (serializedValue == null) {
				@SuppressWarnings("unchecked")
				Array<Object> template = (Array<Object>) context.template();
				if (template == null) {
					return;
				}

				array.ensureCapacity(template.size - array.items.length);
				for (int i = 0; i < template.size; i++) {
					array.add(Objects.copyValue(template.get(i), context));
				}
			} else {
				Class<?> componentType = array.items.getClass().getComponentType();
				array.ensureCapacity(serializedValue.size - array.items.length);

				for (JsonValue item = serializedValue.child; item != null; item = item.next) {
					if (item.isNull()) {
						array.add(null);
					} else {
						Class<?> resolvedType = JsonSerialization.resolveObjectType(componentType, item);
						array.add(Objects.deserialize(item, resolvedType, context));
					}
				}
			}
		}

		@Override
		public Object[] getValue(Object object) {
			Array<?> array = (Array<?>) object;
			return Arrays.copyOf(array.items, array.size);
		}

		@Override
		public void setValue(Object object, Object[] value) {
			@SuppressWarnings("unchecked")
			Array<Object> array = (Array<Object>) object;
			array.clear();
			array.addAll(value);
		}

		@Override
		public void serialize(Object object, Object template, Output output) {
			Array<?> array = (Array<?>) object;
			Array<?> templateArray = (Array<?>) template;
			if ((templateArray == null && array.size == 0) || array.equals(templateArray)) {
				return;
			}

			Object templateItems = templateArray == null ? null
					: Arrays.copyOf(templateArray.items, templateArray.size);
			Object[] items = Arrays.copyOf(array.items, array.size);
			output.writeObjectProperty(name, array.items.getClass(), templateItems, items);
		}

		@Override
		public void deserialize(Object object, Object template, Input input) {
			if (input.hasProperty(name)) {
				@SuppressWarnings("unchecked")
				Array<Object> array = (Array<Object>) object;

				Object[] property = input.readObjectProperty(name, array.items.getClass());
				array.ensureCapacity(property.length - array.size);
				array.addAll(property);
			}
		}

		@Override
		public void copy(Object original, Object duplicate, CopyContext context) {
			Array<?> originalArray = (Array<?>) original;
			@SuppressWarnings("unchecked")
			Array<Object> duplicateArray = (Array<Object>) duplicate;
			int size = originalArray.size;
			duplicateArray.ensureCapacity(size - duplicateArray.size);
			for (int i = 0; i < originalArray.size; i++) {
				duplicateArray.add(context.copy(originalArray.get(i)));
			}
		}
	}
}
