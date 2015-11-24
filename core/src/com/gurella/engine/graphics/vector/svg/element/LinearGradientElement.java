package com.gurella.engine.graphics.vector.svg.element;

import com.gurella.engine.graphics.vector.Paint;
import com.gurella.engine.graphics.vector.svg.property.PropertyType;
import com.gurella.engine.graphics.vector.svg.property.value.Length;

public class LinearGradientElement extends GradientElement {
	public LinearGradientElement() {
		super(ElementType.linearGradient);
	}

	public float getX1() {
		return this.<Length> getPropertyOrDefault(PropertyType.x1).getPixels();
	}

	public float getY1() {
		return this.<Length> getPropertyOrDefault(PropertyType.y1).getPixels();
	}

	public float getX2() {
		return this.<Length> getPropertyOrDefault(PropertyType.x2).getPixels();
	}

	public float getY2() {
		return this.<Length> getPropertyOrDefault(PropertyType.y2).getPixels();
	}

	@Override
	public void initCanvasPaint(Paint canvasPaint, float opacity) {
		canvasPaint.setToLinearGradient(getX1(), getY1(), getX2(), getY2(), getGradientSpread(), getGradientTransform(), getGradient());
		canvasPaint.mulAlpha(opacity);
	}
}
