package com.gurella.engine.utils.struct;

import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.gurella.engine.utils.Reflection;

public abstract class StructProperty {
	String name;
	int offset = 0;
	byte alignment = 0;
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
		return name + " size: " + size + " offset: " + offset + " alignment: " + alignment;
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

	public static class FloatArrayStructProperty extends StructProperty {
		public final int length;

		public FloatArrayStructProperty(int length) {
			super(4 * length);
			this.length = length;
		}

		public float get(Struct struct, int index) {
			return struct.buffer.getFloat(struct.offset + offset + 4 * index);
		}

		public void set(Struct struct, int index, float value) {
			struct.buffer.setFloat(struct.offset + offset + 4 * index, value);
		}

		public float[] get(Struct struct, float[] out) {
			return struct.buffer.getFloatArray(struct.offset + offset, out, 0, length);
		}

		public void set(Struct struct, float[] value) {
			struct.buffer.setFloatArray(struct.offset + offset, value);
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
			switch (alignment) {
			case 0:
				return struct.buffer.getShort0(struct.offset + offset);
			case 2:
				return struct.buffer.getShort2(struct.offset + offset);
			default:
				throw new IllegalStateException();
			}
		}

		public void set(Struct struct, short value) {
			switch (alignment) {
			case 0:
				struct.buffer.setShort0(struct.offset + offset, value);
				return;
			case 2:
				struct.buffer.setShort2(struct.offset + offset, value);
				return;
			default:
				throw new IllegalStateException();
			}
		}
	}

	public static class CharStructProperty extends StructProperty {
		public CharStructProperty() {
			super(2);
		}

		public char get(Struct struct) {
			switch (alignment) {
			case 0:
				return struct.buffer.getChar0(struct.offset + offset);
			case 2:
				return struct.buffer.getChar2(struct.offset + offset);
			default:
				throw new IllegalStateException();
			}
		}

		public void set(Struct struct, char value) {
			switch (alignment) {
			case 0:
				struct.buffer.setChar0(struct.offset + offset, value);
				return;
			case 2:
				struct.buffer.setChar2(struct.offset + offset, value);
				return;
			default:
				throw new IllegalStateException();
			}
		}
	}

	public static class ByteStructProperty extends StructProperty {
		public ByteStructProperty() {
			super(1);
		}

		public byte get(Struct struct) {
			switch (alignment) {
			case 0:
				return struct.buffer.getByte0(struct.offset + offset);
			case 1:
				return struct.buffer.getByte1(struct.offset + offset);
			case 2:
				return struct.buffer.getByte2(struct.offset + offset);
			case 3:
				return struct.buffer.getByte3(struct.offset + offset);
			default:
				throw new IllegalStateException();
			}
		}

