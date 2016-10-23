package com.gurella.engine.base.model;

import com.badlogic.gdx.utils.reflect.ArrayReflection;
import com.gurella.engine.base.serialization.Input;
import com.gurella.engine.base.serialization.Output;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.Values;

public class ObjectArrayModelFactory implements ModelFactory {
	public static final ObjectArrayModelFactory instance = new ObjectArrayModelFactory();

	private ObjectArrayModelFactory() {
	}

	@Override
	public <T> Model<T> create(Class<T> type) {
		if (type.isArray()) {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			ObjectArrayModel raw = new ObjectArrayModel(type);
			@SuppressWarnings("unchecked")
			Model<T> casted = raw;
			return casted;
		} else {
			return null;
		}
	}

	public static class ObjectArrayModel<T> implements Model<T> {
		private Class<T> type;
		private Class<?> componentType;

		public ObjectArrayModel(Class<T> type) {
			this.type = type;
			componentType = type.getComponentType();
		}

		@Override
		public Class<T> getType() {
			return type;
		}

		@Override
		public String getName() {
			return type.getComponentType().getName() + "[]";
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
				Object[] array = (Object[]) value;
				int length = array.length;

				Object[] templateArray = template != null && type == template.getClass() ? (Object[]) template : null;
				int templateMax = templateArray == null ? 0 : templateArray.length - 1;

				output.writeInt(length);
				for (int i = 0; i < length; i++) {
					Object templateItem = templateMax > i ? templateArray[i] : null;
					output.writeObject(componentType, templateItem, array[i]);
				}
			}
		}

		@Override
		public T deserialize(Object template, Input input) {
			if (!input.isValuePresent()) {
				if (template == null) {
					return null;
				} else {
					@SuppressWarnings("unchecked")
					T instance = (T) input.copyObject(template);
					return instance;
				}
			} else if (input.isNull()) {
				return null;
			} else {
				int length = input.readInt();
				Object[] array = (Object[]) ArrayReflection.newInstance(componentType, length);
				Object[] templateArray = template != null && type == template.getClass() ? (Object[]) template : null;
				int templateMax = templateArray == null ? 0 : templateArray.length - 1;

				input.pushObject(array);
				for (int i = 0; i < length; i++) {
					Object templateItem = templateMax > i ? templateArray[i] : null;
					array[i] = input.readObject(componentType, templateItem);
				}
				input.popObject();

				@SuppressWarnings("unchecked")
				T value = (T) array;
				return value;
			}
		}

		@Override
		public T copy(T original, CopyContext context) {
			Object[] originalArray = (Object[]) original;
			int length = originalArray.length;
			Object[] duplicate = (Object[]) ArrayReflection.newInstance(componentType, length);
			context.pushObject(duplicate);
			for (int i = 0; i < length; i++) {
				duplicate[i] = context.copy(originalArray[i]);
			}
			context.popObject();
			@SuppressWarnings("unchecked")
			T casted = (T) duplicate;
			return casted;
		}
	}
}
