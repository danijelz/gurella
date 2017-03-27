package com.gurella.engine.graphics.render.renderable;

import com.gurella.engine.graphics.render.gl.GlContext;

public interface Renderer<T extends RenederableGeometry> {
	void render(T geometry, GlContext glContext);
}
