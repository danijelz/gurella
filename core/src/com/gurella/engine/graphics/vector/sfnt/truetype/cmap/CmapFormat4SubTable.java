package com.gurella.engine.graphics.vector.sfnt.truetype.cmap;

import com.gurella.engine.graphics.vector.sfnt.SfntDataType;

class CmapFormat4SubTable extends CmapSubTable {
	private int arrayValueLength;
	private boolean binarySearchEnabled;
	private boolean overlapping;

	CmapFormat4SubTable(CmapTable parentTable, int offset) {
		super(parentTable, offset);
		initSearchData();
	}

	private void initSearchData() {
		int segCount = getSegCount();
		if (segCount != 0) {
			arrayValueLength = SfntDataType.unsignedShortValue.size * segCount;

			int lastStartCount = getStartCount(0);
			int lastEndCount = getEndCount(0);

			for (int i = 1; i < segCount; i++) {
				int endCount = getEndCount(i);
				int startCount = getStartCount(i);

				if (startCount <= lastEndCount) {
					if (lastStartCount > startCount || lastEndCount > endCount) {
						binarySearchEnabled = false;
						return;
					} else {
						overlapping = true;
					}
				}

				lastEndCount = endCount;
				lastStartCount = startCount;
			}
		}

		binarySearchEnabled = true;
	}

	@Override
	public int getLength() {
		return readUnsignedShort(CmapFormat4Offsets.length);
	}

	@Override
	public int getLanguage() {
		return readUnsignedShort(CmapFormat4Offsets.language);
	}

	@Override
	public int getGlyphId(int charCode) {
		if (charCode >= 0x10000) {
			return 0;
		}

		int segCount2 = getSegCount2();
		if (segCount2 < 1) {
			return 0;
		}

		int segCount = segCount2 >> 1;

		if (binarySearchEnabled) {
			return findGlyphIdByBinarySearch(charCode, segCount);
		} else {
			return findGlyphIdByLinearSearch(charCode, segCount);
		}
	}

	private int findGlyphIdByBinarySearch(int charCode, int segCount) {
		int segIndex = getSegIndexByBinarySearch(charCode, segCount);
		if (segIndex < 0) {
			return 0;
		}

		if (overlapping) {
			segIndex = findMinSegIndex(charCode, segIndex);
		}

		return getGlyphIdFromSeg(charCode, segIndex, segCount);
	}

	private int getSegIndexByBinarySearch(int charCode, int segCount) {
		int lo = 0;
		int hi = segCount - 1;

		while (lo <= hi) {
			int mid = lo + (hi - lo) / 2;
			int midEndCount = getEndCount(mid);

			if (charCode > midEndCount) {
				lo = mid + 1;
			} else if (charCode <= midEndCount && charCode < getStartCount(mid)) {
				hi = mid - 1;
			} else {
				return mid;
			}
		}
		return -1;
	}

	private int findMinSegIndex(int charCode, int segIndex) {
		int validSegIndex = segIndex;
		while (validSegIndex > 0 && isValidSegment(charCode, validSegIndex - 1)) {
			validSegIndex--;
		}
		return validSegIndex;
	}

	private boolean isValidSegment(int charCode, int segIndex) {
		int endCount = getEndCount(segIndex);
		if (endCount < charCode) {
			return false;
		}
		int startCount = getStartCount(segIndex);
		return charCode >= startCount && charCode <= endCount;
	}

	private int getGlyphIdFromSeg(int charCode, int segIndex, int segCount) {
		int endCount = getEndCount(segIndex);
		int startCount = getStartCount(segIndex);
		int idDelta = getIdDelta(segIndex);
		int idRagesStartOfsset = CmapFormat4Offsets.endCount.offset + (3 * arrayValueLength) + SfntDataType.unsignedShortValue.size;
		int segmentIdRangeOffset = idRagesStartOfsset + SfntDataType.unsignedShortValue.size * segIndex;
		int idRangeOffsetValue = readShort(segmentIdRangeOffset);
		int glyphIdOffset = segmentIdRangeOffset + idRangeOffsetValue + (charCode - startCount) * 2;

		if (segIndex == segCount - 1 && startCount == 0xFFFF && endCount == 0xFFFF) {
			int cmapTableLimit = parentTable.offset + parentTable.length;
			if (idRangeOffsetValue != 0 && (offset + glyphIdOffset) > cmapTableLimit) {
				idDelta = 1;
				idRangeOffsetValue = 0;
			}
		}

		if (idRangeOffsetValue == 0) {
			return (charCode + idDelta) & 0xFFFF;
		} else {
			int baseGlyphId = readUnsignedShort(glyphIdOffset);
			return baseGlyphId == 0 ? 0 : (baseGlyphId + idDelta) & 0xFFFF;
		}
	}

	private int findGlyphIdByLinearSearch(int charCode, int segCount) {
		for (int i = 0; i < segCount; i++) {
			if (isValidSegment(charCode, i)) {
				return getGlyphIdFromSeg(charCode, i, segCount);
			}
		}

		return 0;
	}

	private int getSegCount2() {
		return readUnsignedShort(CmapFormat4Offsets.segCountX2);
	}

	private int getSegCount() {
		return getSegCount2() >> 1;
	}

	private int getEndCount(int index) {
		return readUnsignedShort(CmapFormat4Offsets.endCount.offset + SfntDataType.unsignedShortValue.size * index);
	}

	private int getStartCount(int index) {
		int startCountsOffset = CmapFormat4Offsets.endCount.offset + arrayValueLength + SfntDataType.unsignedShortValue.size;
		return readUnsignedShort(startCountsOffset + SfntDataType.unsignedShortValue.size * index);
	}

	private int getIdDelta(int index) {
		int idDeltasOffset = CmapFormat4Offsets.endCount.offset + (2 * arrayValueLength) + SfntDataType.unsignedShortValue.size;
		return readShort(idDeltasOffset + SfntDataType.unsignedShortValue.size * index);
	}

	private enum CmapFormat4Offsets implements Offset {
		format(0),
		length(2),
		language(4),
		segCountX2(6),
		searchRange(8),
		entrySelector(10),
		rangeShift(12),
		endCount(14),
		reservedPad(-1),
		startCount(-1),
		idDelta(-1),
		idRangeOffset(-1),
		glyphIdArray(-1);

		private final int offset;

		private CmapFormat4Offsets(int offset) {
			this.offset = offset;
		}

		@Override
		public int getOffset() {
			return offset;
		}
	}
}