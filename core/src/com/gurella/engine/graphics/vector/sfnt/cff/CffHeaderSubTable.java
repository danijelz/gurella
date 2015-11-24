package com.gurella.engine.graphics.vector.sfnt.cff;

class CffHeaderSubTable extends CffSubTable {
	short majorVersion;
	short minorVersion;
	short hdrSize;
	short offSize;
	
	CffHeaderSubTable(CffTable cffTable, int offset) {
		super(cffTable, offset);
	}

	@Override
	protected void init() {
		super.init();
		majorVersion = raf.readUnsignedByte();
		minorVersion = raf.readUnsignedByte();
		hdrSize = raf.readUnsignedByte();
		offSize = raf.readUnsignedByte();
	    tableEndOffset = offset + 4;
	}
}
