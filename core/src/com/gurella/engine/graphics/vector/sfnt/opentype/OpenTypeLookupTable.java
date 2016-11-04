package com.gurella.engine.graphics.vector.sfnt.opentype;

import com.badlogic.gdx.utils.Array;
import com.gurella.engine.graphics.vector.sfnt.opentype.lookup.LookupSubTable;

public class OpenTypeLookupTable extends OpenTypeLayoutSubTable {
	private Array<LookupSubTable> subtables = new Array<LookupSubTable>();

	OpenTypeLookupTable(OpenTypeLayoutTable openTypeTable, int offset, OpenTypeLookupSubTableFactory lookupSubTableFactory) {
		super(openTypeTable, offset);
		
		int lookupType = getLookupType();
		for(int i = 0; i < getSubTableCount(); i++) {
			int subtableOffset = readUnsignedShort(OpenTypeLookupOffsets.subTables.offset + 2 * i);
			subtables.add(lookupSubTableFactory.create(lookupType, this, offset + subtableOffset));
		}
	}
	
	public int getLookupType() {
		return readUnsignedShort(OpenTypeLookupOffsets.lookupType);
	}
	
	public int getLookupFlag() {
		return readUnsignedShort(OpenTypeLookupOffsets.lookupFlag);
	}
	
	private int getSubTableCount() {
		return readUnsignedShort(OpenTypeLookupOffsets.subTableCount);
	}
	
	public int getMarkFilteringSet() {
		return readUnsignedShort(OpenTypeLookupOffsets.subTables.offset + (2 * getSubTableCount()));
	}
	
	interface OpenTypeLookupSubTableFactory {
		LookupSubTable create(int lookupType, OpenTypeLookupTable openTypeLookupTable, int offset);
	}
	
	private enum OpenTypeLookupOffsets implements Offset {
		lookupType(0), 
		lookupFlag(2), 
		subTableCount(4),
		subTables(6),
		markFilteringSet(-1);

		private final int offset;

		private OpenTypeLookupOffsets(int offset) {
			this.offset = offset;
		}

		@Override
		public int getOffset() {
			return offset;
		}
	}
}
