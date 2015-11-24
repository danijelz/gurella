package com.gurella.engine.graphics.vector.svg.element;

import com.gurella.engine.graphics.vector.Canvas;
import com.gurella.engine.graphics.vector.Path;
import com.gurella.engine.graphics.vector.svg.property.PropertyType;
import com.gurella.engine.graphics.vector.svg.property.value.Length;

public class LineElement extends ShapeElement {
	public LineElement() {
		super(ElementType.line);
	}

	@Override
	protected void initPath(Path out) {
		out.moveTo(getX1(), getY1());
		out.lineTo(getX2(), getY2());
	}

	public float getX1() {
		return this.<Length> getPropertyOrDefault(PropertyType.x1).getPixels();
	}

	public float getY1() {
		return this.<Length> getPropertyOrDefault(PropertyType.y1).getPixels();
	}

	public float getX2() {
		return this.<Length> getPropertyOrDefault(PropertyType.x2).getPixels();
	}

	public float getY2() {
		return this.<Length> getPropertyOrDefault(PropertyType.y2).getPixels();
	}

	@Override
	protected boolean initFillPaint(Canvas canvas, float globalOpacity) {
		// http://www.w3.org/TR/SVG/shapes.html#LineElement
		// Because ‘line’ elements are single lines and thus are geometrically
		// one-dimensional, they have no interior; thus, ‘line’ elements are
		// never filled
		return false;
	}
}
