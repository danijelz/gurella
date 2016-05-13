package com.gurella.engine.scene.renderable.shape;

import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;

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
	protected void buildParts(ModelBuilder builder, Matrix4 parentTransform) {
		MeshPartBuilder part = builder.part("box", getGlPrimitiveType(), getVertexAttributes(), getMaterial());
		if (parentTransform != null) {
			part.setVertexTransform(parentTransform);
		}
		part.box(width, height, depth);
	}
}
