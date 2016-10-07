package com.gurella.engine.scene.bullet.shapes;

import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btEmptyShape;
import com.gurella.engine.graphics.render.GenericBatch;
import com.gurella.engine.scene.transform.TransformComponent;

public class EmptyCollisionShape extends BulletCollisionShape {
	@Override
	public btCollisionShape createNativeShape() {
		return new btEmptyShape();
	}

	@Override
	public void debugRender(GenericBatch batch, TransformComponent transformComponent) {
	}
}
