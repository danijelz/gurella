package com.gurella.engine.base.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import com.gurella.engine.base.serialization.Input;
import com.gurella.engine.base.serialization.Output;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.ReflectionUtils;
import com.gurella.engine.utils.ValueUtils;

public class DefaultModels {
	private DefaultModels() {
	}

	public static abstract class SimpleModel<T> implements Model<T> {
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
		public ImmutableArray<Property<?>> getProperties() {
			return ImmutableArray.empty();
		}

		@Override
		public <P> Property<P> getProperty(String name) {
			return null;
		}

		@Override
		public T copy(T original, CopyContext context) {
			return original;
		}

		// TODO
		// @Override
		// public T deserialize(Object template, Input input) {
		// if (!input.isValid()) {
		// if (template == null) {
		// return null;
		// } else {
		// @SuppressWarnings("unchecked")
		// T instance = (T) CopyContext.copyObject(template);
		// return instance;
		// }
		// } if (input.isNull()) {
		// return null;
		// } else {
		// return readValues(input);
		// }
		// }
		//
		// protected abstract T readValues(Input input);
	}

	public static abstract class PrimitiveModel<T> extends SimpleModel<T> {
		public PrimitiveModel(Class<T> type) {
			super(type);
		}

		@Override
		public T deserialize(Object template, Input input) {
			if (!input.isValid()) {
				if (template == null) {
					return null;
				} else {
					@SuppressWarnings("unchecked")
					T instance = (T) CopyContext.copyObject(template);
					return instance;
				}
			} else {
				return readValues(input);
			}
			// TODO
			// else if (input.isNull()) {
			// return null;
			// }
		}

		protected abstract T readValues(Input input);
	}

	public static final class IntegerPrimitiveModel extends PrimitiveModel<Integer> {
		public static final IntegerPrimitiveModel instance = new IntegerPrimitiveModel();

		private IntegerPrimitiveModel() {
			super(int.class);
		}

		@Override
		public void serialize(Integer value, Object template, Output output) {
			output.writeInt(value);
		}

		@Override
		public Integer readValues(Input input) {
			return Integer.valueOf(input.readInt());
		}
	}

	public static final class LongPrimitiveModel extends PrimitiveModel<Long> {
		public static final LongPrimitiveModel instance = new LongPrimitiveModel();

		private LongPrimitiveModel() {
			super(long.class);
		}

		@Override
		public void serialize(Long value, Object template, Output output) {
			output.writeLong(value);
		}

		@Override
		public Long readValues(Input input) {
			return Long.valueOf(input.readLong());
		}
	}

	public static final class ShortPrimitiveModel extends PrimitiveModel<Short> {
		public static final ShortPrimitiveModel instance = new ShortPrimitiveModel();

		private ShortPrimitiveModel() {
			super(short.class);
		}

		@Override
		public void serialize(Short value, Object template, Output output) {
			output.writeShort(value);
		}

		@Override
		public Short readValues(Input input) {
			return Short.valueOf(input.readShort());
		}
	}

	public static final class BytePrimitiveModel extends PrimitiveModel<Byte> {
		public static final BytePrimitiveModel instance = new BytePrimitiveModel();

		private BytePrimitiveModel() {
			super(byte.class);
		}

		@Override
		public void serialize(Byte value, Object template, Output output) {
			output.writeByte(value);
		}

		@Override
		public Byte readValues(Input input) {
			return Byte.valueOf(input.readByte());
		}
	}

	public static final class CharPrimitiveModel extends PrimitiveModel<Character> {
		public static final CharPrimitiveModel instance = new CharPrimitiveModel();

		private CharPrimitiveModel() {
			super(char.class);
		}

		@Override
		public void serialize(Character value, Object template, Output output) {
			output.writeChar(value);
		}

		@Override
		public Character readValues(Input input) {
			return Character.valueOf(input.readChar());
		}
	}

	public static final class BooleanPrimitiveModel extends PrimitiveModel<Boolean> {
		public static final BooleanPrimitiveModel instance = new BooleanPrimitiveModel();

		private BooleanPrimitiveModel() {
			super(boolean.class);
		}

		@Override
		public void serialize(Boolean value, Object template, Output output) {
			output.writeBoolean(value);
		}

		@Override
		public Boolean readValues(Input input) {
			return Boolean.valueOf(input.readBoolean());
		}
	}

	public static final class DoublePrimitiveModel extends PrimitiveModel<Double> {
		public static final DoublePrimitiveModel instance = new DoublePrimitiveModel();

