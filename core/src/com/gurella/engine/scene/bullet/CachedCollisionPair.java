package com.gurella.engine.scene.bullet;

import com.gurella.engine.pool.PoolService;

class CachedCollisionPair {
	BulletRigidBodyComponent rigidBodyComponent0;
	BulletRigidBodyComponent rigidBodyComponent1;

	private CachedCollisionPair() {
	}

	static CachedCollisionPair obtain(BulletRigidBodyComponent rigidBodyComponent0,
			BulletRigidBodyComponent rigidBodyComponent1) {
		CachedCollisionPair cachedCollisionPair = PoolService.obtain(CachedCollisionPair.class);
		cachedCollisionPair.rigidBodyComponent0 = rigidBodyComponent0;
		cachedCollisionPair.rigidBodyComponent1 = rigidBodyComponent1;
		return cachedCollisionPair;
	}

	void free() {
		PoolService.free(this);
	}

	@Override
	public int hashCode() {
		return rigidBodyComponent0.getInstanceId() + rigidBodyComponent1.getInstanceId();
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
		int component0Id = rigidBodyComponent0.getInstanceId();
		int otherComponent0Id = other.rigidBodyComponent0.getInstanceId();
		int component1Id = rigidBodyComponent1.getInstanceId();
		int otherComponent1Id = other.rigidBodyComponent1.getInstanceId();
		return (component0Id == otherComponent0Id && component1Id == otherComponent1Id)
				|| (component0Id == otherComponent1Id && component1Id == otherComponent0Id);
	}
}
