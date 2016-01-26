package com.gurella.engine.base.model;

import java.util.Arrays;

import com.gurella.engine.base.serialization.Input;
import com.gurella.engine.base.serialization.Output;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.ValueUtils;

public class DefaultArrayModels {
	private DefaultArrayModels() {
	}

	private static abstract class PrimitiveArrayModel<T> implements Model<T> {
		private Class<T> type;

		private PrimitiveArrayModel(Class<T> type) {
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
				writeValues(value, output);
			}
		}

		protected abstract void writeValues(T value, Output output);

		@Override
		public T deserialize(Object template, Input input) {
			if (!input.isValid()) {
				if (template == null) {
					return null;
				} else {
					@SuppressWarnings("unchecked")
					T instance = (T) input.copyObject(template);
					return instance;
				}
			} else if (input.isNull()) {
				return null;
			} else {
				return readValues(input);
			}
		}

		protected abstract T readValues(Input input);
	}

	public static final class IntArrayModel extends PrimitiveArrayModel<int[]> {
		public static final IntArrayModel instance = new IntArrayModel();

		private IntArrayModel() {
			super(int[].class);
		}

		@Override
		protected void writeValues(int[] value, Output output) {
			output.writeInt(value.length);
			for (int i = 0; i < value.length; i++) {
				output.writeInt(value[i]);
			}
		}

		@Override
		protected int[] readValues(Input input) {
			int length = input.readInt();
			int[] value = new int[length];
			input.pushObject(value);
			for (int i = 0; i < length; i++) {
				value[i] = input.readInt();
			}
			input.popObject();
			return value;
		}

		@Override
		public int[] copy(int[] original, CopyContext context) {
			return Arrays.copyOf(original, original.length);
		}
	}

	public static final class LongArrayModel extends PrimitiveArrayModel<long[]> {
		public static final LongArrayModel instance = new LongArrayModel();

		private LongArrayModel() {
			super(long[].class);
		}

		@Override
		protected void writeValues(long[] value, Output output) {
			output.writeInt(value.length);
			for (int i = 0; i < value.length; i++) {
				output.writeLong(value[i]);
			}
		}

		@Override
		protected long[] readValues(Input input) {
			int length = input.readInt();
			long[] value = new long[length];
			input.pushObject(value);
			for (int i = 0; i < length; i++) {
				value[i] = input.readLong();
			}
			input.popObject();
			return value;
		}

		@Override
		public long[] copy(long[] original, CopyContext context) {
			return Arrays.copyOf(original, original.length);
		}
	}

	public static final class ShortArrayModel extends PrimitiveArrayModel<short[]> {
		public static final ShortArrayModel instance = new ShortArrayModel();

		private ShortArrayModel() {
			super(short[].class);
		}

		@Override
		protected void writeValues(short[] value, Output output) {
			output.writeInt(value.length);
			for (int i = 0; i < value.length; i++) {
				output.writeShort(value[i]);
			}
		}

		@Override
		protected short[] readValues(Input input) {
			int length = input.readInt();
			short[] value = new short[length];
			input.pushObject(value);
			for (int i = 0; i < length; i++) {
				value[i] = input.readShort();
			}
			input.popObject();
			return value;
		}

		@Override
		public short[] copy(short[] original, CopyContext context) {
			return Arrays.copyOf(original, original.length);
		}
	}

	public static final class ByteArrayModel extends PrimitiveArrayModel<byte[]> {
		public static final ByteArrayModel instance = new ByteArrayModel();

		private ByteArrayModel() {
			super(byte[].class);
		}

		@Override
		protected void writeValues(byte[] value, Output output) {
			output.writeInt(value.length);
			for (int i = 0; i < value.length; i++) {
				output.writeByte(value[i]);
			}
		}

		@Override
		protected byte[] readValues(Input input) {
			int length = input.readInt();
			byte[] value = new byte[length];
			input.pushObject(value);
			for (int i = 0; i < length; i++) {
				value[i] = input.readByte();
			}
			input.popObject();
			return value;
		}

		@Override
		public byte[] copy(byte[] original, CopyContext context) {
			return Arrays.copyOf(original, original.length);
		}
	}

	public static final class CharArrayModel extends PrimitiveArrayModel<char[]> {
		public static final CharArrayModel instance = new CharArrayModel();

		private CharArrayModel() {
			super(char[].class);
		}

		@Override
		protected void writeValues(char[] value, Output output) {
			output.writeInt(value.length);
			for (int i = 0; i < value.length; i++) {
				output.writeChar(value[i]);
			}
		}

		@Override
		protected char[] readValues(Input input) {
			int length = input.readInt();
			char[] value = new char[length];
			input.pushObject(value);
			for (int i = 0; i < length; i++) {
				value[i] = input.readChar();
			}
			input.popObject();
			return value;
		}

		@Override
		public char[] copy(char[] original, CopyContext context) {
			return Arrays.copyOf(original, original.length);
		}
	}

	public static final class BooleanArrayModel extends PrimitiveArrayModel<boolean[]> {
		public static final BooleanArrayModel instance = new BooleanArrayModel();

		private BooleanArrayModel() {
			super(boolean[].class);
		}

		@Override
		protected void writeValues(boolean[] value, Output output) {
			output.writeInt(value.length);
			for (int i = 0; i < value.length; i++) {
				output.writeBoolean(value[i]);
			}
		}

		@Override
		protected boolean[] readValues(Input input) {
			int length = input.readInt();
			boolean[] value = new boolean[length];
			input.pushObject(value);
			for (int i = 0; i < length; i++) {
				value[i] = input.readBoolean();
			}
			input.popObject();
			return value;
		}

		@Override
		public boolean[] copy(boolean[] original, CopyContext context) {
			return Arrays.copyOf(original, original.length);
		}
	}

	public static final class DoubleArrayModel extends PrimitiveArrayModel<double[]> {
		public static final DoubleArrayModel instance = new DoubleArrayModel();

		private DoubleArrayModel() {
			super(double[].class);
		}

		@Override
		protected void writeValues(double[] value, Output output) {
			output.writeInt(value.length);
			for (int i = 0; i < value.length; i++) {
				output.writeDouble(value[i]);
			}
		}

		@Override
		protected double[] readValues(Input input) {
			int length = input.readInt();
			double[] value = new double[length];
			input.pushObject(value);
			for (int i = 0; i < length; i++) {
				value[i] = input.readDouble();
			}
			input.popObject();
			return value;
		}

		@Override
		public double[] copy(double[] original, CopyContext context) {
			return Arrays.copyOf(original, original.length);
		}
	}

	public static final class FloatArrayModel extends PrimitiveArrayModel<float[]> {
		public static final FloatArrayModel instance = new FloatArrayModel();

		private FloatArrayModel() {
			super(float[].class);
		}

		@Override
		protected void writeValues(float[] value, Output output) {
			output.writeInt(value.length);
			for (int i = 0; i < value.length; i++) {
				output.writeFloat(value[i]);
			}
		}

		@Override
		protected float[] readValues(Input input) {
			int length = input.readInt();
			float[] value = new float[length];
			input.pushObject(value);
			for (int i = 0; i < length; i++) {
				value[i] = input.readFloat();
			}
			input.popObject();
			return value;
		}

		@Override
		public float[] copy(float[] original, CopyContext context) {
			return Arrays.copyOf(original, original.length);
		}
	}
}
