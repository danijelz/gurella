package com.gurella.engine.graphics.vector.svg.element;

import com.badlogic.gdx.math.Rectangle;
import com.gurella.engine.graphics.vector.svg.property.PropertyType;
import com.gurella.engine.graphics.vector.svg.property.value.Length;
import com.gurella.engine.graphics.vector.svg.property.value.Overflow;
import com.gurella.engine.graphics.vector.svg.property.value.PreserveAspectRatio;

public class SvgElement extends KnownElement implements RenderableElement {
	public SvgElement() {
		super(ElementType.svg);
	}

	public Rectangle getViewBox() {
		return getProperty(PropertyType.viewBox);
	}

	public PreserveAspectRatio getPreserveAspectRatio() {
		return getPropertyOrDefault(PropertyType.preserveAspectRatio);
	}

	public Overflow getOverflow() {
		return getPropertyOrDefault(PropertyType.overflow);
	}

	public float getX() {
		return this.<Length> getPropertyOrDefault(PropertyType.x).getPixels();
	}

	public float getY() {
		return this.<Length> getPropertyOrDefault(PropertyType.y).getPixels();
	}

	public float getWidth() {
		return this.<Length> getProperty(PropertyType.width).getPixels();
	}

	public float getHeight() {
		return this.<Length> getProperty(PropertyType.height).getPixels();
	}
}
