package com.gurella.engine.graphics.vector.sfnt.truetype.kern;

import com.gurella.engine.graphics.vector.sfnt.SfntDataType;

class KernVersion0Format0SubTable extends KernVersion0SubTable {
	private static final byte pairSize = 6;

	public KernVersion0Format0SubTable(KernVersionTable parentTable, int offset) {
		super(parentTable, offset);
	}

	@Override
	public int getKerning(int leftGlyphId, int rightGlyphId) {
		long glyphPairId = (leftGlyphId << 16) + (rightGlyphId << 0);

		int lo = 0;
		int hi = getNumPairs() - 1;
		while (lo <= hi) {
			int mid = lo + (hi - lo) / 2;
			long midGlyphId = getGlyphPairId(mid);

			if (glyphPairId < midGlyphId) {
				hi = mid - 1;
			} else if (glyphPairId > midGlyphId) {
				lo = mid + 1;
			} else {
				return getValueFromPair(mid);
			}
		}
		return 0;
	}

	private int getNumPairs() {
		return readUnsignedShort(KernVersion0Format0Offset.nPairs);
	}

	private long getGlyphPairId(int index) {
		int relativeOffset = KernVersion0Format0Offset.pairs.offset + pairSize * index;
		return readUnsignedInt(relativeOffset);
	}

	private int getValueFromPair(int index) {
		int relativeOffset = KernVersion0Format0Offset.pairs.offset + (pairSize * index) + SfntDataType.unsignedIntValue.size;
		return readShort(relativeOffset);
	}

	private enum KernVersion0Format0Offset implements Offset {
		version(0), length(2), format(4), coverage(5), nPairs(6), searchRange(8), entrySelector(10), rangeShift(12), pairs(14);

		private final int offset;

		private KernVersion0Format0Offset(int offset) {
			this.offset = offset;
		}

		@Override
		public int getOffset() {
			return offset;
		}
	}
}