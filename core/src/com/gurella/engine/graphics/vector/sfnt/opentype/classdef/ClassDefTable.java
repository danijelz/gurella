package com.gurella.engine.graphics.vector.sfnt.opentype.classdef;

import com.gurella.engine.graphics.vector.sfnt.RandomAccessFile;
import com.gurella.engine.graphics.vector.sfnt.Table;

public class ClassDefTable extends Table {
	private ClassDefSubTable classDefSubTable;
	
	public ClassDefTable(RandomAccessFile raf, int offset) {
		super(raf, offset);
		classDefSubTable = createClassDefSubTable();
	}

	private ClassDefSubTable createClassDefSubTable() {
		int classFormat = readUnsignedShort(0);
		switch (classFormat) {
		case 1:
			return new ClassDefFormat1SubTable(raf, offset);
		case 2:
			return new ClassDefFormat2SubTable(raf, offset);
		default:
			return new ClassDefSubTable(raf, offset);
		}
	}
	
	public int getGlyphClass(int glyphId) {
		return classDefSubTable.getGlyphClass(glyphId);
	}
}
