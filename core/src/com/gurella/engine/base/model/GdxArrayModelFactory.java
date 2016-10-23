package com.gurella.engine.base.model;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Constructor;
import com.gurella.engine.base.serialization.Input;
import com.gurella.engine.base.serialization.Output;
import com.gurella.engine.utils.ImmutableArray;
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
		private static final String componentTypePropertyName = "componentType";
		private static final String orderedPropertyName = "ordered";
		private static final String sizePropertyName = "size";
		private static final String itemsPropertyName = "items";

		private final Class<T> type;

		public GdxArrayModel(Class<T> type) {
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
		@SuppressWarnings("unchecked")
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
				output.writeObjectProperty(itemsPropertyName, value.items.getClass(), templateItems, value.items);
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

				if (input.hasProperty(itemsPropertyName)) {
					Object[] items = (Object[]) input.readObjectProperty(itemsPropertyName, componentType,
							templateArray.items);
					((Array<Object>) array).items = items;
				} else if (templateArray != null) {
					int size = templateArray.size;
					for (int i = 0; i < size; i++) {
						((Array<Object>) array).add(input.copyObject(templateArray.get(i)));
					}
				}

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
			array.ordered = original.ordered;
			array.size = original.size;
			Array<Object> casted = (Array<Object>) array;
			casted.items = (Object[]) context.copy(original.items);
			context.popObject();
			return array;
		}
	}
}
