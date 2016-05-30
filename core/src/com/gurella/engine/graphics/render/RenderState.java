package com.gurella.engine.graphics.render;

import com.badlogic.gdx.graphics.Color;

public class RenderState {
	private boolean blending;
	private int blendSFactor;
	private int blendDFactor;
	
	private int depthFunc;
	private float depthRangeNear;
	private float depthRangeFar;
	private boolean depthMask;
	
	private boolean stencil;
	private int frontStencilMask;
	private int frontSfailFunction;
	private int frontDpfailFunction;
	private int frontPassFunction;
	private int backStencilMask;
	private int backSfailFunction;
	private int backDpfailFunction;
	private int backfrontPassFunction;
	
	private int cullFace;
	
	private Color clearBackgroundColorValue;
	private float clearDepthValue;
	private int clearStencilValue;
}
