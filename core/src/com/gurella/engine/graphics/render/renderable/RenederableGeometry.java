package com.gurella.engine.graphics.render.renderable;

public interface RenederableGeometry {
	RenederableGeometryType getType();

	public enum RenederableGeometryType {
		vbo, vertices2d, vertices3d;
	}
}
