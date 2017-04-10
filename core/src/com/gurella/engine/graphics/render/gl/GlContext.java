package com.gurella.engine.graphics.render.gl;

import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.GLTexture;
import com.gurella.engine.graphics.GraphicsService;
import com.gurella.engine.graphics.render.RenderTarget;
import com.gurella.engine.graphics.render.shader.ShaderProgramExt;
import com.gurella.engine.math.GridRectangle;

public class GlContext {
	private static final int defaultStencilRef = 0x0;
	private static final int defaultStencilMask = 0xffffffff;
	private static final Color defaultColor = new Color(0, 0, 0, 0);
	private static final GridRectangle defaultScissorRect = new GridRectangle(-1, -1, -1, -1);
	private static final int textureReuseWeight = 3;

	public final GL20 gl20;
	public final GL30 gl30;

	private boolean active;

	private ColorMask colorMask = ColorMask.defaultValue;

	private boolean blendingEnabled;
	private final Color blendColor = new Color(defaultColor);
	private BlendEquation rgbBlendEquation = BlendEquation.defaultValue;
	private BlendEquation alphaBlendEquation = BlendEquation.defaultValue;
	private BlendFunction srcRgbBlendFunction = BlendFunction.defaultSource;
	private BlendFunction srcAlphaBlendFunction = BlendFunction.defaultSource;
	private BlendFunction dstRgbBlendFunction = BlendFunction.defaultDestination;
	private BlendFunction dstAlphaBlendFunction = BlendFunction.defaultDestination;

	private boolean depthMask = true;
	private boolean depthTestEnabled;
	private DepthTestFunction depthFunction = DepthTestFunction.defaultValue;
	private float depthRangeNear = 0;
	private float depthRangeFar = 1;

	private boolean stencilEnabled;
	private StencilFunction frontStencilFunction = StencilFunction.defaultValue;
	private int frontStencilRef = defaultStencilRef;
	private int frontStencilMask = defaultStencilMask;
	private StencilOp frontStencilFailOp = StencilOp.defaultValue;
	private StencilOp frontDepthFailOp = StencilOp.defaultValue;
	private StencilOp frontPassOp = StencilOp.defaultValue;
	private StencilFunction backStencilFunction = StencilFunction.defaultValue;
	private int backStencilRef = defaultStencilRef;
	private int backStencilMask = defaultStencilMask;
	private StencilOp backStencilFailOp = StencilOp.defaultValue;
	private StencilOp backDepthFailOp = StencilOp.defaultValue;
	private StencilOp backPassOp = StencilOp.defaultValue;

	private boolean enableCullFace = true;
	private CullFace cullFace = CullFace.defaultValue;
	private FrontFace frontFace = FrontFace.defaultValue;

	private float lineWidth = 1;

	private boolean scissorEnabled;
	private final GridRectangle scissorRect = new GridRectangle(defaultScissorRect);
	private final GridRectangle scissorRectOut = new GridRectangle(scissorRect);

	private final Color clearColorValue = new Color(defaultColor);
	private float clearDepthValue;
	private int clearStencilValue;

	//////////////////////////////
	private RenderTarget activeRenderTarget;
	private ShaderProgramExt activeShaderProgram;

	private final GLTexture[] textures;
	private final int[] textureWeights;
	private final int[] textureIds;
	private int textureSequence = Integer.MIN_VALUE + 1;
	private int activeTexture;

	public GlContext() {
		gl20 = Gdx.gl20;
		gl30 = Gdx.gl30;

		int textureUnits = GraphicsService.getMaxGlesTextureImageUnits();
		this.textures = new GLTexture[textureUnits];
		this.textureWeights = new int[textureUnits];
		this.textureIds = new int[textureUnits];
	}

	public void activate() {
		active = true;

		updateColorMask();

		updateEnableBlending();
		updateBlendColor();
		updateBlendEquation();
		updateBlendFunction();

		updateDepthMask();
		updateDepthTestEnabled();
		updateDepthFunction();
		updateDepthRange();

		updateStencilEnabled();
		updateStencilFunction();
		updateStencilOp();

		updateEnableCullFace();
		updateCullFace();
		updateFrontFace();
		updateLineWidth();

		updateScissorEnabled();
		updateScissorRect();

		updateClearColor();
		updateClearDepth();
		updateClearStencil();

		activateTexture();
	}

