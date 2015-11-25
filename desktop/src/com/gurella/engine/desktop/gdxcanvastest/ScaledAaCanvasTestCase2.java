package com.gurella.engine.desktop.gdxcanvastest;

import com.gurella.engine.graphics.vector.Canvas;

public class ScaledAaCanvasTestCase2 implements CanvasTestCase {
	@Override
	public void render(Canvas canvas) {
		canvas.saveState(true);
		canvas.setFillColor(0, 0, 1f, 0.3f);
		canvas.drawCircle(200, 201, 100);
		
		canvas.drawCircle(500, 200, 100);
		
		canvas.scale(10f, 10f);
		canvas.setFillColor(0, 1f, 0, 0.3f);
		canvas.drawCircle(20, 20, 10);
		canvas.drawCircle(80, 20, 10);
	}
}
