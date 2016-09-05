package com.gurella.engine.utils;

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

	private static class TestClass {
		Vector3 vector = new Vector3(Double.valueOf(Math.random()).intValue(), Double.valueOf(Math.random()).intValue(),
				Double.valueOf(Math.random()).intValue());
	}

	public static void main(String[] args) {
		int size = 10000000;

		TestClass[] tc = new TestClass[size];
		StructArray sa = new StructArray(3, size);
		for (int i = 0; i < size * 3; i++) {
			sa.buffer[i] = Double.valueOf(Math.random()).intValue();
		}
	}

	private static void testTc(TestClass[] tc) {

	}

	private static void testSa(StructArray sa) {

	}
}
