package com.gurella.engine.graphics.render.material;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;
import com.gurella.engine.graphics.render.gl.BlendEquation;
import com.gurella.engine.graphics.render.gl.BlendFunction;
import com.gurella.engine.graphics.render.gl.ColorMask;
import com.gurella.engine.graphics.render.gl.CullFace;
import com.gurella.engine.graphics.render.gl.DepthTestFunction;
import com.gurella.engine.graphics.render.gl.FrontFace;
import com.gurella.engine.graphics.render.gl.StencilFunction;
import com.gurella.engine.graphics.render.gl.StencilOp;

public class Pass implements Disposable {
	public final Technique technique;

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
	private StencilFunction frontStencilFunction = StencilFunction.always;
	private StencilOp frontStencilFailOp = StencilOp.keep;
	private StencilOp frontDepthFailOp = StencilOp.keep;
	private StencilOp frontPassOp = StencilOp.keep;
	private int backStencilMask = 0xffffffff;
	private StencilFunction backStencilFunction = StencilFunction.always;
	private StencilOp backStencilFailOp = StencilOp.keep;
	private StencilOp backDepthFailOp = StencilOp.keep;
	private StencilOp backPassOp = StencilOp.keep;

	private CullFace cullFace = CullFace.back;
	private FrontFace frontFace = FrontFace.ccw;

	private float lineWidth = 1;

	transient ShaderProgram shaderProgram;

	public Pass(Technique technique) {
		this.technique = technique;
	}

	public void begin() {
		// TODO Auto-generated method stub
		shaderProgram.begin();
	}

	@Override
	public void dispose() {
		if (shaderProgram != null) {
			shaderProgram.dispose();
		}
	}
}
