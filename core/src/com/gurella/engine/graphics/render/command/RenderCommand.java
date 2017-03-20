package com.gurella.engine.graphics.render.command;

import com.gurella.engine.graphics.render.RenderContext;
import com.gurella.engine.graphics.render.gl.GlContext;

public interface RenderCommand {
	void process(RenderContext renderContext, GlContext glContext);
}
