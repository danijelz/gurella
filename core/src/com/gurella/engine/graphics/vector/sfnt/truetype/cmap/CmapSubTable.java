package com.gurella.engine.graphics.vector.sfnt.truetype.cmap;

import com.gurella.engine.graphics.vector.sfnt.SubTable;

class CmapSubTable extends SubTable<CmapTable> {
	public CmapSubTable(CmapTable parentTable, int offset) {
		super(parentTable, offset);
	}
	
	public int getFormat() {
		return readUnsignedShort(0);
	}
	
	public int getLength() {
		return 0;
	}
	
	public int getLanguage() {
		return -1;
	}
	
	public int getGlyphId(int charCode) {
		return 0;
	}
}