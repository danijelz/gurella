package com.gurella.engine.scene.bullet.shapes;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btTriangleShapeEx;
import com.gurella.engine.graphics.render.GenericBatch;

public class TriangleCollisionShape extends BulletCollisionShape {
	public final Vector3 p0 = new Vector3(1, 0, 0);
	public final Vector3 p1 = new Vector3(0, 1, 0);
	public final Vector3 p2 = new Vector3(0, 0, 1);

	@Override
	public btCollisionShape createNativeShape() {
		return new btTriangleShapeEx(p0, p1, p2);
	}

	@Override
	public void debugRender(GenericBatch batch) {
		// TODO Auto-generated method stub
		
	}
}
