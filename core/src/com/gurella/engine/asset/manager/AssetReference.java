package com.gurella.engine.asset.manager;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.utils.SynchronizedPools;

class AssetReference implements Poolable {
	Object asset;
	int refCount = 0;
	final Array<String> dependencies = new Array<String>(4);
	final ObjectSet<String> dependents = new ObjectSet<String>(4);

	public static AssetReference obtain() {
		return SynchronizedPools.obtain(AssetReference.class);
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
		if(refCount > 0) {
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
		return refCount > 0 || dependents.size > 0;
	}

	@Override
	public void reset() {
		asset = null;
		refCount = 0;
		dependencies.clear();
		dependents.clear();
	}

	void free() {
		SynchronizedPools.free(this);
	}
}
