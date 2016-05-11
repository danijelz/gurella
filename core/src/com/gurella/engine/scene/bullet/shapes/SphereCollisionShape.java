package com.gurella.engine.scene.bullet.shapes;

import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;

public class SphereCollisionShape extends BulletCollisionShape {
	public float radius = 1;

	@Override
	public btCollisionShape createNativeShape() {
		return new btSphereShape(radius);
	}
}
