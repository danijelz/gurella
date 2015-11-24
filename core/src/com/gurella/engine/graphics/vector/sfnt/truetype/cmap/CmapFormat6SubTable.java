package com.gurella.engine.graphics.vector.sfnt.truetype.cmap;

import com.gurella.engine.graphics.vector.sfnt.SfntDataType;

class CmapFormat6SubTable extends CmapSubTable {
	CmapFormat6SubTable(CmapTable parentTable, int offset) {
		super(parentTable, offset);
	}
	
	@Override
	public int getLength() {
		return readUnsignedShort(CmapFormat6Offsets.length);
	}
	
	@Override
	public int getLanguage() {
		return readUnsignedShort(CmapFormat6Offsets.language);
	}
	
	private int getFirstCode() {
		return readUnsignedShort(CmapFormat6Offsets.firstCode);
	}
	
	private int getEntryCount() {
		return readUnsignedShort(CmapFormat6Offsets.entryCount);
	}
	
	@Override
	public int getGlyphId(int charCode) {
		int firstCode = getFirstCode();
		int entryCount = getEntryCount();
		int entryId = charCode - firstCode;
		
		if(entryId >= 0 && entryId < entryCount) {
			return readUnsignedShort(CmapFormat6Offsets.glyphIdArray.offset + (entryId * SfntDataType.unsignedShortValue.size));
		}
		
		return 0;
	}
	
	private enum CmapFormat6Offsets implements Offset {
		format(0),
		length(2),
		language(4),
		firstCode(6),
		entryCount(8),
		glyphIdArray(10);

		private final int offset;

		private CmapFormat6Offsets(int offset) {
			this.offset = offset;
		}
		
		@Override
		public int getOffset() {
			return offset;
		}
	}
}