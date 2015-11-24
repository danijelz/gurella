package com.gurella.engine.graphics.vector.svg.element;

import com.gurella.engine.graphics.vector.Path;
import com.gurella.engine.graphics.vector.svg.property.PropertyType;
import com.gurella.engine.graphics.vector.svg.property.value.Length;

public class EllipseElement extends ShapeElement {
	public EllipseElement() {
		super(ElementType.ellipse);
	}

	public float getCx() {
		return this.<Length> getPropertyOrDefault(PropertyType.cx).getPixels();
	}

	public float getCy() {
		return this.<Length> getPropertyOrDefault(PropertyType.cy).getPixels();
	}

	public float getRx() {
		return this.<Length> getProperty(PropertyType.rx).getPixels();
	}

	public float getRy() {
		return this.<Length> getProperty(PropertyType.ry).getPixels();
	}

	@Override
	protected void initPath(Path out) {
		out.ellipse(getCx(), getCy(), getRx(), getRy());
	}
}
