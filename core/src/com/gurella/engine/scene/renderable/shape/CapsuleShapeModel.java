package com.gurella.engine.scene.renderable.shape;

import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.gurella.engine.math.geometry.Axis;
import com.gurella.engine.pool.PoolService;

public class CapsuleShapeModel extends ShapeModel {
	private Axis axis = Axis.y;
	private float radius = 0.2f;
	private float height = 1;
	private int divisions = 10;

	public Axis getAxis() {
		return axis;
	}

	public void setAxis(Axis axis) {
		this.axis = axis == null ? Axis.y : axis;
		dirty = true;
	}

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

	public void set(Axis axis, float radius, float height) {
		this.axis = axis == null ? Axis.y : axis;
		this.radius = radius;
		this.height = height;
		dirty = true;
	}

	@Override
	protected void buildParts(ModelBuilder builder, Matrix4 parentTransform) {
		MeshPartBuilder part = builder.part("capsule", getGlPrimitiveType(), getVertexAttributes(), getMaterial());

		if (axis != Axis.y) {
			Matrix4 rotationMatrix = PoolService.obtain(Matrix4.class);
			rotationMatrix.setToRotation(axis == Axis.z ? 1 : 0, 0, axis == Axis.x ? 1 : 0, 90);
			parentTransform.mulLeft(rotationMatrix);
			PoolService.free(rotationMatrix);
		}

		part.setVertexTransform(parentTransform);
		part.capsule(radius, height, divisions);
	}
}
