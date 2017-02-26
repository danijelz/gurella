package com.gurella.engine.utils.struct;

public abstract class StructProperty {
	protected final int offset = 0;
	private final Alignment alignment = Alignment._0;
	protected final int size;

	public StructProperty(int size) {
		int mod = size % 4;
		this.size = mod == 3 ? size + 1 : size;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " size: " + size + " offset: " + offset + " alignment: " + alignment.name();
	}

	private enum Alignment {
		_0, _1, _2, _3;
	}

	public static class FloatStructProperty extends StructProperty {
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

	public static class IntStructProperty extends StructProperty {
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

	public static class DoubleStructProperty extends StructProperty {
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

	public static class LongStructProperty extends StructProperty {
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

	public static class ShortStructProperty extends StructProperty {
		public ShortStructProperty() {
			super(2);
		}

		public short get(Struct struct) {
			return struct.buffer.getShort(struct.offset + offset);
		}

		public void set(Struct struct, short value) {
			struct.buffer.setShort(struct.offset + offset, value);
		}
	}

	public static class ByteStructProperty extends StructProperty {
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
}
