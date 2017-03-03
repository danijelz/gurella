package com.gurella.engine.utils.struct;

import static org.junit.Assert.assertEquals;

import java.util.Comparator;

import org.junit.Test;

import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.utils.Sort;
import com.gurella.engine.utils.Values;
import com.gurella.engine.utils.struct.StructProperty.GridPoint3StructProperty;

public class StructArraySortTest {
	@Test
	public void testSort() {
		int testSize = 100000;
		TestClass[] tc = new TestClass[testSize];
		StructArray<TestStruct> sa = new StructArray<TestStruct>(TestStruct.class, testSize);
		for (int i = 0; i < testSize; i++) {
			TestClass testClass = new TestClass();
			tc[i] = testClass;
			TestStruct testStruct = sa.add();
			testStruct.setPoint(testClass.point);
		}

		for (int i = 0; i < testSize; i++) {
			if (!tc[i].point.equals(sa.get(i).getPoint())) {
				throw new IllegalStateException("Diff 1");
			}
		}

		Sort sort = new Sort();
		StructArraySort<TestStruct> structSort = new StructArraySort<TestStruct>(TestStruct.class, testSize / 2);

		System.out.println("SORT X -----------------------------------");

		long tcTotalTime = 0;
		long saTotalTime = 0;

		long time = System.nanoTime();
		sort.sort(tc, new TestClassComparatorX());
		long tcSortTime = System.nanoTime() - time;

		time = System.nanoTime();
		structSort.sort(sa, new TestStructComparatorX());
		long saSortTime = System.nanoTime() - time;

		tcTotalTime += tcSortTime;
		saTotalTime += saSortTime;

		for (int i = 0; i < testSize; i++) {
			if (!tc[i].point.equals(sa.get(i).getPoint())) {
				System.out.println("Diff after sort. index = " + i);
				System.out.println("\t" + tc[i].point);
				System.out.println("\t" + sa.get(i).getPoint());
				System.out.println(sa.get(i).getPoint());
			}
		}

		System.out.println();
		System.out.println("TestClass:  " + tcSortTime);
		System.out.println("TestStruct: " + saSortTime);
		System.out.println("ratio " + ((double) saSortTime / tcSortTime));

		System.out.println("\n\nSORT Y -----------------------------------");
		time = System.nanoTime();
		sort.sort(tc, new TestClassComparatorY());
		tcSortTime = System.nanoTime() - time;

		time = System.nanoTime();
		structSort.sort(sa, new TestStructComparatorY());
		saSortTime = System.nanoTime() - time;

		tcTotalTime += tcSortTime;
		saTotalTime += saSortTime;

		for (int i = 0; i < testSize; i++) {
			if (!tc[i].point.equals(sa.get(i).getPoint())) {
				System.out.println("Diff after sort. index = " + i);
				System.out.println("\t" + tc[i].point);
				System.out.println("\t" + sa.get(i).getPoint());
				System.out.println(sa.get(i).getPoint());
			}
		}

		System.out.println();
		System.out.println("TestClass:  " + tcSortTime);
		System.out.println("TestStruct: " + saSortTime);
		System.out.println("ratio " + ((double) saSortTime / tcSortTime));

		System.out.println("\n\nSORT Z -----------------------------------");
		time = System.nanoTime();
		sort.sort(tc, new TestClassComparatorZ());
		tcSortTime = System.nanoTime() - time;

		time = System.nanoTime();
		structSort.sort(sa, new TestStructComparatorZ());
		saSortTime = System.nanoTime() - time;

		tcTotalTime += tcSortTime;
		saTotalTime += saSortTime;

		for (int i = 0; i < testSize; i++) {
			if (!tc[i].point.equals(sa.get(i).getPoint())) {
				System.out.println("Diff after sort. index = " + i);
				System.out.println("\t" + tc[i].point);
				System.out.println("\t" + sa.get(i).getPoint());
				System.out.println(sa.get(i).getPoint());
			}
		}

		System.out.println();
		System.out.println("TestClass:  " + tcSortTime);
		System.out.println("TestStruct: " + saSortTime);
		System.out.println("ratio " + ((double) saSortTime / tcSortTime));

		System.out.println("\n\nTotal -----------------------------------");
		System.out.println("TestClass:  " + tcTotalTime);
		System.out.println("TestStruct: " + saTotalTime);
		System.out.println("ratio " + ((double) saTotalTime / tcTotalTime));

		int diffElementIndex = getDiffElementIndex(tc, sa);
		assertEquals("Element diff at index: " + diffElementIndex, -1, diffElementIndex);
	}

	private static int getDiffElementIndex(TestClass[] tc, StructArray<TestStruct> sa) {
		for (int i = 0; i < tc.length; i++) {
			if (!tc[i].point.equals(sa.get(i).getPoint())) {
				return i;
			}
		}

		return -1;
	}

	public static class TestClass {
		GridPoint3 point = new GridPoint3(randomInt(), randomInt(), randomInt());

		private static int randomInt() {
			return Double.valueOf(Math.random() * Integer.MAX_VALUE).intValue();
		}
	}

	public static class TestStruct extends Struct {
		public static final GridPoint3StructProperty point = new GridPoint3StructProperty();

		public GridPoint3 getPoint() {
			return point.get(this);
		}

		public void setPoint(GridPoint3 value) {
			point.set(this, value);
		}
	}

	private static class TestClassComparatorX implements Comparator<TestClass> {
		@Override
		public int compare(TestClass o1, TestClass o2) {
			return Values.compare(o1.point.x, o2.point.x);
		}
	}

	private static class TestStructComparatorX implements Comparator<TestStruct> {
		@Override
		public int compare(TestStruct o1, TestStruct o2) {
			return Values.compare(o1.getPoint().x, o2.getPoint().x);
		}
	}

	private static class TestClassComparatorY implements Comparator<TestClass> {
		@Override
		public int compare(TestClass o1, TestClass o2) {
			return Values.compare(o1.point.y, o2.point.y);
		}
	}

	private static class TestStructComparatorY implements Comparator<TestStruct> {
		@Override
		public int compare(TestStruct o1, TestStruct o2) {
			return Values.compare(o1.getPoint().y, o2.getPoint().y);
		}
	}

	private static class TestClassComparatorZ implements Comparator<TestClass> {
		@Override
		public int compare(TestClass o1, TestClass o2) {
			return Values.compare(o1.point.z, o2.point.z);
		}
	}

	private static class TestStructComparatorZ implements Comparator<TestStruct> {
		@Override
		public int compare(TestStruct o1, TestStruct o2) {
			return Values.compare(o1.getPoint().z, o2.getPoint().z);
		}
	}
}
