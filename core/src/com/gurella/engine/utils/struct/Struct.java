package com.gurella.engine.utils.struct;

import com.gurella.engine.utils.struct.StructType.FloatStructProperty;

public class Struct {
	StructType type;
	int index;
	int offset;
	ArrayOfStructs aos;

	public Struct(StructType descriptor, ArrayOfStructs aos) {
		this.type = descriptor;
		this.aos = aos;
	}

	public float getFloat(FloatStructProperty p) {
		return p.get(aos, index);
	}
}
