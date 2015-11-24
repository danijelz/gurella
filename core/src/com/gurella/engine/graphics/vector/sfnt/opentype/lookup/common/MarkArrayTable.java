package com.gurella.engine.graphics.vector.sfnt.opentype.lookup.common;

import com.gurella.engine.graphics.vector.sfnt.RandomAccessFile;
import com.gurella.engine.graphics.vector.sfnt.Table;

public class MarkArrayTable extends Table {
	private static final int markRecordSize = 4;
	
	private AnchorTableReader anchorTableReader;
	
	public MarkArrayTable(RandomAccessFile raf, int offset) {
		super(raf, offset);
		anchorTableReader = new AnchorTableReader(raf);
	}
	
	public int getMarkCount() {
		return readUnsignedShort(MarkArrayOffsets.MarkCount);
	}
	
	public int getMarkClass(int index) {
		return readUnsignedShort(MarkArrayOffsets.MarkRecords.offset + (index * markRecordSize));
	}
	
	private int getMarkAnchorOffset(int index) {
		return offset + readUnsignedShort(MarkArrayOffsets.MarkRecords.offset + (index * markRecordSize) + MarkRecordOffsets.MarkAnchor.offset);
	}
	
	public short getXCoordinate(int index) {
		int markAnchorOffset = getMarkAnchorOffset(index);
		if(markAnchorOffset < 0) {
			return 0;
		}
		return anchorTableReader.getXCoordinate(markAnchorOffset);
	}
	
	public short getYCoordinate(int index) {
		int markAnchorOffset = getMarkAnchorOffset(index);
		if(markAnchorOffset < 0) {
			return 0;
		}
		return anchorTableReader.getYCoordinate(markAnchorOffset);
	}
	
	public int getAnchorPoint(int index) {
		int markAnchorOffset = getMarkAnchorOffset(index);
		if(markAnchorOffset < 0) {
			return 0;
		}
		return anchorTableReader.getAnchorPoint(markAnchorOffset);
	}

	private enum MarkArrayOffsets implements Offset {
		MarkCount(0), 
		MarkRecords(2);

		private final int offset;

		private MarkArrayOffsets(int offset) {
			this.offset = offset;
		}

		@Override
		public int getOffset() {
			return offset;
		}
	}
	
	private enum MarkRecordOffsets implements Offset {
		Class(0), 
		MarkAnchor(2);
		
		private final int offset;
		
		private MarkRecordOffsets(int offset) {
			this.offset = offset;
		}
		
		@Override
		public int getOffset() {
			return offset;
		}
	}
}
