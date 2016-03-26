package com.gurella.engine.graphics.vector.svg.element;

import com.gurella.engine.graphics.vector.svg.SvgRenderContext;
import com.gurella.engine.graphics.vector.svg.property.PropertyType;
import com.gurella.engine.graphics.vector.svg.property.value.Length;

public class UseElement extends KnownElement implements RenderableElement {
	public UseElement() {
		super(ElementType.use);
	}

	private Element getReferencedElement() {
		return getSvg().getElement(this.<String> getProperty(PropertyType.href));
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

	@Override
	public void renderGeometry(SvgRenderContext state) {
		// TODO Auto-generated method stub
		
	}
}
