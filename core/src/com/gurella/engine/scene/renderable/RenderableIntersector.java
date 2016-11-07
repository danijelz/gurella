package com.gurella.engine.scene.renderable;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.math.Intersection;
import com.gurella.engine.math.ModelIntesector;
import com.gurella.engine.math.SpiteIntersector;
import com.gurella.engine.scene.transform.TransformComponent;

public class RenderableIntersector implements Poolable {
	private final Intersection intersection = new Intersection();

	private final ModelIntesector modelIntesector = new ModelIntesector();
	private final SpiteIntersector spiteIntersector = new SpiteIntersector();

	private final BoundingBox bounds = new BoundingBox();
	private final Matrix4 transform = new Matrix4();
	private final Vector3 closestIntersection = new Vector3(Float.NaN, Float.NaN, Float.NaN);
	private float closestDistance = Float.MAX_VALUE;
	private RenderableComponent closestRenderable;

	private Camera camera;
	private Ray ray;

	public void set(Camera camera, Ray ray) {
		this.camera = camera;
		this.ray = ray;
	}

	public boolean append(RenderableComponent renderable, boolean considerLooseInput) {
		if (renderable.looseInput && considerLooseInput) {
			intersection.reset();
			renderable.getBounds(bounds);
			Vector3 location = intersection.location;
			if (Intersector.intersectRayBounds(ray, bounds, location)) {
				intersection.distance = location.dst2(camera.position);
			}
			return evaluateIntersection(renderable);
		} else {
			return append(renderable);
		}
	}

	public boolean append(RenderableComponent renderable) {
		intersection.reset();
		Vector3 cameraPosition = camera.position;

		if (renderable instanceof RenderableComponent2d) {
			RenderableComponent2d renderable2d = (RenderableComponent2d) renderable;
			TransformComponent transformComponent = renderable2d.transformComponent;
			Matrix4 temp = null;
			if (transformComponent != null) {
				temp = transformComponent.getWorldTransform(transform);
			}
			spiteIntersector.getIntersection(cameraPosition, ray, renderable2d.sprite, temp, intersection);
		} else if (renderable instanceof RenderableComponent3d) {
			RenderableComponent3d renderable3d = (RenderableComponent3d) renderable;
			modelIntesector.getIntersection(cameraPosition, ray, renderable3d.getModelInstance(), intersection);
		} else {
			return false;
		}

		return evaluateIntersection(renderable);
	}

	protected boolean evaluateIntersection(RenderableComponent renderable) {
		float distance = intersection.distance;
		if (distance < closestDistance) {
			closestDistance = distance;
			closestIntersection.set(intersection.location);
			closestRenderable = renderable;
			return true;
		} else if (closestDistance != Float.MAX_VALUE && distance == closestDistance
				&& renderable instanceof RenderableComponent2d && closestRenderable instanceof RenderableComponent2d) {
			RenderableComponent2d closest = (RenderableComponent2d) closestRenderable;
			RenderableComponent2d current = (RenderableComponent2d) renderable;
			if (closest.zOrder < current.zOrder) {
				closestIntersection.set(intersection.location);
				closestRenderable = renderable;
				return true;
			}
		}

		return false;
	}

	public float getClosestDistance() {
		return closestDistance;
	}

	public RenderableComponent getClosestRenderable() {
		return closestRenderable;
	}

	public Vector3 getClosestIntersection() {
		return closestIntersection;
	}

	public boolean isIntersectionDetected() {
		return closestRenderable != null;
	}

	@Override
	public void reset() {
		closestDistance = Float.MAX_VALUE;
		intersection.reset();
		closestRenderable = null;
	}
}
