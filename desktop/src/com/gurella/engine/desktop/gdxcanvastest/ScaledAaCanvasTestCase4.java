package com.gurella.engine.desktop.gdxcanvastest;

import com.gurella.engine.graphics.vector.Canvas;
import com.gurella.engine.graphics.vector.DrawingStyle;
import com.gurella.engine.graphics.vector.LineCap;

public class ScaledAaCanvasTestCase4 implements CanvasTestCase {
	@Override
	public void render(Canvas canvas) {
		canvas.setDrawingStyle(DrawingStyle.stroke);
		canvas.setStrokeLineCap(LineCap.square);
		
		canvas.setStrokeColor(0, 0, 1f, 1f);
		canvas.setStrokeWidth(10);
		canvas.drawPolyline(100, 100, 105, 100, 200, 100, 295, 100, 300, 100);
		
		canvas.scale(1f, 10f);
		canvas.setStrokeColor(1f, 0, 1f, 1f);
		canvas.setStrokeWidth(1);
		canvas.drawPolyline(100, 12, 105, 12, 200, 12, 295, 12, 300, 12);
	}
}
