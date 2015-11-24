package com.gurella.engine.graphics.vector.sfnt.opentype.lookup.position.marktoligature;

import com.gurella.engine.graphics.vector.sfnt.RandomAccessFile;
import com.gurella.engine.graphics.vector.sfnt.SfntDataType;
import com.gurella.engine.graphics.vector.sfnt.Table;
import com.gurella.engine.graphics.vector.sfnt.opentype.lookup.common.AnchorTableReader;

public class LigatureArrayTable extends Table {
	private final int classCount;
	private AnchorTableReader anchorTableReader;
	
	public LigatureArrayTable(RandomAccessFile raf, int offset, int classCount) {
		super(raf, offset);
		this.classCount = classCount;
		anchorTableReader = new AnchorTableReader(raf);
	}
	
	public int getLigatureCount() {
		return readUnsignedShort(BaseArrayOffsets.LigatureCount);
	}
	
	private int getComponentRecordAnchorOffset(int ligatureIndex, int componentIndex, int classIndex) {
		if(classIndex < 0 || componentIndex >= classCount) {
			return -1;
		}
		
		int ligatureCount = getLigatureCount();
		if(ligatureIndex < 0 || ligatureIndex >= ligatureCount) {
			return -1;
		}

		int ligatureAttachOffset = BaseArrayOffsets.LigatureCount.offset + ligatureIndex * SfntDataType.offsetValue.size;
		int componentCount = readUnsignedShort(ligatureAttachOffset);
		if(componentIndex < 0 || componentIndex >= componentCount) {
			return -1;
		}
		
		int componentRecordSize = classCount * SfntDataType.offsetValue.size;
		int componentRecordOffset = ligatureAttachOffset + SfntDataType.unsignedShortValue.size + (componentIndex * componentRecordSize);
		return ligatureAttachOffset + readUnsignedShort(componentRecordOffset + (classIndex * SfntDataType.offsetValue.size));
	}
	
	public short getXCoordinate(int ligatureIndex, int componentIndex, int classIndex) {
		int componentRecordAnchorOffset = getComponentRecordAnchorOffset(ligatureIndex, componentIndex, classIndex);
		if(componentRecordAnchorOffset < 0) {
			return 0;
		}
		return anchorTableReader.getXCoordinate(componentRecordAnchorOffset);
	}
	
	public short getYCoordinate(int ligatureIndex, int componentIndex, int classIndex) {
		int componentRecordAnchorOffset = getComponentRecordAnchorOffset(ligatureIndex, componentIndex, classIndex);
		if(componentRecordAnchorOffset < 0) {
			return 0;
		}
		return anchorTableReader.getYCoordinate(componentRecordAnchorOffset);
	}
	
	public int getAnchorPoint(int ligatureIndex, int componentIndex, int classIndex) {
		int componentRecordAnchorOffset = getComponentRecordAnchorOffset(ligatureIndex, componentIndex, classIndex);
		if(componentRecordAnchorOffset < 0) {
			return 0;
		}
		return anchorTableReader.getAnchorPoint(componentRecordAnchorOffset);
	}
	
	public boolean isPairCovered(int ligatureIndex, int componentIndex, int classIndex) {
		int componentRecordAnchorOffset = getComponentRecordAnchorOffset(ligatureIndex, componentIndex, classIndex);
		return componentRecordAnchorOffset >= 0;
	}

	private enum BaseArrayOffsets implements Offset {
		LigatureCount(0), 
		LigatureAttach(2);

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
