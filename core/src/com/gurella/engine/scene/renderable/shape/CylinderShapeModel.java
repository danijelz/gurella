package com.gurella.engine.scene.renderable.shape;

import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;

public class CylinderShapeModel extends ShapeModel {
	private float width = 1;
	private float height = 1;
	private float depth = 1;
	private int divisions = 10;
	private float angleFrom = 0;
	private float angleTo = 360;

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

	public int getDivisions() {
		return divisions;
	}

	public void setDivisions(int divisions) {
		this.divisions = divisions;
		dirty = true;
	}

	public float getAngleFrom() {
		return angleFrom;
	}

	public void setAngleFrom(float angleFrom) {
		this.angleFrom = angleFrom;
		dirty = true;
	}

	public float getAngleTo() {
		return angleTo;
	}

	public void setAngleTo(float angleTo) {
		this.angleTo = angleTo;
		dirty = true;
	}

	@Override
	protected void buildParts(ModelBuilder builder, Matrix4 parentTransform) {
		MeshPartBuilder part = builder.part("cylinder", getGlPrimitiveType(), getVertexAttributes(), getMaterial());
		if (parentTransform != null) {
			part.setVertexTransform(parentTransform);
		}
		part.cylinder(width, height, depth, divisions, angleFrom, angleTo);
	}
}
