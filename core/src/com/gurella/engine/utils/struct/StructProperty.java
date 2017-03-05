package com.gurella.engine.utils.struct;

import static com.gurella.engine.utils.struct.Buffer.word;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.gurella.engine.math.GridRectangle;

public abstract class StructProperty {
	String name;
	int offset;
	final int size;

	public abstract String toString(Struct struct);

	public boolean equals(Struct s1, Struct s2) {
		return s1.buffer.equals(s1.offset + offset, s2.buffer, s2.offset + offset, size);
	}

	public int hashCode(Struct struct) {
		return struct.buffer.hashCode(struct.offset + offset, size);
	}

	public StructProperty(int size) {
		if (size < 1) {
			throw new IllegalArgumentException("Size must be > 0");
		} else if (size < 3) {
			this.size = size;
		} else if (size < 5) {
			this.size = 4;
		} else {
			this.size = size + (word - size % word);
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "{name=" + name + " size=" + size + " offset=" + offset + "}";
	}

	public static abstract class ArrayStructProperty extends StructProperty {
		public final int length;

		public ArrayStructProperty(int itemSize, int length) {
			super(itemSize * length);
			this.length = length;
		}

		protected abstract String itemToString(Struct struct, int index);

		@Override
		public String toString(Struct struct) {
			StringBuilder builder = new StringBuilder();
			builder.append("[");
			for (int i = 0; i < length; i++) {
				builder.append(itemToString(struct, i));
				if (i < length - 1) {
					builder.append(", ");
				}
			}
			builder.append("]");
			return builder.toString();
		}
	}

	public static abstract class ObjectArrayStructProperty<T> extends ArrayStructProperty {
		public ObjectArrayStructProperty(int itemSize, int length) {
			super(itemSize, length);
		}

		public abstract T get(Struct struct, int index);

		public abstract T get(Struct struct, int index, T out);

		public abstract void set(Struct struct, int index, T value);
	}

	public static abstract class ObjectStructProperty<T> extends StructProperty {
		public ObjectStructProperty(int size) {
			super(size);
		}

		public abstract T get(Struct struct);

		public abstract T get(Struct struct, T out);

		public abstract void set(Struct struct, T value);

		@Override
		public String toString(Struct struct) {
			return get(struct).toString();
		}
	}

	public static class FloatStructProperty extends StructProperty {
		public FloatStructProperty() {
			super(word);
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

	public static class FloatArrayStructProperty extends ArrayStructProperty {
		public FloatArrayStructProperty(int length) {
			super(word, length);
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
			if (value.length != length) {
				throw new IllegalArgumentException("Invalid length.");
			}
			struct.buffer.setFloatArray(struct.offset + offset, value);
		}

		@Override
		protected String itemToString(Struct struct, int index) {
			return String.valueOf(get(struct, index));
		}
	}

	public static class IntStructProperty extends StructProperty {
		public IntStructProperty() {
			super(word);
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
			super(2 * word);
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
			super(2 * word);
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
			super(word);
		}

		public int getFlags(Struct struct) {
			return struct.buffer.getInt(struct.offset + offset);
		}

		public void setFlags(Struct struct, int value) {
			struct.buffer.setInt(struct.offset + offset, value);
		}

		public void and(Struct struct, int flags) {
			int value = getFlags(struct);
			struct.buffer.setInt(struct.offset + offset, value & flags);
		}

		public void or(Struct struct, int flags) {
			int value = getFlags(struct);
			struct.buffer.setInt(struct.offset + offset, value | flags);
		}

		public void xor(Struct struct, int flags) {
			int value = getFlags(struct);
			struct.buffer.setInt(struct.offset + offset, value ^ flags);
		}

		public void not(Struct struct) {
			int value = getFlags(struct);
			struct.buffer.setInt(struct.offset + offset, ~value);
		}

		public void flip(Struct struct, int flag) {
			int value = getFlags(struct);
			struct.buffer.setInt(struct.offset + offset, value ^ (1 << flag));
		}

		public boolean isSet(Struct struct, int flag) {
			return struct.buffer.getFlag(struct.offset + offset, flag);
		}

		public void set(Struct struct, int flag) {
			struct.buffer.setFlag(struct.offset + offset, flag);
		}

		public void unset(Struct struct, int flag) {
			struct.buffer.unsetFlag(struct.offset + offset, flag);
		}

		@Override
		public String toString(Struct struct) {
			return Integer.toBinaryString(getFlags(struct));
		}
	}

	public static class ComplexStructProperty<T extends Struct> extends ObjectStructProperty<T> {
		private StructType<T> structType;
		private final T shared;

		public ComplexStructProperty(Class<T> type) {
			this(StructType.get(type));
		}

		public ComplexStructProperty(StructType<T> structType) {
			super(structType.size);
			this.structType = structType;
			shared = structType.newInstance(null, 0);
		}

		public StructType<T> getStructType() {
			return structType;
		}

		@Override
		public T get(Struct struct) {
			shared.buffer = struct.buffer;
			shared.offset = struct.offset + offset;
			return shared;
		}

		@Override
		public T get(Struct struct, T out) {
			int sourceOffset = struct.offset + offset;
			if (out.buffer == null) {
				out.buffer = struct.buffer;
				out.offset = sourceOffset;
			} else if (out.buffer == struct.buffer) {
				out.offset = sourceOffset;
			} else {
				out.buffer.set(struct.buffer, sourceOffset, out.offset, size);
			}
			return out;
		}

		@Override
		public void set(Struct struct, T value) {
			struct.buffer.set(value.buffer, value.offset, struct.offset + offset, size);
		}
	}

	public static class ComplexArrayStructProperty<T extends Struct> extends ObjectArrayStructProperty<T> {
		private StructType<T> structType;
		private final T shared;

		public ComplexArrayStructProperty(Class<T> type, int length) {
			this(StructType.get(type), length);
		}

		public ComplexArrayStructProperty(StructType<T> structType, int length) {
			super(structType.size, length);
			this.structType = structType;
			shared = structType.newInstance(null, 0);
		}

		public StructType<T> getStructType() {
			return structType;
		}

		@Override
		public T get(Struct struct, int index) {
			shared.buffer = struct.buffer;
			shared.offset = struct.offset + offset + structType.size * index;
			return shared;
		}

		@Override
		public T get(Struct struct, int index, T out) {
			int sourceOffset = struct.offset + offset + structType.size * index;
			if (out.buffer == null) {
				out.buffer = struct.buffer;
				out.offset = sourceOffset;
			} else if (out.buffer == struct.buffer) {
				out.offset = sourceOffset;
			} else {
				out.buffer.set(struct.buffer, sourceOffset, out.offset, structType.size);
			}
			return out;
		}

		@Override
		public void set(Struct struct, int index, T value) {
			int valueOffset = struct.offset + offset + structType.size * index;
			struct.buffer.set(value.buffer, value.offset, valueOffset, structType.size);
		}

		@Override
		protected String itemToString(Struct struct, int index) {
			return get(struct, index).toString();
		}
	}

	public static class ReferenceStructProperty<T extends Struct> extends ObjectStructProperty<T> {
		private StructType<T> structType;
		private final T shared;

		public ReferenceStructProperty(Class<T> type) {
			this(StructType.get(type));
		}

		public ReferenceStructProperty(StructType<T> structType) {
			super(structType.size);
			this.structType = structType;
			shared = structType.newInstance(null, 0);
		}

		public StructType<T> getStructType() {
			return structType;
		}

		@Override
		public T get(Struct struct) {
			Buffer buffer = struct.buffer;
			shared.buffer = buffer;
			shared.offset = buffer.getInt(struct.offset + offset);
			return shared;
		}

		@Override
		public T get(Struct struct, T out) {
			out.buffer = struct.buffer;
			out.offset = struct.buffer.getInt(struct.offset + offset);
			return out;
		}

		@Override
		public void set(Struct struct, T value) {
			if (struct.buffer != value.buffer) {
				throw new IllegalArgumentException("Value doesn't belong to struct.buffer.");
			}
			struct.buffer.setInt(struct.offset + offset, value.offset);
		}
	}

	public static class ReferenceArrayStructProperty<T extends Struct> extends ObjectArrayStructProperty<T> {
		private StructType<T> structType;
		private final T shared;

		public ReferenceArrayStructProperty(Class<T> type, int length) {
			this(StructType.get(type), length);
		}

		public ReferenceArrayStructProperty(StructType<T> structType, int length) {
			super(structType.size, length);
			this.structType = structType;
			shared = structType.newInstance(null, 0);
		}

		public StructType<T> getStructType() {
			return structType;
		}

		@Override
		public T get(Struct struct, int index) {
			Buffer buffer = struct.buffer;
			shared.buffer = buffer;
			shared.offset = buffer.getInt(struct.offset + offset + structType.size * index);
			return shared;
		}

		@Override
		public T get(Struct struct, int index, T out) {
			out.buffer = struct.buffer;
			out.offset = struct.buffer.getInt(struct.offset + offset + structType.size * index);
			return out;
		}

		@Override
		public void set(Struct struct, int index, T value) {
			if (struct.buffer != value.buffer) {
				throw new IllegalArgumentException("Value doesn't belong to struct.buffer.");
			}
			int valueOffset = struct.offset + offset + structType.size * index;
			struct.buffer.setInt(valueOffset, value.offset);
		}

		@Override
		protected String itemToString(Struct struct, int index) {
			return get(struct, index).toString();
		}
	}

	public static class Vector2StructProperty extends ObjectStructProperty<Vector2> {
		private final Vector2 shared = new Vector2();

		public Vector2StructProperty() {
			super(2 * word);
		}

		@Override
		public Vector2 get(Struct struct) {
			Buffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset;
			shared.x = buffer.getFloat(tempOffset);
			shared.y = buffer.getFloat(tempOffset + word);
			return shared;
		}

		@Override
		public Vector2 get(Struct struct, Vector2 out) {
			Buffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset;
			out.x = buffer.getFloat(tempOffset);
			out.y = buffer.getFloat(tempOffset + word);
			return out;
		}

		@Override
		public void set(Struct struct, Vector2 value) {
			Buffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset;
			buffer.setFloat(tempOffset, value.x);
			buffer.setFloat(tempOffset + word, value.y);
		}
	}

	public static class Vector2ArrayStructProperty extends ObjectArrayStructProperty<Vector2> {
		private final Vector2 shared = new Vector2();

		public Vector2ArrayStructProperty(int length) {
			super(2 * word, length);
		}

		@Override
		public Vector2 get(Struct struct, int index) {
			Buffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset + 8 * index;
			shared.x = buffer.getFloat(tempOffset);
			shared.y = buffer.getFloat(tempOffset + word);
			return shared;
		}

		@Override
		public Vector2 get(Struct struct, int index, Vector2 out) {
			Buffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset + 8 * index;
			out.x = buffer.getFloat(tempOffset);
			out.y = buffer.getFloat(tempOffset + word);
			return out;
		}

		@Override
		public void set(Struct struct, int index, Vector2 value) {
			Buffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset + 8 * index;
			buffer.setFloat(tempOffset, value.x);
			buffer.setFloat(tempOffset + word, value.y);
		}

		@Override
		protected String itemToString(Struct struct, int index) {
			return get(struct, index).toString();
		}
	}

	public static class Vector3StructProperty extends ObjectStructProperty<Vector3> {
		private final Vector3 shared = new Vector3();

		public Vector3StructProperty() {
			super(3 * word);
		}

		@Override
		public Vector3 get(Struct struct) {
			Buffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset;
			shared.x = buffer.getFloat(tempOffset);
			shared.y = buffer.getFloat(tempOffset + 4);
			shared.z = buffer.getFloat(tempOffset + 8);
			return shared;
		}

		@Override
		public Vector3 get(Struct struct, Vector3 out) {
			Buffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset;
			out.x = buffer.getFloat(tempOffset);
			out.y = buffer.getFloat(tempOffset + 4);
			out.z = buffer.getFloat(tempOffset + 8);
			return out;
		}

		@Override
		public void set(Struct struct, Vector3 value) {
			Buffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset;
			buffer.setFloat(tempOffset, value.x);
			buffer.setFloat(tempOffset + 4, value.y);
			buffer.setFloat(tempOffset + 8, value.z);
		}
	}

	public static class Vector3ArrayStructProperty extends ObjectArrayStructProperty<Vector3> {
		private final Vector3 shared = new Vector3();

		public Vector3ArrayStructProperty(int length) {
			super(3 * word, length);
		}

		@Override
		public Vector3 get(Struct struct, int index) {
			Buffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset + 12 * index;
			shared.x = buffer.getFloat(tempOffset);
			shared.y = buffer.getFloat(tempOffset + 4);
			shared.z = buffer.getFloat(tempOffset + 8);
			return shared;
		}

		@Override
		public Vector3 get(Struct struct, int index, Vector3 out) {
			Buffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset + 12 * index;
			out.x = buffer.getFloat(tempOffset);
			out.y = buffer.getFloat(tempOffset + 4);
			out.z = buffer.getFloat(tempOffset + 8);
			return out;
		}

		@Override
		public void set(Struct struct, int index, Vector3 value) {
			Buffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset + 12 * index;
			buffer.setFloat(tempOffset, value.x);
			buffer.setFloat(tempOffset + 4, value.y);
			buffer.setFloat(tempOffset + 8, value.z);
		}

		@Override
		protected String itemToString(Struct struct, int index) {
			return get(struct, index).toString();
		}
	}

	public static class GridPoint2StructProperty extends ObjectStructProperty<GridPoint2> {
		private final GridPoint2 shared = new GridPoint2();

		public GridPoint2StructProperty() {
			super(2 * word);
		}

		@Override
		public GridPoint2 get(Struct struct) {
			Buffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset;
			shared.x = buffer.getInt(tempOffset);
			shared.y = buffer.getInt(tempOffset + 4);
			return shared;
		}

		@Override
		public GridPoint2 get(Struct struct, GridPoint2 out) {
			Buffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset;
			out.x = buffer.getInt(tempOffset);
			out.y = buffer.getInt(tempOffset + 4);
			return out;
		}

		@Override
		public void set(Struct struct, GridPoint2 value) {
			Buffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset;
			buffer.setInt(tempOffset, value.x);
			buffer.setInt(tempOffset + 4, value.y);
		}
	}

	public static class GridPoint2ArrayStructProperty extends ObjectArrayStructProperty<GridPoint2> {
		private final GridPoint2 shared = new GridPoint2();

		public GridPoint2ArrayStructProperty(int length) {
			super(2 * word, length);
		}

		@Override
		public GridPoint2 get(Struct struct, int index) {
			Buffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset + 8 * index;
			shared.x = buffer.getInt(tempOffset);
			shared.y = buffer.getInt(tempOffset + word);
			return shared;
		}

		@Override
		public GridPoint2 get(Struct struct, int index, GridPoint2 out) {
			Buffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset + 8 * index;
			out.x = buffer.getInt(tempOffset);
			out.y = buffer.getInt(tempOffset + word);
			return out;
		}

		@Override
		public void set(Struct struct, int index, GridPoint2 value) {
			Buffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset + 8 * index;
			buffer.setInt(tempOffset, value.x);
			buffer.setInt(tempOffset + word, value.y);
		}

		@Override
		protected String itemToString(Struct struct, int index) {
			return get(struct, index).toString();
		}
	}

	public static class GridPoint3StructProperty extends ObjectStructProperty<GridPoint3> {
		private final GridPoint3 shared = new GridPoint3();

		public GridPoint3StructProperty() {
			super(12);
		}

		@Override
		public GridPoint3 get(Struct struct) {
			Buffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset;
			shared.x = buffer.getInt(tempOffset);
			shared.y = buffer.getInt(tempOffset + 4);
			shared.z = buffer.getInt(tempOffset + 8);
			return shared;
		}

		@Override
		public GridPoint3 get(Struct struct, GridPoint3 out) {
			Buffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset;
			out.x = buffer.getInt(tempOffset);
			out.y = buffer.getInt(tempOffset + 4);
			out.z = buffer.getInt(tempOffset + 8);
			return out;
		}

		@Override
		public void set(Struct struct, GridPoint3 value) {
			Buffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset;
			buffer.setInt(tempOffset, value.x);
			buffer.setInt(tempOffset + 4, value.y);
			buffer.setInt(tempOffset + 8, value.z);
		}
	}

	public static class GridPoint3ArrayStructProperty extends ObjectArrayStructProperty<GridPoint3> {
		private final GridPoint3 shared = new GridPoint3();

		public GridPoint3ArrayStructProperty(int length) {
			super(12, length);
		}

		@Override
		public GridPoint3 get(Struct struct, int index) {
			Buffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset + 12 * index;
			shared.x = buffer.getInt(tempOffset);
			shared.y = buffer.getInt(tempOffset + 4);
			shared.z = buffer.getInt(tempOffset + 8);
			return shared;
		}

		@Override
		public GridPoint3 get(Struct struct, int index, GridPoint3 out) {
			Buffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset + 12 * index;
			out.x = buffer.getInt(tempOffset);
			out.y = buffer.getInt(tempOffset + 4);
			out.z = buffer.getInt(tempOffset + 8);
			return out;
		}

		@Override
		public void set(Struct struct, int index, GridPoint3 value) {
			Buffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset + 12 * index;
			buffer.setInt(tempOffset, value.x);
			buffer.setInt(tempOffset + 4, value.y);
			buffer.setInt(tempOffset + 8, value.z);
		}

		@Override
		protected String itemToString(Struct struct, int index) {
			return get(struct, index).toString();
		}
	}

	public static class QuaternionStructProperty extends ObjectStructProperty<Quaternion> {
		private final Quaternion shared = new Quaternion();

		public QuaternionStructProperty() {
			super(16);
		}

		@Override
		public Quaternion get(Struct struct) {
			Buffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset;
			shared.x = buffer.getFloat(tempOffset);
			shared.y = buffer.getFloat(tempOffset + 4);
			shared.z = buffer.getFloat(tempOffset + 8);
			shared.w = buffer.getFloat(tempOffset + 12);
			return shared;
		}

		@Override
		public Quaternion get(Struct struct, Quaternion out) {
			Buffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset;
			out.x = buffer.getFloat(tempOffset);
			out.y = buffer.getFloat(tempOffset + 4);
			out.z = buffer.getFloat(tempOffset + 8);
			out.w = buffer.getFloat(tempOffset + 12);
			return out;
		}

		@Override
		public void set(Struct struct, Quaternion value) {
			Buffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset;
			buffer.setFloat(tempOffset, value.x);
			buffer.setFloat(tempOffset + 4, value.y);
			buffer.setFloat(tempOffset + 8, value.z);
			buffer.setFloat(tempOffset + 12, value.w);
		}
	}

	public static class QuaternionArrayStructProperty extends ObjectArrayStructProperty<Quaternion> {
		private final Quaternion shared = new Quaternion();

		public QuaternionArrayStructProperty(int length) {
			super(16, length);
		}

		@Override
		public Quaternion get(Struct struct, int index) {
			Buffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset + 16 * index;
			shared.x = buffer.getFloat(tempOffset);
			shared.y = buffer.getFloat(tempOffset + 4);
			shared.z = buffer.getFloat(tempOffset + 8);
			shared.w = buffer.getFloat(tempOffset + 12);
			return shared;
		}

		@Override
		public Quaternion get(Struct struct, int index, Quaternion out) {
			Buffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset + 16 * index;
			out.x = buffer.getFloat(tempOffset);
			out.y = buffer.getFloat(tempOffset + 4);
			out.z = buffer.getFloat(tempOffset + 8);
			out.w = buffer.getFloat(tempOffset + 12);
			return out;
		}

		@Override
		public void set(Struct struct, int index, Quaternion value) {
			Buffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset + 16 * index;
			buffer.setFloat(tempOffset, value.x);
			buffer.setFloat(tempOffset + 4, value.y);
			buffer.setFloat(tempOffset + 8, value.z);
			buffer.setFloat(tempOffset + 12, value.w);
		}

		@Override
		protected String itemToString(Struct struct, int index) {
			return get(struct, index).toString();
		}
	}

	public static class Matrix3StructProperty extends ObjectStructProperty<Matrix3> {
		private final Matrix3 shared = new Matrix3();

		public Matrix3StructProperty() {
			super(36);
		}

		@Override
		public Matrix3 get(Struct struct) {
			Buffer buffer = struct.buffer;
			buffer.getFloatArray(struct.offset + offset, shared.val);
			return shared;
		}

		@Override
		public Matrix3 get(Struct struct, Matrix3 out) {
			Buffer buffer = struct.buffer;
			buffer.getFloatArray(struct.offset + offset, out.val);
			return out;
		}

		@Override
		public void set(Struct struct, Matrix3 value) {
			Buffer buffer = struct.buffer;
			buffer.setFloatArray(struct.offset + offset, value.val);
		}
	}

	public static class Matrix3ArrayStructProperty extends ObjectArrayStructProperty<Matrix3> {
		private final Matrix3 shared = new Matrix3();

		public Matrix3ArrayStructProperty(int length) {
			super(36, length);
		}

		@Override
		public Matrix3 get(Struct struct, int index) {
			Buffer buffer = struct.buffer;
			buffer.getFloatArray(struct.offset + offset + 36 * index, shared.val);
			return shared;
		}

		@Override
		public Matrix3 get(Struct struct, int index, Matrix3 out) {
			Buffer buffer = struct.buffer;
			buffer.getFloatArray(struct.offset + offset + 36 * index, out.val);
			return out;
		}

		@Override
		public void set(Struct struct, int index, Matrix3 value) {
			Buffer buffer = struct.buffer;
			buffer.setFloatArray(struct.offset + offset + 36 * index, value.val);
		}

		@Override
		protected String itemToString(Struct struct, int index) {
			return get(struct, index).toString();
		}
	}

	public static class Matrix4StructProperty extends ObjectStructProperty<Matrix4> {
		private final Matrix4 shared = new Matrix4();

		public Matrix4StructProperty() {
			super(64);
		}

		@Override
		public Matrix4 get(Struct struct) {
			Buffer buffer = struct.buffer;
			buffer.getFloatArray(struct.offset + offset, shared.val);
			return shared;
		}

		@Override
		public Matrix4 get(Struct struct, Matrix4 out) {
			Buffer buffer = struct.buffer;
			buffer.getFloatArray(struct.offset + offset, out.val);
			return out;
		}

		@Override
		public void set(Struct struct, Matrix4 value) {
			Buffer buffer = struct.buffer;
			buffer.setFloatArray(struct.offset + offset, value.val);
		}
	}

	public static class Matrix4ArrayStructProperty extends ObjectArrayStructProperty<Matrix4> {
		private final Matrix4 shared = new Matrix4();

		public Matrix4ArrayStructProperty(int length) {
			super(64, length);
		}

		@Override
		public Matrix4 get(Struct struct, int index) {
			Buffer buffer = struct.buffer;
			buffer.getFloatArray(struct.offset + offset + 64 * index, shared.val);
			return shared;
		}

		@Override
		public Matrix4 get(Struct struct, int index, Matrix4 out) {
			Buffer buffer = struct.buffer;
			buffer.getFloatArray(struct.offset + offset + 64 * index, out.val);
			return out;
		}

		@Override
		public void set(Struct struct, int index, Matrix4 value) {
			Buffer buffer = struct.buffer;
			buffer.setFloatArray(struct.offset + offset + 64 * index, value.val);
		}

		@Override
		protected String itemToString(Struct struct, int index) {
			return get(struct, index).toString();
		}
	}

	public static class BoundingBoxStructProperty extends ObjectStructProperty<BoundingBox> {
		private final BoundingBox shared = new BoundingBox();

		public BoundingBoxStructProperty() {
			super(24);
		}

		@Override
		public BoundingBox get(Struct struct) {
			Buffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset;
			shared.min.x = buffer.getFloat(tempOffset);
			shared.min.y = buffer.getFloat(tempOffset + 4);
			shared.min.z = buffer.getFloat(tempOffset + 8);
			shared.max.x = buffer.getFloat(tempOffset + 12);
			shared.max.y = buffer.getFloat(tempOffset + 16);
			shared.max.z = buffer.getFloat(tempOffset + 20);
			return shared;
		}

		@Override
		public BoundingBox get(Struct struct, BoundingBox out) {
			Buffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset;
			out.min.x = buffer.getFloat(tempOffset);
			out.min.y = buffer.getFloat(tempOffset + 4);
			out.min.z = buffer.getFloat(tempOffset + 8);
			out.max.x = buffer.getFloat(tempOffset + 12);
			out.max.y = buffer.getFloat(tempOffset + 16);
			out.max.z = buffer.getFloat(tempOffset + 20);
			return out;
		}

		@Override
		public void set(Struct struct, BoundingBox value) {
			Buffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset;
			buffer.setFloat(tempOffset, value.min.x);
			buffer.setFloat(tempOffset + 4, value.min.y);
			buffer.setFloat(tempOffset + 8, value.min.z);
			buffer.setFloat(tempOffset + 12, value.max.x);
			buffer.setFloat(tempOffset + 16, value.max.y);
			buffer.setFloat(tempOffset + 20, value.max.z);
		}
	}

	public static class RectangleStructProperty extends ObjectStructProperty<Rectangle> {
		private final Rectangle shared = new Rectangle();

		public RectangleStructProperty() {
			super(16);
		}

		@Override
		public Rectangle get(Struct struct) {
			Buffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset;
			shared.x = buffer.getFloat(tempOffset);
			shared.y = buffer.getFloat(tempOffset + 4);
			shared.width = buffer.getFloat(tempOffset + 8);
			shared.height = buffer.getFloat(tempOffset + 12);
			return shared;
		}

		@Override
		public Rectangle get(Struct struct, Rectangle out) {
			Buffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset;
			out.x = buffer.getFloat(tempOffset);
			out.y = buffer.getFloat(tempOffset + 4);
			out.width = buffer.getFloat(tempOffset + 8);
			out.height = buffer.getFloat(tempOffset + 12);
			return out;
		}

		@Override
		public void set(Struct struct, Rectangle value) {
			Buffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset;
			buffer.setFloat(tempOffset, value.x);
			buffer.setFloat(tempOffset + 4, value.y);
			buffer.setFloat(tempOffset + 8, value.width);
			buffer.setFloat(tempOffset + 12, value.height);
		}
	}

	public static class GridRectangleStructProperty extends ObjectStructProperty<GridRectangle> {
		private final GridRectangle shared = new GridRectangle();

		public GridRectangleStructProperty() {
			super(16);
		}

		@Override
		public GridRectangle get(Struct struct) {
			Buffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset;
			shared.x = buffer.getInt(tempOffset);
			shared.y = buffer.getInt(tempOffset + 4);
			shared.width = buffer.getInt(tempOffset + 8);
			shared.height = buffer.getInt(tempOffset + 12);
			return shared;
		}

		@Override
		public GridRectangle get(Struct struct, GridRectangle out) {
			Buffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset;
			out.x = buffer.getInt(tempOffset);
			out.y = buffer.getInt(tempOffset + 4);
			out.width = buffer.getInt(tempOffset + 8);
			out.height = buffer.getInt(tempOffset + 12);
			return out;
		}

		@Override
		public void set(Struct struct, GridRectangle value) {
			Buffer buffer = struct.buffer;
			int tempOffset = struct.offset + offset;
			buffer.setInt(tempOffset, value.x);
			buffer.setInt(tempOffset + 4, value.y);
			buffer.setInt(tempOffset + 8, value.width);
			buffer.setInt(tempOffset + 12, value.height);
		}
	}
}
