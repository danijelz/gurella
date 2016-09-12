package com.gurella.engine.utils.struct;

import com.gurella.engine.utils.struct.StructType.FloatStructProperty;

public class Struct {
	StructType descriptor;
	int index;
	int offset;
	ArrayOfStructs aos;

	public Struct(StructType descriptor, ArrayOfStructs aos) {
		this.descriptor = descriptor;
		this.aos = aos;
	}

	public float getFloat(FloatStructProperty p) {
		return p.get(aos, index);
	}
}
