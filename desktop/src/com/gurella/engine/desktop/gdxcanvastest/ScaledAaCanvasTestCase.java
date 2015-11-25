package com.gurella.engine.desktop.gdxcanvastest;

import com.gurella.engine.graphics.vector.Canvas;
import com.gurella.engine.graphics.vector.DrawingStyle;

public class ScaledAaCanvasTestCase implements CanvasTestCase {
	@Override
	public void render(Canvas canvas) {
		canvas.saveState(true);
		canvas.setFillColor(0, 0, 1f, 1f);

		canvas.drawEllipse(300, 310, 20, 251);

		canvas.scale(2f, 25f);
		canvas.translate(35f, 60);
		canvas.drawCircle(10, 10, 10);

		canvas.saveState(true);
		canvas.setDrawingStyle(DrawingStyle.stroke);
		canvas.setStrokeColor(1f, 0, 1f, 1f);
		canvas.setStrokeWidth(2);
		canvas.translate(580, 0);
		canvas.drawCircle(10, 10, 10);
	}
}
