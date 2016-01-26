package com.gurella.engine.base.model;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.gurella.engine.base.model.DefaultModels.SimpleModel;
import com.gurella.engine.base.serialization.Input;
import com.gurella.engine.base.serialization.Output;
import com.gurella.engine.utils.ValueUtils;

public class EnumModelFactory implements ModelFactory {
	public static final EnumModelFactory instance = new EnumModelFactory();

	private EnumModelFactory() {
	}

	@Override
	public <T> Model<T> create(Class<T> type) {
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

	public static final class EnumModel<T extends Enum<T>> extends SimpleModel<T> {
		private Class<T> enumType;
		private T[] constants;

		private EnumModel(Class<T> type) {
			super(type);
			constants = type.getEnumConstants();
			enumType = type;
			if (constants == null) {
				@SuppressWarnings("unchecked")
				Class<T> casted = (Class<T>) type.getSuperclass();
				enumType = casted;
				constants = enumType.getEnumConstants();
			}
		}

		@Override
		public void serialize(T value, Object template, Output output) {
			if (ValueUtils.isEqual(template, value)) {
				return;
			} else if (value == null) {
				output.writeNull();
			} else {
				output.writeString(value.name());
			}
		}

		@Override
		public T deserialize(Object template, Input input) {
			if (!input.isValid()) {
				@SuppressWarnings("unchecked")
				T instance = (T) template;
				return instance;
			} else if (input.isNull()) {
				return null;
			} else {
				String enumName = input.readString();
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
}
