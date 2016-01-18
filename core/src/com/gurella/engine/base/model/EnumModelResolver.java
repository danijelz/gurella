package com.gurella.engine.base.model;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonValue;
import com.gurella.engine.base.model.DefaultModels.SimpleObjectModel;

public class EnumModelResolver implements ModelResolver {
	public static final EnumModelResolver instance = new EnumModelResolver();

	private EnumModelResolver() {
	}

	@Override
	public <T> Model<T> resolve(Class<T> type) {
		if (type.isEnum()) {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			EnumModel raw = new EnumModel(type);
			@SuppressWarnings("unchecked")
			Model<T> casted = raw;
			return casted;
		} else {
			return null;
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
