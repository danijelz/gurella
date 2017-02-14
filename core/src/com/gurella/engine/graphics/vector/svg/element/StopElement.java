package com.gurella.engine.graphics.vector.svg.element;

import com.gurella.engine.graphics.vector.svg.property.PropertyType;
import com.gurella.engine.graphics.vector.svg.property.value.Color;

public class StopElement extends KnownElement {
	public StopElement() {
		super(ElementType.stop);
	}

	public Color getStopColor() {
		return getPropertyOrDefault(PropertyType.stopColor);
	}

	public float getStopOpacity() {
		return this.<Float> getPropertyOrDefault(PropertyType.stopOpacity).floatValue();
	}

	public float getOffset() {
		return this.<Float> getPropertyOrDefault(PropertyType.offset).floatValue();
	}
}
