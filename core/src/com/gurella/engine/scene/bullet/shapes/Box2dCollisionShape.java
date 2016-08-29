package com.gurella.engine.scene.bullet.shapes;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBox2dShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;

public class Box2dCollisionShape extends BulletCollisionShape {
	public final Vector3 halfExtents = new Vector3(0.5f, 0.5f, 0.5f);

	@Override
	public btCollisionShape createNativeShape() {
		return new btBox2dShape(halfExtents);
	}
}
