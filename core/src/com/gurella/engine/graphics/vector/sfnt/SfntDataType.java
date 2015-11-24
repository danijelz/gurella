package com.gurella.engine.graphics.vector.sfnt;

public enum SfntDataType {
	byteValue(1, true), 
	unsignedByteValue(1, false),
	charValue(1, true), //by MS spec
	shortValue(2, true),
	unsignedShortValue(2, false),
	glyphIdValue(2, false),
	offsetValue(2, false),
	f2Dot14Value(2, true),
	fWordValue(2, true),
	unsignedFWordValue(2, false),
	unsignedInt24Value(3, false),
	fixedValue(4, true),
	intValue(4, true),
	unsignedIntValue(4, false),
	tagValue(4, false),
	longDateTimeValue(8, true)
	;
	
	public final int size;
	public final boolean signed;
	
	private SfntDataType(int size, boolean signed) {
		this.size = size;
		this.signed = signed;
	}
}
