package com.gurella.engine.utils.struct;

public class StructArray<T extends Struct> extends Buffer {
	public StructArray(StructType<T> structType, int initialSize) {
		super(structType.size * initialSize);
	}
}
