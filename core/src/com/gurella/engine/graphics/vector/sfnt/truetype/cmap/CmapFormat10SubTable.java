package com.gurella.engine.graphics.vector.sfnt.truetype.cmap;

import com.gurella.engine.graphics.vector.sfnt.SfntDataType;

class CmapFormat10SubTable extends CmapSubTable {
	CmapFormat10SubTable(CmapTable parentTable, int offset) {
		super(parentTable, offset);
	}

	@Override
	public int getLength() {
		return readUnsignedIntAsInt(CmapFormat10Offsets.length);
	}

	@Override
	public int getLanguage() {
		return readUnsignedIntAsInt(CmapFormat10Offsets.language);
	}

	private int getStartCharCode() {
		return readUnsignedIntAsInt(CmapFormat10Offsets.startCharCode);
	}

	private int getNumChars() {
		return readUnsignedIntAsInt(CmapFormat10Offsets.numChars);
	}

	@Override
	public int getGlyphId(int charCode) {
		int startCharCode = getStartCharCode();
		int numChars = getNumChars();
		int entryId = charCode - startCharCode;

		if (entryId >= 0 && entryId < numChars) {
			return readUnsignedShort(CmapFormat10Offsets.glyphs.offset + (entryId * SfntDataType.unsignedShortValue.size));
		}

		return 0;
	}

	private enum CmapFormat10Offsets implements Offset {
		format(0), reserved(2), length(4), language(8), startCharCode(12), numChars(16), glyphs(20);

		private final int offset;

		private CmapFormat10Offsets(int offset) {
			this.offset = offset;
		}

		@Override
		public int getOffset() {
			return offset;
		}
	}
}