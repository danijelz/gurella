package com.gurella.engine.graphics.vector.sfnt.opentype.classdef;

import com.gurella.engine.graphics.vector.sfnt.RandomAccessFile;
import com.gurella.engine.graphics.vector.sfnt.Table;

public class ClassDefSubTable extends Table {
	public ClassDefSubTable(RandomAccessFile raf, int offset) {
		super(raf, offset);
	}
	
	public int getClassFormat() {
		return -1;
	}
	
	public int getGlyphClass(int glyphId) {
		return -1;
	}
}
