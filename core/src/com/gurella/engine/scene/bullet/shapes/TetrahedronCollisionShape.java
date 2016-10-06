package com.gurella.engine.scene.bullet.shapes;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btTetrahedronShapeEx;
import com.gurella.engine.graphics.render.GenericBatch;

public class TetrahedronCollisionShape extends BulletCollisionShape {
	public final Vector3 v0 = new Vector3();
	public final Vector3 v1 = new Vector3();
	public final Vector3 v2 = new Vector3();
	public final Vector3 v3 = new Vector3();

	@Override
	public btCollisionShape createNativeShape() {
		btTetrahedronShapeEx shape = new btTetrahedronShapeEx();
		shape.setVertices(v0, v1, v2, v3);
		return shape;
	}

	@Override
	public void debugRender(GenericBatch batch) {
		// TODO Auto-generated method stub
		
	}
}
