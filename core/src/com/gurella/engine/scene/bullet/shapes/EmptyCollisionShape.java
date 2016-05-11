package com.gurella.engine.scene.bullet.shapes;

import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btEmptyShape;

public class EmptyCollisionShape extends BulletCollisionShape {
	@Override
	public btCollisionShape createNativeShape() {
		return new btEmptyShape();
	}
}
