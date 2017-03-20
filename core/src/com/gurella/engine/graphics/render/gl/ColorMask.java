package com.gurella.engine.graphics.render.gl;

public enum ColorMask {
	none(false, false, false, false),
	r(true, false, false, false),
	rg(true, true, false, false),
	rgb(true, true, true, false),
	rgba(true, true, true, true),
	g(false, true, false, false),
	gb(false, true, true, false),
	gba(false, true, true, true),
	b(false, false, true, false),
	ba(false, false, true, true),
	a(false, false, false, true);
	
	public static final ColorMask defaultValue = rgba;

	public final boolean rMask;
	public final boolean gMask;
	public final boolean bMask;
	public final boolean aMask;

	private ColorMask(boolean colorMaskR, boolean colorMaskG, boolean colorMaskB, boolean colorMaskA) {
		this.rMask = colorMaskR;
		this.gMask = colorMaskG;
		this.bMask = colorMaskB;
		this.aMask = colorMaskA;
	}
}
