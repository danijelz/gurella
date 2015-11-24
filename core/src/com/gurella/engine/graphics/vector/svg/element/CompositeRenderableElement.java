package com.gurella.engine.graphics.vector.svg.element;

import com.gurella.engine.graphics.vector.svg.SvgRenderContext;

public abstract class CompositeRenderableElement extends KnownElement implements RenderableElement {
	public CompositeRenderableElement(ElementType elementType) {
		super(elementType);
	}

	@Override
	public void renderGeometry(SvgRenderContext state) {
	}
}