	public void deactivate() {
		active = false;
		colorMask = ColorMask.defaultValue;

		blendingEnabled = false;
		blendColor.set(defaultColor);
		rgbBlendEquation = BlendEquation.defaultValue;
		alphaBlendEquation = BlendEquation.defaultValue;
		srcRgbBlendFunction = BlendFunction.defaultSource;
		srcAlphaBlendFunction = BlendFunction.defaultSource;
		dstRgbBlendFunction = BlendFunction.defaultDestination;
		dstAlphaBlendFunction = BlendFunction.defaultDestination;

		depthMask = true;
		depthTestEnabled = false;
		depthFunction = DepthTestFunction.defaultValue;
		depthRangeNear = 0;
		depthRangeFar = 1;

		stencilEnabled = false;
		frontStencilMask = defaultStencilMask;
		frontStencilRef = defaultStencilRef;
		frontStencilFunction = StencilFunction.defaultValue;
		frontStencilFailOp = StencilOp.defaultValue;
		frontDepthFailOp = StencilOp.defaultValue;
		frontPassOp = StencilOp.defaultValue;
		backStencilMask = defaultStencilMask;
		backStencilRef = defaultStencilRef;
		backStencilFunction = StencilFunction.defaultValue;
		backStencilFailOp = StencilOp.defaultValue;
		backDepthFailOp = StencilOp.defaultValue;
		backPassOp = StencilOp.defaultValue;

		enableCullFace = true;
		cullFace = CullFace.defaultValue;
		frontFace = FrontFace.defaultValue;

		lineWidth = 1;

		scissorEnabled = false;
		scissorRect.set(defaultScissorRect);

		clearColorValue.set(defaultColor);
		clearDepthValue = 0;
		clearStencilValue = 0;

		activeRenderTarget = null;
		activeShaderProgram = null;

		Arrays.fill(textures, null);
		Arrays.fill(textureWeights, 0);
		Arrays.fill(textureIds, 0);
		activeTexture = GL20.GL_TEXTURE0;
		activateTexture();
	}

	public ColorMask getColorMask() {
		return colorMask;
	}

	public void setColorMask(ColorMask colorMask) {
		if (this.colorMask == colorMask) {
			return;
		}

		this.colorMask = colorMask == null ? ColorMask.defaultValue : colorMask;

		if (active) {
			updateColorMask();
		}
	}

	private void updateColorMask() {
		gl20.glColorMask(colorMask.rMask, colorMask.gMask, colorMask.bMask, colorMask.aMask);
	}

	public boolean isBlendingEnabled() {
		return blendingEnabled;
	}

	public void setBlendingEnabled(boolean enabled) {
		if (this.blendingEnabled == enabled) {
			return;
		}

		this.blendingEnabled = enabled;

		if (active) {
			updateEnableBlending();
		}
	}

	private void updateEnableBlending() {
		if (blendingEnabled) {
			gl20.glEnable(GL20.GL_BLEND);
		} else {
			gl20.glDisable(GL20.GL_BLEND);
		}
	}

	public Color getBlendColor() {
		return blendColor;
	}

	public void setBlendColor(Color blendColor) {
		Color resolved = blendColor == null ? defaultColor : blendColor;

		if (this.blendColor.equals(resolved)) {
			return;
		}

		this.blendColor.set(resolved);

		if (active) {
			updateBlendColor();
		}
	}

	private void updateBlendColor() {
		gl20.glBlendColor(blendColor.r, blendColor.g, blendColor.b, blendColor.a);
	}

	public BlendEquation getRgbBlendEquation() {
		return rgbBlendEquation;
	}

	public void setRgbBlendEquation(BlendEquation rgbBlendEquation) {
		if (this.rgbBlendEquation == rgbBlendEquation) {
			return;
		}

		this.rgbBlendEquation = rgbBlendEquation == null ? BlendEquation.defaultValue : rgbBlendEquation;

		if (active) {
			updateBlendEquation();
		}
	}

	private void updateBlendEquation() {
		gl20.glBlendEquationSeparate(rgbBlendEquation.glValue, alphaBlendEquation.glValue);
	}

	public BlendEquation getAlphaBlendEquation() {
		return alphaBlendEquation;
	}

	public void setAlphaBlendEquation(BlendEquation alphaBlendEquation) {
		if (this.alphaBlendEquation == alphaBlendEquation) {
			return;
		}

		this.alphaBlendEquation = alphaBlendEquation == null ? BlendEquation.defaultValue : alphaBlendEquation;

		if (active) {
			updateBlendEquation();
		}
	}

	public void setBlendEquation(BlendEquation blendEquation) {
		if (this.rgbBlendEquation == blendEquation && this.alphaBlendEquation == blendEquation) {
			return;
		}

		this.rgbBlendEquation = blendEquation == null ? BlendEquation.defaultValue : blendEquation;
		this.alphaBlendEquation = blendEquation == null ? BlendEquation.defaultValue : blendEquation;

		if (active) {
			updateBlendEquation();
		}
	}

