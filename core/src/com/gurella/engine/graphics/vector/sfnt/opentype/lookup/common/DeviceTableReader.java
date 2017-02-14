package com.gurella.engine.graphics.vector.sfnt.opentype.lookup.common;

import com.gurella.engine.graphics.vector.sfnt.RandomAccessFile;
import com.gurella.engine.graphics.vector.sfnt.SfntDataType;
import com.gurella.engine.graphics.vector.sfnt.Table.Offset;

public class DeviceTableReader {
	final RandomAccessFile raf;
	
	public DeviceTableReader(RandomAccessFile raf) {
		this.raf = raf;
	}
	
	public boolean hasRecord(int offset, int ppemSize) {
		return getStartSize(offset) <= ppemSize && getEndSize(offset) <= ppemSize;
	}
	
	public int getStartSize(int offset) {
		raf.setPosition(offset);
		return raf.readUnsignedShort();
	}
	
	public int getEndSize(int offset) {
		raf.setPosition(offset + DeviceTableOffsets.endSize.offset);
		return raf.readUnsignedShort();
	}
	
	public int getDeltaFormat(int offset) {
		raf.setPosition(offset + DeviceTableOffsets.deltaFormat.offset);
		return raf.readUnsignedShort();
	}
	
	public short getDeltaSize(int deltaFormat) {
		switch (deltaFormat) {
		case 1:
			return 2;
		case 2:
			return 4;
		case 3:
			return 8;
		default:
			return -1;
		}
	}
	
	public int getDeltaValue(int offset, int ppemSize) {
		int startSize = getStartSize(offset);
		if(startSize > ppemSize) {
			return 0;
		}
		
		int endSize = getEndSize(offset);
		if(endSize < ppemSize) {
			return 0;
		}

		int deltaFormat = getDeltaFormat(offset);
		short deltaSize = getDeltaSize(deltaFormat);
		int index = ppemSize - startSize;
		int nuberOfFullValues = (deltaSize * index) / SfntDataType.unsignedShortValue.size;
		int deltaValueOffset = offset + nuberOfFullValues;
		raf.setPosition(deltaValueOffset);
		int deltaValue = raf.readUnsignedShort();
		
		int deltaValueIndex = (index - (nuberOfFullValues * (SfntDataType.unsignedShortValue.size / deltaSize)));
		return extractDeltaValue(deltaValue, deltaSize, deltaValueIndex);
	}
	
	private static int extractDeltaValue(int deltaValue, short deltaSize, int deltaValueIndex) {
		int endBitPos = deltaSize * deltaValueIndex;
		int startBitPos = endBitPos + deltaSize;
		// Clear unnecessary high bits
		int tempValue = deltaValue << (31 - endBitPos);
		// Shift back to the lowest bits
		return tempValue >>> (31 - endBitPos + startBitPos);
	}
	
	private enum DeviceTableOffsets implements Offset {
		startSize(0), 
		endSize(2), 
		deltaFormat(4), 
		deltaValue(6);

		private final int offset;

		private DeviceTableOffsets(int offset) {
			this.offset = offset;
		}

		@Override
		public int getOffset() {
			return offset;
		}
	}
}
