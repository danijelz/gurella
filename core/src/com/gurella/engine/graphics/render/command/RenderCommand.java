package com.gurella.engine.graphics.render.command;

import com.gurella.engine.graphics.render.RenderContext;
import com.gurella.engine.graphics.render.gl.GlContext;

public interface RenderCommand {
	void init(RenderContext renderContext);

	void process(GlContext glContext);
}