	public void setBlendEquation(BlendEquation rgbBlendEquation, BlendEquation alphaBlendEquation) {
		if (this.rgbBlendEquation == rgbBlendEquation && this.alphaBlendEquation == alphaBlendEquation) {
			return;
		}

		this.rgbBlendEquation = rgbBlendEquation == null ? BlendEquation.defaultValue : rgbBlendEquation;
		this.alphaBlendEquation = alphaBlendEquation == null ? BlendEquation.defaultValue : alphaBlendEquation;

		if (active) {
			updateBlendEquation();
		}
	}

	public BlendFunction getSrcRgbBlendFunction() {
		return srcRgbBlendFunction;
	}

	public void setSrcRgbBlendFunction(BlendFunction srcRgbBlendFunction) {
		if (this.srcRgbBlendFunction == srcRgbBlendFunction) {
			return;
		}

		this.srcRgbBlendFunction = srcRgbBlendFunction == null ? BlendFunction.defaultSource : srcRgbBlendFunction;

		if (active) {
			updateBlendFunction();
		}
	}

	private void updateBlendFunction() {
		gl20.glBlendFuncSeparate(srcRgbBlendFunction.glValue, dstRgbBlendFunction.glValue,
				srcAlphaBlendFunction.glValue, dstAlphaBlendFunction.glValue);
	}

	public BlendFunction getSrcAlphaBlendFunction() {
		return srcAlphaBlendFunction;
	}

	public void setSrcAlphaBlendFunction(BlendFunction srcAlphaBlendFunction) {
		if (this.srcAlphaBlendFunction == srcAlphaBlendFunction) {
			return;
		}

		this.srcAlphaBlendFunction = srcAlphaBlendFunction == null ? BlendFunction.defaultSource
				: srcAlphaBlendFunction;

		if (active) {
			updateBlendFunction();
		}
	}

	public void setSrcBlendFunc(BlendFunction srcBlendFunction) {
		if (this.srcRgbBlendFunction == srcBlendFunction && this.srcAlphaBlendFunction == srcBlendFunction) {
			return;
		}

		this.srcRgbBlendFunction = srcBlendFunction == null ? BlendFunction.defaultSource : srcBlendFunction;
		this.srcAlphaBlendFunction = srcBlendFunction == null ? BlendFunction.defaultSource : srcBlendFunction;

		if (active) {
			updateBlendFunction();
		}
	}

	public BlendFunction getDstRgbBlendFunction() {
		return dstRgbBlendFunction;
	}

	public void setDstRgbBlendFunction(BlendFunction dstRgbBlendFunction) {
		if (this.dstRgbBlendFunction == dstRgbBlendFunction) {
			return;
		}

		this.dstRgbBlendFunction = dstRgbBlendFunction == null ? BlendFunction.defaultDestination : dstRgbBlendFunction;

		if (active) {
			updateBlendFunction();
		}
	}

	public BlendFunction getDstAlphaBlendFunction() {
		return dstAlphaBlendFunction;
	}

	public void setDstAlphaBlendFunction(BlendFunction dstAlphaBlendFunction) {
		if (this.dstAlphaBlendFunction == dstAlphaBlendFunction) {
			return;
		}

		this.dstAlphaBlendFunction = dstAlphaBlendFunction == null ? BlendFunction.defaultDestination
				: dstAlphaBlendFunction;

		if (active) {
			updateBlendFunction();
		}
	}

	public void setDstBlendFunc(BlendFunction dstBlendFunction) {
		if (this.dstRgbBlendFunction == dstBlendFunction && this.dstAlphaBlendFunction == dstBlendFunction) {
			return;
		}

		this.dstRgbBlendFunction = dstBlendFunction == null ? BlendFunction.defaultDestination : dstBlendFunction;
		this.dstAlphaBlendFunction = dstBlendFunction == null ? BlendFunction.defaultDestination : dstBlendFunction;

		if (active) {
			updateBlendFunction();
		}
	}

	public void setBlendFunc(BlendFunction srcBlendFunction, BlendFunction dstBlendFunction) {
		if (this.srcRgbBlendFunction == srcBlendFunction && this.srcAlphaBlendFunction == srcBlendFunction
				&& this.dstRgbBlendFunction == dstBlendFunction && this.dstAlphaBlendFunction == dstBlendFunction) {
			return;
		}

		this.srcRgbBlendFunction = srcBlendFunction == null ? BlendFunction.defaultSource : srcBlendFunction;
		this.srcAlphaBlendFunction = srcBlendFunction == null ? BlendFunction.defaultSource : srcBlendFunction;
		this.dstRgbBlendFunction = dstBlendFunction == null ? BlendFunction.defaultDestination : dstBlendFunction;
		this.dstAlphaBlendFunction = dstBlendFunction == null ? BlendFunction.defaultDestination : dstBlendFunction;

		if (active) {
			updateBlendFunction();
		}
	}

