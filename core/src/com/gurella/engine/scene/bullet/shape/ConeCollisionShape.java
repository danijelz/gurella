package com.gurella.engine.scene.bullet.shape;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btConeShape;
import com.badlogic.gdx.physics.bullet.collision.btConeShapeX;
import com.badlogic.gdx.physics.bullet.collision.btConeShapeZ;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.gurella.engine.graphics.render.GenericBatch;
import com.gurella.engine.math.geometry.Axis;
import com.gurella.engine.metatype.PropertyChangeListener;
import com.gurella.engine.scene.renderable.debug.WireframeShader;
import com.gurella.engine.scene.renderable.shape.ConeShapeModel;
import com.gurella.engine.scene.transform.TransformComponent;

public class ConeCollisionShape extends CollisionShape implements PropertyChangeListener {
	private Axis axis = Axis.y;
	float radius = 1;
	float height = 1;

	private ConeShapeModel debugModel;

	public Axis getAxis() {
		return axis;
	}

	public void setAxis(Axis axis) {
		this.axis = axis == null ? Axis.y : axis;
	}

	@Override
	public btCollisionShape createNativeShape() {
		switch (axis) {
		case x:
			return new btConeShapeX(radius, height);
		case y:
			return new btConeShape(radius, height);
		case z:
			return new btConeShapeZ(radius, height);
		default:
			throw new GdxRuntimeException("Unsuported axis.");
		}
	}

	@Override
	public void debugRender(GenericBatch batch, TransformComponent transformComponent) {
		if (debugModel == null) {
			debugModel = new ConeShapeModel();
			debugModel.set(axis, radius, height, radius);
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
			debugModel.set(axis, radius, height, radius);
		}
	}
}
