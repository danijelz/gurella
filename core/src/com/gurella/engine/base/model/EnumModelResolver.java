package com.gurella.engine.base.model;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.gurella.engine.base.registry.InitializationContext;
import com.gurella.engine.base.serialization.Archive;
import com.gurella.engine.utils.ImmutableArray;

public class EnumModelResolver implements ModelResolver {
	public static final EnumModelResolver instance = new EnumModelResolver();

	private EnumModelResolver() {
	}

	@Override
	public <T> Model<T> resolve(Class<T> type) {
		if (ClassReflection.isAssignableFrom(Enum.class, type)) {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			EnumModel raw = new EnumModel(type);
			@SuppressWarnings("unchecked")
			Model<T> casted = raw;
			return casted;
		} else {
			return null;
		}
	}

	public static final class EnumModel<T extends Enum<T>> implements Model<T> {
		private Class<T> type;

		private EnumModel(Class<T> type) {
			this.type = type;
		}

		@Override
		public Class<T> getType() {
			return type;
		}

		@Override
		public String getName() {
			return type.getName();
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
		public T createInstance(InitializationContext context) {
			if (context == null) {
				return null;
			}

			JsonValue serializedValue = context.serializedValue();
			if (serializedValue == null) {
				return context.template();
			} else if (serializedValue.isNull()) {
				return null;
			} else {
				String enumName = getEnumName(serializedValue);
				T[] constants = type.getEnumConstants();
				if (constants == null) {
					@SuppressWarnings("unchecked")
					T[] casted = (T[]) type.getSuperclass().getEnumConstants();
					constants = casted;
				}
				for (int i = 0; i < constants.length; i++) {
					T constant = constants[i];
					if (enumName.equals(constant.name())) {
						return constant;
					}
				}
				throw new GdxRuntimeException("Invalid enum name: " + enumName);
			}
		}

		@Override
		public void initInstance(InitializationContext context) {
		}

		private static String getEnumName(JsonValue serializedValue) {
			if (serializedValue.isObject()) {
				return serializedValue.getString("value");
			} else {
				return serializedValue.asString();
			}
		}

		@Override
		public void serialize(T value, Class<?> knownType, Archive archive) {
			if (value == null) {
				archive.writeValue(null, null);
			} else {
				if (knownType == value.getClass()) {
					archive.writeValue(value.name(), String.class);
				} else {
					@SuppressWarnings("unchecked")
					Class<Enum<?>> enumType = (Class<Enum<?>>) value.getClass();
					if (enumType.getEnumConstants() == null) {
						@SuppressWarnings({ "unchecked" })
						Class<Enum<?>> casted = (Class<Enum<?>>) enumType.getSuperclass();
						enumType = casted;
					}

					if (knownType == enumType) {
						archive.writeValue(value.name(), String.class);
					} else {
						archive.writeObjectStart(enumType);
						archive.writeValue("value", value.name(), String.class);
						archive.writeObjectEnd();
					}
				}
			}
		}
	}
}
