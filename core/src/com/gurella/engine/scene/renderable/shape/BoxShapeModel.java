package com.gurella.engine.scene.renderable.shape;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

public class BoxShapeModel extends ShapeModel {
	private float width = 1;
	private float height = 1;
	private float depth = 1;

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
		dirty = true;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
		dirty = true;
	}

	public float getDepth() {
		return depth;
	}

	public void setDepth(float depth) {
		this.depth = depth;
		dirty = true;
	}

	@Override
	protected Model createModel(ModelBuilder builder) {
		builder.begin();
		builder.part("box", getGlPrimitiveType(), getVertexAttributes(), getMaterial()).box(width, height, depth);
		return builder.end();
	}
}
