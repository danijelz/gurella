package com.gurella.engine.graphics.vector.svg.property.value;

import com.gurella.engine.graphics.vector.svg.Svg;

public abstract class Paint {
	public static final Paint none = new NonePaint();
	public static final Paint currentColor = new NonePaint();

	public abstract void initCanvasPaint(Svg svg, com.gurella.engine.graphics.vector.Paint canvasPaint, float opacity);

	private static final class NonePaint extends Paint {
		@Override
		public void initCanvasPaint(Svg svg, com.gurella.engine.graphics.vector.Paint canvasPaint, float opacity) {
		}
	}
}
