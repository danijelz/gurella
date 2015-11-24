package com.gurella.engine.graphics.vector.sfnt;

import com.gurella.engine.graphics.vector.sfnt.truetype.HheaTable;

public class HmtxTable extends SfntTable {
	HmtxTable(TrueTypeTableDirectory sfntHeaderTable, int tag, long checkSum, int offset, int length) {
		super(sfntHeaderTable, offset, tag, checkSum, length);
	}
	
	public HmtxTable(RandomAccessFile raf, TableDirectory directoryTable, int tag, long checkSum, int offset, int length) {
		super(raf, directoryTable, offset, tag, checkSum, length);
	}

	private int getNumberOfHMetrics() {
		return this.<HheaTable>getTable(SfntTableTag.hhea).getNumberOfHMetrics();
	}
	
	public short getLeftSideBearing(int index) {
		int numberOfHMetrics = getNumberOfHMetrics();
		if(index < numberOfHMetrics -1) {
			return readShort(index * 4 + 2);
		} else {
			int leftSideBearingsStartOffset =  numberOfHMetrics * 4;
			int leftSideBearingsIndex = index - numberOfHMetrics;
			return readShort(leftSideBearingsStartOffset + 2 * leftSideBearingsIndex);
		}
	}
	
	public int getAdvanceWidth(int index) {
		int numberOfHMetrics = getNumberOfHMetrics();
		int adjustedIndex = Math.min(numberOfHMetrics - 1, index);
		return readUnsignedShort(adjustedIndex * 4);
	}
}
