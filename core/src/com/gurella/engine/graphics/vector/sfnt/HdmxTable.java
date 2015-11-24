package com.gurella.engine.graphics.vector.sfnt;

public class HdmxTable extends SfntTable {
	HdmxTable(TrueTypeTableDirectory sfntHeaderTable, int tag, long checkSum, int offset, int length) {
		super(sfntHeaderTable, offset, tag, checkSum, length);
	}
	
	public HdmxTable(RandomAccessFile raf, TableDirectory directoryTable, int tag, long checkSum, int offset, int length) {
		super(raf, directoryTable, offset, tag, checkSum, length);
	}
	
	public int getVersion() {
		return readUnsignedShort(HdmxOffsets.version);
	}
	
	private short getNumRecords() {
		return readShort(HdmxOffsets.numRecords);
	}
	
	private int getRecordSize() {
		return readInt(HdmxOffsets.recordSize);
	}
	
	private int getDeviceRecordOffset(int index) {
		return HdmxOffsets.deviceRecords.offset + index * getRecordSize();
	}
	
	private int findDeviceRecordOffset(int pixelSize) {
		for(int i = 0; i < getNumRecords(); i++) {
			int deviceRecordOffset = getDeviceRecordOffset(i);
			if(pixelSize == readByte(deviceRecordOffset)) {
				return deviceRecordOffset;
			}
		}
		return -1;
	}
	
	public byte getMaximumWidth(int pixelSize) {
		int deviceRecordOffset = findDeviceRecordOffset(pixelSize);
		if(deviceRecordOffset < 0) {
			return -1;
		}
		return readByte(deviceRecordOffset + 1);
	}
	
	public int getWidth(int pixelSize, int glyiphId) {
		int numGlyphs = parentTable.getNumGlyphs();
		if(glyiphId < 0 || glyiphId >= numGlyphs) {
			return -1;
		}
		
		int deviceRecordOffset = findDeviceRecordOffset(pixelSize);
		if(deviceRecordOffset < 0) {
			return -1;
		}
		
		return readByte(deviceRecordOffset + 2 + glyiphId);
	}
	
	private enum HdmxOffsets implements Offset {
		version(0),
		numRecords(2),
		recordSize(4),
		deviceRecords(8);

		private final int offset;

		private HdmxOffsets(int offset) {
			this.offset = offset;
		}
		
		@Override
		public int getOffset() {
			return offset;
		}
	}
}
