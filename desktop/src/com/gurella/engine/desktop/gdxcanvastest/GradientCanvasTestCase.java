package com.gurella.engine.desktop.gdxcanvastest;

import com.gurella.engine.graphics.vector.Canvas;
import com.gurella.engine.graphics.vector.DrawingStyle;
import com.gurella.engine.graphics.vector.GradientSpread;

public class GradientCanvasTestCase implements CanvasTestCase {
	private int gradientHandle = -1;

	@Override
	public void render(Canvas canvas) {
		canvas.setDrawingStyle(DrawingStyle.fill);
		if (gradientHandle < 0) {
			gradientHandle = canvas.newGradient().add(0, 1, 0, 0, 1f).add(0.5f, 0, 0, 1, 1f).build();
		}
		//canvas.setFillRadialGradient(305, 305, 405, 405, 10, GradientSpread.reflect, gradientHandle);
		//canvas.setFillConicalGradient(305, 305, gradientHandle);
		canvas.setFillToLinearGradient(100, 100, 120, 120, GradientSpread.reflect, gradientHandle);
		canvas.drawCircle(305, 305, 100);
	}
}
