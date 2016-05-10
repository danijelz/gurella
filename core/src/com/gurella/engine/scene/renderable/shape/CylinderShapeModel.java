package com.gurella.engine.scene.renderable.shape;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

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
	protected Model createModel(ModelBuilder builder) {
		builder.begin();
		builder.part("cylinder", getGlPrimitiveType(), getVertexAttributes(), getMaterial()).cylinder(width, height,
				depth, divisions, angleFrom, angleTo);
		return builder.end();
	}

}
