package com.gurella.engine.graphics.vector.svg.element;

import com.badlogic.gdx.math.Rectangle;
import com.gurella.engine.graphics.vector.svg.property.PropertyType;
import com.gurella.engine.graphics.vector.svg.property.value.Overflow;
import com.gurella.engine.graphics.vector.svg.property.value.PreserveAspectRatio;

public class ViewElement extends KnownElement {
	public ViewElement() {
		super(ElementType.view);
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

	private SvgElement getSvgElement() {
		Element temp = getParent();
		while (temp != null) {
			if (temp instanceof SvgElement) {
				return (SvgElement) temp;
			}
		}

		throw new IllegalStateException();
	}
}
