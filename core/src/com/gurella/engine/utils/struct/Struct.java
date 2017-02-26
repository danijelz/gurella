package com.gurella.engine.utils.struct;

public class Struct {
	StructType type;
	int offset;
	Buffer buffer;

	public Struct() {
		this.type = StructType.get(getClass());
	}
}
