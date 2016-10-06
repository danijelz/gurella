package com.gurella.engine.scene.bullet.shapes;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.gurella.engine.base.model.PropertyChangeListener;
import com.gurella.engine.graphics.render.GenericBatch;
import com.gurella.engine.scene.renderable.debug.WireframeShader;
import com.gurella.engine.scene.renderable.shape.BoxShapeModel;

public class BoxCollisionShape extends BulletCollisionShape implements PropertyChangeListener {
	public final Vector3 halfExtents = new Vector3(0.5f, 0.5f, 0.5f);
	private BoxShapeModel debugModel;

	@Override
	public btCollisionShape createNativeShape() {
		return new btBoxShape(halfExtents);
	}

	@Override
	public void debugRender(GenericBatch batch) {
		if (debugModel == null) {
			debugModel = new BoxShapeModel();
		}

		ModelInstance instance = debugModel.getModelInstance();
		if (instance != null) {
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