	public void setBlendFunc(BlendFunction srcRgbBlendFunction, BlendFunction srcAlphaBlendFunction,
			BlendFunction dstRgbBlendFunction, BlendFunction dstAlphaBlendFunction) {
		if (this.srcRgbBlendFunction == srcRgbBlendFunction && this.srcAlphaBlendFunction == srcAlphaBlendFunction
				&& this.dstRgbBlendFunction == dstRgbBlendFunction
				&& this.dstAlphaBlendFunction == dstAlphaBlendFunction) {
			return;
		}

		this.srcRgbBlendFunction = srcRgbBlendFunction == null ? BlendFunction.defaultSource : srcRgbBlendFunction;
		this.srcAlphaBlendFunction = srcAlphaBlendFunction == null ? BlendFunction.defaultSource
				: srcAlphaBlendFunction;
		this.dstRgbBlendFunction = dstRgbBlendFunction == null ? BlendFunction.defaultDestination : dstRgbBlendFunction;
		this.dstAlphaBlendFunction = dstAlphaBlendFunction == null ? BlendFunction.defaultDestination
				: dstAlphaBlendFunction;

		if (active) {
			updateBlendFunction();
		}
	}

	public boolean getDepthMask() {
		return depthMask;
	}

	public void setDepthMask(boolean depthMask) {
		if (this.depthMask == depthMask) {
			return;
		}

		this.depthMask = depthMask;

		if (active) {
			updateDepthMask();
		}
	}

	private void updateDepthMask() {
		gl20.glDepthMask(depthMask);
	}

	public boolean isDepthTestEnabled() {
		return depthTestEnabled;
	}

	public void setDepthTestEnabled(boolean depthTestEnabled) {
		if (this.depthTestEnabled == depthTestEnabled) {
			return;
		}

		this.depthTestEnabled = depthTestEnabled;

		if (active) {
			updateDepthTestEnabled();
		}
	}

	private void updateDepthTestEnabled() {
		if (depthTestEnabled) {
			gl20.glEnable(GL20.GL_DEPTH_TEST);
		} else {
			gl20.glDisable(GL20.GL_DEPTH_TEST);
		}
	}

	public DepthTestFunction getDepthFunction() {
		return depthFunction;
	}

	public void setDepthFunction(DepthTestFunction depthFunction) {
		if (this.depthFunction == depthFunction) {
			return;
		}

		this.depthFunction = depthFunction == null ? DepthTestFunction.defaultValue : depthFunction;

		if (active) {
			updateDepthFunction();
		}
	}

	private void updateDepthFunction() {
		gl20.glDepthFunc(depthFunction.glValue);
	}

	public float getDepthRangeNear() {
		return depthRangeNear;
	}

	public void setDepthRangeNear(float depthRangeNear) {
		if (this.depthRangeNear == depthRangeNear) {
			return;
		}

		this.depthRangeNear = depthRangeNear;

		if (active) {
			updateDepthRange();
		}
	}

	private void updateDepthRange() {
		gl20.glDepthRangef(depthRangeNear, depthRangeFar);
	}

	public float getDepthRangeFar() {
		return depthRangeFar;
	}

	public void setDepthRangeFar(float depthRangeFar) {
		if (this.depthRangeFar == depthRangeFar) {
			return;
		}

		this.depthRangeFar = depthRangeFar;

		if (active) {
			updateDepthRange();
		}
	}

	public void setDepthRange(float depthRangeNear, float depthRangeFar) {
		if (this.depthRangeNear == depthRangeNear && this.depthRangeFar == depthRangeFar) {
			return;
		}

		this.depthRangeNear = depthRangeNear;
		this.depthRangeFar = depthRangeFar;

		if (active) {
			updateDepthRange();
		}
	}

	public boolean isStencilEnabled() {
		return stencilEnabled;
	}

	public void setStencilEnabled(boolean stencilEnabled) {
		if (this.stencilEnabled == stencilEnabled) {
			return;
		}

		this.stencilEnabled = stencilEnabled;

		if (active) {
			updateStencilEnabled();
		}
	}

	private void updateStencilEnabled() {
		if (stencilEnabled) {
			gl20.glEnable(GL20.GL_STENCIL_TEST);
		} else {
			gl20.glDisable(GL20.GL_STENCIL_TEST);
		}
	}

	public StencilFunction getFrontStencilFunction() {
		return frontStencilFunction;
	}

