package com.gurella.engine.graphics.render.command;

import com.gurella.engine.graphics.render.gl.GlContext;

public class CompositeRenderCommand implements RenderCommand {
	private final RenderComandBuffer composition;

	public CompositeRenderCommand(RenderComandBuffer composition) {
		this.composition = composition;
	}

	@Override
	public void process(GlContext glContext) {
		composition.process(glContext);
	}
}
