package com.gurella.engine.graphics.vector.sfnt.cff;

class CffSubrIndexSubTable extends CffIndexSubTable<byte[]> {
	int bias;
	
	CffSubrIndexSubTable(CffTable cffTable, int offset) {
		super(cffTable, offset);
	}
	
	@Override
	protected void init() {
		super.init();
		if (values.size < 1240) {
	        bias = 107;
	    } else if (values.size < 33900) {
	        bias = 1131;
	    } else {
	        bias = 32768;
	    }
	}

	@Override
	byte[] createValue(byte[] valueData) {
		return valueData;
	}
}
