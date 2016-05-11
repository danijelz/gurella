package com.gurella.engine.scene.bullet.shapes;

import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;

//TODO Poolable
public abstract class BulletCollisionShape {
	public abstract btCollisionShape createNativeShape();
}
