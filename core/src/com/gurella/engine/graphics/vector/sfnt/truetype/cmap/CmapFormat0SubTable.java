package com.gurella.engine.graphics.vector.sfnt.truetype.cmap;

class CmapFormat0SubTable extends CmapSubTable {
	CmapFormat0SubTable(CmapTable parentTable, int offset) {
		super(parentTable, offset);
	}

	@Override
	public int getLength() {
		return readUnsignedShort(CmapFormat0Offsets.length);
	}

	@Override
	public int getLanguage() {
		return readUnsignedShort(CmapFormat0Offsets.language);
	}

	@Override
	public int getGlyphId(int charCode) {
		if (charCode < 256) {
			return (readByte(CmapFormat0Offsets.glyphIdArray.offset + charCode) + 256) % 256;
		}
		return 0;
	}

	private enum CmapFormat0Offsets implements Offset {
		format(0), length(2), language(4), glyphIdArray(6);

		private final int offset;

		private CmapFormat0Offsets(int offset) {
			this.offset = offset;
		}

		@Override
		public int getOffset() {
			return offset;
		}
	}
}