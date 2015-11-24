package com.gurella.engine.graphics.vector.sfnt.truetype.cmap;

class CmapFormat13SubTable extends CmapSubTable {
	private static final int groupSize = 12;

	CmapFormat13SubTable(CmapTable parentTable, int offset) {
		super(parentTable, offset);
	}

	@Override
	public int getLength() {
		return readUnsignedIntAsInt(CmapFormat13Offsets.length);
	}

	@Override
	public int getLanguage() {
		return readUnsignedIntAsInt(CmapFormat13Offsets.nGroups);
	}

	private int getNumGroups() {
		return readUnsignedIntAsInt(CmapFormat13Offsets.language);
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
				return getGroupGlyphId(mid);
			}
		}

		return 0;
	}

	private int getStartCharCode(int groupIndex) {
		int groupOffset = CmapFormat13Offsets.groups.offset + (groupIndex * groupSize);
		return readUnsignedIntAsInt(groupOffset);
	}

	private int getEndCharCode(int groupIndex) {
		int groupOffset = CmapFormat13Offsets.groups.offset + (groupIndex * groupSize) + CmapFormat13GroupOffsets.endCharCode.offset;
		return readUnsignedIntAsInt(groupOffset);
	}

	private int getGroupGlyphId(int groupIndex) {
		int groupOffset = CmapFormat13Offsets.groups.offset + (groupIndex * groupSize) + CmapFormat13GroupOffsets.glyphID.offset;
		return readUnsignedIntAsInt(groupOffset);
	}

	private enum CmapFormat13Offsets implements Offset {
		format(0), reserved(2), length(4), language(8), nGroups(12), groups(16);

		private final int offset;

		private CmapFormat13Offsets(int offset) {
			this.offset = offset;
		}

		@Override
		public int getOffset() {
			return offset;
		}
	}

	private enum CmapFormat13GroupOffsets implements Offset {
		startCharCode(0), endCharCode(4), glyphID(8);

		private final int offset;

		private CmapFormat13GroupOffsets(int offset) {
			this.offset = offset;
		}

		@Override
		public int getOffset() {
			return offset;
		}
	}
}