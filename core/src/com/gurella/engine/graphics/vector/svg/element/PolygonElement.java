package com.gurella.engine.graphics.vector.svg.element;

import com.badlogic.gdx.utils.FloatArray;
import com.gurella.engine.graphics.vector.Path;
import com.gurella.engine.graphics.vector.svg.property.PropertyType;

public class PolygonElement extends ShapeElement {
	public PolygonElement() {
		super(ElementType.polygon);
	}

	@Override
	protected void initPath(Path out) {
		FloatArray points = getPoints();
		out.polygon(points);
	}

	public FloatArray getPoints() {
		return getProperty(PropertyType.points);
	}
}
