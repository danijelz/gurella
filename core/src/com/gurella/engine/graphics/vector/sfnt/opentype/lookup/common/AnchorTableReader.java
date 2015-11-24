package com.gurella.engine.graphics.vector.sfnt.opentype.lookup.common;

import com.gurella.engine.graphics.vector.sfnt.RandomAccessFile;
import com.gurella.engine.graphics.vector.sfnt.Table.Offset;

public class AnchorTableReader {
	private final RandomAccessFile raf;
	private DeviceTableReader deviceTableReader;
	
	public AnchorTableReader(RandomAccessFile raf) {
		this.raf = raf;
	}
	
	private DeviceTableReader getDeviceTableReader() {
		if(deviceTableReader == null) {
			deviceTableReader = new DeviceTableReader(raf);
		}
		return deviceTableReader;
	}
	
	public int getAnchorFormat(int offset) {
		raf.setPosition(offset);
		return raf.readUnsignedShort();
	}
	
	public short getXCoordinate(int offset) {
		raf.setPosition(offset + AnchorOffsets.XCoordinate.offset);
		return raf.readShort();
	}
	
	public short getYCoordinate(int offset) {
		raf.setPosition(offset + AnchorOffsets.YCoordinate.offset);
		return raf.readShort();
	}
	
	public int getAnchorPoint(int offset) {
		int anchorFormat = getAnchorFormat(offset);
		if(anchorFormat == 2) {
			raf.setPosition(offset + AnchorOffsets.AnchorPoint.offset);
			return raf.readUnsignedShort();
		} else {
			return -1;
		}
	}
	
	private enum AnchorOffsets implements Offset {
		AnchorFormat(0), 
		XCoordinate(2), 
		YCoordinate(4), 
		
		AnchorPoint(6), 
		
		XDeviceTable(6), 
		YDeviceTable(8);

		private final int offset;

		private AnchorOffsets(int offset) {
			this.offset = offset;
		}

		@Override
		public int getOffset() {
			return offset;
		}
	}
}
