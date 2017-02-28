package com.gurella.engine.utils.struct;

import java.util.Arrays;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public abstract class StructProperty {
	String name;
	int offset = 0;
	final int size;

	public abstract String toString(Struct struct);

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
		return getClass().getSimpleName() + "{name=" + name + " size=" + size + " offset=" + offset + "}";
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

		@Override
		public String toString(Struct struct) {
			return String.valueOf(get(struct));
		}
	}

	public static class FloatArrayStructProperty extends StructProperty {
		public final int length;

		public FloatArrayStructProperty(int length) {
			super(4 * length);
			this.length = length;
		}

		public float[] get(Struct struct) {
			return struct.buffer.getFloatArray(struct.offset + offset, new float[length], 0, length);
		}

		public float[] get(Struct struct, float[] out) {
			return struct.buffer.getFloatArray(struct.offset + offset, out, 0, length);
		}

		public float get(Struct struct, int index) {
			return struct.buffer.getFloat(struct.offset + offset + 4 * index);
		}

		public void set(Struct struct, int index, float value) {
			struct.buffer.setFloat(struct.offset + offset + 4 * index, value);
		}

		public void set(Struct struct, float[] value) {
			struct.buffer.setFloatArray(value, struct.offset + offset);
		}

		@Override
		public String toString(Struct struct) {
			return Arrays.toString(get(struct));
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

		@Override
		public String toString(Struct struct) {
			return String.valueOf(get(struct));
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

		@Override
		public String toString(Struct struct) {
			return String.valueOf(get(struct));
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

		@Override
		public String toString(Struct struct) {
			return String.valueOf(get(struct));
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

		@Override
		public String toString(Struct struct) {
			return String.valueOf(get(struct));
		}
	}

	public static class CharStructProperty extends StructProperty {
		public CharStructProperty() {
			super(2);
		}

		public char get(Struct struct) {
			return struct.buffer.getChar(struct.offset + offset);
		}

		public void set(Struct struct, char value) {
			struct.buffer.setChar(struct.offset + offset, value);
		}

		@Override
		public String toString(Struct struct) {
			return String.valueOf(get(struct));
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

		@Override
		public String toString(Struct struct) {
			return String.valueOf(get(struct));
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

		@Override
		public String toString(Struct struct) {
			return Integer.toBinaryString(get(struct));
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
			temp = structType.newInstance(null, 0);
		}

		public StructType<T> getStructType() {
			return structType;
		}

		public T get(Struct struct) {
			temp.buffer = struct.buffer;
			temp.offset = struct.offset + offset;
			return temp;
		}

		public T get(Struct struct, T out) {
			int sourceOffset = struct.offset + offset;
			out.buffer.set(struct.buffer, sourceOffset, out.offset, size);
			return out;
		}
		
		public void set(Struct struct, T value) {
			struct.buffer.setFloatArray(value.buffer.arr, value.offset, struct.offset + offset, size);
		}

		@Override
		public String toString(Struct struct) {
			return get(struct).toString();
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
			temp = structType.newInstance(null, 0);
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
			int sourceOffset = struct.offset + offset + size * index;
			out.buffer.set(struct.buffer, sourceOffset, out.offset, size);
			return out;
		}

		public void set(Struct struct, int index, T value) {
			struct.buffer.setFloatArray(value.buffer.arr, value.offset,
					struct.offset + offset + structType.size * index, size);
		}

		@Override
		public String toString(Struct struct) {
			StringBuilder builder = new StringBuilder();
			builder.append("[");
			for (int i = 0; i < length; i++) {
				builder.append(get(struct, i).toString());
				if (i < length - 1) {
					builder.append(", ");
				}
			}
			builder.append("]");
			return builder.toString();
		}
	}

	public static class Vector2StructProperty extends StructProperty {
		private final Vector2 temp = new Vector2();

		public Vector2StructProperty() {
			super(8);
		}

		public Vector2 get(Struct struct) {
			BaseBuffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset;
			temp.x = buffer.getFloat(tempOffset);
			temp.y = buffer.getFloat(tempOffset + 4);
			return temp;
		}

		public Vector2 get(Struct struct, Vector2 out) {
			BaseBuffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset;
			out.x = buffer.getFloat(tempOffset);
			out.y = buffer.getFloat(tempOffset + 4);
			return out;
		}

		public void set(Struct struct, Vector2 value) {
			BaseBuffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset;
			buffer.setFloat(tempOffset, value.x);
			buffer.setFloat(tempOffset + 4, value.y);
		}

		@Override
		public String toString(Struct struct) {
			return get(struct).toString();
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
			BaseBuffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset + 8 * index;
			temp.x = buffer.getFloat(tempOffset);
			temp.y = buffer.getFloat(tempOffset + 4);
			return temp;
		}

		public Vector2 get(Struct struct, int index, Vector2 out) {
			BaseBuffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset + 8 * index;
			out.x = buffer.getFloat(tempOffset);
			out.y = buffer.getFloat(tempOffset + 4);
			return out;
		}

		public void set(Struct struct, int index, Vector2 value) {
			BaseBuffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset + 8 * index;
			buffer.setFloat(tempOffset, value.x);
			buffer.setFloat(tempOffset + 4, value.y);
		}

		@Override
		public String toString(Struct struct) {
			StringBuilder builder = new StringBuilder();
			builder.append("[");
			for (int i = 0; i < length; i++) {
				builder.append(get(struct, i).toString());
				if (i < length - 1) {
					builder.append(", ");
				}
			}
			builder.append("]");
			return builder.toString();
		}
	}

	public static class Vector3StructProperty extends StructProperty {
		private final Vector3 temp = new Vector3();

		public Vector3StructProperty() {
			super(12);
		}

		public Vector3 get(Struct struct) {
			BaseBuffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset;
			temp.x = buffer.getFloat(tempOffset);
			temp.y = buffer.getFloat(tempOffset + 4);
			temp.z = buffer.getFloat(tempOffset + 8);
			return temp;
		}

		public Vector3 get(Struct struct, Vector3 out) {
			BaseBuffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset;
			out.x = buffer.getFloat(tempOffset);
			out.y = buffer.getFloat(tempOffset + 4);
			out.z = buffer.getFloat(tempOffset + 8);
			return out;
		}

		public void set(Struct struct, Vector3 value) {
			BaseBuffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset;
			buffer.setFloat(tempOffset, value.x);
			buffer.setFloat(tempOffset + 4, value.y);
			buffer.setFloat(tempOffset + 8, value.z);
		}

		@Override
		public String toString(Struct struct) {
			return get(struct).toString();
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
			BaseBuffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset + 12 * index;
			temp.x = buffer.getFloat(tempOffset);
			temp.y = buffer.getFloat(tempOffset + 4);
			temp.z = buffer.getFloat(tempOffset + 8);
			return temp;
		}

		public Vector3 get(Struct struct, int index, Vector3 out) {
			BaseBuffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset + 12 * index;
			out.x = buffer.getFloat(tempOffset);
			out.y = buffer.getFloat(tempOffset + 4);
			out.z = buffer.getFloat(tempOffset + 8);
			return out;
		}

		public void set(Struct struct, int index, Vector3 value) {
			BaseBuffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset + 12 * index;
			buffer.setFloat(tempOffset, value.x);
			buffer.setFloat(tempOffset + 4, value.y);
			buffer.setFloat(tempOffset + 8, value.z);
		}

		@Override
		public String toString(Struct struct) {
			StringBuilder builder = new StringBuilder();
			builder.append("[");
			for (int i = 0; i < length; i++) {
				builder.append(get(struct, i).toString());
				if (i < length - 1) {
					builder.append(", ");
				}
			}
			builder.append("]");
			return builder.toString();
		}
	}

	public static class GridPoint2StructProperty extends StructProperty {
		private final GridPoint2 temp = new GridPoint2();

		public GridPoint2StructProperty() {
			super(8);
		}

		public GridPoint2 get(Struct struct) {
			BaseBuffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset;
			temp.x = buffer.getInt(tempOffset);
			temp.y = buffer.getInt(tempOffset + 4);
			return temp;
		}

		public GridPoint2 get(Struct struct, GridPoint2 out) {
			BaseBuffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset;
			out.x = buffer.getInt(tempOffset);
			out.y = buffer.getInt(tempOffset + 4);
			return out;
		}

		public void set(Struct struct, GridPoint2 value) {
			BaseBuffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset;
			buffer.setInt(tempOffset, value.x);
			buffer.setInt(tempOffset + 4, value.y);
		}

		@Override
		public String toString(Struct struct) {
			return get(struct).toString();
		}
	}

	public static class GridPoint2ArrayStructProperty extends StructProperty {
		private int length;
		private final GridPoint2 temp = new GridPoint2();

		public GridPoint2ArrayStructProperty(int length) {
			super(8 * length);
			this.length = length;
		}

		public int getLength() {
			return length;
		}

		public GridPoint2 get(Struct struct, int index) {
			BaseBuffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset + 8 * index;
			temp.x = buffer.getInt(tempOffset);
			temp.y = buffer.getInt(tempOffset + 4);
			return temp;
		}

		public GridPoint2 get(Struct struct, int index, GridPoint2 out) {
			BaseBuffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset + 8 * index;
			out.x = buffer.getInt(tempOffset);
			out.y = buffer.getInt(tempOffset + 4);
			return out;
		}

		public void set(Struct struct, int index, GridPoint2 value) {
			BaseBuffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset + 8 * index;
			buffer.setInt(tempOffset, value.x);
			buffer.setInt(tempOffset + 4, value.y);
		}

		@Override
		public String toString(Struct struct) {
			StringBuilder builder = new StringBuilder();
			builder.append("[");
			for (int i = 0; i < length; i++) {
				builder.append(get(struct, i).toString());
				if (i < length - 1) {
					builder.append(", ");
				}
			}
			builder.append("]");
			return builder.toString();
		}
	}

	public static class GridPoint3StructProperty extends StructProperty {
		private final GridPoint3 temp = new GridPoint3();

		public GridPoint3StructProperty() {
			super(12);
		}

		public GridPoint3 get(Struct struct) {
			BaseBuffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset;
			temp.x = buffer.getInt(tempOffset);
			temp.y = buffer.getInt(tempOffset + 4);
			temp.z = buffer.getInt(tempOffset + 8);
			return temp;
		}

		public GridPoint3 get(Struct struct, GridPoint3 out) {
			BaseBuffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset;
			out.x = buffer.getInt(tempOffset);
			out.y = buffer.getInt(tempOffset + 4);
			out.z = buffer.getInt(tempOffset + 8);
			return out;
		}

		public void set(Struct struct, GridPoint3 value) {
			BaseBuffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset;
			buffer.setInt(tempOffset, value.x);
			buffer.setInt(tempOffset + 4, value.y);
			buffer.setInt(tempOffset + 8, value.z);
		}

		@Override
		public String toString(Struct struct) {
			return get(struct).toString();
		}
	}

	public static class GridPoint3ArrayStructProperty extends StructProperty {
		private int length;
		private final GridPoint3 temp = new GridPoint3();

		public GridPoint3ArrayStructProperty(int length) {
			super(12 * length);
			this.length = length;
		}

		public int getLength() {
			return length;
		}

		public GridPoint3 get(Struct struct, int index) {
			BaseBuffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset + 12 * index;
			temp.x = buffer.getInt(tempOffset);
			temp.y = buffer.getInt(tempOffset + 4);
			temp.z = buffer.getInt(tempOffset + 8);
			return temp;
		}

		public GridPoint3 get(Struct struct, int index, GridPoint3 out) {
			BaseBuffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset + 12 * index;
			out.x = buffer.getInt(tempOffset);
			out.y = buffer.getInt(tempOffset + 4);
			out.z = buffer.getInt(tempOffset + 8);
			return out;
		}

		public void set(Struct struct, int index, GridPoint3 value) {
			BaseBuffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset + 12 * index;
			buffer.setInt(tempOffset, value.x);
			buffer.setInt(tempOffset + 4, value.y);
			buffer.setInt(tempOffset + 8, value.z);
		}

		@Override
		public String toString(Struct struct) {
			StringBuilder builder = new StringBuilder();
			builder.append("[");
			for (int i = 0; i < length; i++) {
				builder.append(get(struct, i).toString());
				if (i < length - 1) {
					builder.append(", ");
				}
			}
			builder.append("]");
			return builder.toString();
		}
	}

	public static class QuaternionStructProperty extends StructProperty {
		private final Quaternion temp = new Quaternion();

		public QuaternionStructProperty() {
			super(16);
		}

		public Quaternion get(Struct struct) {
			BaseBuffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset;
			temp.x = buffer.getFloat(tempOffset);
			temp.y = buffer.getFloat(tempOffset + 4);
			temp.z = buffer.getFloat(tempOffset + 8);
			temp.w = buffer.getFloat(tempOffset + 12);
			return temp;
		}

		public Quaternion get(Struct struct, Quaternion out) {
			BaseBuffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset;
			out.x = buffer.getFloat(tempOffset);
			out.y = buffer.getFloat(tempOffset + 4);
			out.z = buffer.getFloat(tempOffset + 8);
			out.w = buffer.getFloat(tempOffset + 12);
			return out;
		}

		public void set(Struct struct, Quaternion value) {
			BaseBuffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset;
			buffer.setFloat(tempOffset, value.x);
			buffer.setFloat(tempOffset + 4, value.y);
			buffer.setFloat(tempOffset + 8, value.z);
			buffer.setFloat(tempOffset + 12, value.w);
		}

		@Override
		public String toString(Struct struct) {
			return get(struct).toString();
		}
	}

	public static class QuaternionArrayStructProperty extends StructProperty {
		private int length;
		private final Quaternion temp = new Quaternion();

		public QuaternionArrayStructProperty(int length) {
			super(16 * length);
			this.length = length;
		}

		public int getLength() {
			return length;
		}

		public Quaternion get(Struct struct, int index) {
			BaseBuffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset + 16 * index;
			temp.x = buffer.getFloat(tempOffset);
			temp.y = buffer.getFloat(tempOffset + 4);
			temp.z = buffer.getFloat(tempOffset + 8);
			temp.w = buffer.getFloat(tempOffset + 12);
			return temp;
		}

		public Quaternion get(Struct struct, int index, Quaternion out) {
			BaseBuffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset + 16 * index;
			out.x = buffer.getFloat(tempOffset);
			out.y = buffer.getFloat(tempOffset + 4);
			out.z = buffer.getFloat(tempOffset + 8);
			out.w = buffer.getFloat(tempOffset + 12);
			return out;
		}

		public void set(Struct struct, int index, Quaternion value) {
			BaseBuffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset + 16 * index;
			buffer.setFloat(tempOffset, value.x);
			buffer.setFloat(tempOffset + 4, value.y);
			buffer.setFloat(tempOffset + 8, value.z);
			buffer.setFloat(tempOffset + 12, value.w);
		}

		@Override
		public String toString(Struct struct) {
			StringBuilder builder = new StringBuilder();
			builder.append("[");
			for (int i = 0; i < length; i++) {
				builder.append(get(struct, i).toString());
				if (i < length - 1) {
					builder.append(", ");
				}
			}
			builder.append("]");
			return builder.toString();
		}
	}

	public static class Matrix3StructProperty extends StructProperty {
		private final Matrix3 temp = new Matrix3();

		public Matrix3StructProperty() {
			super(36);
		}

		public Matrix3 get(Struct struct) {
			BaseBuffer buffer = struct.buffer;
			buffer.getFloatArray(struct.offset + offset, temp.val);
			return temp;
		}

		public Matrix3 get(Struct struct, Matrix3 out) {
			BaseBuffer buffer = struct.buffer;
			buffer.getFloatArray(struct.offset + offset, out.val);
			return out;
		}

		public void set(Struct struct, Matrix3 value) {
			BaseBuffer buffer = struct.buffer;
			buffer.setFloatArray(value.val, struct.offset + offset);
		}

		@Override
		public String toString(Struct struct) {
			return get(struct).toString();
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
			BaseBuffer buffer = struct.buffer;
			buffer.getFloatArray(struct.offset + offset + 36 * index, temp.val);
			return temp;
		}

		public Matrix3 get(Struct struct, int index, Matrix3 out) {
			BaseBuffer buffer = struct.buffer;
			buffer.getFloatArray(struct.offset + offset + 36 * index, out.val);
			return out;
		}

		public void set(Struct struct, int index, Matrix3 value) {
			BaseBuffer buffer = struct.buffer;
			buffer.setFloatArray(value.val, struct.offset + offset + 36 * index);
		}

		@Override
		public String toString(Struct struct) {
			StringBuilder builder = new StringBuilder();
			builder.append("[");
			for (int i = 0; i < length; i++) {
				builder.append(get(struct, i).toString());
				if (i < length - 1) {
					builder.append(", ");
				}
			}
			builder.append("]");
			return builder.toString();
		}
	}

	public static class Matrix4StructProperty extends StructProperty {
		private final Matrix4 temp = new Matrix4();

		public Matrix4StructProperty() {
			super(64);
		}

		public Matrix4 get(Struct struct) {
			BaseBuffer buffer = struct.buffer;
			buffer.getFloatArray(struct.offset + offset, temp.val);
			return temp;
		}

		public Matrix4 get(Struct struct, Matrix4 out) {
			BaseBuffer buffer = struct.buffer;
			buffer.getFloatArray(struct.offset + offset, out.val);
			return out;
		}

		public void set(Struct struct, Matrix4 value) {
			BaseBuffer buffer = struct.buffer;
			buffer.setFloatArray(value.val, struct.offset + offset);
		}

		@Override
		public String toString(Struct struct) {
			return get(struct).toString();
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
			BaseBuffer buffer = struct.buffer;
			buffer.getFloatArray(struct.offset + offset + 64 * index, temp.val);
			return temp;
		}

		public Matrix4 get(Struct struct, int index, Matrix4 out) {
			BaseBuffer buffer = struct.buffer;
			buffer.getFloatArray(struct.offset + offset + 64 * index, out.val);
			return out;
		}

		public void set(Struct struct, int index, Matrix4 value) {
			BaseBuffer buffer = struct.buffer;
			buffer.setFloatArray(value.val, struct.offset + offset + 64 * index);
		}

		@Override
		public String toString(Struct struct) {
			StringBuilder builder = new StringBuilder();
			builder.append("[");
			for (int i = 0; i < length; i++) {
				builder.append(get(struct, i).toString());
				if (i < length - 1) {
					builder.append(", ");
				}
			}
			builder.append("]");
			return builder.toString();
		}
	}
}
