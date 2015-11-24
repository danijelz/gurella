package com.gurella.engine.graphics.vector.sfnt.truetype.cmap;

class CmapFormat12SubTable extends CmapSubTable {
	private static final int groupSize = 12;

	CmapFormat12SubTable(CmapTable parentTable, int offset) {
		super(parentTable, offset);
	}

	@Override
	public int getLength() {
		return readUnsignedIntAsInt(CmapFormat12Offsets.length);
	}

	@Override
	public int getLanguage() {
		return readUnsignedIntAsInt(CmapFormat12Offsets.language);
	}

	private int getNumGroups() {
		return readUnsignedIntAsInt(CmapFormat12Offsets.nGroups);
	}

	@Override
	public int getGlyphId(int charCode) {
		int lo = 0;
		int hi = getNumGroups() - 1;
		while (lo <= hi) {
			int mid = lo + (hi - lo) / 2;
			int midStart = getStartCharCode(mid);

			if (charCode < midStart) {
				hi = mid - 1;
			} else if (charCode > midStart && charCode > getEndCharCode(mid)) {
				lo = mid + 1;
			} else {
				return getStartGlyphId(mid) + charCode - midStart;
			}
		}

		return 0;
	}

	private int getStartCharCode(int groupIndex) {
		int groupOffset = CmapFormat12Offsets.groups.offset + (groupIndex * groupSize);
		return readUnsignedIntAsInt(groupOffset);
	}

	private int getEndCharCode(int groupIndex) {
		int groupOffset = CmapFormat12Offsets.groups.offset + (groupIndex * groupSize) + CmapFormat12GroupOffsets.endCharCode.offset;
		return readUnsignedIntAsInt(groupOffset);
	}

	private int getStartGlyphId(int groupIndex) {
		int groupOffset = CmapFormat12Offsets.groups.offset + (groupIndex * groupSize) + CmapFormat12GroupOffsets.startGlyphID.offset;
		return readUnsignedIntAsInt(groupOffset);
	}

	private enum CmapFormat12Offsets implements Offset {
		format(0), reserved(2), length(4), language(8), nGroups(12), groups(16);

		private final int offset;

		private CmapFormat12Offsets(int offset) {
			this.offset = offset;
		}

		@Override
		public int getOffset() {
			return offset;
		}
	}

	private enum CmapFormat12GroupOffsets implements Offset {
		startCharCode(0), endCharCode(4), startGlyphID(8);

		private final int offset;

		private CmapFormat12GroupOffsets(int offset) {
			this.offset = offset;
		}

		@Override
		public int getOffset() {
			return offset;
		}
	}
}