	public void setFrontStencilFunction(StencilFunction frontStencilFunction) {
		if (this.frontStencilFunction == frontStencilFunction) {
			return;
		}

		this.frontStencilFunction = frontStencilFunction == null ? StencilFunction.defaultValue : frontStencilFunction;

		if (active) {
			updateFrontStencilFunction();
		}
	}

	private void updateFrontStencilFunction() {
		gl20.glStencilFuncSeparate(GL20.GL_FRONT, frontStencilFunction.glValue, frontStencilRef, frontStencilMask);
	}

	public int getFrontStencilRef() {
		return frontStencilRef;
	}

	public void setFrontStencilRef(int frontStencilRef) {
		if (this.frontStencilRef == frontStencilRef) {
			return;
		}

		this.frontStencilRef = frontStencilRef;

		if (active) {
			updateFrontStencilFunction();
		}
	}

	public int getFrontStencilMask() {
		return frontStencilMask;
	}

	public void setFrontStencilMask(int frontStencilMask) {
		if (this.frontStencilMask == frontStencilMask) {
			return;
		}

		this.frontStencilMask = frontStencilMask;

		if (active) {
			updateFrontStencilFunction();
		}
	}

	public void setFrontStencilFunction(StencilFunction frontStencilFunction, int frontStencilRef,
			int frontStencilMask) {
		if (this.frontStencilFunction == frontStencilFunction && this.frontStencilRef == frontStencilRef
				&& this.frontStencilMask == frontStencilMask) {
			return;
		}

		this.frontStencilFunction = frontStencilFunction == null ? StencilFunction.defaultValue : frontStencilFunction;
		this.frontStencilRef = frontStencilRef;
		this.frontStencilMask = frontStencilMask;

		if (active) {
			updateFrontStencilFunction();
		}
	}

	public StencilFunction getBackStencilFunction() {
		return backStencilFunction;
	}

	public void setBackStencilFunction(StencilFunction backStencilFunction) {
		if (this.backStencilFunction == backStencilFunction) {
			return;
		}

		this.backStencilFunction = backStencilFunction == null ? StencilFunction.defaultValue : backStencilFunction;

		if (active) {
			updateBackStencilFunction();
		}
	}

	private void updateBackStencilFunction() {
		gl20.glStencilFuncSeparate(GL20.GL_BACK, backStencilFunction.glValue, backStencilRef, backStencilMask);
	}

	public int getBackStencilRef() {
		return backStencilRef;
	}

	public void setBackStencilRef(int backStencilRef) {
		if (this.backStencilRef == backStencilRef) {
			return;
		}

		this.backStencilRef = backStencilRef;

		if (active) {
			updateBackStencilFunction();
		}
	}

	public int getBackStencilMask() {
		return backStencilMask;
	}

	public void setBackStencilMask(int backStencilMask) {
		if (this.backStencilMask == backStencilMask) {
			return;
		}

		this.backStencilMask = backStencilMask;

		if (active) {
			updateBackStencilFunction();
		}
	}

	public void setBackStencilFunction(StencilFunction backStencilFunction, int backStencilRef, int backStencilMask) {
		if (this.backStencilFunction == backStencilFunction && this.backStencilRef == backStencilRef
				&& this.backStencilMask == backStencilMask) {
			return;
		}

		this.backStencilFunction = backStencilFunction == null ? StencilFunction.defaultValue : backStencilFunction;
		this.backStencilRef = backStencilRef;
		this.backStencilMask = backStencilMask;

		if (active) {
			updateBackStencilFunction();
		}
	}

	public void setStencilFunction(StencilFunction stencilFunction, int stencilRef, int stencilMask) {
		if (frontStencilFunction == stencilFunction && frontStencilRef == stencilRef && frontStencilMask == stencilMask
				&& backStencilFunction == stencilFunction && backStencilRef == stencilRef
				&& backStencilMask == stencilMask) {
			return;
		}

		this.frontStencilFunction = stencilFunction == null ? StencilFunction.defaultValue : stencilFunction;
		this.backStencilFunction = stencilFunction == null ? StencilFunction.defaultValue : stencilFunction;
		this.frontStencilRef = stencilRef;
		this.backStencilRef = stencilRef;
		this.frontStencilMask = stencilMask;
		this.backStencilMask = stencilMask;

		if (active) {
			gl20.glStencilFuncSeparate(GL20.GL_FRONT_AND_BACK, stencilFunction.glValue, stencilRef, stencilMask);
		}
	}

	private void updateStencilFunction() {
		if (frontStencilFunction == backStencilFunction && frontStencilRef == backStencilRef
				&& frontStencilMask == backStencilMask) {
			gl20.glStencilFuncSeparate(GL20.GL_FRONT_AND_BACK, frontStencilFunction.glValue, frontStencilRef,
					frontStencilMask);
		} else {
			gl20.glStencilFuncSeparate(GL20.GL_FRONT, frontStencilFunction.glValue, frontStencilRef, frontStencilMask);
			gl20.glStencilFuncSeparate(GL20.GL_BACK, backStencilFunction.glValue, backStencilRef, backStencilMask);
		}
	}

