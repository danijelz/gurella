package com.gurella.engine.utils.struct;

public abstract class StructProperty {
	protected final int offset = 0;
	protected final int alignment;
	protected final int size;

	public StructProperty(int alignment, int size) {
		this.alignment = alignment;
		int mod = size % 4;
		this.size = mod == 3 ? size + 1 : size;
	}
}
