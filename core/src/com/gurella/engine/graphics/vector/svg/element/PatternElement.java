package com.gurella.engine.graphics.vector.svg.element;

import com.gurella.engine.graphics.vector.Paint;

public class PatternElement extends KnownElement implements PaintElement {
	public PatternElement() {
		super(ElementType.pattern);
	}

	@Override
	public void initCanvasPaint(Paint canvasPaint, float opacity) {
		// TODO Auto-generated method stub
		
	}
}
