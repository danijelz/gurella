package com.gurella.engine.math.geometry.shape;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class Sphere extends Shape {
	Vector3 center = new Vector3();
	float radius;

	public Vector3 getCenter(Vector3 out) {
		return out.set(center);
	}

	public Vector3 getCenter() {
		return center;
	}

	public void setCenter(Vector3 center) {
		this.center.set(center);
		boundsDirty = true;
	}

	public void setCenter(float x, float y, float z) {
		this.center.set(x, y, z);
		boundsDirty = true;
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
		boundsDirty = true;
	}

	@Override
	public boolean contains(float x, float y, float z) {
		Matrix4 transform = getGlobalTransform();
		final float val[] = transform.val;
		float cx = center.x;
		float cy = center.y;
		float cz = center.z;
		float tx = cx * val[Matrix4.M00] + cy * val[Matrix4.M01] + cz * val[Matrix4.M02] + val[Matrix4.M03];
		float ty = cx * val[Matrix4.M10] + cy * val[Matrix4.M11] + cz * val[Matrix4.M12] + val[Matrix4.M13];
		float tz = cx * val[Matrix4.M20] + cy * val[Matrix4.M21] + cz * val[Matrix4.M22] + val[Matrix4.M23];
		return Vector3.dst2(tx, ty, tz, x, y, z) <= (radius * radius);
	}
}
