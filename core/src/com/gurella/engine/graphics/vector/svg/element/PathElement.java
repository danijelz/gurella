package com.gurella.engine.graphics.vector.svg.element;

import com.gurella.engine.graphics.vector.Path;
import com.gurella.engine.graphics.vector.svg.property.PropertyType;

public class PathElement extends ShapeElement {
	public PathElement() {
		super(ElementType.path);
	}

	@Override
	protected void initPath(Path out) {
		Path path = getPath();
		out.set(path);
	}

	private Path getPath() {
		return getProperty(PropertyType.d);
	}
}
