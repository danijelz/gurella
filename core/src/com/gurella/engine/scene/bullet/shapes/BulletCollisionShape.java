package com.gurella.engine.scene.bullet.shapes;

import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.gurella.engine.graphics.render.GenericBatch;

//TODO Poolable
public abstract class BulletCollisionShape {
	public abstract btCollisionShape createNativeShape();

	public abstract void debugRender(GenericBatch batch);
}
