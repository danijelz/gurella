package com.gurella.engine.graphics.render.command;

import com.gurella.engine.graphics.render.gl.GlContext;

public class FlushCommand implements RenderCommand {
	@Override
	public void process(GlContext glContext) {
		glContext.gl20.glFlush();
	}
}
