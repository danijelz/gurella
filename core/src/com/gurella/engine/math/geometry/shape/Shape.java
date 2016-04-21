package com.gurella.engine.math.geometry.shape;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public abstract class Shape {
	protected CompositeShape parent;

	protected final BoundingBox bounds = new BoundingBox();
	protected boolean boundsDirty = true;

	protected final Matrix4 localTransform = new Matrix4();
	protected final Matrix4 globalTransform = new Matrix4();
	protected boolean transformDirty = true;

	protected boolean graphicsDirty = true;

	public CompositeShape getParent() {
		return parent;
	}

	public BoundingBox getBounds() {
		if (boundsDirty) {
			bounds.clr();
			updateBounds();
			bounds.mul(getGlobalTransform());
		}
		return bounds;
	}

	public Matrix4 getGlobalTransform() {
		if (transformDirty) {
			if (parent == null) {
				globalTransform.set(localTransform);
			} else {
				globalTransform.set(parent.getGlobalTransform()).mul(localTransform);
			}
		}
		return globalTransform;
	}

	public void setLocalTransform(Matrix4 localTransform) {
		this.localTransform.set(localTransform);
		transformDirty = true;
	}

	protected void updateBounds() {
	}

	public boolean contains(Vector2 point) {
		return contains(point.x, point.y, 0f);
	}

	public boolean contains(float x, float y) {
		return contains(x, y, 0f);
	}

	public boolean contains(Vector3 point) {
		return contains(point.x, point.y, point.z);
	}

	public boolean contains(float x, float y, float z) {
		getBounds();
		Vector3 min = bounds.min;
		Vector3 max = bounds.max;
		return bounds.isValid() && min.x <= x && max.x >= x && min.y <= y && max.y >= y && min.z <= z && max.z >= z;
	}

	public boolean intersects(Shape geometry) {
		return getBounds().intersects(geometry.getBounds());
	}
}
