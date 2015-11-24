package com.gurella.engine.graphics.vector.sfnt.truetype.kern;

import com.gurella.engine.graphics.vector.sfnt.SfntDataType;

class KernVersion1Format3SubTable extends KernVersion1SubTable {
	private int leftClassOffset;
	private int rightClassOffset;
	private int kernIndexOffset;

	public KernVersion1Format3SubTable(KernVersionTable parentTable, int offset) {
		super(parentTable, offset);

		int glyphCount = readUnsignedShort(KernVersion1Format3Offset.glyphCount);
		short kernValueCount = readUnsignedByte(KernVersion1Format3Offset.glyphCount);
		leftClassOffset = KernVersion1Format3Offset.kernValue.offset + (kernValueCount * SfntDataType.fWordValue.size);
		rightClassOffset = leftClassOffset + (glyphCount * SfntDataType.unsignedByteValue.size);
		kernIndexOffset = rightClassOffset + (glyphCount * SfntDataType.unsignedByteValue.size);
	}

	@Override
	public int getKerning(int leftGlyphId, int rightGlyphId) {
		int glyphCount = readUnsignedShort(KernVersion1Format3Offset.glyphCount);
		if (leftGlyphId >= glyphCount || rightGlyphId >= glyphCount) {
			return 0;
		}

		short leftClass = readUnsignedByte(leftClassOffset + leftGlyphId);
		short rightClass = readUnsignedByte(rightClassOffset + rightGlyphId);
		short kernIndex = readUnsignedByte(kernIndexOffset + (leftClass * glyphCount + rightClass));
		return readShort(KernVersion1Format3Offset.kernValue.offset + (kernIndex * SfntDataType.fWordValue.size));
	}

	private enum KernVersion1Format3Offset implements Offset {
		length(0),
		format(4),
		coverage(5),
		tupleIndex(6),
		glyphCount(8),
		kernValueCount(10),
		leftClassCount(11),
		rightClassCount(12),
		flags(13),
		kernValue(14),
		leftClass(-1),
		rightClass(-1),
		kernIndex(-1);

		private final int offset;

		private KernVersion1Format3Offset(int offset) {
			this.offset = offset;
		}

		@Override
		public int getOffset() {
			return offset;
		}
	}
}