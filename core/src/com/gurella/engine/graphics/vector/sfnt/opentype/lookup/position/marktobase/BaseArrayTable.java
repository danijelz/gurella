package com.gurella.engine.graphics.vector.sfnt.opentype.lookup.position.marktobase;

import com.gurella.engine.graphics.vector.sfnt.RandomAccessFile;
import com.gurella.engine.graphics.vector.sfnt.SfntDataType;
import com.gurella.engine.graphics.vector.sfnt.Table;
import com.gurella.engine.graphics.vector.sfnt.opentype.lookup.common.AnchorTableReader;

public class BaseArrayTable extends Table {
	private final int classCount;
	private AnchorTableReader anchorTableReader;
	
	public BaseArrayTable(RandomAccessFile raf, int offset, int classCount) {
		super(raf, offset);
		this.classCount = classCount;
		anchorTableReader = new AnchorTableReader(raf);
	}
	
	public int getBaseCount() {
		return readUnsignedShort(BaseArrayOffsets.BaseCount);
	}
	
	private int getBaseAnchorOffset(int baseIndex, int classIndex) {
		if(baseIndex < 0 || baseIndex >= getBaseCount()) {
			return -1;
		}
		
		if(classIndex < 0 || classIndex >= classCount) {
			return -1;
		}
		
		int baseRecordSize = classCount * SfntDataType.offsetValue.size;
		int baseRecordOffset = BaseArrayOffsets.BaseRecords.offset + (baseIndex * baseRecordSize);
		int baseAnchorOffset = baseRecordOffset + (SfntDataType.offsetValue.size * classIndex);
		return offset + readUnsignedShort(baseAnchorOffset);
	}
	
	public short getXCoordinate(int baseIndex, int classIndex) {
		int baseAnchorOffset = getBaseAnchorOffset(baseIndex, classIndex);
		if(baseAnchorOffset < 0) {
			return 0;
		}
		return anchorTableReader.getXCoordinate(baseAnchorOffset);
	}
	
	public short getYCoordinate(int baseIndex, int classIndex) {
		int baseAnchorOffset = getBaseAnchorOffset(baseIndex, classIndex);
		if(baseAnchorOffset < 0) {
			return 0;
		}
		return anchorTableReader.getYCoordinate(baseAnchorOffset);
	}
	
	public int getAnchorPoint(int baseIndex, int classIndex) {
		int baseAnchorOffset = getBaseAnchorOffset(baseIndex, classIndex);
		if(baseAnchorOffset < 0) {
			return 0;
		}
		return anchorTableReader.getAnchorPoint(baseAnchorOffset);
	}
	
	public boolean isPairCovered(int baseIndex, int classIndex) {
		int baseAnchorOffset = getBaseAnchorOffset(baseIndex, classIndex);
		return baseAnchorOffset >= 0;
	}

	private enum BaseArrayOffsets implements Offset {
		BaseCount(0), 
		BaseRecords(2);

		private final int offset;

		private BaseArrayOffsets(int offset) {
			this.offset = offset;
		}

		@Override
		public int getOffset() {
			return offset;
		}
	}
}
