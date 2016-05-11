package com.gurella.engine.scene.bullet.shapes;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btStaticPlaneShape;

public class PlaneCollisionShape extends BulletCollisionShape {
	public final Vector3 planeNormal = new Vector3(0, 1, 0);
	public float planeConstant;

	@Override
	public btCollisionShape createNativeShape() {
		return new btStaticPlaneShape(planeNormal, planeConstant);
	}
}
