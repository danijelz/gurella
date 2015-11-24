package com.gurella.engine.graphics.vector.sfnt.opentype.coverage;

import com.gurella.engine.graphics.vector.sfnt.RandomAccessFile;
import com.gurella.engine.graphics.vector.sfnt.Table;

public class CoverageTable extends Table {
	private CoverageSubTable coverageFormatTable;
	
	public CoverageTable(RandomAccessFile raf, int offset) {
		super(raf, offset);
		coverageFormatTable = createCoverageFormatTable();
	}
	
	private CoverageSubTable createCoverageFormatTable() {
		int coverageFormat = readUnsignedShort(0);
		switch (coverageFormat) {
		case 1:
			return new CoverageFormat1SubTable(raf, offset);
		case 2:
			return new CoverageFormat2SubTable(raf, offset);
		default:
			return new CoverageSubTable(raf, offset);
		}
	}
	
	public int getGlyphIndex(int glyphId) {
		return coverageFormatTable.getGlyphIndex(glyphId);
	}
}