		private DoublePrimitiveModel() {
			super(double.class);
		}

		@Override
		public void serialize(Double value, Object template, Output output) {
			output.writeDouble(value);
		}

		@Override
		public Double readValues(Input input) {
			return Double.valueOf(input.readDouble());
		}
	}

	public static final class FloatPrimitiveModel extends PrimitiveModel<Float> {
		public static final FloatPrimitiveModel instance = new FloatPrimitiveModel();

		private FloatPrimitiveModel() {
			super(float.class);
		}

		@Override
		public void serialize(Float value, Object template, Output output) {
			output.writeFloat(value);
		}

		@Override
		public Float readValues(Input input) {
			return Float.valueOf(input.readFloat());
		}
	}

	public static final class VoidModel extends PrimitiveModel<Void> {
		public static final VoidModel instance = new VoidModel();

		private VoidModel() {
			super(void.class);
		}

		@Override
		public void serialize(Void value, Object template, Output output) {
			output.writeNull();
		}

		@Override
		public Void readValues(Input input) {
			return null;
		}
	}

	public static final class IntegerModel extends SimpleModel<Integer> {
		public static final IntegerModel instance = new IntegerModel();

		private IntegerModel() {
			super(Integer.class);
		}

		@Override
		public void serialize(Integer value, Object template, Output output) {
			if (ValueUtils.isEqual(template, value)) {
				return;
			} else if (value == null) {
				output.writeNull();
			} else {
				output.writeInt(value);
			}
		}

		@Override
		public Integer deserialize(Object template, Input input) {
			if (input.isNull()) {
				return null;
			} else {
				return Integer.valueOf(input.readInt());
			}
		}
	}

	public static final class LongModel extends SimpleModel<Long> {
		public static final LongModel instance = new LongModel();

		private LongModel() {
			super(Long.class);
		}

		@Override
		public void serialize(Long value, Object template, Output output) {
			if (ValueUtils.isEqual(template, value)) {
				return;
			} else if (value == null) {
				output.writeNull();
			} else {
				output.writeLong(value);
			}
		}

		@Override
		public Long deserialize(Object template, Input input) {
			if (input.isNull()) {
				return null;
			} else {
				return Long.valueOf(input.readLong());
			}
		}
	}

	public static final class ShortModel extends SimpleModel<Short> {
		public static final ShortModel instance = new ShortModel();

		private ShortModel() {
			super(Short.class);
		}

		@Override
		public void serialize(Short value, Object template, Output output) {
			if (ValueUtils.isEqual(template, value)) {
				return;
			} else if (value == null) {
				output.writeNull();
			} else {
				output.writeShort(value);
			}
		}

		@Override
		public Short deserialize(Object template, Input input) {
			if (input.isNull()) {
				return null;
			} else {
				return Short.valueOf(input.readShort());
			}
		}
	}

	public static final class ByteModel extends SimpleModel<Byte> {
		public static final ByteModel instance = new ByteModel();

		private ByteModel() {
			super(Byte.class);
		}

		@Override
		public void serialize(Byte value, Object template, Output output) {
			if (ValueUtils.isEqual(template, value)) {
				return;
			} else if (value == null) {
				output.writeNull();
			} else {
				output.writeByte(value);
			}
		}

		@Override
		public Byte deserialize(Object template, Input input) {
			if (input.isNull()) {
				return null;
			} else {
				return Byte.valueOf(input.readByte());
			}
		}
	}

	public static final class CharModel extends SimpleModel<Character> {
		public static final CharModel instance = new CharModel();

		private CharModel() {
			super(Character.class);
		}

		@Override
		public void serialize(Character value, Object template, Output output) {
			if (ValueUtils.isEqual(template, value)) {
				return;
			} else if (value == null) {
				output.writeNull();
			} else {
				output.writeChar(value);
			}
		}

		@Override
		public Character deserialize(Object template, Input input) {
			if (input.isNull()) {
				return null;
			} else {
				return Character.valueOf(input.readChar());
			}
		}
	}

	public static final class BooleanModel extends SimpleModel<Boolean> {
		public static final BooleanModel instance = new BooleanModel();

		private BooleanModel() {
			super(Boolean.class);
		}

		@Override
		public void serialize(Boolean value, Object template, Output output) {
			if (ValueUtils.isEqual(template, value)) {
				return;
			} else if (value == null) {
				output.writeNull();
			} else {
				output.writeBoolean(value);
			}
		}

		@Override
		public Boolean deserialize(Object template, Input input) {
			if (input.isNull()) {
				return null;
			} else {
				return Boolean.valueOf(input.readBoolean());
			}
		}
	}

