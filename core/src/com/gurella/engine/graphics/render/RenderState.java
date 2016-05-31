package com.gurella.engine.graphics.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.utils.TextureBinder;
import com.badlogic.gdx.utils.IntMap;

public class RenderState {
	private boolean blending;
	private int blendSourceFactor;
	private int blendDestinationFactor;

	private boolean depthMask;
	private int depthFunction;
	private float depthRangeNear;
	private float depthRangeFar;

	private boolean stencil;
	private int frontStencilMask;
	private int frontStencilFailFunction;
	private int frontDepthFailFunction;
	private int frontPassFunction;
	private int backStencilMask;
	private int backStencilFailFunction;
	private int backDepthFailFunction;
	private int backPassFunction;

	private int cullFace;

	private boolean colorCleared;
	private Color clearBackgroundColorValue;
	private boolean depthCleared;
	private float clearDepthValue;
	private boolean stencilCleared;
	private int clearStencilValue;

	private RenderTarget renderTarget;
	private IntMap<BindedTexture> bindedTextures;

	private TextureBinder textureBinder;

}
