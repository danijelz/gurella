package com.gurella.engine.graphics.vector.sfnt;

public class SfntTable extends SubTable<TableDirectory> {
	public final int tag;
	public final long checkSum;
	public final int length;
	
	public SfntTable(TableDirectory headerTable, int offset, int tag, long checkSum, int length) {
		super(headerTable, offset);
		this.tag = tag;
		this.checkSum = checkSum;
		this.length = length;
	}
	
	public SfntTable(RandomAccessFile raf, TableDirectory headerTable, int offset, int tag, long checkSum, int length) {
		super(raf, headerTable, offset);
		this.tag = tag;
		this.checkSum = checkSum;
		this.length = length;
	}
	
	protected <T extends SfntTable> T getTable(SfntTableTag sfntTableType) {
		return parentTable.getTable(sfntTableType);
	}
}
