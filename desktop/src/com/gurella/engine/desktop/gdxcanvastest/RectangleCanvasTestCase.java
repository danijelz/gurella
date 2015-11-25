package com.gurella.engine.desktop.gdxcanvastest;

import com.gurella.engine.graphics.vector.Canvas;
import com.gurella.engine.graphics.vector.DrawingStyle;

public class RectangleCanvasTestCase implements CanvasTestCase {
	@Override
	public void render(Canvas canvas) {
		canvas.setDrawingStyle(DrawingStyle.fill);
		canvas.setFillColor(0, 0, 1f, 1f);
		canvas.drawRectangle(300, 300, 100, 100);
	}
}
