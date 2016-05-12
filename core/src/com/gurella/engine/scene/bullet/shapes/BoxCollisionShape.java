package com.gurella.engine.scene.bullet.shapes;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;

public class BoxCollisionShape extends BulletCollisionShape {
	public final Vector3 halfExtents = new Vector3(0.5f, 0.5f, 0.5f);

	@Override
	public btCollisionShape createNativeShape() {
		return new btBoxShape(halfExtents);
	}
}
