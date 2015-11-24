package com.gurella.engine.graphics.vector.sfnt.truetype.glyf;

public class GlyfTableConstants {
	static final byte onCurve = 0x01;
	static final byte xShortVector = 0x02;
	static final byte yShortVector = 0x04;
	static final byte repeat = 0x08;
	static final byte xDual = 0x10;
	static final byte yDual = 0x20;
	
	static final short ARG_1_AND_2_ARE_WORDS = 0x0001;
	static final short ARGS_ARE_XY_VALUES = 0x0002;
	static final short ROUND_XY_TO_GRID = 0x0004;
	static final short WE_HAVE_A_SCALE = 0x0008;
	static final short MORE_COMPONENTS = 0x0020;
	static final short WE_HAVE_AN_X_AND_Y_SCALE = 0x0040;
	static final short WE_HAVE_A_TWO_BY_TWO = 0x0080;
	static final short WE_HAVE_INSTRUCTIONS = 0x0100;
	static final short USE_MY_METRICS = 0x0200;
}
