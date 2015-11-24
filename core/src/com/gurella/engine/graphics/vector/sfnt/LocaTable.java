package com.gurella.engine.graphics.vector.sfnt;

public class LocaTable extends SfntTable {
	public LocaTable(TrueTypeTableDirectory sfntHeaderTable, int tag, long checkSum, int offset, int length) {
		super(sfntHeaderTable, offset, tag, checkSum, length);
	}
	
	public LocaTable(RandomAccessFile raf, TableDirectory directoryTable, int tag, long checkSum, int offset, int length) {
		super(raf, directoryTable, offset, tag, checkSum, length);
	}

	public int getOffset(int index) {
		int adjustedIndex = (index < 0 || index >= parentTable.getNumGlyphs()) ? 0 : index;
		HeadTable headTable = getTable(SfntTableTag.head);

		switch (headTable.getIndexToLocFormat()) {
		case shortOffset:
			return readUnsignedShort(adjustedIndex * 2) * 2;
		case longOffset:
			return readUnsignedIntAsInt(adjustedIndex * 4);
		default:
			throw new IllegalArgumentException("Unknown offset format.");
		}
	}
}
