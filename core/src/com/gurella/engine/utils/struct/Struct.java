package com.gurella.engine.utils.struct;

public class Struct {
	int offset;
	Buffer buffer;

	protected Struct() {
	}

	public Struct(int offset, Buffer buffer) {
		this.offset = offset;
		this.buffer = buffer;
	}
}
