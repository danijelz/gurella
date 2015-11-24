package com.gurella.engine.graphics.vector.sfnt.opentype.classdef;

import com.gurella.engine.graphics.vector.sfnt.RandomAccessFile;

public class ClassDefFormat2SubTable extends ClassDefSubTable {
	public ClassDefFormat2SubTable(RandomAccessFile raf, int offset) {
		super(raf, offset);
	}
	
	@Override
	public int getClassFormat() {
		return readUnsignedShort(ClassDefFormat2Offsets.classFormat);
	}
	
	public int getClassRangeCount() {
		return readUnsignedShort(ClassDefFormat2Offsets.classRangeCount);
	}
	
	@Override
	public int getGlyphClass(int glyphId) {
		int lo = 0;
        int hi = getClassRangeCount() - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            int midStart = getStart(mid);
            
			if(glyphId < midStart) {
				hi = mid - 1;
			} else if (glyphId > midStart && glyphId > getEnd(mid)) {
				lo = mid + 1;
			} else {
				return getRangeClass(mid);
			}
        }
        return -1;
	}
	
	private static int getRangeRecordOffset(int index) {
		return ClassDefFormat2Offsets.classRangeRecord.offset + index * 6;
	}
	
	private int getStart(int index) {
		return readUnsignedShort(getRangeRecordOffset(index));
	}
	
	private int getEnd(int index) {
		return readUnsignedShort(getRangeRecordOffset(index) + RangeRecordOffsets.end.offset);
	}
	
	private int getRangeClass(int index) {
		return readUnsignedShort(getRangeRecordOffset(index) + RangeRecordOffsets.rangeClass.offset);
	}
	
	private enum ClassDefFormat2Offsets implements Offset {
		classFormat(0), 
		classRangeCount(2), 
		classRangeRecord(4);

		private final int offset;

		private ClassDefFormat2Offsets(int offset) {
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
		rangeClass(4);

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
