package com.gurella.engine.utils;

import static com.badlogic.gdx.utils.NumberUtils.floatToRawIntBits;

import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.math.Vector3;

public class StructArray {
	public int structSize;
	public float[] buffer;
	public int offset;

	public StructArray(int structSize, int bufferSize) {
		this.structSize = structSize;
		this.buffer = new float[structSize * bufferSize];
	}

	public static class StructProperty {
		byte index;
	}

	public int getOffset(int structIndex) {
		return structIndex * structSize;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffsetByIndex(int structIndex) {
		offset = structIndex * structSize;
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

	public int getInt() {
		return floatToRawIntBits(buffer[offset++]);
	}

	public void setInt(int value) {
		buffer[offset++] = Float.intBitsToFloat(value);
	}

	public long getLong() {
		return (long) floatToRawIntBits(buffer[offset++]) << 32 | floatToRawIntBits(buffer[offset++]) & 0xFFFFFFFFL;
	}

	public void setLong(long value) {
		buffer[offset++] = Float.intBitsToFloat((int) (value >> 32));
		buffer[offset++] = Float.intBitsToFloat((int) value);
	}

	public double getDouble() {
		return Double.longBitsToDouble(
				(long) floatToRawIntBits(buffer[offset++]) << 32 | floatToRawIntBits(buffer[offset++]) & 0xFFFFFFFFL);
	}

	public void setDouble(double value) {
		long l = Double.doubleToRawLongBits(value);
		buffer[offset++] = Float.intBitsToFloat((int) (l >> 32));
		buffer[offset++] = Float.intBitsToFloat((int) l);
	}

	//////// Short

	public short asShort() {
		return (short) floatToRawIntBits(buffer[offset++]);
	}

	public void setAsShort(short value) {
		buffer[offset++] = Float.intBitsToFloat(value);
	}

	public short getShort1() {
		return (short) (floatToRawIntBits(buffer[offset]) >> 16);
	}

	public int getShort1AsInt() {
		return floatToRawIntBits(buffer[offset]) >> 16;
	}

	public void setShort1(short value) {
		int i = floatToRawIntBits(buffer[offset]) & 0x0000ffff;
		buffer[offset] = Float.intBitsToFloat(i | (value << 16));
	}

	public void setShort1FromInt(int value) {
		int i = floatToRawIntBits(buffer[offset]) & 0x0000ffff;
		buffer[offset] = Float.intBitsToFloat(i | (value << 16));
	}

	public short getShort2() {
		return (short) floatToRawIntBits(buffer[offset++]);
	}

	public int getShort2AsInt() {
		return floatToRawIntBits(buffer[offset++]) & 0xffff0000;
	}

	public void setShort2(short value) {
		int i = floatToRawIntBits(buffer[offset]) & 0xffff0000;
		buffer[offset++] = Float.intBitsToFloat(i | value);
	}

	public void setShort2FromInt(int value) {
		int i = floatToRawIntBits(buffer[offset]) & 0xffff0000;
		buffer[offset++] = Float.intBitsToFloat(i | (value | 0xffff0000));
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

	static int size = 2000000;
	static int testStructSize = 7;
	static int iterations = 1000;
	static int subIterations = size / iterations;

	public static void main(String[] args) {
		StructArray t = new StructArray(1, 2);
		t.setShort1((short) 1);
		t.setShort2((short) 1);
		t.offset = 0;
		short short1 = t.getShort1();
		short short2 = t.getShort2();

		if (short1 == short2) {
			System.out.println("short");
		}

		TestClass[] tc = new TestClass[size];
		for (int i = 0; i < size; i++) {
			tc[i] = new TestClass();
		}
		System.out.println(1);

		StructArray sa = new StructArray(testStructSize, size);
		int off = 0;
		for (int i = 0; i < size; i++) {
			TestClass testClass = tc[i];
			sa.buffer[off++] = testClass.vector.x;
			sa.buffer[off++] = testClass.vector.y;
			sa.buffer[off++] = testClass.vector.z;
			sa.buffer[off++] = Float.intBitsToFloat(testClass.point.x);
			sa.buffer[off++] = Float.intBitsToFloat(testClass.point.y);
			sa.buffer[off++] = Float.intBitsToFloat(testClass.point.z);
			sa.buffer[off++] = Float.intBitsToFloat(testClass.next);
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

	private static void testSa(StructArray sa, int index) {
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

			sa.offset = sa.getInt() * testStructSize;
		}

		sar += r;
		saTime += System.currentTimeMillis() - millis;
	}

	private static int rand() {
		return (int) (Math.random() * (size - iterations - 1));
	}
}
