package com.gurella.engine.graphics.vector.sfnt.cff;

import com.gurella.engine.graphics.vector.sfnt.SubTable;

abstract class CffSubTable extends SubTable<CffTable> {
	int tableEndOffset;
	
	CffSubTable(CffTable cffTable, int offset) {
		super(cffTable, offset);
		init();
	}
	
	protected void init() {
		raf.setPosition(offset);
	}
}
