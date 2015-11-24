package com.gurella.engine.graphics.vector.svg.element;

import com.badlogic.gdx.utils.FloatArray;
import com.gurella.engine.graphics.vector.Path;
import com.gurella.engine.graphics.vector.svg.property.PropertyType;

public class PolylineElement extends ShapeElement {
	public PolylineElement() {
		super(ElementType.polyline);
	}

	@Override
	protected void initPath(Path out) {
		FloatArray points = getPoints();
		out.polyline(points);
	}

	public FloatArray getPoints() {
		return getProperty(PropertyType.points);
	}
}
