package com.gurella.engine.scene.renderable.shape;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

public class CapsuleShapeModel extends ShapeModel {
	private float radius = 0.2f;
	private float height = 1;
	private int divisions = 10;

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
		float radiusTimesTwo = 2 * radius;
		if (height < radiusTimesTwo) {
			height = radiusTimesTwo;
		}
		dirty = true;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
		float halfHeight = height / 2;
		if (halfHeight > radius) {
			radius = halfHeight;
		}
		dirty = true;
	}

	public int getDivisions() {
		return divisions;
	}

	public void setDivisions(int divisions) {
		this.divisions = divisions;
		dirty = true;
	}

	@Override
	protected Model createModel(ModelBuilder builder) {
		builder.begin();
		builder.part("capsule", getGlPrimitiveType(), getVertexAttributes(), getMaterial()).capsule(radius, height,
				divisions);
		return builder.end();
	}
}
