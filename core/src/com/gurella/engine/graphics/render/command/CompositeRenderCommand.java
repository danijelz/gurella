package com.gurella.engine.graphics.render.command;

import com.gurella.engine.graphics.render.RenderComandBuffer;
import com.gurella.engine.graphics.render.RenderContext;
import com.gurella.engine.graphics.render.gl.GlContext;

public class CompositeRenderCommand implements RenderCommand {
	private final RenderComandBuffer composition;

	public CompositeRenderCommand(RenderComandBuffer composition) {
		this.composition = composition;
	}

	@Override
	public void init(RenderContext renderContext) {
	}

	@Override
	public void process(GlContext glContext) {
		composition.process(glContext);
	}
}
