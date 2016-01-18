package com.gurella.engine.base.model;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.base.model.DefaultModels.SimpleObjectModel;

public class EnumModelResolver implements ModelResolver {
	public static final EnumModelResolver instance = new EnumModelResolver();

	private static final ObjectMap<Class<?>, EnumModel<?>> modelsByType = new ObjectMap<Class<?>, EnumModel<?>>();

	private EnumModelResolver() {
	}

	@Override
	public <T> Model<T> resolve(Class<T> type) {
		synchronized (modelsByType) {
			EnumModel<?> instance = modelsByType.get(type);
			if (instance == null && type.isEnum()) {
				@SuppressWarnings({ "rawtypes", "unchecked" })
				EnumModel<?> raw = new EnumModel(type);
				instance = raw;
				modelsByType.put(type, instance);
			}
			@SuppressWarnings("unchecked")
			Model<T> casted = (Model<T>) instance;
			return casted;
		}
	}

	public static final class EnumModel<T extends Enum<T>> extends SimpleObjectModel<T> {
		private EnumModel(Class<T> type) {
			super(type);
		}

		@Override
		protected Class<?> getSimpleValueType() {
			return String.class;
		}

		@Override
		protected Object extractSimpleValue(T value) {
			return value.name();
		}

		@Override
		protected T deserializeSimpleValue(JsonValue serializedValue) {
			String enumName = serializedValue.asString();
			T[] constants = getType().getEnumConstants();
			for (int i = 0; i < constants.length; i++) {
				T constant = constants[i];
				if (enumName.equals(constant.name())) {
					return constant;
				}
			}
			throw new GdxRuntimeException("Invalid enum name: " + enumName);
		}
	}
}
