package com.gurella.engine.utils.struct;

public abstract class StructProperty {
	int offset = 0;
	Alignment alignment = Alignment._0;
	final int size;

	public StructProperty(int size) {
		if (size == 1 || size == 2) {
			this.size = size;
		} else if (size == 3 || size == 4) {
			this.size = 4;
		} else {
			this.size = size + (size % 4);
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " size: " + size + " offset: " + offset + " alignment: " + alignment.name();
	}

	enum Alignment {
		_0, _1, _2, _3;
	}

	static abstract class PrimitiveStructProperty extends StructProperty {
		PrimitiveStructProperty(int size) {
			super(size);
		}
	}

	public static class FloatStructProperty extends PrimitiveStructProperty {
		public FloatStructProperty() {
			super(4);
		}

		public float get(Struct struct) {
			return struct.buffer.getFloat(struct.offset + offset);
		}

		public void set(Struct struct, float value) {
			struct.buffer.setFloat(struct.offset + offset, value);
		}
	}

	public static class IntStructProperty extends PrimitiveStructProperty {
		public IntStructProperty() {
			super(4);
		}

		public int get(Struct struct) {
			return struct.buffer.getInt(struct.offset + offset);
		}

		public void set(Struct struct, int value) {
			struct.buffer.setInt(struct.offset + offset, value);
		}
	}

	public static class DoubleStructProperty extends PrimitiveStructProperty {
		public DoubleStructProperty() {
			super(8);
		}

		public double get(Struct struct) {
			return struct.buffer.getDouble(struct.offset + offset);
		}

		public void set(Struct struct, double value) {
			struct.buffer.setDouble(struct.offset + offset, value);
		}
	}

	public static class LongStructProperty extends PrimitiveStructProperty {
		public LongStructProperty() {
			super(8);
		}

		public long get(Struct struct) {
			return struct.buffer.getLong(struct.offset + offset);
		}

		public void set(Struct struct, long value) {
			struct.buffer.setLong(struct.offset + offset, value);
		}
	}

	public static class ShortStructProperty extends PrimitiveStructProperty {
		public ShortStructProperty() {
			super(2);
		}

		public short get(Struct struct) {
			switch (alignment) {
			case _0:
				return struct.buffer.getShort0(struct.offset + offset);
			case _2:
				return struct.buffer.getShort2(struct.offset + offset);
			default:
				throw new IllegalStateException();
			}
		}

		public void set(Struct struct, short value) {
			switch (alignment) {
			case _0:
				struct.buffer.setShort0(struct.offset + offset, value);
				return;
			case _2:
				struct.buffer.setShort2(struct.offset + offset, value);
				return;
			default:
				throw new IllegalStateException();
			}
		}
	}

	public static class ByteStructProperty extends PrimitiveStructProperty {
		public ByteStructProperty() {
			super(1);
		}

		public byte get(Struct struct) {
			return struct.buffer.getByte(struct.offset + offset);
		}

		public void set(Struct struct, byte value) {
			struct.buffer.setByte(struct.offset + offset, value);
		}
	}

	public static class FlagStructProperty extends StructProperty {
		public FlagStructProperty() {
			super(4);
		}

		public int get(Struct struct) {
			return struct.buffer.getInt(struct.offset + offset);
		}

		public void set(Struct struct, int value) {
			struct.buffer.setInt(struct.offset + offset, value);
		}

		public boolean getFlag(Struct struct, int flag) {
			return struct.buffer.getFlag(struct.offset + offset, flag);
		}

		public void setFlag(Struct struct, int flag) {
			struct.buffer.setFlag(struct.offset + offset, flag);
		}

		public void unsetFlag(Struct struct, int flag) {
			struct.buffer.unsetFlag(struct.offset + offset, flag);
		}
	}

	public static class FloatArrayStructProperty extends StructProperty {
		private int capacity;

		FloatArrayStructProperty(int capacity) {
			super(4 * capacity);
			this.capacity = capacity;
		}

		public float[] get(Struct struct, float[] out) {
			return struct.buffer.getFloatArray(struct.offset + offset, out, 0, capacity);
		}

		public void set(Struct struct, float[] value) {
			struct.buffer.setFloatArray(struct.offset + offset, value);
		}
	}

	public static class ComplexArrayStructProperty<T extends Struct> extends StructProperty {
		private StructType<T> structType;
		private int capacity;

		ComplexArrayStructProperty(StructType<T> structType, int capacity) {
			super(structType.size * capacity);
			this.structType = structType;
			this.capacity = capacity;
		}

		public int getCapacity() {
			return capacity;
		}

		public StructType<T> getStructType() {
			return structType;
		}

		public T get(Struct struct, int index, T out) {
			out.buffer = struct.buffer;
			out.offset = offset + size * index;
			return out;
		}

		public void set(Struct struct, T value) {
			//TODO struct.buffer.setFloatArray(struct.offset + offset, value);
		}
	}
}
