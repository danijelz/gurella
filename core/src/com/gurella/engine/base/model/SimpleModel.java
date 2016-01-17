package com.gurella.engine.base.model;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.badlogic.gdx.utils.JsonValue;
import com.gurella.engine.base.registry.InitializationContext;
import com.gurella.engine.base.serialization.Archive;
import com.gurella.engine.utils.ImmutableArray;

public abstract class SimpleModel<T> implements Model<T> {
	private Class<T> type;

	public SimpleModel(Class<T> type) {
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

	public static final class IntegerPrimitiveModel extends SimpleModel<Integer> {
		public IntegerPrimitiveModel() {
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

	public static final class LongPrimitiveModel extends SimpleModel<Long> {
		public LongPrimitiveModel() {
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

	public static final class ShortPrimitiveModel extends SimpleModel<Short> {
		public ShortPrimitiveModel() {
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

	public static final class BytePrimitiveModel extends SimpleModel<Byte> {
		public BytePrimitiveModel() {
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

	public static final class CharPrimitiveModel extends SimpleModel<Character> {
		public CharPrimitiveModel() {
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

	public static final class BooleanPrimitiveModel extends SimpleModel<Boolean> {
		public BooleanPrimitiveModel() {
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

	public static final class DoublePrimitiveModel extends SimpleModel<Double> {
		public DoublePrimitiveModel() {
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

	public static final class FloatPrimitiveModel extends SimpleModel<Float> {
		public FloatPrimitiveModel() {
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

	public static abstract class SimpleObjectModel<T> extends SimpleModel<T> {
		public SimpleObjectModel(Class<T> type) {
			super(type);
		}

		@Override
		protected T createDefaultValue() {
			return null;
		}
	}

	public static final class IntegerModel extends SimpleObjectModel<Integer> {
		public IntegerModel() {
			super(Integer.class);
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

	public static final class LongModel extends SimpleObjectModel<Long> {
		public LongModel() {
			super(Long.class);
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

	public static final class ShortModel extends SimpleObjectModel<Short> {
		public ShortModel() {
			super(Short.class);
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

	public static final class ByteModel extends SimpleObjectModel<Byte> {
		public ByteModel() {
			super(Byte.class);
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

	public static final class CharModel extends SimpleObjectModel<Character> {
		public CharModel() {
			super(Character.class);
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

	public static final class BooleanModel extends SimpleObjectModel<Boolean> {
		public BooleanModel() {
			super(Boolean.class);
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

	public static final class DoubleModel extends SimpleObjectModel<Double> {
		public DoubleModel() {
			super(Double.class);
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

	public static final class FloatModel extends SimpleObjectModel<Float> {
		public FloatModel() {
			super(Float.class);
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

	public static final class StringModel extends SimpleObjectModel<String> {
		public StringModel() {
			super(String.class);
		}

		@Override
		protected String deserializeValue(JsonValue serializedValue) {
			return serializedValue.asString();
		}
	}

	public static final class BigIntegerModel extends SimpleObjectModel<BigInteger> {
		public BigIntegerModel() {
			super(BigInteger.class);
		}

		@Override
		protected BigInteger deserializeValue(JsonValue serializedValue) {
			return new BigInteger(serializedValue.get("value").asString());
		}
	}

	public static final class BigDecimalModel extends SimpleObjectModel<BigDecimal> {
		public BigDecimalModel() {
			super(BigDecimal.class);
		}

		@Override
		protected BigDecimal deserializeValue(JsonValue serializedValue) {
			return new BigDecimal(serializedValue.get("value").asString());
		}
	}
}
