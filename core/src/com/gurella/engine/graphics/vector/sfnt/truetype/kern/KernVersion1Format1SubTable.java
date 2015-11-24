package com.gurella.engine.graphics.vector.sfnt.truetype.kern;

class KernVersion1Format1SubTable extends KernVersion1SubTable {
	public KernVersion1Format1SubTable(KernVersionTable parentTable, int offset) {
		super(parentTable, offset);
	}
	
	@Override
	public int getKerning(int leftGlyphId, int rightGlyphId) {
		//TODO
        return 0;
	}
}