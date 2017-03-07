package com.gurella.engine.graphics.render.command;

import com.gurella.engine.graphics.render.RenderContext;

public interface RenderCommand {
	void process(RenderContext context);
}
