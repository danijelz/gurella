package com.gurella.engine.scene.bullet;

import com.badlogic.gdx.math.Vector3;

public class BulletMaterial {
	public boolean inertiaFromShape = true;
	public final Vector3 localInertia = new Vector3(0, 0, 0);

	public final Vector3 angularFactor = new Vector3(1, 1, 1);

	public final Vector3 gravity = new Vector3(0f, -9.8f, 0f);

	public float mass;

	public float margin = 0.04f;

	public float linearDamping;
	public float angularDamping;

	public float friction;
	public float rollingFriction;

	public float restitution;

	public float linearSleepingThreshold;
	public float angularSleepingThreshold;

	public boolean additionalDamping;
	public float additionalDampingFactor;
	public float additionalAngularDampingFactor;
}
