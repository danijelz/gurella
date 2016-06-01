package com.gurella.engine.graphics.render.command;

import com.badlogic.gdx.graphics.Color;
import com.gurella.engine.graphics.render.RenderContext;

public class ClearRenderTargetCommand implements RenderCommand {
	public final Color clearColorValue = new Color();
	public float clearDepthValue;
	public int clearStencilValue;
	
	@Override
	public void render(RenderContext context) {
		// TODO Auto-generated method stub
		
	}
	
	public enum ClearType {
		color, depth, stencil, colorDepth, colorStencil, colorDepthStencil, depthStencil;
	}
}
