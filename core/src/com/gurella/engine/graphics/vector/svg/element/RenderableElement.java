package com.gurella.engine.graphics.vector.svg.element;

import com.gurella.engine.graphics.vector.svg.SvgRenderContext;

public interface RenderableElement {
	void renderGeometry(SvgRenderContext state);
}
