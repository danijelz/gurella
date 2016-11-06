package com.gurella.engine.math;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.gurella.engine.scene.transform.TransformComponent;

public class SpiteIntersector {
	private final Ray tempRay = new Ray();
	private final Vector3 intersection = new Vector3();
	private final Vector3 cameraPosition = new Vector3();
	private final Ray ray = new Ray();

	private Sprite closestSprite;
	private final Vector3 closestIntersection = new Vector3();
	private float closestDistance = Float.MAX_VALUE;

	private final Vector3 v1 = new Vector3();
	private final Vector3 v2 = new Vector3();
	private final Vector3 v3 = new Vector3();

	public boolean getIntersection(Vector3 cameraPosition, Ray ray, Vector3 intersection, Sprite sprite,
			TransformComponent transform) {
		init(cameraPosition, ray);
		process(sprite, transform);
		return extractResult(intersection);
	}

	void init(Vector3 cameraPosition, Ray ray) {
		this.cameraPosition.set(cameraPosition);
		this.ray.set(ray);
		closestIntersection.set(Float.NaN, Float.NaN, Float.NaN);
		closestDistance = Float.MAX_VALUE;
	}

	boolean extractResult(Vector3 intersection) {
		if (closestSprite != null) {
			intersection.set(closestIntersection);
			return true;
		} else {
			return false;
		}
	}

	private void process(Sprite sprite, TransformComponent transform) {
		Ray inv = new Ray().set(ray);
		if (transform != null) {
			transform.transformRayFromWorld(inv);
		}

		float[] vertices = sprite.getVertices();

		v1.set(vertices[Batch.X1], vertices[Batch.Y1], 0);
		v2.set(vertices[Batch.X2], vertices[Batch.Y2], 0);
		v3.set(vertices[Batch.X3], vertices[Batch.Y3], 0);

		if (Intersector.intersectRayTriangle(inv, v1, v2, v3, intersection)) {
			closestSprite = sprite;
			return;
		}

		v1.set(vertices[Batch.X3], vertices[Batch.Y3], 0);
		v2.set(vertices[Batch.X4], vertices[Batch.Y4], 0);
		v3.set(vertices[Batch.X1], vertices[Batch.Y1], 0);
		if (Intersector.intersectRayTriangle(inv, v1, v2, v3, intersection)) {
			closestSprite = sprite;
			return;
		}
	}
}
