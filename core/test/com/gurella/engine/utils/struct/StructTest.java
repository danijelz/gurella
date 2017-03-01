package com.gurella.engine.utils.struct;

import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.utils.struct.StructProperty.GridPoint3StructProperty;
import com.gurella.engine.utils.struct.StructProperty.Matrix4StructProperty;
import com.gurella.engine.utils.struct.StructProperty.Vector3StructProperty;

public class StructTest {
	private static TestClass[] createTestClassArray(int size) {
		TestClass[] tc = new TestClass[size];
		for (int i = 0; i < size; i++) {
			tc[i] = new TestClass();
		}

		for (int i = 0; i < size; i++) {
			int index = randomIndex(size);
			TestClass temp = tc[i];
			tc[i] = tc[index];
			tc[index] = temp;
		}

		return tc;
	}

	private static int randomIndex(int size) {
		return (int) (Math.random() * size);
	}

	private static StructArray<TestStruct> createTestStructArray(TestClass[] tc) {
		StructArray<TestStruct> sa = new StructArray<TestStruct>(TestStruct.class, tc.length);
		for (int i = 0; i < tc.length; i++) {
			TestClass testClass = tc[i];
			TestStruct testStruct = sa.add();
			testStruct.setVector(testClass.vector);
			testStruct.setPoint(testClass.point);
			testStruct.setMatrix4(testClass.matrix4);
		}
		return sa;
	}

	private static int validateElementsEqual(TestClass[] tc, StructArray<TestStruct> sa) {
		for (int i = 0; i < tc.length; i++) {
			if (!tc[i].point.equals(sa.get(i).getPoint())) {
				return i;
			}
		}

		return -1;
	}

	public static void testReverse(int size) {
		TestClass[] tc = createTestClassArray(size);
		StructArray<TestStruct> sa = createTestStructArray(tc);

		Array<TestClass> arr = Array.with(tc);
		arr.reverse();
		sa.reverse();
		System.out.println("testReverse: " + validateElementsEqual(arr.items, sa));
	}

	public static void testEqualsAndHashCode() {
		TestClass[] tc = createTestClassArray(100);
		StructArray<TestStruct> sa1 = createTestStructArray(tc);
		StructArray<TestStruct> sa2 = createTestStructArray(tc);
		System.out.println("testEquals: " + sa1.equals(sa2));
		System.out.println("testHashCode: " + (sa1.hashCode() == sa2.hashCode()));
	}

	public static void main(String[] args) {
		testReverse(55);
		testReverse(54);
		testReverse(3);
		testReverse(2);
		testReverse(1);
		testEqualsAndHashCode();
	}

	private static class TestClass {
		Vector3 vector = new Vector3(randomFloat(), randomFloat(), randomFloat());
		GridPoint3 point = new GridPoint3(randomInt(), randomInt(), randomInt());
		Matrix4 matrix4 = new Matrix4(new float[] { randomFloat(), randomFloat(), randomFloat(), randomFloat(),
				randomFloat(), randomFloat(), randomFloat(), randomFloat(), randomFloat(), randomFloat(), randomFloat(),
				randomFloat(), randomFloat(), randomFloat(), randomFloat(), randomFloat() });

		private static float randomFloat() {
			return Double.valueOf(Math.random()).floatValue();
		}

		private static int randomInt() {
			return Double.valueOf(Math.random() * Integer.MAX_VALUE).intValue();
		}
	}

	private static class TestStruct extends Struct {
		public static final Vector3StructProperty vector = new Vector3StructProperty();
		public static final GridPoint3StructProperty point = new GridPoint3StructProperty();
		public static final Matrix4StructProperty matrix4 = new Matrix4StructProperty();

		@SuppressWarnings("unused")
		public Vector3 getVector() {
			return vector.get(this);
		}

		public GridPoint3 getPoint() {
			return point.get(this);
		}

		@SuppressWarnings("unused")
		public Matrix4 getMatrix4(Matrix4 out) {
			return matrix4.get(this, out);
		}

		public void setVector(Vector3 value) {
			vector.set(this, value);
		}

		public void setPoint(GridPoint3 value) {
			point.set(this, value);
		}

		public void setMatrix4(Matrix4 value) {
			matrix4.set(this, value);
		}
	}
}
