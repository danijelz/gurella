package com.gurella.engine.graphics.vector.sfnt.opentype;

import com.gurella.engine.graphics.vector.sfnt.SubTable;

public class OpenTypeLayoutSubTable extends SubTable<OpenTypeLayoutTable> {
	OpenTypeLayoutSubTable(OpenTypeLayoutTable openTypeTable, int offset) {
		super(openTypeTable, offset);
	}
}
