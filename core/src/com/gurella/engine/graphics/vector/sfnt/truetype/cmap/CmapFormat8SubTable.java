package com.gurella.engine.graphics.vector.sfnt.truetype.cmap;

import com.gurella.engine.graphics.vector.sfnt.SfntDataType;

class CmapFormat8SubTable extends CmapSubTable {
	private static final int groupSize = 12;
	
	CmapFormat8SubTable(CmapTable parentTable, int offset) {
		super(parentTable, offset);
	}
	
	@Override
	public int getLength() {
		return readUnsignedIntAsInt(CmapFormat8Offsets.length);
	}
	
	@Override
	public int getLanguage() {
		return readUnsignedIntAsInt(CmapFormat8Offsets.language);
	}
	
	@Override
	public int getGlyphId(int charCode) {
		int nGroupsOffset = CmapFormat8Offsets.is32.offset + 8192;
		int nGroups = readUnsignedIntAsInt(nGroupsOffset);
		
		int groupsOffset = nGroupsOffset + SfntDataType.unsignedIntValue.size;

		for (int i = 0; i < nGroups; i++) {
			int groupOffset = groupsOffset + i * groupSize;
			
			int startCharCode = readUnsignedIntAsInt(groupOffset);
			if (charCode < startCharCode) {
				return 0;
			}
			
			int endCharCode = readUnsignedIntAsInt(groupOffset + CmapFormatGroup8Offsets.endCharCode.offset);
			if (charCode <= endCharCode) {
				int startGlyphId = readUnsignedIntAsInt(groupOffset + CmapFormatGroup8Offsets.startGlyphID.offset);
				return startGlyphId + charCode - startCharCode;
			}
		}

		return 0;
	}
	
	private enum CmapFormat8Offsets implements Offset {
		format(0),
		reserved(2),
		length(4),
		language(8),
		is32(12);

		private final int offset;

		private CmapFormat8Offsets(int offset) {
			this.offset = offset;
		}
		
		@Override
		public int getOffset() {
			return offset;
		}
	}
	
	private enum CmapFormatGroup8Offsets implements Offset {
		startCharCode(0),
		endCharCode(4),
		startGlyphID(8);
		
		private final int offset;
		
		private CmapFormatGroup8Offsets(int offset) {
			this.offset = offset;
		}
		
		@Override
		public int getOffset() {
			return offset;
		}
	}
}