package com.gurella.engine.graphics.vector;

interface PathConstants {
	static final int moveTo = 0;
	static final int lineTo = 1;
	static final int cubicTo = 2;
	static final int close = 3;
	static final int winding = 4;
	
	static final int PT_CORNER = 0x01;
	static final int PT_LEFT = 0x02;
	static final int PT_BEVEL = 0x04;
	static final int PT_INNERBEVEL = 0x08;
	static final int PT_FILL_BEVEL = 0x10;
	static final int PT_FILL_INNERBEVEL = 0x20;
}
