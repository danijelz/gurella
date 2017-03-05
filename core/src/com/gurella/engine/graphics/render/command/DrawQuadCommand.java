package com.gurella.engine.graphics.render.command;

import com.gurella.engine.graphics.render.RenderContext;
import com.gurella.engine.graphics.render.RenderTarget;
import com.gurella.engine.graphics.render.material.Material;

public class DrawQuadCommand implements RenderCommand {
	String sourceTextureUniformName = "u_sourceTexture";
	private RenderTarget source;
	private RenderTarget destination;
	private Material material;

	@Override
	public void render(RenderContext context) {
		destination.bind();
		material.begin();
		// TODO Auto-generated method stub
	}
}
