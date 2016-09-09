package com.gurella.engine.utils.struct;

import com.gurella.engine.utils.struct.StructDescriptor.FloatStructProperty;

public class Struct {
	StructDescriptor descriptor;
	int index;
	int offset;
	ArrayOfStructs aos;

	public Struct(StructDescriptor descriptor, ArrayOfStructs aos) {
		this.descriptor = descriptor;
		this.aos = aos;
	}

	public float getFloat(FloatStructProperty p) {
		return p.get(aos, index);
	}
}
