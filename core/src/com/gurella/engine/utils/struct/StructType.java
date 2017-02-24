package com.gurella.engine.utils.struct;

import com.badlogic.gdx.utils.Array;
import com.gurella.engine.utils.ImmutableArray;

public class StructType {
	final boolean packed = false;
	final Array<StructProperty> _properties = new Array<StructProperty>();
	public final ImmutableArray<StructProperty> properties = new ImmutableArray<StructProperty>(_properties);

	public int byteSize() {
		int size = 0;
		StructProperty[] items = _properties.items;
		for (int i = 0, n = _properties.size; i < n; i++) {
			size += items[i].type.byteSize();
		}
		return size + (size % 4);
	}

	public static abstract class PropertyType {
		public abstract int byteSize();

		public abstract int packedByteSize();
	}

	public static class PrimitivePropertyType extends PropertyType {
		public static final PrimitivePropertyType floatType = new PrimitivePropertyType(PrimitiveType.floatType);

		public final PrimitiveType primitiveType;

		public PrimitivePropertyType(PrimitiveType primitiveType) {
			this.primitiveType = primitiveType;
		}

		@Override
		public int byteSize() {
			return primitiveType.byteSize;
		}

		@Override
		public int packedByteSize() {
			return primitiveType.packedByteSize;
		}
	}

	public static class ArrayPropertyType extends PropertyType {
		public final PropertyType componentType;
		public final int arraySize;

		public ArrayPropertyType(PropertyType componentType, int arraySize) {
			this.componentType = componentType;
			this.arraySize = arraySize;
		}

		@Override
		public int byteSize() {
			int size = arraySize * componentType.byteSize();
			return size + (size % 4);
		}

		@Override
		public int packedByteSize() {
			return byteSize();
		}
	}

	public static class CompositePropertyType extends PropertyType {
		public final StructType structType;

		public CompositePropertyType(StructType structType) {
			this.structType = structType;
		}

		@Override
		public int byteSize() {
			return structType.byteSize();
		}

		@Override
		public int packedByteSize() {
			return structType.byteSize();
		}
	}

	public enum PrimitiveType {
		intType(4), floatType(4),;

		public final byte byteSize;
		public final byte packedByteSize;

		private PrimitiveType(int byteSize) {
			this.byteSize = (byte) byteSize;
			this.packedByteSize = (byte) byteSize;
		}

		private PrimitiveType(int byteSize, int packedByteSize) {
			this.byteSize = (byte) byteSize;
			this.packedByteSize = (byte) packedByteSize;
		}
	}

	public static abstract class StructProperty {
		final int wordOffset;
		final PropertyType type;

		public StructProperty(int wordOffset, PropertyType type) {
			this.wordOffset = wordOffset;
			this.type = type;
		}
	}

	public static class FloatStructProperty extends StructProperty {
		public FloatStructProperty(int wordOffset) {
			super(wordOffset, PrimitivePropertyType.floatType);
		}

		public float get(ArrayOfStructs aos, int index) {
			return aos.getFloatByIndex(index, wordOffset);
		}

		public Float getFloat(ArrayOfStructs aos, int index) {
			return Float.valueOf(aos.getFloatByIndex(index, wordOffset));
		}

		public void set(ArrayOfStructs aos, int index, float value) {
			aos.setFloatByIndex(index, wordOffset, value);
		}

		public void set(ArrayOfStructs aos, int index, Float value) {
			aos.setFloatByIndex(index, wordOffset, value.floatValue());
		}
	}
}
