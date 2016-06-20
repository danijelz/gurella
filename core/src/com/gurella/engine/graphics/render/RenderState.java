package com.gurella.engine.graphics.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.utils.TextureBinder;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.pool.PoolService;

public class RenderState implements Poolable {
	private boolean[] colorMask = new boolean[] { true, true, true, true };

	private boolean blendingEnabled;
	private int blendSourceFactor;
	private int blendDestinationFactor;

	private boolean depthEnabled;
	private boolean depthMask;
	private int depthFunction;
	private float depthRangeNear;
	private float depthRangeFar;

	private boolean stencilEnabled;
	private int frontStencilMask;
	private int frontStencilFailFunction;
	private int frontDepthFailFunction;
	private int frontPassFunction;
	private int backStencilMask;
	private int backStencilFailFunction;
	private int backDepthFailFunction;
	private int backPassFunction;

	// TODO scissor
	private boolean scissorEnabled;
	private final float[] scissorExtent = new float[4];

	private boolean cullFaceEnabled;
	private int cullFace;
	private int frontFace;

	private boolean colorCleared;
	private Color clearColorValue;

	private boolean depthCleared;
	private float clearDepthValue;
	private boolean stencilCleared;
	private int clearStencilValue;

	private float lineWidth = 1;

	private RenderTarget renderTarget;
	private IntMap<BindedTexture> bindedTextures;

	//////////////////////////////
	private TextureBinder textureBinder;

	////////////////////////

	static RenderState obtain() {
		return PoolService.obtain(RenderState.class);
	}

	void free() {
		PoolService.free(this);
	}

	public boolean isBlendingEnabled() {
		return blendingEnabled;
	}

	public void setBlendingEnabled(boolean blending) {
		this.blendingEnabled = blending;
	}

	public int getBlendSourceFactor() {
		return blendSourceFactor;
	}

	public void setBlendSourceFactor(int blendSourceFactor) {
		this.blendSourceFactor = blendSourceFactor;
	}

	public int getBlendDestinationFactor() {
		return blendDestinationFactor;
	}

	public void setBlendDestinationFactor(int blendDestinationFactor) {
		this.blendDestinationFactor = blendDestinationFactor;
	}

	public boolean getDepthMask() {
		return depthMask;
	}

	public void setDepthMask(boolean depthMask) {
		this.depthMask = depthMask;
	}

	public int getDepthFunction() {
		return depthFunction;
	}

	public void setDepthFunction(int depthFunction) {
		this.depthFunction = depthFunction;
	}

	public float getDepthRangeNear() {
		return depthRangeNear;
	}

	public void setDepthRangeNear(float depthRangeNear) {
		this.depthRangeNear = depthRangeNear;
	}

	public float getDepthRangeFar() {
		return depthRangeFar;
	}

	public void setDepthRangeFar(float depthRangeFar) {
		this.depthRangeFar = depthRangeFar;
	}

	public boolean isStencilEnabled() {
		return stencilEnabled;
	}

	public void setStencilEnabled(boolean stencil) {
		this.stencilEnabled = stencil;
	}

	public int getFrontStencilMask() {
		return frontStencilMask;
	}

	public void setFrontStencilMask(int frontStencilMask) {
		this.frontStencilMask = frontStencilMask;
	}

	public int getFrontStencilFailFunction() {
		return frontStencilFailFunction;
	}

	public void setFrontStencilFailFunction(int frontStencilFailFunction) {
		this.frontStencilFailFunction = frontStencilFailFunction;
	}

	public int getFrontDepthFailFunction() {
		return frontDepthFailFunction;
	}

	public void setFrontDepthFailFunction(int frontDepthFailFunction) {
		this.frontDepthFailFunction = frontDepthFailFunction;
	}

	public int getFrontPassFunction() {
		return frontPassFunction;
	}

	public void setFrontPassFunction(int frontPassFunction) {
		this.frontPassFunction = frontPassFunction;
	}

	public int getBackStencilMask() {
		return backStencilMask;
	}

	public void setBackStencilMask(int backStencilMask) {
		this.backStencilMask = backStencilMask;
	}

	public int getBackStencilFailFunction() {
		return backStencilFailFunction;
	}

	public void setBackStencilFailFunction(int backStencilFailFunction) {
		this.backStencilFailFunction = backStencilFailFunction;
	}

	public int getBackDepthFailFunction() {
		return backDepthFailFunction;
	}

	public void setBackDepthFailFunction(int backDepthFailFunction) {
		this.backDepthFailFunction = backDepthFailFunction;
	}

	public int getBackPassFunction() {
		return backPassFunction;
	}

	public void setBackPassFunction(int backPassFunction) {
		this.backPassFunction = backPassFunction;
	}

	public int getCullFace() {
		return cullFace;
	}

	public void setCullFace(int cullFace) {
		this.cullFace = cullFace;
	}

	public boolean isColorCleared() {
		return colorCleared;
	}

	public void setColorCleared(boolean colorCleared) {
		this.colorCleared = colorCleared;
	}

	public Color getClearColorValue() {
		return clearColorValue;
	}

	public void setClearColorValue(Color clearColorValue) {
		this.clearColorValue = clearColorValue;
	}

	public boolean isDepthCleared() {
		return depthCleared;
	}

	public void setDepthCleared(boolean depthCleared) {
		this.depthCleared = depthCleared;
	}

	public float getClearDepthValue() {
		return clearDepthValue;
	}

	public void setClearDepthValue(float clearDepthValue) {
		this.clearDepthValue = clearDepthValue;
	}

	public boolean isStencilCleared() {
		return stencilCleared;
	}

	public void setStencilCleared(boolean stencilCleared) {
		this.stencilCleared = stencilCleared;
	}

	public int getClearStencilValue() {
		return clearStencilValue;
	}

	public void setClearStencilValue(int clearStencilValue) {
		this.clearStencilValue = clearStencilValue;
	}

	public RenderTarget getRenderTarget() {
		return renderTarget;
	}

	public void setRenderTarget(RenderTarget renderTarget) {
		this.renderTarget = renderTarget;
	}

	public IntMap<BindedTexture> getBindedTextures() {
		return bindedTextures;
	}

	public void setBindedTextures(IntMap<BindedTexture> bindedTextures) {
		this.bindedTextures = bindedTextures;
	}

	public TextureBinder getTextureBinder() {
		return textureBinder;
	}

	public void setTextureBinder(TextureBinder textureBinder) {
		this.textureBinder = textureBinder;
	}

	@Override
	public void reset() {
		// TODO
	}

	public void set(RenderState currentState) {
		// TODO Auto-generated method stub

	}
}
