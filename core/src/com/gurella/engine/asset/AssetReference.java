package com.gurella.engine.asset;

import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.pool.PoolService;

class AssetReference implements Poolable {
	Object asset;
	boolean sticky;
	int refCount = 0;
	final ObjectSet<String> dependencies = new ObjectSet<String>(4);
	final ObjectSet<String> dependents = new ObjectSet<String>(4);

	public static AssetReference obtain() {
		return PoolService.obtain(AssetReference.class);
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
		if (refCount > 0) {
			refCount--;
		}
	}

	void addDependency(String dependency) {
		dependencies.add(dependency);
	}

	void addDependent(String dependent) {
		dependents.add(dependent);
	}

	void removeDependent(String dependent) {
		dependents.remove(dependent);
	}

	boolean isReferenced() {
		return sticky || refCount > 0 || dependents.size > 0;
	}

	@Override
	public void reset() {
		asset = null;
		sticky = false;
		refCount = 0;
		dependencies.clear();
		dependents.clear();
	}

	void free() {
		PoolService.free(this);
	}
}