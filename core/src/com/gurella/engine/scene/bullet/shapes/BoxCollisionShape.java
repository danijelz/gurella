package com.gurella.engine.scene.bullet.shapes;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;

public class BoxCollisionShape extends BulletCollisionShape {
	public final Vector3 dimensions = new Vector3();

	@Override
	public btCollisionShape createNativeShape() {
		float x = dimensions.x;
		float y = dimensions.y;
		float z = dimensions.z;
		btBoxShape shape = new btBoxShape(dimensions.scl(0.5f));
		dimensions.set(x, y, z);
		return shape;
	}
}
