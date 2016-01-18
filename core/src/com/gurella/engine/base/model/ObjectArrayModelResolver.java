package com.gurella.engine.base.model;

import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.ArrayReflection;
import com.gurella.engine.base.registry.InitializationContext;
import com.gurella.engine.base.registry.Objects;
import com.gurella.engine.base.serialization.Archive;
import com.gurella.engine.base.serialization.ArrayType;
import com.gurella.engine.base.serialization.Serialization;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.ReflectionUtils;

public class ObjectArrayModelResolver implements ModelResolver {
	public static final ObjectArrayModelResolver instance = new ObjectArrayModelResolver();
	private static final ObjectMap<Class<?>, ObjectArrayModel<?>> modelsByType = new ObjectMap<Class<?>, ObjectArrayModel<?>>();

	private ObjectArrayModelResolver() {
	}

	@Override
	public <T> Model<T> resolve(Class<T> type) {
		if (type.isArray()) {
			synchronized (modelsByType) {
				ObjectArrayModel<?> instance = modelsByType.get(type);
				if (instance == null) {
					@SuppressWarnings({ "rawtypes", "unchecked" })
					ObjectArrayModel<?> raw = new ObjectArrayModel(type);
					instance = raw;
					modelsByType.put(type, instance);
				}
				@SuppressWarnings("unchecked")
				Model<T> casted = (Model<T>) instance;
				return casted;
			}
		} else {
			return null;
		}
	}

	public static class ObjectArrayModel<T> implements Model<T> {
		private Class<T> type;

		public ObjectArrayModel(Class<T> type) {
			this.type = type;
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
					Class<?> itemType = Serialization.resolveObjectType(Object.class, itemValue);
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
				Class<?> itemType = Serialization.resolveObjectType(Object.class, item);
				if (itemType == ArrayType.class) {
					item = item.next;
				}

				int i = 0;
				for (; item != null; item = item.next) {
					if (item.isNull()) {
						initializingObject[i++] = null;
					} else {
						Class<?> resolvedType = Serialization.resolveObjectType(componentType, item);
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
		public void serialize(T value, Class<?> knownType, Archive archive) {
			if (value == null) {
				archive.writeValue(null, null);
			} else {
				Object[] array = (Object[]) value;
				Class<? extends Object> actualType = value.getClass();
				archive.writeArrayStart();

				if (actualType != knownType) {
					ArrayType arrayType = new ArrayType();
					arrayType.typeName = actualType.getName();
					archive.writeValue(arrayType, null);
				}

				Class<?> componentType = actualType.getComponentType();
				for (int i = 0; i < array.length; i++) {
					archive.writeValue(array[i], componentType);
				}
				archive.writeArrayEnd();
			}
		}
	}
}
