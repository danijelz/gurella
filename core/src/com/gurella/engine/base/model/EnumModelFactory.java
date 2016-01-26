package com.gurella.engine.base.model;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.gurella.engine.base.serialization.Input;
import com.gurella.engine.base.serialization.Output;
import com.gurella.engine.utils.ImmutableArray;
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

	//TODO convert to SimpleModel
	public static final class EnumModel<T extends Enum<T>> implements Model<T> {
		private Class<T> type;
		private Class<T> enumType;
		private T[] constants;

		private EnumModel(Class<T> type) {
			this.type = type;
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
			// TODO input.isValid()
			String enumName = input.readString();
			for (int i = 0; i < constants.length; i++) {
				T constant = constants[i];
				if (enumName.equals(constant.name())) {
					return constant;
				}
			}
			throw new GdxRuntimeException("Invalid enum name: " + enumName);
		}

		@Override
		public T copy(T original, CopyContext context) {
			return original;
		}
	}
}
