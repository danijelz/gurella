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
		return getPropertyOrDefault(PropertyType.stopOpacity);
	}

	public float getOffset() {
		return getPropertyOrDefault(PropertyType.offset);
	}
}
