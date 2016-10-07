package com.gurella.engine.scene.bullet.shapes;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btCylinderShape;
import com.badlogic.gdx.physics.bullet.collision.btCylinderShapeX;
import com.badlogic.gdx.physics.bullet.collision.btCylinderShapeZ;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.gurella.engine.base.model.PropertyChangeListener;
import com.gurella.engine.graphics.render.GenericBatch;
import com.gurella.engine.math.geometry.Axis;
import com.gurella.engine.scene.renderable.debug.WireframeShader;
import com.gurella.engine.scene.renderable.shape.CylinderShapeModel;
import com.gurella.engine.scene.transform.TransformComponent;

public class CylinderCollisionShape extends BulletCollisionShape implements PropertyChangeListener {
	private Axis axis = Axis.y;
	public final Vector3 halfExtents = new Vector3(0.5f, 0.5f, 0.5f);

	private CylinderShapeModel debugModel;

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
			return new btCylinderShapeX(halfExtents);
		case y:
			return new btCylinderShape(halfExtents);
		case z:
			return new btCylinderShapeZ(halfExtents);
		default:
			throw new GdxRuntimeException("Unsuported axis.");
		}
	}

	@Override
	public void debugRender(GenericBatch batch, TransformComponent transformComponent) {
		if (debugModel == null) {
			debugModel = new CylinderShapeModel();
			debugModel.set(axis, halfExtents.x * 2, halfExtents.y * 2, halfExtents.z * 2);
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
			debugModel.set(axis, halfExtents.x * 2, halfExtents.y * 2, halfExtents.z * 2);
		}
	}
}
