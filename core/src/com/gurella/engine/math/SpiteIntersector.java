package com.gurella.engine.math;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

public class SpiteIntersector {
	private final Vector3 intersection = new Vector3();
	private final Vector3 cameraPosition = new Vector3();
	private final Ray ray = new Ray();
	private final Ray invRay = new Ray();
	private final Matrix4 transform = new Matrix4();

	private Sprite closestSprite;
	private final Vector3 closestIntersection = new Vector3();
	private float closestDistance = Float.MAX_VALUE;

	private final Vector3 v1 = new Vector3();
	private final Vector3 v2 = new Vector3();
	private final Vector3 v3 = new Vector3();

	public boolean getIntersection(Vector3 cameraPosition, Ray ray, Sprite sprite, Matrix4 transform,
			Intersection result) {
		init(cameraPosition, ray);
		process(sprite, transform);
		return extractResult(result);
	}

	void init(Vector3 cameraPosition, Ray ray) {
		this.cameraPosition.set(cameraPosition);
		this.ray.set(ray);
		closestIntersection.set(Float.NaN, Float.NaN, Float.NaN);
		closestDistance = Float.MAX_VALUE;
	}

	boolean extractResult(Intersection intersection) {
		if (closestSprite != null) {
			intersection.distance = closestDistance;
			intersection.location.set(closestIntersection);
			return true;
		} else {
			return false;
		}
	}

	private void process(Sprite sprite, Matrix4 spriteTransform) {
		invRay.set(ray);
		if (spriteTransform != null && Matrix4.inv(transform.set(spriteTransform.val).val)) {
			invRay.mul(transform);
		}

		float[] vertices = sprite.getVertices();
		v1.set(vertices[Batch.X1], vertices[Batch.Y1], 0);
		v2.set(vertices[Batch.X2], vertices[Batch.Y2], 0);
		v3.set(vertices[Batch.X3], vertices[Batch.Y3], 0);

		if (Intersector.intersectRayTriangle(invRay, v1, v2, v3, intersection)) {
			if (spriteTransform != null) {
				intersection.mul(spriteTransform);
			}
			float distance = intersection.dst2(cameraPosition);
			if (closestDistance > distance) {
				closestDistance = distance;
				closestIntersection.set(intersection);
				closestSprite = sprite;
				return;
			}
		}

		v1.set(vertices[Batch.X3], vertices[Batch.Y3], 0);
		v2.set(vertices[Batch.X4], vertices[Batch.Y4], 0);
		v3.set(vertices[Batch.X1], vertices[Batch.Y1], 0);
		if (Intersector.intersectRayTriangle(invRay, v1, v2, v3, intersection)) {
			float distance = intersection.dst2(cameraPosition);
			if (closestDistance > distance) {
				closestDistance = distance;
				closestIntersection.set(intersection);
				closestSprite = sprite;
			}
		}
	}
}
