package com.gurella.engine.metatype;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Constructor;
import com.gurella.engine.serialization.Input;
import com.gurella.engine.serialization.Output;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.Reflection;
import com.gurella.engine.utils.Values;

public class GdxArrayMetaTypeFactory implements MetaTypeFactory {
	public static final GdxArrayMetaTypeFactory instance = new GdxArrayMetaTypeFactory();

	private GdxArrayMetaTypeFactory() {
	}

	@Override
	public <T> MetaType<T> create(Class<T> type) {
		if (ClassReflection.isAssignableFrom(Array.class, type)) {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			GdxArrayMetaType raw = new GdxArrayMetaType(type);
			@SuppressWarnings("unchecked")
			MetaType<T> casted = raw;
			return casted;
		} else {
			return null;
		}
	}

	public static class GdxArrayMetaType<T extends Array<?>> implements MetaType<T> {
		private static final String componentTypePropertyName = "componentType";
		private static final String orderedPropertyName = "ordered";
		private static final String sizePropertyName = "size";
		private static final String itemsPropertyName = "items";

		private final Class<T> type;

		public GdxArrayMetaType(Class<T> type) {
			this.type = type;
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
			return ImmutableArray.empty();
		}

		@Override
		public <P> Property<P> getProperty(String name) {
			return null;
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
					output.writeStringProperty(componentTypePropertyName, componentType.getName());
				}

				@SuppressWarnings("unchecked")
				T templateArray = template != null && template.getClass() == type ? (T) template : null;

				if (templateArray == null ? !value.ordered : value.ordered != templateArray.ordered) {
					output.writeBooleanProperty(orderedPropertyName, value.ordered);
				}

				if ((templateArray == null && value.size == 0) || value.equals(templateArray)) {
					return;
				}

				Object[] templateItems = templateArray == null ? null : templateArray.items;
				output.writeIntProperty(sizePropertyName, value.size);
				output.writeObjectProperty(itemsPropertyName, value.items.getClass(), value.items, templateItems);
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
				return deserializeValue(template, input);
			}
		}

		private T deserializeValue(Object template, Input input) {
			@SuppressWarnings("unchecked")
			T templateArray = template != null && template.getClass() == type ? (T) template : null;

			Class<?> componentType;
			if (input.hasProperty(componentTypePropertyName)) {
				componentType = Reflection.forNameSilently(input.readStringProperty(componentTypePropertyName));
			} else if (templateArray != null) {
				componentType = templateArray.items.getClass();
			} else {
				componentType = Object.class;
			}

			T array = createArray(componentType);
			input.pushObject(array);

			if (input.hasProperty(orderedPropertyName)) {
				array.ordered = input.readBooleanProperty(orderedPropertyName);
			} else if (templateArray != null) {
				array.ordered = templateArray.ordered;
			}

			if (input.hasProperty(sizePropertyName)) {
				int size = input.readIntProperty(sizePropertyName);
				array.ensureCapacity(size);
				array.size = size;
			} else if (templateArray != null) {
				array.ensureCapacity(templateArray.size);
				array.size = templateArray.size;
			}

			@SuppressWarnings("unchecked")
			Array<Object> casted = (Array<Object>) array;
			if (input.hasProperty(itemsPropertyName)) {
				Object[] templateItems = templateArray.items;
				Object[] items = (Object[]) input.readObjectProperty(itemsPropertyName, componentType, templateItems);
				casted.items = items;
			} else if (templateArray != null) {
				int size = templateArray.size;
				for (int i = 0; i < size; i++) {
					casted.add(input.copyObject(templateArray.get(i)));
				}
			}

			input.popObject();
			return array;
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
			array.ordered = original.ordered;
			array.size = original.size;
			@SuppressWarnings("unchecked")
			Array<Object> casted = (Array<Object>) array;
			@SuppressWarnings("cast")
			Object[] copy = (Object[]) context.copy(original.items);
			casted.items = copy;
			context.popObject();
			return array;
		}
	}
}
