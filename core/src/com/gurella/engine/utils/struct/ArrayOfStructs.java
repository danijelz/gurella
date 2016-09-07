package com.gurella.engine.utils.struct;

import static com.badlogic.gdx.utils.NumberUtils.floatToRawIntBits;
import static java.lang.Double.doubleToRawLongBits;
import static java.lang.Double.longBitsToDouble;
import static java.lang.Float.intBitsToFloat;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class ArrayOfStructs {
	public int structSize;
	public float[] buffer;
	public int offset;
	public int size;

	public ArrayOfStructs(int structSize, int capacity) {
		this.structSize = structSize;
		this.buffer = new float[structSize * capacity];
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public void next() {
		offset++;
	}

	public void rewind() {
		offset = 0;
	}

	public int getItemOffset(int itemIndex) {
		return itemIndex * structSize;
	}

	public void setIndex(int index) {
		offset = index * structSize;
	}

	public int getIndex() {
		return offset / structSize;
	}

	public int getWordIndex() {
		return offset % structSize;
	}

	public void setWordIndex(int wordIndex) {
		offset = offset / structSize + wordIndex;
	}

	public int getCapacity() {
		return buffer.length / structSize;
	}

	public int getSize() {
		return size;
	}

	public void remove(int index) {
		float[] buffer = this.buffer;
		int lastItemOffset = size * structSize;
		int removedItemOffset = index * structSize;
		System.arraycopy(buffer, lastItemOffset, buffer, removedItemOffset, structSize);
		size--;
	}

	public void removeOrdered(int index) {
		float[] buffer = this.buffer;
		int removedItemOffset = index * structSize;
		int lastItemOffset = removedItemOffset + structSize;
		int length = (size - index -1) * structSize;
		System.arraycopy(buffer, lastItemOffset, buffer, removedItemOffset, length);
		size--;
	}

	public void remove(int index, int count) {
		float[] buffer = this.buffer;
		int lastItemOffset = (size - count) * structSize;
		int removedItemOffset = index * structSize;
		System.arraycopy(buffer, lastItemOffset, buffer, removedItemOffset, structSize);
		size -= count;
	}

	public void removeOrdered(int index, int count) {
		float[] buffer = this.buffer;
		int removedItemOffset = index * structSize;
		int lastItemOffset = removedItemOffset + (structSize * count);
		int length = (size - index + - + count) * structSize;
		System.arraycopy(buffer, lastItemOffset, buffer, removedItemOffset, length);
		size -= count;
	}

	public void insert(int index) {
		float[] buffer = this.buffer;
		int addedItemOffset = index * structSize;
		int length = (size - index - 1) * structSize;
		System.arraycopy(buffer, addedItemOffset, buffer, addedItemOffset + structSize, length);
		size++;
	}

	public void insert(int index, int count) {
		// TODO
		size += count;
	}

	public void insertSafely(int index) {
		resizeIfNeeded(size + 1);
		// TODO
		size++;
	}

	public void insertSafely(int index, int count) {
		resizeIfNeeded(size + count);
		// TODO
		size += count;
	}

	public void add() {
		size++;
	}

	public void add(int count) {
		size += count;
	}

	public void addSafely() {
		resizeIfNeeded(size + 1);
		size++;
	}

	public void addSafely(int count) {
		resizeIfNeeded(size + count);
		size += count;
	}

	private void resizeIfNeeded(int newSize) {
		int capacity = buffer.length / structSize;
		if (capacity < newSize) {
			resize(Math.max(8, (int) (newSize * 1.75f)));
		}
	}

	public void resize(int newSize) {
		float[] buffer = this.buffer;
		float[] newBuffer = new float[newSize * structSize];
		System.arraycopy(buffer, 0, newBuffer, 0, Math.min(size, newSize));
		this.buffer = newBuffer;
		size = Math.min(size, newSize);
	}

	public void swap(int fromIndex, int toIndex) {
		float[] buffer = this.buffer;
		int fromOffset = fromIndex * structSize;
		int toOffset = toIndex * structSize;
		for (int i = 0, n = structSize; i < n; i++) {
			buffer[fromOffset++] = buffer[toOffset++];
		}
	}

	public void swap(int fromIndex, int toIndex, float[] temp) {
		float[] buffer = this.buffer;
		int fromOffset = fromIndex * structSize;
		int toOffset = toIndex * structSize;
		getFloatArrayByOffset(temp, fromIndex, structSize);
		System.arraycopy(buffer, toOffset, buffer, fromOffset, structSize);
		setFloatArrayByOffset(toOffset, temp, structSize);
	}

	public void pop() {
		size = Math.max(0, size - 1);
	}

	public void clear() {
		size = 0;
	}

	public void shrink() {
		resize(size);
	}

	public void truncate(int newSize) {
		int capacity = buffer.length / structSize;
		if (capacity <= newSize) {
			resize(newSize);
		}
	}

	public void ensureCapacity(int additionalCapacity) {
		int sizeNeeded = size + additionalCapacity;
		int capacity = buffer.length / structSize;
		if (sizeNeeded > capacity) {
			resize(Math.max(8, sizeNeeded));
		}
	}

	public void sort(StructComparator comparator) {
		// TODO
	}

	public void sortRange(StructComparator comparator, int fromIndex, int count) {
		// TODO
	}

	public void forEach(StructConsumer action) {
		for (int i = 0, n = size; i < n; i++) {
			action.accept(this, i);
		}
	}

	public void forEach(StructConsumer action, int fromIndex) {
		for (int i = fromIndex, n = size; i < n; i++) {
			action.accept(this, i);
		}
	}

	public void forEach(StructConsumer action, int fromIndex, int count) {
		for (int i = fromIndex, n = count; i < n && i < size; i++) {
			action.accept(this, i);
		}
	}

	//////// float

	public float getFloatByIndex(int structIndex, int propertyIndex) {
		return buffer[structIndex * structSize + propertyIndex];
	}

	public void setFloatByIndex(int structIndex, int propertyIndex, float value) {
		buffer[structIndex * structSize + propertyIndex] = value;
	}

	public float getFloatByOffset(int structOffset, int propertyIndex) {
		return buffer[structOffset + propertyIndex];
	}

	public void setFloatByStructOffset(int structOffset, int propertyIndex, float value) {
		buffer[structOffset + propertyIndex] = value;
	}

	public float getFloat(int offset) {
		return buffer[offset];
	}

	public void setFloat(int offset, float value) {
		buffer[offset] = value;
	}

	public float getFloat() {
		return buffer[offset++];
	}

	public void setFloat(float value) {
		buffer[offset++] = value;
	}

	/////////// int

	public int getIntByIndex(int structIndex, int propertyIndex) {
		return floatToRawIntBits(buffer[structIndex * structSize + propertyIndex]);
	}

	public void setIntByIndex(int structIndex, int propertyIndex, int value) {
		buffer[structIndex * structSize + propertyIndex] = intBitsToFloat(value);
	}

	public int getIntByOffset(int structOffset, int propertyIndex) {
		return floatToRawIntBits(buffer[structOffset + propertyIndex]);
	}

	public void setIntByStructOffset(int structOffset, int propertyIndex, int value) {
		buffer[structOffset + propertyIndex] = intBitsToFloat(value);
	}

	public int getInt(int offset) {
		return floatToRawIntBits(buffer[offset]);
	}

	public void setInt(int offset, int value) {
		buffer[offset] = intBitsToFloat(value);
	}

	public int getInt() {
		return floatToRawIntBits(buffer[offset++]);
	}

	public void setInt(int value) {
		buffer[offset++] = intBitsToFloat(value);
	}

	////////// long

	public long getLongByIndex(int structIndex, int propertyIndex) {
		float[] buffer = this.buffer;
		int offset = structIndex * structSize + propertyIndex;
		return (long) floatToRawIntBits(buffer[offset++]) << 32 | floatToRawIntBits(buffer[offset]) & 0xFFFFFFFFL;
	}

	public void setLongByIndex(int structIndex, int propertyIndex, long value) {
		int offset = structIndex * structSize + propertyIndex;
		float[] buffer = this.buffer;
		buffer[offset++] = intBitsToFloat((int) (value >> 32));
		buffer[offset] = intBitsToFloat((int) value);
	}

	public long getLongByOffset(int structOffset, int propertyIndex) {
		int offset = structOffset + propertyIndex;
		float[] buffer = this.buffer;
		return (long) floatToRawIntBits(buffer[offset++]) << 32 | floatToRawIntBits(buffer[offset]) & 0xFFFFFFFFL;
	}

	public void setLongByStructOffset(int structOffset, int propertyIndex, long value) {
		int offset = structOffset + propertyIndex;
		float[] buffer = this.buffer;
		buffer[offset++] = intBitsToFloat((int) (value >> 32));
		buffer[offset] = intBitsToFloat((int) value);
	}

	public long getLong(int offset) {
		float[] buffer = this.buffer;
		return (long) floatToRawIntBits(buffer[offset++]) << 32 | floatToRawIntBits(buffer[offset]) & 0xFFFFFFFFL;
	}

	public void setLong(int offset, long value) {
		float[] buffer = this.buffer;
		buffer[offset++] = intBitsToFloat((int) (value >> 32));
		buffer[offset] = intBitsToFloat((int) value);
	}

	public long getLong() {
		float[] buffer = this.buffer;
		return (long) floatToRawIntBits(buffer[offset++]) << 32 | floatToRawIntBits(buffer[offset++]) & 0xFFFFFFFFL;
	}

	public void setLong(long value) {
		float[] buffer = this.buffer;
		buffer[offset++] = intBitsToFloat((int) (value >> 32));
		buffer[offset++] = intBitsToFloat((int) value);
	}

	///////// double

	public double getDouble() {
		float[] buffer = this.buffer;
		long hi = (long) floatToRawIntBits(buffer[offset++]) << 32;
		long lo = floatToRawIntBits(buffer[offset++]) & 0xFFFFFFFFL;
		return longBitsToDouble(hi | lo);
	}

	public void setDouble(double value) {
		long l = doubleToRawLongBits(value);
		float[] buffer = this.buffer;
		buffer[offset++] = intBitsToFloat((int) (l >> 32));
		buffer[offset++] = intBitsToFloat((int) l);
	}

	//////// short

	public short asShort() {
		return (short) floatToRawIntBits(buffer[offset++]);
	}

	public void setAsShort(short value) {
		buffer[offset++] = intBitsToFloat(value);
	}

	public short getShort1() {
		return (short) (floatToRawIntBits(buffer[offset]) >> 16);
	}

	public int getShort1AsInt() {
		return floatToRawIntBits(buffer[offset]) >> 16;
	}

	public void setShort1(short value) {
		float[] buffer = this.buffer;
		int i = floatToRawIntBits(buffer[offset]) & 0x0000FFFF;
		buffer[offset] = intBitsToFloat(i | (value << 16));
	}

	public void setShort1FromInt(int value) {
		float[] buffer = this.buffer;
		int i = floatToRawIntBits(buffer[offset]) & 0x0000FFFF;
		buffer[offset] = intBitsToFloat(i | (value << 16));
	}

	public short getShort2() {
		return (short) floatToRawIntBits(buffer[offset++]);
	}

	public int getShort2AsInt() {
		return floatToRawIntBits(buffer[offset++]) & 0xFFFF0000;
	}

	public void setShort2(short value) {
		float[] buffer = this.buffer;
		int i = floatToRawIntBits(buffer[offset]) & 0xFFFF0000;
		buffer[offset++] = intBitsToFloat(i | value);
	}

	public void setShort2FromInt(int value) {
		float[] buffer = this.buffer;
		int i = floatToRawIntBits(buffer[offset]) & 0xFFFF0000;
		buffer[offset++] = intBitsToFloat(i | (value | 0xFFFF0000));
	}

	//////// byte

	public byte asByte() {
		return (byte) floatToRawIntBits(buffer[offset++]);
	}

	public void setAsByte(byte value) {
		buffer[offset++] = intBitsToFloat(value);
	}

	public byte getByte1() {
		return (byte) (floatToRawIntBits(buffer[offset]) >> 24);
	}

	public void setByte1(byte value) {
		float[] buffer = this.buffer;
		int i = floatToRawIntBits(buffer[offset]) & 0x00FFFFFF;
		buffer[offset] = intBitsToFloat(i | (value << 24));
	}

	public byte getByte2() {
		return (byte) (floatToRawIntBits(buffer[offset]) >> 24);
	}

	public void setByte2(byte value) {
		float[] buffer = this.buffer;
		int i = floatToRawIntBits(buffer[offset]) & 0xFF00FFFF;
		buffer[offset] = intBitsToFloat(i | (value << 16));
	}

	public byte getByte3() {
		return (byte) (floatToRawIntBits(buffer[offset]) >> 16);
	}

	public void setByte3(byte value) {
		float[] buffer = this.buffer;
		int i = floatToRawIntBits(buffer[offset]) & 0xFFFF00FF;
		buffer[offset] = intBitsToFloat(i | (value << 8));
	}

	public byte getByte4() {
		return (byte) (floatToRawIntBits(buffer[offset++]) >> 8);
	}

	public void setByte4(byte value) {
		float[] buffer = this.buffer;
		int i = floatToRawIntBits(buffer[offset]) & 0xFFFFFF00;
		buffer[offset++] = intBitsToFloat(i | value);
	}

	//////// flag

	public boolean getFlag(int flag) {
		return (floatToRawIntBits(buffer[offset]) & (1 << flag)) != 0;
	}

	public void setFlag(int flag) {
		float[] buffer = this.buffer;
		int value = floatToRawIntBits(buffer[offset]);
		buffer[offset] = intBitsToFloat(value | (1 << flag));
	}

	public void unsetFlag(int flag) {
		float[] buffer = this.buffer;
		int value = floatToRawIntBits(buffer[offset]);
		buffer[offset] = intBitsToFloat(value & ~(1 << flag));
	}

	/////// float[]

	public float[] getFloatArray(float[] arrOut, int length) {
		System.arraycopy(buffer, offset, arrOut, 0, length);
		return arrOut;
	}

	public float[] getFloatArray(float[] arrOut, int destinationOffset, int length) {
		System.arraycopy(buffer, offset, arrOut, destinationOffset, length);
		return arrOut;
	}

	public float[] getFloatArrayByOffset(float[] arrOut, int offset, int length) {
		System.arraycopy(buffer, offset, arrOut, 0, length);
		return arrOut;
	}

	public float[] getFloatArrayByOffset(float[] arrOut, int offset, int destinationOffset, int length) {
		System.arraycopy(buffer, offset, arrOut, destinationOffset, length);
		return arrOut;
	}

	public void setFloatArray(float[] arr, int length) {
		System.arraycopy(arr, 0, buffer, offset, length);
	}

	public void setFloatArray(float[] arr, int sourceOffset, int length) {
		System.arraycopy(arr, sourceOffset, buffer, offset, length);
	}

	public void setFloatArrayByOffset(int offset, float[] arr, int length) {
		System.arraycopy(arr, 0, buffer, offset, length);
	}

	public void setFloatArrayByOffset(int offset, float[] arr, int sourceOffset, int length) {
		System.arraycopy(arr, sourceOffset, buffer, offset, length);
	}

	/////// Vector2

	public Vector2 getVector2(Vector2 out) {
		float[] buffer = this.buffer;
		return out.set(buffer[offset++], buffer[offset++]);
	}

	public void setVector2(Vector2 value) {
		float[] buffer = this.buffer;
		buffer[offset++] = value.x;
		buffer[offset++] = value.y;
	}

	public Vector2 getVector2(int offset, Vector2 out) {
		float[] buffer = this.buffer;
		return out.set(buffer[offset++], buffer[offset]);
	}

	public void setVector2(int offset, Vector2 value) {
		float[] buffer = this.buffer;
		buffer[offset++] = value.x;
		buffer[offset] = value.y;
	}

	/////// Vector3

	public Vector3 getVector3(Vector3 out) {
		float[] buffer = this.buffer;
		return out.set(buffer[offset++], buffer[offset++], buffer[offset++]);
	}

	public void setVector3(Vector3 value) {
		float[] buffer = this.buffer;
		buffer[offset++] = value.x;
		buffer[offset++] = value.y;
		buffer[offset++] = value.z;
	}

	public Vector3 getVector3(int offset, Vector3 out) {
		float[] buffer = this.buffer;
		return out.set(buffer[offset++], buffer[offset++], buffer[offset]);
	}

	public void setVector3(int offset, Vector3 value) {
		float[] buffer = this.buffer;
		buffer[offset++] = value.x;
		buffer[offset++] = value.y;
		buffer[offset] = value.z;
	}

	/////// GridPoint2

	public GridPoint2 getGridPoint2(GridPoint2 out) {
		float[] buffer = this.buffer;
		return out.set(floatToRawIntBits(buffer[offset++]), floatToRawIntBits(buffer[offset++]));
	}

	public void setGridPoint2(GridPoint2 value) {
		float[] buffer = this.buffer;
		buffer[offset++] = intBitsToFloat(value.x);
		buffer[offset++] = intBitsToFloat(value.y);
	}

	public GridPoint2 getGridPoint2(int offset, GridPoint2 out) {
		float[] buffer = this.buffer;
		return out.set(floatToRawIntBits(buffer[offset++]), floatToRawIntBits(buffer[offset]));
	}

	public void setGridPoint2(int offset, GridPoint2 value) {
		float[] buffer = this.buffer;
		buffer[offset++] = intBitsToFloat(value.x);
		buffer[offset] = intBitsToFloat(value.y);
	}

	/////// GridPoint3

	public GridPoint3 getGridPoint3(GridPoint3 out) {
		float[] buffer = this.buffer;
		return out.set(floatToRawIntBits(buffer[offset++]), floatToRawIntBits(buffer[offset++]),
				floatToRawIntBits(buffer[offset++]));
	}

	public void setGridPoint3(GridPoint3 value) {
		float[] buffer = this.buffer;
		buffer[offset++] = intBitsToFloat(value.x);
		buffer[offset++] = intBitsToFloat(value.y);
		buffer[offset++] = intBitsToFloat(value.z);
	}

	public GridPoint3 getGridPoint3(int offset, GridPoint3 out) {
		float[] buffer = this.buffer;
		return out.set(floatToRawIntBits(buffer[offset++]), floatToRawIntBits(buffer[offset++]),
				floatToRawIntBits(buffer[offset]));
	}

	public void setGridPoint3(int offset, GridPoint3 value) {
		float[] buffer = this.buffer;
		buffer[offset++] = intBitsToFloat(value.x);
		buffer[offset++] = intBitsToFloat(value.y);
		buffer[offset] = intBitsToFloat(value.z);
	}

	/////// Quaternion

	public Quaternion getQuaternion(Quaternion out) {
		float[] buffer = this.buffer;
		return out.set(buffer[offset++], buffer[offset++], buffer[offset++], buffer[offset++]);
	}

	public void setQuaternion(Quaternion value) {
		float[] buffer = this.buffer;
		buffer[offset++] = value.x;
		buffer[offset++] = value.y;
		buffer[offset++] = value.z;
		buffer[offset++] = value.w;
	}

	public Quaternion getQuaternion(int offset, Quaternion out) {
		float[] buffer = this.buffer;
		return out.set(buffer[offset++], buffer[offset++], buffer[offset++], buffer[offset]);
	}

	public void setQuaternion(int offset, Quaternion value) {
		float[] buffer = this.buffer;
		buffer[offset++] = value.x;
		buffer[offset++] = value.y;
		buffer[offset++] = value.z;
		buffer[offset] = value.w;
	}

	/////// Matrix3

	public Matrix3 getMatrix3(Matrix3 out) {
		System.arraycopy(out.val, 0, buffer, offset, 9);
		offset += 9;
		return out;
	}

	public void setMatrix3(Matrix3 value) {
		System.arraycopy(buffer, offset, value.val, 0, 9);
		offset += 9;
	}

	public Matrix3 getMatrix3(int offset, Matrix3 out) {
		System.arraycopy(out.val, 0, buffer, offset, 9);
		return out;
	}

	public void setMatrix3(int offset, Matrix3 value) {
		System.arraycopy(buffer, offset, value.val, 0, 9);
	}

	/////// Matri4

	public Matrix4 getMatrix4(Matrix4 out) {
		System.arraycopy(out.val, 0, buffer, offset, 16);
		offset += 9;
		return out;
	}

	public void setMatrix4(Matrix4 value) {
		System.arraycopy(buffer, offset, value.val, 0, 16);
		offset += 9;
	}

	public Matrix4 getMatrix4(int offset, Matrix4 out) {
		System.arraycopy(out.val, 0, buffer, offset, 16);
		return out;
	}

	public void setMatrix4(int offset, Matrix4 value) {
		System.arraycopy(buffer, offset, value.val, 0, 16);
	}

	/////// BoundingBox

	public BoundingBox getBoundingBox(BoundingBox out) {
		getVector3(out.min);
		getVector3(out.max);
		return out;
	}

	public void setBoundingBox(BoundingBox value) {
		setVector3(value.min);
		setVector3(value.max);
	}

	public BoundingBox getBoundingBox(int offset, BoundingBox out) {
		getVector3(offset, out.min);
		getVector3(offset, out.max);
		return out;
	}

	public void setBoundingBox(int offset, BoundingBox value) {
		setVector3(offset, value.min);
		setVector3(offset, value.max);
	}

	public interface StructComparator {
		int compare(float[] buffer, int firstIndex, int secondIndex);
	}

	public interface StructConsumer {
		int accept(ArrayOfStructs buffer, int index);
	}

	///////////////////////////////////////////

	private static class TestClass {
		Vector3 vector = new Vector3(Double.valueOf(Math.random()).floatValue(),
				Double.valueOf(Math.random()).floatValue(), Double.valueOf(Math.random()).floatValue());
		GridPoint3 point = new GridPoint3(Double.valueOf(Math.random() * Integer.MAX_VALUE).intValue(),
				Double.valueOf(Math.random() * Integer.MAX_VALUE).intValue(),
				Double.valueOf(Math.random() * Integer.MAX_VALUE).intValue());
		int next = rand();
	}

	static int testSize = 9000000;
	static int testStructSize = 7;
	static int iterations = 1000;
	static int subIterations = testSize / iterations;

	public static void main(String[] args) {
		ArrayOfStructs t = new ArrayOfStructs(1, 2);
		t.setShort1((short) 1);
		t.setShort2((short) 1);
		t.offset = 0;
		short short1 = t.getShort1();
		short short2 = t.getShort2();

		if (short1 == short2) {
			System.out.println("short");
		}

		t.setIndex(1);
		t.setByte1((byte) 1);
		t.setByte2((byte) 1);
		t.setByte3((byte) 1);
		t.setByte4((byte) 1);

		t.setIndex(1);
		byte byte1 = t.getByte1();
		byte byte2 = t.getByte2();
		byte byte3 = t.getByte3();
		byte byte4 = t.getByte4();

		if (byte1 == byte2 && byte1 == byte3 && byte1 == byte4) {
			System.out.println("byte");
		}

		t.setIndex(1);
		t.setInt(0, 0);
		t.setFlag(15);
		t.setFlag(21);
		t.setFlag(29);

		if (t.getFlag(15) && t.getFlag(21) && t.getFlag(29)) {
			System.out.println("setFlag");
		}

		t.unsetFlag(15);
		t.unsetFlag(21);
		t.unsetFlag(29);

		if (!t.getFlag(15) && !t.getFlag(21) && !t.getFlag(29)) {
			System.out.println("unsetFlag");
		}

		////////////////////

		TestClass[] tc = new TestClass[testSize];
		for (int i = 0; i < testSize; i++) {
			tc[i] = new TestClass();
		}
		System.out.println(1);

		ArrayOfStructs sa = new ArrayOfStructs(testStructSize, testSize);
		int off = 0;
		for (int i = 0; i < testSize; i++) {
			TestClass testClass = tc[i];
			sa.buffer[off++] = testClass.vector.x;
			sa.buffer[off++] = testClass.vector.y;
			sa.buffer[off++] = testClass.vector.z;
			sa.buffer[off++] = intBitsToFloat(testClass.point.x);
			sa.buffer[off++] = intBitsToFloat(testClass.point.y);
			sa.buffer[off++] = intBitsToFloat(testClass.point.z);
			sa.buffer[off++] = intBitsToFloat(testClass.next);
		}
		System.out.println(2);
		System.out.println("");

		/*
		 * for (int i = 0; i < size; i++) { TestClass testClass = tc[i]; off = i * testStructSize; if (sa.buffer[off++]
		 * != testClass.vector.x || sa.buffer[off++] != testClass.vector.y || sa.buffer[off++] != testClass.vector.z ||
		 * sa.buffer[off++] != Float.intBitsToFloat(testClass.point.x) || sa.buffer[off++] !=
		 * Float.intBitsToFloat(testClass.point.y) || sa.buffer[off++] != Float.intBitsToFloat(testClass.point.z) ||
		 * sa.buffer[off++] != Float.intBitsToFloat(testClass.next)) { System.out.println("ddd"); }
		 * 
		 * off = i * testStructSize; if (sa.buffer[off++] != testClass.vector.x || sa.buffer[off++] !=
		 * testClass.vector.y || sa.buffer[off++] != testClass.vector.z || Float.floatToIntBits(sa.buffer[off++]) !=
		 * testClass.point.x || Float.floatToIntBits(sa.buffer[off++]) != testClass.point.y ||
		 * Float.floatToIntBits(sa.buffer[off++]) != testClass.point.z || Float.floatToIntBits(sa.buffer[off++]) !=
		 * testClass.next) { System.out.println("ddd"); } }
		 */

		for (int j = 0; j < 20; j++) {
			for (int i = 0; i < subIterations; i++) {
				testTc(tc, i);
				testSa(sa, i);
			}
			System.out.println(tcTime);
			System.out.println(saTime);
			System.out.println("");
			tcTotalTime += tcTime;
			saTotalTime += saTime;
			tcTime = 0;
			saTime = 0;
		}

		System.out.println("");
		System.out.println("SUM:");
		System.out.println(tcTotalTime);
		System.out.println(saTotalTime);

		System.out.println("");
		System.out.println("");
		System.out.println(tcr);
		System.out.println(sar);
	}

	private static long tcTime;
	private static long tcTotalTime;
	private static double tcr;

	private static void testTc(TestClass[] tc, int index) {
		double r = 0;
		long millis = System.currentTimeMillis();

		int next = index;
		for (int i = 0; i < iterations; i++) {
			TestClass testClass = tc[next];
			Vector3 vector = testClass.vector;
			r += vector.x;
			r += vector.y;
			r += vector.z;

			GridPoint3 point = testClass.point;
			r += point.x;
			r += point.y;
			r += point.z;

			next = testClass.next;
		}

		tcr += r;
		tcTime += System.currentTimeMillis() - millis;
	}

	private static long saTime;
	private static long saTotalTime;
	private static double sar;

	private static void testSa(ArrayOfStructs sa, int index) {
		double r = 0;
		long millis = System.currentTimeMillis();

		// int next = index * testStructSize;
		sa.offset = index * testStructSize;
		for (int i = 0; i < iterations; i++) {
			// r += sa.buffer[next++];
			// r += sa.buffer[next++];
			// r += sa.buffer[next++];
			//
			// r += floatToRawIntBits(sa.buffer[next++]);
			// r += floatToRawIntBits(sa.buffer[next++]);
			// r += floatToRawIntBits(sa.buffer[next++]);
			//
			// next = floatToRawIntBits(sa.buffer[next]) * testStructSize;

			r += sa.getFloat();
			r += sa.getFloat();
			r += sa.getFloat();

			r += sa.getInt();
			r += sa.getInt();
			r += sa.getInt();

			sa.setIndex(sa.getInt());
		}

		sar += r;
		saTime += System.currentTimeMillis() - millis;
	}

	private static int rand() {
		return (int) (Math.random() * (testSize - iterations - 1));
	}
}
