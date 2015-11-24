package com.gurella.engine.graphics.vector.svg.element;

import com.gurella.engine.graphics.vector.Path;
import com.gurella.engine.graphics.vector.svg.property.PropertyType;
import com.gurella.engine.graphics.vector.svg.property.value.Length;

public class CircleElement extends ShapeElement {
	public CircleElement() {
		super(ElementType.circle);
	}

	@Override
	protected void initPath(Path path) {
		path.circle(getCx(), getCy(), getR());
	}
	
	public float getCx() {
		return this.<Length>getPropertyOrDefault(PropertyType.cx).getPixels();
	}

	public float getCy() {
		return this.<Length>getPropertyOrDefault(PropertyType.cy).getPixels();
	}

	public float getR() {
		return this.<Length>getProperty(PropertyType.r).getPixels();
	}
}
