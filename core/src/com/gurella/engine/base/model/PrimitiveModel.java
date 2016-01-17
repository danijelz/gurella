package com.gurella.engine.base.model;

import com.badlogic.gdx.utils.JsonValue;
import com.gurella.engine.base.registry.InitializationContext;
import com.gurella.engine.base.serialization.Archive;
import com.gurella.engine.utils.ImmutableArray;

public abstract class PrimitiveModel<T> implements Model<T> {
	private Class<T> type;

	public PrimitiveModel(Class<T> type) {
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
	public T createInstance(InitializationContext context) {
		if (context == null) {
			return createDefaultValue();
		}

		JsonValue serializedValue = context.serializedValue();
		if (serializedValue == null) {
			T template = context.template();
			return template == null ? createDefaultValue() : template;
		} else {
			if (serializedValue.isNull()) {
				return createDefaultValue();
			} else {
				return deserializeValue(serializedValue);
			}
		}
	}

	protected abstract T createDefaultValue();

	protected abstract T deserializeValue(JsonValue serializedValue);

	@Override
	public void initInstance(InitializationContext context) {
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
		archive.writeValue(value, knownType);
	}

	/*
	 * Integer.class == type || Long.class == type || Short.class == type ||
	 * Byte.class == type || Character.class == type || Boolean.class == type ||
	 * Double.class == type || Float.class == type || String.class == type
	 */

	public static final class IntegerModel extends PrimitiveModel<Integer> {
		public IntegerModel() {
			super(int.class);
		}

		@Override
		protected Integer createDefaultValue() {
			return Integer.valueOf(0);
		}

		@Override
		protected Integer deserializeValue(JsonValue serializedValue) {
			try {
				Integer.valueOf(serializedValue.asInt());
			} catch (NumberFormatException ignored) {
			}

			return Integer.valueOf(serializedValue.asString());
		}
	}

	public static final class LongModel extends PrimitiveModel<Long> {
		public LongModel() {
			super(long.class);
		}

		@Override
		protected Long createDefaultValue() {
			return Long.valueOf(0);
		}

		@Override
		protected Long deserializeValue(JsonValue serializedValue) {
			try {
				Long.valueOf(serializedValue.asLong());
			} catch (NumberFormatException ignored) {
			}

			return Long.valueOf(serializedValue.asString());
		}
	}

	public static final class ShortModel extends PrimitiveModel<Short> {
		public ShortModel() {
			super(short.class);
		}

		@Override
		protected Short createDefaultValue() {
			return Short.valueOf((short) 0);
		}

		@Override
		protected Short deserializeValue(JsonValue serializedValue) {
			try {
				Short.valueOf(serializedValue.asShort());
			} catch (NumberFormatException ignored) {
			}

			return Short.valueOf(serializedValue.asString());
		}
	}

	public static final class ByteModel extends PrimitiveModel<Byte> {
		public ByteModel() {
			super(byte.class);
		}

		@Override
		protected Byte createDefaultValue() {
			return Byte.valueOf((byte) 0);
		}

		@Override
		protected Byte deserializeValue(JsonValue serializedValue) {
			try {
				Byte.valueOf(serializedValue.asByte());
			} catch (NumberFormatException ignored) {
			}

			return Byte.valueOf(serializedValue.asString());
		}
	}

	public static final class CharModel extends PrimitiveModel<Character> {
		public CharModel() {
			super(char.class);
		}

		@Override
		protected Character createDefaultValue() {
			return Character.valueOf((char) 0);
		}

		@Override
		protected Character deserializeValue(JsonValue serializedValue) {
			try {
				Character.valueOf(serializedValue.asChar());
			} catch (NumberFormatException ignored) {
			}

			return Character.valueOf(serializedValue.asString().charAt(0));
		}
	}

	public static final class BooleanModel extends PrimitiveModel<Boolean> {
		public BooleanModel() {
			super(boolean.class);
		}

		@Override
		protected Boolean createDefaultValue() {
			return Boolean.FALSE;
		}

		@Override
		protected Boolean deserializeValue(JsonValue serializedValue) {
			try {
				Boolean.valueOf(serializedValue.asBoolean());
			} catch (NumberFormatException ignored) {
			}

			return Boolean.valueOf(serializedValue.asString());
		}
	}

	public static final class DoubleModel extends PrimitiveModel<Double> {
		public DoubleModel() {
			super(double.class);
		}

		@Override
		protected Double createDefaultValue() {
			return Double.valueOf(0);
		}

		@Override
		protected Double deserializeValue(JsonValue serializedValue) {
			try {
				Double.valueOf(serializedValue.asDouble());
			} catch (NumberFormatException ignored) {
			}

			return Double.valueOf(serializedValue.asString());
		}
	}

	public static final class FloatModel extends PrimitiveModel<Float> {
		public FloatModel() {
			super(float.class);
		}

		@Override
		protected Float createDefaultValue() {
			return Float.valueOf(0);
		}

		@Override
		protected Float deserializeValue(JsonValue serializedValue) {
			try {
				Float.valueOf(serializedValue.asFloat());
			} catch (NumberFormatException ignored) {
			}

			return Float.valueOf(serializedValue.asString());
		}
	}
}