	public static final class DoubleModel extends SimpleModel<Double> {
		public static final DoubleModel instance = new DoubleModel();

		private DoubleModel() {
			super(Double.class);
		}

		@Override
		public void serialize(Double value, Object template, Output output) {
			if (ValueUtils.isEqual(template, value)) {
				return;
			} else if (value == null) {
				output.writeNull();
			} else {
				output.writeDouble(value);
			}
		}

		@Override
		public Double deserialize(Object template, Input input) {
			if (input.isNull()) {
				return null;
			} else {
				return Double.valueOf(input.readDouble());
			}
		}
	}

	public static final class FloatModel extends SimpleModel<Float> {
		public static final FloatModel instance = new FloatModel();

		private FloatModel() {
			super(Float.class);
		}

		@Override
		public void serialize(Float value, Object template, Output output) {
			if (ValueUtils.isEqual(template, value)) {
				return;
			} else if (value == null) {
				output.writeNull();
			} else {
				output.writeFloat(value);
			}
		}

		@Override
		public Float deserialize(Object template, Input input) {
			if (input.isNull()) {
				return null;
			} else {
				return Float.valueOf(input.readFloat());
			}
		}
	}

	public static final class StringModel extends SimpleModel<String> {
		public static final StringModel instance = new StringModel();

		private StringModel() {
			super(String.class);
		}

		@Override
		public void serialize(String value, Object template, Output output) {
			if (ValueUtils.isEqual(template, value)) {
				return;
			} else if (value == null) {
				output.writeNull();
			} else {
				output.writeString(value);
			}
		}

		@Override
		public String deserialize(Object template, Input input) {
			if (input.isNull()) {
				return null;
			} else {
				return input.readString();
			}
		}
	}

	public static final class BigIntegerModel extends SimpleModel<BigInteger> {
		public static final BigIntegerModel instance = new BigIntegerModel();

		private BigIntegerModel() {
			super(BigInteger.class);
		}

		@Override
		public void serialize(BigInteger value, Object template, Output output) {
			if (ValueUtils.isEqual(template, value)) {
				return;
			} else if (value == null) {
				output.writeNull();
			} else {
				output.writeString(value.toString());
			}
		}

		@Override
		public BigInteger deserialize(Object template, Input input) {
			if (input.isNull()) {
				return null;
			} else {
				return new BigInteger(input.readString());
			}
		}
	}

	public static final class BigDecimalModel extends SimpleModel<BigDecimal> {
		public static final BigDecimalModel instance = new BigDecimalModel();

		private BigDecimalModel() {
			super(BigDecimal.class);
		}

		@Override
		public void serialize(BigDecimal value, Object template, Output output) {
			if (ValueUtils.isEqual(template, value)) {
				return;
			} else if (value == null) {
				output.writeNull();
			} else {
				output.writeString(value.toString());
			}
		}

		@Override
		public BigDecimal deserialize(Object template, Input input) {
			if (input.isNull()) {
				return null;
			} else {
				return new BigDecimal(input.readString());
			}
		}
	}

	public static final class ClassModel extends SimpleModel<Class<?>> {
		public static final ClassModel instance = new ClassModel();

		@SuppressWarnings({ "rawtypes", "unchecked" })
		private ClassModel() {
			super((Class) Class.class);
		}

		@Override
		public void serialize(Class<?> value, Object template, Output output) {
			if (ValueUtils.isEqual(template, value)) {
				return;
			} else if (value == null) {
				output.writeNull();
			} else {
				output.writeString(value.getName());
			}
		}

		@Override
		public Class<?> deserialize(Object template, Input input) {
			if (input.isNull()) {
				return null;
			} else {
				return ReflectionUtils.forName(input.readString());
			}
		}
	}

	// TODO change to resolver
	public static final class DateModel extends SimpleModel<Date> {
		public static final DateModel instance = new DateModel();

		private DateModel() {
			super(Date.class);
		}

		@Override
		public void serialize(Date value, Object template, Output output) {
			if (ValueUtils.isEqual(template, value)) {
				return;
			} else if (value == null) {
				output.writeNull();
			} else {
				output.writeLong(value.getTime());
			}
		}

		@Override
		public Date deserialize(Object template, Input input) {
			if (input.isNull()) {
				return null;
			} else {
				return new Date(input.readLong());
			}
		}

		@Override
		public Date copy(Date original, CopyContext context) {
			return new Date(original.getTime());
		}
	}
}
