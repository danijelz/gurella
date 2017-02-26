package com.gurella.engine.utils.struct;

public class FloatStructProperty extends StructProperty {
	public FloatStructProperty() {
		super(0, 4);
	}

	public float get(Struct struct) {
		return struct.buffer.getFloat(struct.offset + offset);
	}

	public void set(Struct struct, float value) {
		struct.buffer.setFloat(struct.offset + offset, value);
	}
}
