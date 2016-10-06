package com.gurella.engine.scene.bullet.shapes;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBox2dShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.gurella.engine.graphics.render.GenericBatch;

public class Box2dCollisionShape extends BulletCollisionShape {
	public final Vector2 halfExtents = new Vector2(0.5f, 0.5f);

	@Override
	public btCollisionShape createNativeShape() {
		return new btBox2dShape(new Vector3(halfExtents, 0));
	}

	@Override
	public void debugRender(GenericBatch batch) {
		// TODO Auto-generated method stub
		
	}
}
