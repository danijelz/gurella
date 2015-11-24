package com.gurella.engine.graphics.vector.sfnt.opentype.classdef;

import com.gurella.engine.graphics.vector.sfnt.RandomAccessFile;
import com.gurella.engine.graphics.vector.sfnt.SfntDataType;

public class ClassDefFormat1SubTable extends ClassDefSubTable {
	public ClassDefFormat1SubTable(RandomAccessFile raf, int offset) {
		super(raf, offset);
	}
	
	@Override
	public int getClassFormat() {
		return readUnsignedShort(ClassDefFormat1Offsets.classFormat);
	}
	
	public int getStartGlyph() {
		return readUnsignedShort(ClassDefFormat1Offsets.startGlyph);
	}
	
	public int getGlyphCount() {
		return readUnsignedShort(ClassDefFormat1Offsets.glyphCount);
	}
	
	@Override
	public int getGlyphClass(int glyphId) {
		int startGlyph = getStartGlyph();
		if(startGlyph > glyphId) {
			return -1;
		}
		
		int glyphCount = getGlyphCount();
		if(startGlyph + glyphCount <= glyphId) {
			return -1;
		}
		
		int classValueArrayIndex = glyphId - startGlyph;
		int classValueArrayOffset = classValueArrayIndex * SfntDataType.unsignedShortValue.size;
		return readUnsignedShort(ClassDefFormat1Offsets.classValueArray.offset + classValueArrayOffset);
	}

	private enum ClassDefFormat1Offsets implements Offset {
		classFormat(0), 
		startGlyph(2), 
		glyphCount(4),
		classValueArray(6);

		private final int offset;

		private ClassDefFormat1Offsets(int offset) {
			this.offset = offset;
		}

		@Override
		public int getOffset() {
			return offset;
		}
	}
}
