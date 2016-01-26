package com.gurella.engine.base.model;

import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.reflect.ArrayReflection;
import com.gurella.engine.base.registry.InitializationContext;
import com.gurella.engine.base.registry.Objects;
import com.gurella.engine.base.serialization.Input;
import com.gurella.engine.base.serialization.JsonSerialization;
import com.gurella.engine.base.serialization.Output;
import com.gurella.engine.base.serialization.json.ArrayType;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.ReflectionUtils;
import com.gurella.engine.utils.ValueUtils;

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
		public T createInstance(InitializationContext context) {
			if (context == null) {
				return null;
			}

			JsonValue serializedValue = context.serializedValue();
			if (serializedValue == null) {
				Object[] template = context.template();
				if (template == null) {
					return null;
				}

				Class<? extends Object> templateType = template.getClass();
				@SuppressWarnings("unchecked")
				T array = (T) ArrayReflection.newInstance(templateType.getComponentType(), template.length);
				return array;
			} else if (serializedValue.isNull()) {
				return null;
			} else {
				int length = serializedValue.size;
				if (length > 0) {
					JsonValue itemValue = serializedValue.child;
					Class<?> itemType = JsonSerialization.resolveObjectType(Object.class, itemValue);
					if (itemType == ArrayType.class) {
						Class<?> arrayType = ReflectionUtils.forName(itemValue.getString(ArrayType.typeNameField));
						@SuppressWarnings("unchecked")
						T array = (T) ArrayReflection.newInstance(arrayType.getComponentType(), length - 1);
						return array;
					}
				}
				@SuppressWarnings("unchecked")
				T array = (T) ArrayReflection.newInstance(type.getComponentType(), length);
				return array;
			}
		}

		@Override
		public void initInstance(InitializationContext context) {
			if (context == null) {
				return;
			}

			Object[] initializingObject = context.initializingObject();
			if (initializingObject == null) {
				return;
			}

			JsonValue serializedValue = context.serializedValue();
			if (serializedValue == null) {
				Object[] template = context.template();
				if (template == null) {
					return;
				}

				for (int i = 0; i < template.length; i++) {
					initializingObject[i] = Objects.copyValue(template[i], context);
				}
			} else {
				Class<?> componentType = initializingObject.getClass().getComponentType();
				JsonValue item = serializedValue.child;
				Class<?> itemType = JsonSerialization.resolveObjectType(Object.class, item);
				if (itemType == ArrayType.class) {
					item = item.next;
				}

				int i = 0;
				for (; item != null; item = item.next) {
					if (item.isNull()) {
						initializingObject[i++] = null;
					} else {
						Class<?> resolvedType = JsonSerialization.resolveObjectType(componentType, item);
						initializingObject[i++] = Objects.deserialize(item, resolvedType, context);
					}
				}
			}
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
			if (ValueUtils.isEqual(template, value)) {
				return;
			} else if (value == null) {
				output.writeNull();
			} else {
				Object[] array = (Object[]) value;
				int length = array.length;

				Object[] templateArray = template != null && type == template.getClass() ? (Object[]) template : null;
				int templateLength = templateArray == null ? 0 : templateArray.length;

				output.writeInt(length);
				for (int i = 0; i < length; i++) {
					Object templateItem = templateLength > 1 ? templateArray[i] : null;
					output.writeObject(componentType, templateItem, array[i]);
				}
			}
		}

		@Override
		public T deserialize(Object template, Input input) {
			if (!input.isValid()) {
				if (template == null) {
					return null;
				} else {
					Object[] templateArray = (Object[]) template;
					int length = templateArray.length;
					Object[] array = (Object[]) ArrayReflection.newInstance(componentType, length);
					input.pushObject(array);
					for (int i = 0; i < length; i++) {
						array[i] = CopyContext.copyObject(templateArray[i]);
					}
					input.popObject();

					@SuppressWarnings("unchecked")
					T value = (T) array;
					return value;
				}
			} else if (input.isNull()) {
				return null;
			} else {
				int length = input.readInt();
				Object[] array = (Object[]) ArrayReflection.newInstance(componentType, length);
				Object[] templateArray = template != null && type == template.getClass() ? (Object[]) template : null;
				int templateLength = templateArray == null ? 0 : templateArray.length;

				input.pushObject(array);
				for (int i = 0; i < length; i++) {
					Object templateItem = templateLength > 1 ? templateArray[i] : null;
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
