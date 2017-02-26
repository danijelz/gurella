package com.gurella.engine.utils.struct;

import com.gurella.engine.utils.Reflection;

public class StructArray<T extends Struct> extends Buffer {
	private final T temp;
	private int size;
	
	public StructArray(StructType<T> structType, int initialSize) {
		super(structType.size * initialSize);
		temp = Reflection.newInstance(structType.type);
	}
}
