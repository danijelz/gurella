package com.gurella.engine.graphics.vector.sfnt.opentype.lookup;

import com.gurella.engine.graphics.vector.sfnt.SubTable;
import com.gurella.engine.graphics.vector.sfnt.opentype.OpenTypeLookupTable;

public class LookupSubTable extends SubTable<OpenTypeLookupTable> {
	public LookupSubTable(OpenTypeLookupTable parentTable, int offset) {
		super(parentTable, offset);
	}
}
