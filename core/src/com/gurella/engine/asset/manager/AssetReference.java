package com.gurella.engine.asset.manager;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.utils.SynchronizedPools;

class AssetReference implements Poolable {
	Object asset;
	int refCount = 1;

	public static AssetReference obtain(Object asset) {
		AssetReference reference = SynchronizedPools.obtain(AssetReference.class);
		reference.asset = asset;
		return reference;
	}

	private AssetReference() {
	}

	@SuppressWarnings("unchecked")
	<T> T getAsset() {
		return (T) asset;
	}

	void incRefCount() {
		refCount++;
	}

	void decRefCount() {
		refCount--;
	}

	@Override
	public void reset() {
		asset = null;
		refCount = 1;
	}

	void free() {
		SynchronizedPools.free(this);
	}
}
