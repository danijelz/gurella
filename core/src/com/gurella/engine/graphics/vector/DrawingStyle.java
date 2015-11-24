package com.gurella.engine.graphics.vector;

public enum DrawingStyle {
	fill, stroke, fillAndStroke;

	public boolean drawFill() {
		return this != stroke;
	}

	public boolean drawStroke() {
		return this != fill;
	}

	public static DrawingStyle valueOf(boolean hasFill, boolean hasStroke) {
		return hasFill ? hasStroke ? fillAndStroke : fill : hasStroke ? stroke : null;
	}
}
