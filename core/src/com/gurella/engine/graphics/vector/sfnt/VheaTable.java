package com.gurella.engine.graphics.vector.sfnt;

public class VheaTable extends SfntTable {
	VheaTable(TrueTypeTableDirectory descriptor, int tag, long checkSum, int offset, int length) {
		super(descriptor, offset, tag, checkSum, length);
	}
	
	public VheaTable(RandomAccessFile raf, TableDirectory directoryTable, int tag, long checkSum, int offset, int length) {
		super(raf, directoryTable, offset, tag, checkSum, length);
	}
	
	public float getVersion() {
		return readFixed(VheaTableOffsets.version);
	}

	public short getAscent() {
		return getVersion() == 1.0f ? readShort(VheaTableOffsets.ascent) : 0;
	}
	
	public short getVertTypoAscender() {
		return getVersion() == 1.1f ? readShort(VheaTableOffsets.vertTypoAscender) : 0;
	}

	public short getDescent() {
		return getVersion() == 1.0f ? readShort(VheaTableOffsets.descent) : 0;
	}

	public short getVertTypoDescender() {
		return getVersion() == 1.1f ? readShort(VheaTableOffsets.vertTypoDescender) : 0;
	}

	public short getLineGap() {
		return getVersion() == 1.0f ? readShort(VheaTableOffsets.lineGap) : 0;
	}
	
	public short getVertTypoLineGap() {
		return getVersion() == 1.1f ? readShort(VheaTableOffsets.vertTypoLineGap) : 0;
	}

	public short getAdvanceHeightMax() {
		return readShort(VheaTableOffsets.advanceHeightMax);
	}

	public short getMinTopSideBearing() {
		return readShort(VheaTableOffsets.minTopSideBearing);
	}

	public short getMinBottomSideBearing() {
		return readShort(VheaTableOffsets.minBottomSideBearing);
	}

	public short getyMaxExtent() {
		return readShort(VheaTableOffsets.yMaxExtent);
	}

	public short getCaretSlopeRise() {
		return readShort(VheaTableOffsets.caretSlopeRise);
	}

	public short getCaretSlopeRun() {
		return readShort(VheaTableOffsets.caretSlopeRun);
	}

	public short getCaretOffset() {
		return readShort(VheaTableOffsets.caretOffset);
	}

	public short getMetricDataFormat() {
		return readShort(VheaTableOffsets.metricDataFormat);
	}

	public int getNumOfLongVerMetrics() {
		return readUnsignedShort(VheaTableOffsets.numOfLongVerMetrics);
	}
	
	private enum VheaTableOffsets implements Offset {
		version(0), 
		ascent(4), 
		vertTypoAscender(4), 
		descent(6), 
		vertTypoDescender(6), 
		lineGap(8), 
		vertTypoLineGap(8), 
		advanceHeightMax(10),
		minTopSideBearing(12),
		minBottomSideBearing(14),
		yMaxExtent(16),
		caretSlopeRise(18),
		caretSlopeRun(20),
		caretOffset(22),
		metricDataFormat(32),
		numOfLongVerMetrics(34);

		private final int offset;

		private VheaTableOffsets(int offset) {
			this.offset = offset;
		}

		@Override
		public int getOffset() {
			return offset;
		}
	}
}
