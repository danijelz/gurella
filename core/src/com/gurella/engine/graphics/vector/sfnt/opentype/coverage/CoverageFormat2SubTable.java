package com.gurella.engine.graphics.vector.sfnt.opentype.coverage;

import com.gurella.engine.graphics.vector.sfnt.RandomAccessFile;

class CoverageFormat2SubTable extends CoverageSubTable {
	public CoverageFormat2SubTable(RandomAccessFile raf, int offset) {
		super(raf, offset);
	}
	
	public int getCoverageFormat() {
		return readUnsignedShort(CoverageFormat2Offsets.coverageFormat);
	}
	
	public int getRangeCount() {
		return readUnsignedShort(CoverageFormat2Offsets.rangeCount);
	}
	
	private static int getRangeRecordOffset(int index) {
		return CoverageFormat2Offsets.rangeRecord.offset + index * 6;
	}
	
	private int getStart(int index) {
		return readUnsignedShort(getRangeRecordOffset(index));
	}
	
	private int getEnd(int index) {
		return readUnsignedShort(getRangeRecordOffset(index) + RangeRecordOffsets.end.offset);
	}
	
	private int getStartCoverageIndex(int index) {
		return readUnsignedShort(getRangeRecordOffset(index) + RangeRecordOffsets.startCoverageIndex.offset);
	}
	
	@Override
	public int getGlyphIndex(int glyphId) {
		int lo = 0;
        int hi = getRangeCount() - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            int midStart = getStart(mid);
            
			if(glyphId < midStart) {
				hi = mid - 1;
			} else if (glyphId > midStart && glyphId > getEnd(mid)) {
				lo = mid + 1;
			} else {
				return getStartCoverageIndex(mid) + glyphId - midStart;
			}
        }
        return -1;
	}
	
	private enum CoverageFormat2Offsets implements Offset {
		coverageFormat(0), 
		rangeCount(2), 
		rangeRecord(4);

		private final int offset;

		private CoverageFormat2Offsets(int offset) {
			this.offset = offset;
		}

		@Override
		public int getOffset() {
			return offset;
		}
	}
	
	private enum RangeRecordOffsets implements Offset {
		start(0), 
		end(2), 
		startCoverageIndex(4);

		private final int offset;

		private RangeRecordOffsets(int offset) {
			this.offset = offset;
		}

		@Override
		public int getOffset() {
			return offset;
		}
	}
}