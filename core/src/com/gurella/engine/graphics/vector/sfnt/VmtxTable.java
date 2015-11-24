package com.gurella.engine.graphics.vector.sfnt;

public class VmtxTable extends SfntTable {
	VmtxTable(TrueTypeTableDirectory descriptor, int tag, long checkSum, int offset, int length) {
		super(descriptor, offset, tag, checkSum, length);
	}
	
	public VmtxTable(RandomAccessFile raf, TableDirectory directoryTable, int tag, long checkSum, int offset, int length) {
		super(raf, directoryTable, offset, tag, checkSum, length);
	}

	private int getNumOfLongVerMetrics() {
		return this.<VheaTable>getTable(SfntTableTag.vhea).getNumOfLongVerMetrics();
	}
	
	public short getTopSideBearing(int index) {
		int numOfLongVerMetrics = getNumOfLongVerMetrics();
		if(index < numOfLongVerMetrics -1) {
			return readShort(index * 4 + 2);
		} else {
			int topSideBearingsStartOffset =  numOfLongVerMetrics * 4;
			int topSideBearingsIndex = index - numOfLongVerMetrics;
			return readShort(topSideBearingsStartOffset + 2 * topSideBearingsIndex);
		}
	}
	
	public int getAdvanceHeight(int index) {
		int numOfLongVerMetrics = getNumOfLongVerMetrics();
		int adjustedIndex = Math.min(numOfLongVerMetrics - 1, index);
		return readUnsignedShort(adjustedIndex * 4);
	}
}
