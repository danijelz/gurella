package com.gurella.engine.scene.bullet.shape;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.gurella.engine.graphics.render.GenericBatch;
import com.gurella.engine.metatype.PropertyChangeListener;
import com.gurella.engine.scene.renderable.debug.WireframeShader;
import com.gurella.engine.scene.renderable.shape.BoxShapeModel;
import com.gurella.engine.scene.transform.TransformComponent;

public class BoxCollisionShape extends CollisionShape implements PropertyChangeListener {
	public final Vector3 halfExtents = new Vector3(0.5f, 0.5f, 0.5f);

	private BoxShapeModel debugModel;

	@Override
	public btCollisionShape createNativeShape() {
		return new btBoxShape(halfExtents);
	}

	@Override
	public void debugRender(GenericBatch batch, TransformComponent transformComponent) {
		if (debugModel == null) {
			debugModel = new BoxShapeModel();
			debugModel.set(halfExtents.x * 2, halfExtents.y * 2, halfExtents.z * 2);
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
			debugModel.set(halfExtents.x * 2, halfExtents.y * 2, halfExtents.z * 2);
		}
	}
}
