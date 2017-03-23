package com.gurella.engine.graphics.render.command;

import com.gurella.engine.graphics.render.gl.GlContext;

public interface RenderCommand {
	void process(GlContext glContext);
}
