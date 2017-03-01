package com.gurella.engine.utils.struct;

import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.utils.struct.StructProperty.GridPoint3StructProperty;
import com.gurella.engine.utils.struct.StructProperty.IntStructProperty;
import com.gurella.engine.utils.struct.StructProperty.Matrix4StructProperty;
import com.gurella.engine.utils.struct.StructProperty.Vector3StructProperty;

public abstract class Struct {
	Buffer buffer;
	int offset;

	protected Struct() {
	}

	public Struct(Buffer buffer, int offset) {
		this.offset = offset;
		this.buffer = buffer;
	}

	@Override
	public String toString() {
		StructType<?> structType = StructType.get(getClass());
		StringBuilder builder = new StringBuilder();
		builder.append(structType.type.getSimpleName());
		builder.append("{");
		Array<StructProperty> properties = structType._orderedProperties;
		for (int i = 0, n = properties.size; i < n; i++) {
			StructProperty property = properties.get(i);
			builder.append(property.name);
			builder.append("=");
			builder.append(property.toString(this));

			if (i < n - 1) {
				builder.append(", ");
			}
		}
		builder.append("}");
		return builder.toString();
	}

	//////////////////////////////////////

	private static class TestClass {
		Vector3 vector = new Vector3(randomFloat(), randomFloat(), randomFloat());
		GridPoint3 point = new GridPoint3(randomInt(), randomInt(), randomInt());
		Matrix4 matrix4 = new Matrix4(new float[] { randomFloat(), randomFloat(), randomFloat(), randomFloat(),
				randomFloat(), randomFloat(), randomFloat(), randomFloat(), randomFloat(), randomFloat(), randomFloat(),
				randomFloat(), randomFloat(), randomFloat(), randomFloat(), randomFloat() });

		int next = randomIndex();

		private static float randomFloat() {
			return Double.valueOf(Math.random()).floatValue();
		}

		private static int randomInt() {
			return Double.valueOf(Math.random() * Integer.MAX_VALUE).intValue();
		}

		private static int randomIndex() {
			return (int) (Math.random() * (testSize - iterations - 1));
		}
	}

	private static class TestStruct extends Struct {
		public static final Vector3StructProperty vector = new Vector3StructProperty();
		public static final GridPoint3StructProperty point = new GridPoint3StructProperty();
		public static final Matrix4StructProperty matrix4 = new Matrix4StructProperty();
		public static final IntStructProperty next = new IntStructProperty();

		public Vector3 getVector() {
			return vector.get(this);
		}

		public GridPoint3 getPoint() {
			return point.get(this);
		}

		public Matrix4 getMatrix4(Matrix4 out) {
			return matrix4.get(this, out);
		}

		public int getNext() {
			return next.get(this);
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

		public void setNext(int value) {
			next.set(this, value);
		}
	}

	static int testSize = 1000000;
	static int iterations = 10;
	static int subIterations = testSize / iterations;

