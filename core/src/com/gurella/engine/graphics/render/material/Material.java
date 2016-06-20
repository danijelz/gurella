package com.gurella.engine.graphics.render.material;

import com.gurella.engine.graphics.render.path.RenderPath.RenderPathMaterialProperties;
import com.gurella.engine.graphics.render.shader.Shader;

public class Material {
	Shader shader;
	RenderPathMaterialProperties renderPathOverrides;

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

	private boolean cullFaceEnabled;
	private int cullFace;
	private int frontFace;
	
	public void bind() {
		// TODO Auto-generated method stub
		
	}
}
