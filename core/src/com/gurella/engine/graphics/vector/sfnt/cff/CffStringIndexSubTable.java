package com.gurella.engine.graphics.vector.sfnt.cff;

public class CffStringIndexSubTable extends CffIndexSubTable<String> {
	CffStringIndexSubTable(CffTable cffTable, int offset) {
		super(cffTable, offset);
	}

	@Override
	String createValue(byte[] valueData) {
		if(valueData[0] == 0) {
			return "";
		} else {
			return new String(valueData);
		}
	}
}
