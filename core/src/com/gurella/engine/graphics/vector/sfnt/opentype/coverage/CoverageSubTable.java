package com.gurella.engine.graphics.vector.sfnt.opentype.coverage;

import com.gurella.engine.graphics.vector.sfnt.RandomAccessFile;
import com.gurella.engine.graphics.vector.sfnt.Table;

class CoverageSubTable extends Table {
	public CoverageSubTable(RandomAccessFile raf, int offset) {
		super(raf, offset);
	}
	
	public int getGlyphIndex(int glyphId) {
		return -1;
	}
}