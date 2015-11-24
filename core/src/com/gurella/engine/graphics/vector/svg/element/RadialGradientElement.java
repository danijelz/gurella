package com.gurella.engine.graphics.vector.svg.element;

import com.gurella.engine.graphics.vector.Paint;
import com.gurella.engine.graphics.vector.svg.property.PropertyType;
import com.gurella.engine.graphics.vector.svg.property.value.Length;

public class RadialGradientElement extends GradientElement {
	public RadialGradientElement() {
		super(ElementType.radialGradient);
	}

	public float getCx() {
		return this.<Length> getPropertyOrDefault(PropertyType.cx).getPixels();
	}

	public float getCy() {
		return this.<Length> getPropertyOrDefault(PropertyType.cy).getPixels();
	}

	public float getFx() {
		return this.<Length> getPropertyOrDefault(PropertyType.fx).getPixels();
	}

	public float getFy() {
		return this.<Length> getPropertyOrDefault(PropertyType.fy).getPixels();
	}

	public float getRadius() {
		return this.<Length> getPropertyOrDefault(PropertyType.r).getPixels();
	}

	@Override
	public void initCanvasPaint(Paint canvasPaint, float opacity) {
		canvasPaint.setToRadialGradient(getCx(), getCy(), getFx(), getFy(), getRadius(), getGradientSpread(), getGradientTransform(), getGradient());
		canvasPaint.mulAlpha(opacity);
	}
}