	public StencilOp getFrontStencilFailOp() {
		return frontStencilFailOp;
	}

	public void setFrontStencilFailOp(StencilOp frontStencilFailOp) {
		if (this.frontStencilFailOp == frontStencilFailOp) {
			return;
		}

		this.frontStencilFailOp = frontStencilFailOp == null ? StencilOp.defaultValue : frontStencilFailOp;

		if (active) {
			updateFrontStencilOp();
		}
	}

	private void updateFrontStencilOp() {
		gl20.glStencilOpSeparate(GL20.GL_FRONT, frontStencilFailOp.glValue, frontDepthFailOp.glValue,
				frontPassOp.glValue);
	}

	public StencilOp getFrontDepthFailOp() {
		return frontDepthFailOp;
	}

	public void setFrontDepthFailOp(StencilOp frontDepthFailOp) {
		if (this.frontDepthFailOp == frontDepthFailOp) {
			return;
		}

		this.frontDepthFailOp = frontDepthFailOp == null ? StencilOp.defaultValue : frontDepthFailOp;

		if (active) {
			updateFrontStencilOp();
		}
	}

	public StencilOp getFrontPassOp() {
		return frontPassOp;
	}

	public void setFrontPassOp(StencilOp frontPassOp) {
		if (this.frontPassOp == frontPassOp) {
			return;
		}

		this.frontPassOp = frontPassOp == null ? StencilOp.defaultValue : frontPassOp;

		if (active) {
			updateFrontStencilOp();
		}
	}

	public void setFrontStencilOp(StencilOp frontStencilFailOp, StencilOp frontDepthFailOp, StencilOp frontPassOp) {
		if (this.frontStencilFailOp == frontStencilFailOp && this.frontDepthFailOp == frontDepthFailOp
				&& this.frontPassOp == frontPassOp) {
			return;
		}

		this.frontStencilFailOp = frontStencilFailOp == null ? StencilOp.defaultValue : frontStencilFailOp;
		this.frontDepthFailOp = frontDepthFailOp == null ? StencilOp.defaultValue : frontDepthFailOp;
		this.frontPassOp = frontPassOp == null ? StencilOp.defaultValue : frontPassOp;

		if (active) {
			updateFrontStencilOp();
		}
	}

	public StencilOp getBackStencilFailOp() {
		return backStencilFailOp;
	}

	public void setBackStencilFailOp(StencilOp backStencilFailOp) {
		if (this.backStencilFailOp == backStencilFailOp) {
			return;
		}

		this.backStencilFailOp = backStencilFailOp == null ? StencilOp.defaultValue : backStencilFailOp;

		if (active) {
			updateBackStencilOp();
		}
	}

	private void updateBackStencilOp() {
		gl20.glStencilOpSeparate(GL20.GL_BACK, backStencilFailOp.glValue, backDepthFailOp.glValue, backPassOp.glValue);
	}

	public StencilOp getBackDepthFailOp() {
		return backDepthFailOp;
	}

	public void setBackDepthFailOp(StencilOp backDepthFailOp) {
		if (this.backDepthFailOp == backDepthFailOp) {
			return;
		}

		this.backDepthFailOp = backDepthFailOp == null ? StencilOp.defaultValue : backDepthFailOp;

		if (active) {
			updateBackStencilOp();
		}
	}

	public StencilOp getBackPassOp() {
		return backPassOp;
	}

	public void setBackPassOp(StencilOp backPassOp) {
		if (this.backPassOp == backPassOp) {
			return;
		}

		this.backPassOp = backPassOp == null ? StencilOp.defaultValue : backPassOp;

		if (active) {
			updateBackStencilOp();
		}
	}

	public void setBackStencilOp(StencilOp backStencilFailOp, StencilOp backDepthFailOp, StencilOp backPassOp) {
		if (this.backStencilFailOp == backStencilFailOp && this.backDepthFailOp == backDepthFailOp
				&& this.backPassOp == backPassOp) {
			return;
		}

		this.backStencilFailOp = backStencilFailOp == null ? StencilOp.defaultValue : backStencilFailOp;
		this.backDepthFailOp = backDepthFailOp == null ? StencilOp.defaultValue : backDepthFailOp;
		this.backPassOp = backPassOp == null ? StencilOp.defaultValue : backPassOp;

		if (active) {
			updateBackStencilOp();
		}
	}

