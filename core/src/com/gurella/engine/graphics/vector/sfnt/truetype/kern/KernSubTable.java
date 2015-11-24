package com.gurella.engine.graphics.vector.sfnt.truetype.kern;

import com.gurella.engine.graphics.vector.sfnt.SubTable;

abstract class KernSubTable extends SubTable<KernVersionTable> {
	public KernSubTable(KernVersionTable parentTable, int offset) {
		super(parentTable, offset);
	}
	
	public int getKerning(int leftGlyphId, int rightGlyphId) {
		return 0;
	}
}