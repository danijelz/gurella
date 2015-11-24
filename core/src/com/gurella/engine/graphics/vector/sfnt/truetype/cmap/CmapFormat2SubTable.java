package com.gurella.engine.graphics.vector.sfnt.truetype.cmap;

import com.gurella.engine.graphics.vector.sfnt.SfntDataType;

class CmapFormat2SubTable extends CmapSubTable {
	CmapFormat2SubTable(CmapTable parentTable, int offset) {
		super(parentTable, offset);
	}

	@Override
	public int getLength() {
		return readUnsignedShort(CmapFormat2Offsets.length);
	}

	@Override
	public int getLanguage() {
		return readUnsignedShort(CmapFormat2Offsets.language);
	}

	@Override
	public int getGlyphId(int charCode) {
		int subHeaderOffset = getSubHeaderOffset(charCode);

		if (subHeaderOffset > 0) {
			int firstCode = readUnsignedShort(subHeaderOffset + CmapFormat2SubheaderOffsets.firstCode.offset);
			int entryCount = readUnsignedShort(subHeaderOffset + CmapFormat2SubheaderOffsets.entryCount.offset);
			short idDelta = readShort(subHeaderOffset + CmapFormat2SubheaderOffsets.idDelta.offset);
			int subHeaderIdRangeOffset = subHeaderOffset + CmapFormat2SubheaderOffsets.idRangeOffset.offset;
			int idRangeOffset = readUnsignedShort(subHeaderIdRangeOffset);

			int idx = (charCode & 0xFF) - firstCode;
			if (idx < entryCount && idRangeOffset != 0) {
				idx = readShort(subHeaderIdRangeOffset + idRangeOffset + 2 * idx);

				if (idx != 0)
					return (idx + idDelta) & 0xFFFF;
			}
		}

		return 0;
	}

	private int getSubHeaderOffset(int charCode) {
		if (charCode < 0x10000) {
			int lowChar = charCode & 0xFF;
			int highChar = charCode >> 8;
			int currentOffset = CmapFormat2Offsets.subHeaderKeys.offset;
			int subHeadersOffset = currentOffset + SfntDataType.unsignedShortValue.size * 256;
			int subHeaderOffset;

			if (highChar == 0) {
				subHeaderOffset = subHeadersOffset;
				currentOffset += lowChar * 2;

				if (readUnsignedShort(currentOffset) != 0) {
					return -1;
				}
			} else {
				currentOffset += highChar * 2;
				subHeaderOffset = subHeadersOffset + readUnsignedShort(currentOffset);

				if (subHeaderOffset == subHeadersOffset) {
					return -1;
				}
			}

			return subHeaderOffset;
		}

		return -1;
	}

	private enum CmapFormat2Offsets implements Offset {
		format(0), length(2), language(4), subHeaderKeys(6), subHeaders(-1), glyphIndexArray(-1);

		private final int offset;

		private CmapFormat2Offsets(int offset) {
			this.offset = offset;
		}

		@Override
		public int getOffset() {
			return offset;
		}
	}

	private enum CmapFormat2SubheaderOffsets implements Offset {
		firstCode(0), entryCount(2), idDelta(4), idRangeOffset(6);

		private final int offset;

		private CmapFormat2SubheaderOffsets(int offset) {
			this.offset = offset;
		}

		@Override
		public int getOffset() {
			return offset;
		}
	}
}