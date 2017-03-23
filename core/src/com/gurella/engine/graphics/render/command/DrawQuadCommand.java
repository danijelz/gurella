package com.gurella.engine.graphics.render.command;

import com.gurella.engine.graphics.render.RenderTarget;
import com.gurella.engine.graphics.render.gl.GlContext;
import com.gurella.engine.graphics.render.material.Material;

public class DrawQuadCommand implements RenderCommand {
	String sourceTextureUniformName = "u_sourceTexture";
	private RenderTarget source;
	private RenderTarget destination;
	private Material material;

	@Override
	public void process(GlContext glContext) {
		destination.bind();
		material.begin();
		// TODO Auto-generated method stub
	}
}
