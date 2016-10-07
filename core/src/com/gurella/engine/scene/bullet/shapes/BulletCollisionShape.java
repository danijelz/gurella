package com.gurella.engine.scene.bullet.shapes;

import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.gurella.engine.graphics.render.GenericBatch;
import com.gurella.engine.scene.transform.TransformComponent;

//TODO Poolable
public abstract class BulletCollisionShape {
	public abstract btCollisionShape createNativeShape();

	public abstract void debugRender(GenericBatch batch, TransformComponent transformComponent);
}
