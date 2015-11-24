package com.gurella.engine.graphics.vector.sfnt.truetype.cmap;

class CmapFormat14SubTable extends CmapSubTable {
	CmapFormat14SubTable(CmapTable parentTable, int offset) {
		super(parentTable, offset);
	}

	@Override
	public int getLength() {
		return readUnsignedShort(CmapFormat14Offsets.length);
	}

	@Override
	public int getGlyphId(int charCode) {
		// TODO
		return 0;
	}

	private enum CmapFormat14Offsets implements Offset {
		format(0), length(2), numVarSelectorRecords(6);

		private final int offset;

		private CmapFormat14Offsets(int offset) {
			this.offset = offset;
		}

		@Override
		public int getOffset() {
			return offset;
		}
	}
}