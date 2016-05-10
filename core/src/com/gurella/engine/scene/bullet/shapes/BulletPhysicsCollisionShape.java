package com.gurella.engine.scene.bullet.shapes;

import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;

public interface BulletPhysicsCollisionShape {
	btCollisionShape createNativeShape();
}
