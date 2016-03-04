package com.gurella.engine.scene.renderable;

import com.gurella.engine.math.geometry.shape.Shape;

public class SolidComponent extends RenderableComponent3d {
	public Shape shape;

	@Override
	protected void updateDefaultTransform() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void updateTransform() {
		// TODO Auto-generated method stub

	}

	@Override
	public void reset() {
		super.reset();
		shape = null;
	}
}
