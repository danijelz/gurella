package com.gurella.engine.graphics.render.command;

public class CompositeRenderCommand implements RenderCommand {
	private final RenderComandBuffer composition;

	public CompositeRenderCommand(RenderComandBuffer composition) {
		this.composition = composition;
	}
}
