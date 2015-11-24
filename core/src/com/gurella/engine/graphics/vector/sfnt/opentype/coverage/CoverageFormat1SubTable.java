package com.gurella.engine.graphics.vector.sfnt.opentype.coverage;

import com.gurella.engine.graphics.vector.sfnt.RandomAccessFile;

class CoverageFormat1SubTable extends CoverageSubTable {
	public CoverageFormat1SubTable(RandomAccessFile raf, int offset) {
		super(raf, offset);
	}
	
	public int getCoverageFormat() {
		return readUnsignedShort(CoverageFormat1Offsets.coverageFormat);
	}
	
	public int getGlyphCount() {
		return readUnsignedShort(CoverageFormat1Offsets.glyphCount);
	}
	
	@Override
	public int getGlyphIndex(int glyphId) {
		int lo = 0;
        int hi = getGlyphCount() - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            int midGlyphId = getGlyphId(mid);
            
			if(glyphId < midGlyphId) {
				hi = mid - 1;
			} else if (glyphId > midGlyphId) {
				lo = mid + 1;
			} else {
				return mid;
			}
        }
        return -1;
	}
	
	private int getGlyphId(int index) {
		return readUnsignedShort(CoverageFormat1Offsets.glyphArray.offset + 2 * index);
	}
	
	private enum CoverageFormat1Offsets implements Offset {
		coverageFormat(0), 
		glyphCount(2), 
		glyphArray(4);

		private final int offset;

		private CoverageFormat1Offsets(int offset) {
			this.offset = offset;
		}

		@Override
		public int getOffset() {
			return offset;
		}
	}
}