	public static void main(String[] args) {
		TestClass[] tc = new TestClass[testSize];
		for (int i = 0; i < testSize; i++) {
			tc[i] = new TestClass();
		}

		Vector3 testVecTc = new Vector3();
		Vector3 testVecSa = new Vector3();

		for (int i = 0; i < testSize; i++) {
			int index = TestClass.randomIndex();
			TestClass temp = tc[i];
			tc[i] = tc[index];
			tc[index] = temp;
		}

		System.out.println(1);

		StructArray<TestStruct> sa = new StructArray<TestStruct>(TestStruct.class, testSize);
		for (int i = 0; i < testSize; i++) {
			TestClass testClass = tc[i];
			TestStruct testStruct = sa.add();
			testStruct.setVector(testClass.vector);
			testStruct.setPoint(testClass.point);
			testStruct.setMatrix4(testClass.matrix4);
			testStruct.setNext(testClass.next);
		}

		System.out.println(2);
		System.out.println();

		for (int j = 0; j < 10; j++) {
			for (int i = 0; i < subIterations; i++) {
				testTcRandom(tc, i, testVecTc);
				testSaRandom(sa, i, testVecSa);
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
		System.out.println("Random SUM:");
		System.out.println("TestClass:  " + tcTotalTime);
		System.out.println("TestStruct: " + saTotalTime);
		System.out.println("ratio " + ((double) saTotalTime / tcTotalTime));

		System.out.println("");
		System.out.println("R: ");
		System.out.println(tcr);
		System.out.println(sar);

		System.out.println("");
		System.out.println("Vec:");
		System.out.println(testVecTc);
		System.out.println(testVecSa);

		testVecTc.setZero();
		testVecSa.setZero();
		tcTotalTime = 0;
		saTotalTime = 0;
		tcTime = 0;
		saTime = 0;

		for (int j = 0; j < 10; j++) {
			testTcLinear(tc, testVecTc);
			testSaLinear(sa, testVecSa);

			System.out.println(tcTime);
			System.out.println(saTime);
			System.out.println("");

			tcTotalTime += tcTime;
			saTotalTime += saTime;
			tcTime = 0;
			saTime = 0;
		}

		System.out.println("");
		System.out.println("Linear SUM:");
		System.out.println("TestClass:  " + tcTotalTime);
		System.out.println("TestStruct: " + saTotalTime);
		System.out.println("ratio " + ((double) saTotalTime / tcTotalTime));

		System.out.println("");
		System.out.println("R: ");
		System.out.println(tcr);
		System.out.println(sar);

		System.out.println("");
		System.out.println("Vec:");
		System.out.println(testVecTc);
		System.out.println(testVecSa);
	}

	private static long tcTime;
	private static long tcTotalTime;
	private static double tcr;

	private static void testTcRandom(TestClass[] tc, int index, Vector3 testVec) {
		double r = 0;
		long time = System.nanoTime();

		int next = index;
		for (int i = 0; i < iterations; i++) {
			TestClass testClass = tc[next];
			Vector3 vector = testClass.vector;
			r += vector.x;
			r += vector.y;
			r += vector.z;

			testVec.add(vector);

			GridPoint3 point = testClass.point;
			r += point.x;
			r += point.y;
			r += point.z;

			testClass.matrix4.scl(2);

			next = testClass.next;
		}

		tcr += r;
		tcTime += System.nanoTime() - time;
	}

	private static void testTcLinear(TestClass[] tc, Vector3 testVec) {
		double r = 0;
		long time = System.nanoTime();

		for (int i = 0; i < testSize; i++) {
			TestClass testClass = tc[i];
			Vector3 vector = testClass.vector;
			r += vector.x;
			r += vector.y;
			r += vector.z;

			testVec.add(vector);

			GridPoint3 point = testClass.point;
			r += point.x;
			r += point.y;
			r += point.z;

			testClass.matrix4.scl(2);

		}

		tcr += r;
		tcTime += System.nanoTime() - time;
	}

	private static long saTime;
	private static long saTotalTime;
	private static double sar;
	private static Matrix4 tempMatrix = new Matrix4();

	private static void testSaRandom(StructArray<TestStruct> sa, int index, Vector3 testVec) {
		double r = 0;
		long time = System.nanoTime();

		int next = index;
		for (int i = 0; i < iterations; i++) {
			TestStruct testStruct = sa.get(next);
			Vector3 vector = testStruct.getVector();
			r += vector.x;
			r += vector.y;
			r += vector.z;

			testVec.add(vector);

			GridPoint3 point = testStruct.getPoint();
			r += point.x;
			r += point.y;
			r += point.z;

			testStruct.getMatrix4(tempMatrix).scl(2);

			next = testStruct.getNext();
		}

		sar += r;
		saTime += System.nanoTime() - time;
	}

	private static void testSaLinear(StructArray<TestStruct> sa, Vector3 testVec) {
		double r = 0;
		long time = System.nanoTime();

		for (int i = 0; i < testSize; i++) {
			TestStruct testStruct = sa.get(i);
			Vector3 vector = testStruct.getVector();
			r += vector.x;
			r += vector.y;
			r += vector.z;

			testVec.add(vector);

			GridPoint3 point = testStruct.getPoint();
			r += point.x;
			r += point.y;
			r += point.z;

			testStruct.getMatrix4(tempMatrix).scl(2);
		}

		sar += r;
		saTime += System.nanoTime() - time;
	}
}
