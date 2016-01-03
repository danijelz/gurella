package com.gurella.engine.scene.bullet;

import com.gurella.engine.utils.SynchronizedPools;

class CachedCollisionPair {
	BulletPhysicsRigidBodyComponent rigidBodyComponent0;
	BulletPhysicsRigidBodyComponent rigidBodyComponent1;

	private CachedCollisionPair() {
	}

	static CachedCollisionPair obtain(BulletPhysicsRigidBodyComponent rigidBodyComponent0,
			BulletPhysicsRigidBodyComponent rigidBodyComponent1) {
		CachedCollisionPair cachedCollisionPair = SynchronizedPools.obtain(CachedCollisionPair.class);
		cachedCollisionPair.rigidBodyComponent0 = rigidBodyComponent0;
		cachedCollisionPair.rigidBodyComponent1 = rigidBodyComponent1;
		return cachedCollisionPair;
	}

	void free() {
		SynchronizedPools.free(this);
	}

	@Override
	public int hashCode() {
		return rigidBodyComponent0.id + rigidBodyComponent1.id;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CachedCollisionPair other = (CachedCollisionPair) obj;
		return (rigidBodyComponent0.id == other.rigidBodyComponent0.id && rigidBodyComponent1.id == other.rigidBodyComponent1.id)
				|| (rigidBodyComponent0.id == other.rigidBodyComponent1.id && rigidBodyComponent1.id == other.rigidBodyComponent0.id);
	}
}
