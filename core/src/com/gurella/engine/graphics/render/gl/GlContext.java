package com.gurella.engine.graphics.render.gl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g3d.utils.TextureBinder;
import com.badlogic.gdx.utils.IntMap;
import com.gurella.engine.graphics.render.BoundTexture;
import com.gurella.engine.graphics.render.RenderTarget;
import com.gurella.engine.graphics.render.shader.ShaderProgramExt;

//TODO unused
public class GlContext {
	private final GL20 gl20;
	private final GL30 gl30;

	private ColorMask colorMask = ColorMask.rgba;

	private boolean blendingEnabled;
	private Color blendColor = new Color(0, 0, 0, 0);
	private BlendEquation rgbBlendEquation = BlendEquation.add;
	private BlendEquation aBlendEquation = BlendEquation.add;
	private BlendFunction rgbBlendSourceFactor = BlendFunction.one;
	private BlendFunction alphaBlendSourceFactor = BlendFunction.one;
	private BlendFunction rgbBlendDestinationFactor = BlendFunction.zero;
	private BlendFunction alphaBlendDestinationFactor = BlendFunction.zero;

	private boolean depthMask = true;
	private boolean depthTestEnabled;
	private DepthTestFunction depthFunction = DepthTestFunction.less;
	private float depthRangeNear = 0;
	private float depthRangeFar = 1;

	private boolean stencilEnabled;
	private int frontStencilMask = 0xffffffff;
	private int frontStencilRef = 0x0;
	private StencilFunction frontStencilFunction = StencilFunction.always;
	private StencilOp frontStencilFailOp = StencilOp.keep;
	private StencilOp frontDepthFailOp = StencilOp.keep;
	private StencilOp frontPassOp = StencilOp.keep;
	private int backStencilMask = 0xffffffff;
	private int backStencilRef = 0x0;
	private StencilFunction backStencilFunction = StencilFunction.always;
	private StencilOp backStencilFailOp = StencilOp.keep;
	private StencilOp backDepthFailOp = StencilOp.keep;
	private StencilOp backPassOp = StencilOp.keep;

	private CullFace cullFace = CullFace.back;
	private FrontFace frontFace = FrontFace.ccw;

	private float lineWidth = 1;

	// TODO scissor
	private boolean scissorEnabled;
	private final float[] scissorExtent = new float[4];

	private boolean colorCleared;
	private Color clearColorValue;

	private boolean depthCleared;
	private float clearDepthValue;
	private boolean stencilCleared;
	private int clearStencilValue;

	//////////////////////////////
	private RenderTarget activeRenderTarget;
	private ShaderProgramExt activeShaderProgram;
	private IntMap<BoundTexture> activeTextures;

	private TextureBinder textureBinder;

	public GlContext() {
		gl20 = Gdx.gl20;
		gl30 = Gdx.gl30;
	}

	public void begin() {

	}

	public boolean isBlendingEnabled() {
		return blendingEnabled;
	}

	public void setBlendingEnabled(boolean enabled) {
		if (this.blendingEnabled == enabled) {
			return;
		}

		this.blendingEnabled = enabled;
		if (enabled) {
			gl20.glEnable(GL20.GL_BLEND);
		} else {
			gl20.glDisable(GL20.GL_BLEND);
		}
	}

	public boolean getDepthMask() {
		return depthMask;
	}

	public void setDepthMask(boolean depthMask) {
		this.depthMask = depthMask;
	}

	public DepthTestFunction getDepthFunction() {
		return depthFunction;
	}

	public void setDepthFunction(DepthTestFunction depthFunction) {
		this.depthFunction = depthFunction == null ? DepthTestFunction.less : depthFunction;
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

	public StencilOp getFrontStencilFailOp() {
		return frontStencilFailOp;
	}

	public void setFrontStencilFailOp(StencilOp frontStencilFailOp) {
		this.frontStencilFailOp = frontStencilFailOp;
	}

	public StencilOp getFrontDepthFailOp() {
		return frontDepthFailOp;
	}

	public void setFrontDepthFailOp(StencilOp frontDepthFailOp) {
		this.frontDepthFailOp = frontDepthFailOp;
	}

	public StencilOp getFrontPassOp() {
		return frontPassOp;
	}

	public void setFrontPassOp(StencilOp frontPassOp) {
		this.frontPassOp = frontPassOp;
	}

	public int getBackStencilMask() {
		return backStencilMask;
	}

	public void setBackStencilMask(int backStencilMask) {
		this.backStencilMask = backStencilMask;
	}

	public StencilOp getBackStencilFailOp() {
		return backStencilFailOp;
	}

	public void setBackStencilFailOp(StencilOp backStencilFailOp) {
		this.backStencilFailOp = backStencilFailOp;
	}

	public StencilOp getBackDepthFailOp() {
		return backDepthFailOp;
	}

	public void setBackDepthFailOp(StencilOp backDepthFailOp) {
		this.backDepthFailOp = backDepthFailOp;
	}

	public StencilOp getBackPassOp() {
		return backPassOp;
	}

	public void setBackPassOp(StencilOp backPassOp) {
		this.backPassOp = backPassOp;
	}

	public CullFace getCullFace() {
		return cullFace;
	}

	public void setCullFace(CullFace cullFace) {
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

	public RenderTarget getActiveRenderTarget() {
		return activeRenderTarget;
	}

	public void setActiveRenderTarget(RenderTarget renderTarget) {
		this.activeRenderTarget = renderTarget;
	}

	public IntMap<BoundTexture> getBindedTextures() {
		return activeTextures;
	}

	public void setBindedTextures(IntMap<BoundTexture> boundTextures) {
		this.activeTextures = boundTextures;
	}

	public TextureBinder getTextureBinder() {
		return textureBinder;
	}

	public void setTextureBinder(TextureBinder textureBinder) {
		this.textureBinder = textureBinder;
	}

	public void reset() {
		// TODO
	}
}