		public void set(Struct struct, byte value) {
			switch (alignment) {
			case 0:
				struct.buffer.setByte0(struct.offset + offset, value);
				return;
			case 1:
				struct.buffer.setByte1(struct.offset + offset, value);
				return;
			case 2:
				struct.buffer.setByte2(struct.offset + offset, value);
				return;
			case 3:
				struct.buffer.setByte3(struct.offset + offset, value);
				return;
			default:
				throw new IllegalStateException();
			}
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

	public static class ComplexStructProperty<T extends Struct> extends StructProperty {
		private StructType<T> structType;
		private final T temp;

		public ComplexStructProperty(Class<T> type) {
			this(StructType.get(type));
		}

		public ComplexStructProperty(StructType<T> structType) {
			super(structType.size);
			this.structType = structType;
			temp = Reflection.newInstance(structType.type);
		}

		public StructType<T> getStructType() {
			return structType;
		}

		public T get(Struct struct) {
			temp.buffer = struct.buffer;
			temp.offset = struct.offset + offset;
			return temp;
		}

		public void set(Struct struct, T value) {
			struct.buffer.setFloatArray(value.buffer.buffer, value.offset, struct.offset + offset, size);
		}
	}

	public static class ComplexArrayStructProperty<T extends Struct> extends StructProperty {
		private StructType<T> structType;
		private int length;

		private final T temp;

		public ComplexArrayStructProperty(Class<T> type, int length) {
			this(StructType.get(type), length);
		}

		public ComplexArrayStructProperty(StructType<T> structType, int length) {
			super(structType.size * length);
			this.structType = structType;
			this.length = length;
			temp = Reflection.newInstance(structType.type);
		}

		public int getLength() {
			return length;
		}

		public StructType<T> getStructType() {
			return structType;
		}

		public T get(Struct struct, int index) {
			temp.buffer = struct.buffer;
			temp.offset = struct.offset + offset + structType.size * index;
			return temp;
		}

		public T get(Struct struct, int index, T out) {
			out.buffer = struct.buffer;
			out.offset = struct.offset + offset + structType.size * index;
			return out;
		}

		public void set(Struct struct, int index, T value) {
			struct.buffer.setFloatArray(value.buffer.buffer, value.offset,
					struct.offset + offset + structType.size * index, size);
		}
	}

	public static class Vector2StructProperty extends StructProperty {
		private final Vector2 temp = new Vector2();

		public Vector2StructProperty() {
			super(8);
		}

		public Vector2 get(Struct struct) {
			Buffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset;
			temp.x = buffer.getFloat(tempOffset++);
			temp.y = buffer.getFloat(tempOffset);
			return temp;
		}

		public Vector2 get(Struct struct, Vector2 out) {
			Buffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset;
			out.x = buffer.getFloat(tempOffset++);
			out.y = buffer.getFloat(tempOffset);
			return out;
		}

		public void set(Struct struct, Vector2 value) {
			Buffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset;
			buffer.setFloat(tempOffset++, value.x);
			buffer.setFloat(tempOffset, value.y);
		}
	}

	public static class Vector2ArrayStructProperty extends StructProperty {
		private int length;
		private final Vector2 temp = new Vector2();

		public Vector2ArrayStructProperty(int length) {
			super(8 * length);
			this.length = length;
		}

		public int getLength() {
			return length;
		}

		public Vector2 get(Struct struct, int index) {
			Buffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset + 8 * index;
			temp.x = buffer.getFloat(tempOffset++);
			temp.y = buffer.getFloat(tempOffset);
			return temp;
		}

		public Vector2 get(Struct struct, int index, Vector2 out) {
			Buffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset + 8 * index;
			out.x = buffer.getFloat(tempOffset++);
			out.y = buffer.getFloat(tempOffset);
			return out;
		}

		public void set(Struct struct, int index, Vector2 value) {
			Buffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset + 8 * index;
			buffer.setFloat(tempOffset++, value.x);
			buffer.setFloat(tempOffset, value.y);
		}
	}

	public static class Vector3StructProperty extends StructProperty {
		private final Vector3 temp = new Vector3();

		public Vector3StructProperty() {
			super(12);
		}

		public Vector3 get(Struct struct) {
			Buffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset;
			temp.x = buffer.getFloat(tempOffset++);
			temp.y = buffer.getFloat(tempOffset++);
			temp.z = buffer.getFloat(tempOffset);
			return temp;
		}

		public Vector3 get(Struct struct, Vector3 out) {
			Buffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset;
			out.x = buffer.getFloat(tempOffset++);
			out.y = buffer.getFloat(tempOffset++);
			out.z = buffer.getFloat(tempOffset);
			return out;
		}

		public void set(Struct struct, Vector3 value) {
			Buffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset;
			buffer.setFloat(tempOffset++, value.x);
			buffer.setFloat(tempOffset++, value.y);
			buffer.setFloat(tempOffset, value.z);
		}
	}

	public static class Vector3ArrayStructProperty extends StructProperty {
		private int length;
		private final Vector3 temp = new Vector3();

		public Vector3ArrayStructProperty(int length) {
			super(12 * length);
			this.length = length;
		}

		public int getLength() {
			return length;
		}

		public Vector3 get(Struct struct, int index) {
			Buffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset + 12 * index;
			temp.x = buffer.getFloat(tempOffset++);
			temp.y = buffer.getFloat(tempOffset);
			temp.z = buffer.getFloat(tempOffset++);
			return temp;
		}

		public Vector3 get(Struct struct, int index, Vector3 out) {
			Buffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset + 12 * index;
			out.x = buffer.getFloat(tempOffset++);
			out.y = buffer.getFloat(tempOffset++);
			out.z = buffer.getFloat(tempOffset);
			return out;
		}

		public void set(Struct struct, int index, Vector3 value) {
			Buffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset + 12 * index;
			buffer.setFloat(tempOffset++, value.x);
			buffer.setFloat(tempOffset++, value.y);
			buffer.setFloat(tempOffset, value.z);
		}
	}

	public static class Matrix3StructProperty extends StructProperty {
		private final Matrix3 temp = new Matrix3();

		public Matrix3StructProperty() {
			super(36);
		}

		public Matrix3 get(Struct struct) {
			Buffer buffer = struct.buffer;
			buffer.getFloatArray(struct.offset + offset, temp.val);
			return temp;
		}

		public Matrix3 get(Struct struct, Matrix3 out) {
			Buffer buffer = struct.buffer;
			buffer.getFloatArray(struct.offset + offset, out.val);
			return out;
		}

		public void set(Struct struct, Matrix3 value) {
			Buffer buffer = struct.buffer;
			buffer.setFloatArray(struct.offset + offset, value.val);
		}
	}

	public static class Matrix3ArrayStructProperty extends StructProperty {
		private int length;
		private final Matrix3 temp = new Matrix3();

		public Matrix3ArrayStructProperty(int length) {
			super(36 * length);
			this.length = length;
		}

		public int getLength() {
			return length;
		}

		public Matrix3 get(Struct struct, int index) {
			Buffer buffer = struct.buffer;
			buffer.getFloatArray(struct.offset + offset + 36 * index, temp.val);
			return temp;
		}

		public Matrix3 get(Struct struct, int index, Matrix3 out) {
			Buffer buffer = struct.buffer;
			buffer.getFloatArray(struct.offset + offset + 36 * index, out.val);
			return out;
		}

		public void set(Struct struct, int index, Matrix3 value) {
			Buffer buffer = struct.buffer;
			buffer.setFloatArray(struct.offset + offset + 36 * index, value.val);
		}
	}

	public static class Matrix4StructProperty extends StructProperty {
		private final Matrix4 temp = new Matrix4();

		public Matrix4StructProperty() {
			super(64);
		}

		public Matrix4 get(Struct struct) {
			Buffer buffer = struct.buffer;
			buffer.getFloatArray(struct.offset + offset, temp.val);
			return temp;
		}

		public Matrix4 get(Struct struct, Matrix4 out) {
			Buffer buffer = struct.buffer;
			buffer.getFloatArray(struct.offset + offset, out.val);
			return out;
		}

		public void set(Struct struct, Matrix4 value) {
			Buffer buffer = struct.buffer;
			buffer.setFloatArray(struct.offset + offset, value.val);
		}
	}

	public static class Matrix4ArrayStructProperty extends StructProperty {
		private int length;
		private final Matrix4 temp = new Matrix4();

		public Matrix4ArrayStructProperty(int length) {
			super(64 * length);
			this.length = length;
		}

		public int getLength() {
			return length;
		}

		public Matrix4 get(Struct struct, int index) {
			Buffer buffer = struct.buffer;
			buffer.getFloatArray(struct.offset + offset + 64 * index, temp.val);
			return temp;
		}

		public Matrix4 get(Struct struct, int index, Matrix4 out) {
			Buffer buffer = struct.buffer;
			buffer.getFloatArray(struct.offset + offset + 64 * index, out.val);
			return out;
		}

		public void set(Struct struct, int index, Matrix4 value) {
			Buffer buffer = struct.buffer;
			buffer.setFloatArray(struct.offset + offset + 64 * index, value.val);
		}
	}
}
