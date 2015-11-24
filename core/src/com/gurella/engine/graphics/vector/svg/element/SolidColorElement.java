package com.gurella.engine.graphics.vector.svg.element;

import com.gurella.engine.graphics.vector.svg.property.PropertyType;
import com.gurella.engine.graphics.vector.svg.property.value.Paint;

public class SolidColorElement extends KnownElement implements PaintElement {
	public SolidColorElement() {
		super(ElementType.solidColor);
	}

	public Paint getSolidColor() {
		return getPropertyOrDefault(PropertyType.solidColor);
	}

	public float getSolidOpacity() {
		return this.<Float> getPropertyOrDefault(PropertyType.solidOpacity).floatValue();
	}

	@Override
	public void initCanvasPaint(com.gurella.engine.graphics.vector.Paint canvasPaint, float opacity) {
		Paint solidColor = getSolidColor();
		float solidOpacity = getSolidOpacity();
		solidColor.initCanvasPaint(getSvg(), canvasPaint, opacity * solidOpacity);
	}
}
