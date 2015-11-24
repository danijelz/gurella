package com.gurella.engine.graphics.vector.sfnt.opentype;

import com.gurella.engine.graphics.vector.sfnt.RandomAccessFile;
import com.gurella.engine.graphics.vector.sfnt.SfntTable;
import com.gurella.engine.graphics.vector.sfnt.TableDirectory;
import com.gurella.engine.graphics.vector.sfnt.TrueTypeTableDirectory;

public class DsigTable extends SfntTable {
	private DsigSubTable dsigSubTable;
	
	public DsigTable(TrueTypeTableDirectory descriptor, int tag, long checkSum, int offset, int length) {
		super(descriptor, offset, tag, checkSum, length);
		dsigSubTable = new DsigSubTable(this, offset);
	}
	
	public DsigTable(RandomAccessFile raf, TableDirectory directoryTable, int tag, long checkSum, int offset, int length) {
		super(raf, directoryTable, offset, tag, checkSum, length);
	}

	public DsigSubTable getDsigSubTable() {
		return dsigSubTable;
	}
}
