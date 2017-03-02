package com.gurella.engine.utils.struct;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.gurella.engine.utils.struct.StructProperty.ByteStructProperty;

public class StructTypeTest {
	@Test
	public void testSize() {
		assertTrue(StructType.get(TestSizeStruct1.class).size == 4);
		assertTrue(StructType.get(TestSizeStruct2.class).size == 4);
		assertTrue(StructType.get(TestSizeStruct3.class).size == 4);
		assertTrue(StructType.get(TestSizeStruct4.class).size == 8);
	}
	
	public static class TestSizeStruct1 extends Struct {
		public static final ByteStructProperty property = new ByteStructProperty();
	}
	
	public static class TestSizeStruct2 extends Struct {
		public static final ByteStructProperty property1 = new ByteStructProperty();
		public static final ByteStructProperty property2 = new ByteStructProperty();
	}
	
	public static class TestSizeStruct3 extends Struct {
		public static final ByteStructProperty property1 = new ByteStructProperty();
		public static final ByteStructProperty property2 = new ByteStructProperty();
		public static final ByteStructProperty property3 = new ByteStructProperty();
	}
	
	public static class TestSizeStruct4 extends Struct {
		public static final ByteStructProperty property1 = new ByteStructProperty();
		public static final ByteStructProperty property2 = new ByteStructProperty();
		public static final ByteStructProperty property3 = new ByteStructProperty();
		public static final ByteStructProperty property4 = new ByteStructProperty();
		public static final ByteStructProperty property5 = new ByteStructProperty();
	}
}