	public void setStencilOp(StencilOp stencilFailOp, StencilOp depthFailOp, StencilOp passOp) {
		if (frontStencilFailOp == stencilFailOp && frontDepthFailOp == depthFailOp && frontPassOp == passOp
				&& backStencilFailOp == stencilFailOp && backDepthFailOp == depthFailOp && backPassOp == passOp) {
			return;
		}

		this.frontStencilFailOp = stencilFailOp == null ? StencilOp.defaultValue : stencilFailOp;
		this.frontDepthFailOp = depthFailOp == null ? StencilOp.defaultValue : depthFailOp;
		this.frontPassOp = passOp == null ? StencilOp.defaultValue : passOp;
		this.backStencilFailOp = stencilFailOp == null ? StencilOp.defaultValue : stencilFailOp;
		this.backDepthFailOp = depthFailOp == null ? StencilOp.defaultValue : depthFailOp;
		this.backPassOp = passOp == null ? StencilOp.defaultValue : passOp;

		if (active) {
			gl20.glStencilOpSeparate(GL20.GL_FRONT_AND_BACK, stencilFailOp.glValue, depthFailOp.glValue,
					passOp.glValue);
		}
	}

	private void updateStencilOp() {
		if (frontStencilFailOp == backStencilFailOp && frontDepthFailOp == backDepthFailOp
				&& frontPassOp == backPassOp) {
			gl20.glStencilOpSeparate(GL20.GL_FRONT_AND_BACK, frontStencilFailOp.glValue, frontDepthFailOp.glValue,
					frontPassOp.glValue);
		} else {
			gl20.glStencilOpSeparate(GL20.GL_FRONT, frontStencilFailOp.glValue, frontDepthFailOp.glValue,
					frontPassOp.glValue);
			gl20.glStencilOpSeparate(GL20.GL_BACK, backStencilFailOp.glValue, backDepthFailOp.glValue,
					backPassOp.glValue);
		}
	}

	public boolean isScissorEnabled() {
		return scissorEnabled;
	}

	public void setScissorEnabled(boolean scissorEnabled) {
		if (this.scissorEnabled == scissorEnabled) {
			return;
		}

		this.scissorEnabled = scissorEnabled;

		if (active) {
			updateScissorEnabled();
		}
	}

	private void updateScissorEnabled() {
		if (stencilEnabled) {
			gl20.glEnable(GL20.GL_SCISSOR_TEST);
		} else {
			gl20.glDisable(GL20.GL_SCISSOR_TEST);
		}
	}

	public GridRectangle getScissorRect() {
		return scissorRectOut.set(scissorRect);
	}

	public GridRectangle getScissorRect(GridRectangle scissorRectOut) {
		return scissorRectOut.set(scissorRect);
	}

	public void setScissorRect(GridRectangle scissorRect) {
		GridRectangle resolved = scissorRect == null ? defaultScissorRect : scissorRect;

		if (this.scissorRect.equals(resolved)) {
			return;
		}

		this.scissorRect.set(resolved);

		if (active) {
			updateScissorRect();
		}
	}

	private void updateScissorRect() {
		gl20.glScissor(scissorRect.x, scissorRect.y, scissorRect.width, scissorRect.height);
	}

	public Color getClearColorValue() {
		return clearColorValue;
	}

	public void setClearColorValue(Color clearColorValue) {
		Color resolved = clearColorValue == null ? defaultColor : clearColorValue;

		if (this.clearColorValue.equals(resolved)) {
			return;
		}

		this.clearColorValue.set(resolved);

		if (active) {
			updateClearColor();
		}
	}

	private void updateClearColor() {
		gl20.glClearColor(clearColorValue.r, clearColorValue.g, clearColorValue.b, clearColorValue.a);
	}

	public void clearColor() {
		if (active) {
			gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
		}
	}

	public void clearColor(Color clearColorValue) {
		setClearColorValue(clearColorValue);
		clearColor();
	}

	public float getClearDepthValue() {
		return clearDepthValue;
	}

	public void setClearDepthValue(float clearDepthValue) {
		if (this.clearDepthValue == clearDepthValue) {
			return;
		}

		this.clearDepthValue = clearDepthValue;

		if (active) {
			updateClearDepth();
		}
	}

	private void updateClearDepth() {
		gl20.glClearDepthf(clearDepthValue);
	}

	public void clearDepth() {
		if (active) {
			gl20.glClear(GL20.GL_DEPTH_BUFFER_BIT);
		}
	}

	public void clearDepth(float clearDepthValue) {
		setClearDepthValue(clearDepthValue);
		clearDepth();
	}

	public int getClearStencilValue() {
		return clearStencilValue;
	}

