package com.gurella.engine.graphics.vector.sfnt;

public class SubTable<PT extends Table> extends Table {
	public final PT parentTable;

	public SubTable(PT parentTable, int offset) {
		super(parentTable.raf, offset);
		this.parentTable = parentTable;
	}
	
	public SubTable(RandomAccessFile raf, PT parentTable, int offset) {
		super(raf, offset);
		this.parentTable = parentTable;
	}
}
