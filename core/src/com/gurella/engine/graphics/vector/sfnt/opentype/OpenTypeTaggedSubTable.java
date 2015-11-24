package com.gurella.engine.graphics.vector.sfnt.opentype;

public class OpenTypeTaggedSubTable extends OpenTypeLayoutSubTable {
	final int tag;

	OpenTypeTaggedSubTable(OpenTypeLayoutTable openTypeTable, int offset, int tag) {
		super(openTypeTable, offset);
		this.tag = tag;
		init();
	}

	protected void init() {
		raf.setPosition(offset);
	}
}
