package com.gurella.engine.resource;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.pool.PoolService;

public class DependencyMap implements Poolable {
	private final IntMap<ResourceMapEntry<?>> resourcesById = new IntMap<ResourceMapEntry<?>>();
	final Array<ResourceMapEntry<?>> entries = new Array<ResourceMapEntry<?>>();
	private final IntArray requestedResourceIdsOrder = new IntArray();

	int resolvedResourcesCount;
	private ResourceContext context;

	public static DependencyMap obtain(ResourceContext context, IntArray resourceIds) {
		DependencyMap instance = PoolService.obtain(DependencyMap.class);
		instance.context = context;
		instance.addDependecies(resourceIds);
		return instance;
	}

	private synchronized void addDependecies(IntArray dependentResourceIds) {
		for (int i = 0; i < dependentResourceIds.size; i++) {
			addDependency(dependentResourceIds.get(i));
		}
	}

	private <T> void addDependency(int resourceId) {
		requestedResourceIdsOrder.add(resourceId);

		@SuppressWarnings("unchecked")
		ResourceMapEntry<T> entry = (ResourceMapEntry<T>) resourcesById.get(resourceId);
		if (entry == null) {
			@SuppressWarnings("unchecked")
			ResourceMapEntry<T> casted = PoolService.obtain(ResourceMapEntry.class);
			entry = casted;
			resourcesById.put(resourceId, entry);
			entries.add(entry);
		}
	}

	public synchronized void addResolvedResource(int resourceId, Object resource) {
		getDependency(resourceId).addInitialized(resource);
		resolvedResourcesCount++;
	}

	@SuppressWarnings("unchecked")
	private <T> ResourceMapEntry<T> getDependency(int resourceId) {
		return (ResourceMapEntry<T>) resourcesById.get(resourceId);
	}

	public synchronized <T> T getResource(int resourceId) {
		return this.<T> getDependency(resourceId).pop();
	}

	public synchronized <T> T getAssetResource(String fileName) {
		return this.<T> getDependency(context.getAssetReference(fileName).getId()).pop();
	}

	int getRequestedResourcesCount() {
		return requestedResourceIdsOrder.size;
	}

	int getRequestedResourceId(int index) {
		return requestedResourceIdsOrder.get(index);
	}

	int getResolvedResourcesCount() {
		return resolvedResourcesCount;
	}

	@Override
	public synchronized void reset() {
		for (int i = 0; i < entries.size; i++) {
			ResourceMapEntry<?> dependency = entries.get(i);
			PoolService.free(dependency);
		}
		entries.clear();
		resourcesById.clear();
		requestedResourceIdsOrder.clear();
		resolvedResourcesCount = 0;
		context = null;
	}

	public void free() {
		PoolService.free(this);
	}

	static class ResourceMapEntry<T> implements Poolable {
		final Array<T> initializedResources = new Array<T>();

		private void addInitialized(T resource) {
			initializedResources.add(resource);
		}

		private T pop() {
			return initializedResources.pop();
		}

		@Override
		public void reset() {
			initializedResources.clear();
		}
	}
}
