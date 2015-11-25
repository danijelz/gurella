package com.gurella.engine.desktop.gdxcanvastest;

import com.gurella.engine.graphics.vector.Canvas;
import com.gurella.engine.graphics.vector.DrawingStyle;

public class ScaledAaCanvasTestCase3 implements CanvasTestCase {
	@Override
	public void render(Canvas canvas) {
		canvas.saveState(true);
		canvas.setDrawingStyle(DrawingStyle.stroke);
		canvas.setStrokeColor(0, 0, 1f, 1f);
		canvas.setStrokeWidth(10);
		canvas.drawCircle(200, 201, 100);

		canvas.setStrokeColor(1f, 0, 0, 1f);
		canvas.drawCircle(500, 200, 100);

		canvas.scale(10f, 10f);
		canvas.setStrokeColor(0, 1f, 0, 1f);
		canvas.setStrokeWidth(1);
		canvas.drawCircle(20, 20, 10);

		canvas.setStrokeColor(1f, 0, 0, 1f);
		canvas.drawCircle(80, 20, 10);
	}
}
