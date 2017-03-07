package com.gurella.engine.graphics.render.command;

import com.gurella.engine.graphics.render.RenderContext;

public class CompositeRenderCommand implements RenderCommand {
	private final RenderComandBuffer composition;

	public CompositeRenderCommand(RenderComandBuffer composition) {
		this.composition = composition;
	}

	@Override
	public void process(RenderContext context) {
		composition.render(context);
	}
}
