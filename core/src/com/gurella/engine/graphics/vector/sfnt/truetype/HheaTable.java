package com.gurella.engine.graphics.vector.sfnt.truetype;

import com.gurella.engine.graphics.vector.sfnt.RandomAccessFile;
import com.gurella.engine.graphics.vector.sfnt.SfntTable;
import com.gurella.engine.graphics.vector.sfnt.TableDirectory;
import com.gurella.engine.graphics.vector.sfnt.TrueTypeTableDirectory;

public class HheaTable extends SfntTable {
	public HheaTable(TrueTypeTableDirectory sfntHeaderTable, int tag, long checkSum, int offset, int length) {
		super(sfntHeaderTable, offset, tag, checkSum, length);
	}
	
	public HheaTable(RandomAccessFile raf, TableDirectory directoryTable, int tag, long checkSum, int offset, int length) {
		super(raf, directoryTable, offset, tag, checkSum, length);
	}
	
	public float getVersion() {
		return readFixed(HheaOffset.version);
	}

	public short getAscender() {
		return readShort(HheaOffset.Ascender);
	}

	public short getDescender() {
		return readShort(HheaOffset.Descender);
	}

	public short getLineGap() {
		return readShort(HheaOffset.LineGap);
	}

	public int getAdvanceWidthMax() {
		return readUnsignedShort(HheaOffset.advanceWidthMax);
	}

	public short getMinLeftSideBearing() {
		return readShort(HheaOffset.minLeftSideBearing);
	}

	public short getMinRightSideBearing() {
		return readShort(HheaOffset.minRightSideBearing);
	}

	public short getXMaxExtent() {
		return readShort(HheaOffset.xMaxExtent);
	}

	public short getCaretSlopeRise() {
		return readShort(HheaOffset.caretSlopeRise);
	}

	public short getCaretSlopeRun() {
		return readShort(HheaOffset.caretSlopeRun);
	}

	public short getMetricDataFormat() {
		return readShort(HheaOffset.metricDataFormat);
	}

	public int getNumberOfHMetrics() {
		return readUnsignedShort(HheaOffset.numberOfHMetrics);
	}

	private enum HheaOffset implements Offset {
	    version(0),
	    Ascender(4),
	    Descender(6),
	    LineGap(8),
	    advanceWidthMax(10),
	    minLeftSideBearing(12),
	    minRightSideBearing(14),
	    xMaxExtent(16),
	    caretSlopeRise(18),
	    caretSlopeRun(20),
	    caretOffset(22),
	    metricDataFormat(32),
	    numberOfHMetrics(34);

		private final int offset;

		private HheaOffset(int offset) {
			this.offset = offset;
		}
		
		@Override
		public int getOffset() {
			return offset;
		}
	}
}
