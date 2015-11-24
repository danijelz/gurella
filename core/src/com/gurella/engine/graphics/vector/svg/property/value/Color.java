package com.gurella.engine.graphics.vector.svg.property.value;

import com.gurella.engine.graphics.vector.svg.Svg;

public class Color extends Paint {
	public static final Color black = new UnmodifiableColor(0);

	private int color;

	public Color(int color) {
		this.color = color;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	@Override
	public void initCanvasPaint(Svg svg, com.gurella.engine.graphics.vector.Paint canvasPaint, float opacity) {
		float r = ((color & 0x00ff0000) >>> 16) / 255f;
		float g = ((color & 0x0000ff00) >>> 8) / 255f;
		float b = ((color & 0x000000ff)) / 255f;
		canvasPaint.setToColor(r, g, b, opacity);
	}

	public static class UnmodifiableColor extends Color {
		public UnmodifiableColor(int color) {
			super(color);
		}

		@Override
		public void setColor(int color) {
			throw new UnsupportedOperationException();
		}
	}
}
