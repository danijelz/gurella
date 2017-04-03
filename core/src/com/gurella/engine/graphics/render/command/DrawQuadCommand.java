package com.gurella.engine.graphics.render.command;

import com.gurella.engine.graphics.render.RenderContext;
import com.gurella.engine.graphics.render.RenderTarget;
import com.gurella.engine.graphics.render.gl.GlContext;
import com.gurella.engine.graphics.render.material.Material;
import com.gurella.engine.graphics.render.material.MaterialInstance;

public class DrawQuadCommand implements RenderCommand {
	String sourceTextureUniformName = "u_sourceTexture";
	private RenderTarget source;
	private RenderTarget destination;

	private String passName;
	private Material material;
	private MaterialInstance materialInstance;

	@Override
	public void init(RenderContext renderContext) {
	}

	@Override
	public void process(GlContext glContext) {
		if (materialInstance == null) {
			materialInstance = material.createInstance();
		}

		destination.bind();
		materialInstance.bind(passName);
		// TODO Auto-generated method stub
	}
}
