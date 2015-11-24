package com.gurella.engine.graphics.vector.sfnt.cff;

class CffCharStringsIndexSubTable extends CffIndexSubTable<byte[]> {
	CffCharStringsIndexSubTable(CffTable cffTable, int offset) {
		super(cffTable, offset);
	}
	
	@Override
	byte[] createValue(byte[] valueData) {
		return valueData;
	}
}
