package com.gurella.engine.graphics.vector.sfnt.opentype;

import com.gurella.engine.graphics.vector.sfnt.SubTable;
import com.gurella.engine.graphics.vector.sfnt.Table;

public class DsigSubTable extends SubTable<Table> {
	public DsigSubTable(Table parentTable, int offset) {
		super(parentTable, offset);
	}
}