	public void setClearStencilValue(int clearStencilValue) {
		if (this.clearStencilValue == clearStencilValue) {
			return;
		}

		this.clearStencilValue = clearStencilValue;

		if (active) {
			updateClearStencil();
		}
	}

	private void updateClearStencil() {
		gl20.glClearStencil(clearStencilValue);
	}

	public void clearStencil() {
		if (active) {
			gl20.glClear(GL20.GL_STENCIL_BUFFER_BIT);
		}
	}

	public void clearStencil(int clearStencilValue) {
		setClearStencilValue(clearStencilValue);
		clearStencil();
	}

	public void clear(boolean color, boolean depth, boolean stencil) {
		if (!active) {
			return;
		}

		int clearMask = 0;

		if (color) {
			clearMask |= GL20.GL_COLOR_BUFFER_BIT;
		}
		if (depth) {
			clearMask |= GL20.GL_DEPTH_BUFFER_BIT;
		}
		if (stencil) {
			clearMask |= GL20.GL_STENCIL_BUFFER_BIT;
		}

		if (clearMask != 0) {
			gl20.glClear(clearMask);
		}
	}

	public boolean isEnableCullFace() {
		return enableCullFace;
	}

	public void setEnableCullFace(boolean enableCullFace) {
		if (this.enableCullFace == enableCullFace) {
			return;
		}

		this.enableCullFace = enableCullFace;

		if (active) {
			updateEnableCullFace();
		}
	}

	private void updateEnableCullFace() {
		if (enableCullFace) {
			Gdx.gl.glEnable(GL20.GL_CULL_FACE);
		} else {
			Gdx.gl.glDisable(GL20.GL_CULL_FACE);
		}
	}

	public CullFace getCullFace() {
		return cullFace;
	}

	public void setCullFace(CullFace cullFace) {
		if (this.cullFace == cullFace) {
			return;
		}

		this.cullFace = cullFace == null ? CullFace.defaultValue : cullFace;

		if (active) {
			updateCullFace();
		}
	}

	private void updateCullFace() {
		gl20.glCullFace(cullFace.glValue);
	}

	public FrontFace getFrontFace() {
		return frontFace;
	}

	public void setFrontFace(FrontFace frontFace) {
		if (this.frontFace == frontFace) {
			return;
		}

		this.frontFace = frontFace == null ? FrontFace.defaultValue : frontFace;

		if (active) {
			updateFrontFace();
		}
	}

	private void updateFrontFace() {
		gl20.glFrontFace(frontFace.glValue);
	}

	public float getLineWidth() {
		return lineWidth;
	}

	public void setLineWidth(float lineWidth) {
		if (this.lineWidth == lineWidth) {
			return;
		}

		this.lineWidth = lineWidth;

		if (active) {
			updateLineWidth();
		}
	}

	private void updateLineWidth() {
		gl20.glLineWidth(lineWidth);
	}

	public RenderTarget getActiveRenderTarget() {
		return activeRenderTarget;
	}

	public void setActiveRenderTarget(RenderTarget renderTarget) {
		this.activeRenderTarget = renderTarget;
	}

	public final int bind(final GLTexture texture) {
		int boundUnit = -1;
		int weightUnit = -1;

		for (int i = 0; i < textures.length; i++) {
			if (textures[i] == texture && (boundUnit < 0 || replace(boundUnit, i))) {
				boundUnit = i;
			} else if (boundUnit < 0 && (weightUnit < 0 || replace(weightUnit, i))) {
				weightUnit = i;
			}
		}

		int unit = boundUnit < 0 ? weightUnit : boundUnit;
		bind(texture, unit);
		return unit;
	}

	private boolean replace(int oldUnit, int newUnit) {
		int oldWeight = textureWeights[oldUnit];
		int newWeight = textureWeights[newUnit];

		if (oldWeight > newWeight) {
			return true;
		} else if (oldWeight == newWeight) {
			return textureIds[oldUnit] > textureIds[newUnit];
		} else {
			return false;
		}
	}

	public final void bind(final GLTexture texture, int unit) {
		if (textures[unit] == texture) {
			textureWeights[unit] += textureReuseWeight;
			activateTexture(unit);
		} else {
			textures[unit] = texture;
			textureWeights[unit] = 100;
			textureIds[unit] = textureSequence++;
			activeTexture = GL20.GL_TEXTURE0 + unit;
			texture.bind(unit);
		}
	}

	private void activateTexture() {
		Gdx.gl.glActiveTexture(activeTexture);
	}

	public void activateTexture(int unit) {
		int glUnit = GL20.GL_TEXTURE0 + unit;
		if (glUnit != activeTexture) {
			activeTexture = glUnit;
			Gdx.gl.glActiveTexture(activeTexture);
		}
	}
}
