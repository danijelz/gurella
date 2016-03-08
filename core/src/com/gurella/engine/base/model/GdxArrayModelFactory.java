package com.gurella.engine.base.model;

import java.util.Arrays;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Constructor;
import com.gurella.engine.base.serialization.Input;
import com.gurella.engine.base.serialization.Output;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.Range;
import com.gurella.engine.utils.Reflection;
import com.gurella.engine.utils.Values;

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
			properties.add(new ArrayOrderedProperty());
			properties.add(new ArrayItemsProperty());
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
			if (Values.isEqual(template, value)) {
				return;
			} else if (value == null) {
				output.writeNull();
			} else {
				Class<?> componentType = value.items.getClass().getComponentType();
				if (Object.class != componentType) {
					output.writeStringProperty("componentType", componentType.getName());
				}

				@SuppressWarnings("unchecked")
				T templateArray = template != null && template.getClass() == type ? (T) template : null;
				properties.get(0).serialize(value, templateArray, output);
				properties.get(1).serialize(value, templateArray, output);
			}
		}

		@Override
		public T deserialize(Object template, Input input) {
			if (!input.isValuePresent()) {
				if (template == null) {
					return null;
				} else {
					@SuppressWarnings("unchecked")
					T array = (T) input.copyObject(template);
					return array;
				}
			} else if (input.isNull()) {
				return null;
			} else {
				@SuppressWarnings("unchecked")
				T templateArray = template != null && template.getClass() == type ? (T) template : null;

				Class<?> componentType;
				if (input.hasProperty("componentType")) {
					componentType = Reflection.forNameSilently(input.readStringProperty("componentType"));
				} else if (templateArray != null) {
					componentType = templateArray.items.getClass();
				} else {
					componentType = Object.class;
				}

				T array = createArray(componentType);
				input.pushObject(array);
				properties.get(0).deserialize(array, templateArray, input);
				properties.get(1).deserialize(array, templateArray, input);
				input.popObject();
				return array;
			}
		}

		private T createArray(Class<?> componentType) {
			Constructor constructor = Reflection.getDeclaredConstructorSilently(type, Class.class);
			if (constructor != null) {
				return Reflection.invokeConstructor(constructor, componentType);
			} else {
				return Reflection.newInstance(type);
			}
		}

		@Override
		public T copy(T original, CopyContext context) {
			Class<?> componentType = original.items.getClass().getComponentType();
			T array = createArray(componentType);
			context.pushObject(array);
			properties.get(0).copy(original, array, context);
			properties.get(1).copy(original, array, context);
			context.popObject();
			return array;
		}
	}

	private static class ArrayOrderedProperty implements Property<Boolean> {
		private static final String name = "ordered";

		@Override
		public String getName() {
			return name;
		}

		@Override
		public Class<Boolean> getType() {
			return boolean.class;
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
		public boolean isCopyable() {
			return true;
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
			return new ArrayOrderedProperty();
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
			} else if (template != null) {
				((Array<?>) object).ordered = ((Array<?>) template).ordered;
			}
		}

		@Override
		public void copy(Object original, Object duplicate, CopyContext context) {
			((Array<?>) duplicate).ordered = ((Array<?>) original).ordered;
		}
	}

	private static class ArrayItemsProperty implements Property<Object[]> {
		private static final String name = "items";

		@Override
		public String getName() {
			return name;
		}

		@Override
		public Class<Object[]> getType() {
			return Object[].class;
		}

		@Override
		public Property<Object[]> newInstance(Model<?> model) {
			return new ArrayItemsProperty();
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
		public boolean isCopyable() {
			return true;
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
				Object templateValue = template == null ? null : getValue(template);
				Object[] value = input.readObjectProperty(name, array.items.getClass(), templateValue);
				array.ensureCapacity(value.length - array.size);
				array.addAll(value);
			} else if (template != null) {
				@SuppressWarnings("unchecked")
				Array<Object> array = (Array<Object>) object;
				Object[] value = getValue(template);
				int length = value.length;
				array.ensureCapacity(length - array.size);
				for (int i = 0; i < length; i++) {
					array.add(input.copyObject(value[i]));
				}
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
