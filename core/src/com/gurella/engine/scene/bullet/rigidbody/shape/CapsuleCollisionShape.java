package com.gurella.engine.scene.bullet.rigidbody.shape;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShapeX;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShapeZ;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.gurella.engine.base.model.PropertyChangeListener;
import com.gurella.engine.graphics.render.GenericBatch;
import com.gurella.engine.math.geometry.Axis;
import com.gurella.engine.scene.renderable.debug.WireframeShader;
import com.gurella.engine.scene.renderable.shape.CapsuleShapeModel;
import com.gurella.engine.scene.transform.TransformComponent;

public class CapsuleCollisionShape extends CollisionShape implements PropertyChangeListener {
	private Axis axis = Axis.y;
	private float radius = 0.2f;
	private float height = 1f;

	private CapsuleShapeModel debugModel;

	public Axis getAxis() {
		return axis;
	}

	public void setAxis(Axis axis) {
		this.axis = axis == null ? Axis.y : axis;
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
	}

	@Override
	public btCollisionShape createNativeShape() {
		switch (axis) {
		case x:
			return new btCapsuleShapeX(radius, height);
		case y:
			return new btCapsuleShape(radius, height);
		case z:
			return new btCapsuleShapeZ(radius, height);
		default:
			throw new GdxRuntimeException("Unsuported axis.");
		}
	}

	@Override
	public void debugRender(GenericBatch batch, TransformComponent transformComponent) {
		if (debugModel == null) {
			debugModel = new CapsuleShapeModel();
			debugModel.set(axis, radius, height);
		}

		ModelInstance instance = debugModel.getModelInstance();
		if (instance != null) {
			transformComponent.getWorldTransform(instance.transform);
			batch.render(instance, WireframeShader.getInstance());
		}
	}

	@Override
	public void propertyChanged(String propertyName, Object oldValue, Object newValue) {
		if (debugModel != null) {
			debugModel.set(axis, radius, height);
		}
	}
}
