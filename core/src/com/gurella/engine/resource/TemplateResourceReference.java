package com.gurella.engine.resource;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.pool.PoolService;

public class TemplateResourceReference<T> extends FactoryResourceReference<T> {
	private static final String INIT_ON_START_COUNT_TAG = "initOnStartCount";

	public int initOnStartCount;

	private Array<T> pool = new Array<T>();
	private Array<T> inUse = new Array<T>();

	private boolean initialized;

	protected TemplateResourceReference() {
	}

	public TemplateResourceReference(int id, String name, boolean persistent, boolean initOnStart,
			ResourceFactory<T> resourceFactory) {
		super(id, name, persistent, initOnStart, resourceFactory);
	}

	@Override
	protected void init() {
		if (isInitOnStart()) {
			if (handleInit()) {
				for (int i = 0; i < initOnStartCount; i++) {
					CreateResourceWorker.createResource(this, null);
				}
			}
		}
	}

	private synchronized boolean handleInit() {
		if (!initialized) {
			initialized = true;
			return initOnStartCount > 0;
		} else {
			return false;
		}
	}

	@Override
	protected void obtain(AsyncResourceCallback<T> callback) {
		if (handleCallbackConcurrently(callback)) {
			CreateResourceWorker.createResource(this, callback);
		}
	}

	private synchronized boolean handleCallbackConcurrently(AsyncResourceCallback<T> callback) {
		if (pool.size > 0) {
			T resource = pool.pop();
			inUse.add(resource);
			callback.handleResource(resource);
			return false;
		} else {
			return true;
		}
	}

	private synchronized void addUsedResource(T resource) {
		inUse.add(resource);
	}

	private synchronized void addPooledResource(T pooledResource) {
		pool.add(pooledResource);
	}

	@Override
	protected synchronized void release(T resourceToRelease) {
		if (!inUse.removeValue(resourceToRelease, true)) {
			return;
		}

		releaseResource(resourceToRelease);
	}

	private void releaseResource(T resourceToRelease) {
		if (isPersistent()) {
			if (resourceToRelease instanceof Poolable) {
				((Poolable) resourceToRelease).reset();
			}
			addPooledResource(resourceToRelease);
		} else if (resourceToRelease instanceof Disposable) {
			((Disposable) resourceToRelease).dispose();
		}
	}

	@Override
	public void dispose() {
		if (inUse.size > 0) {
			throw new IllegalStateException("Resource in use.");
		}
		getResourceFactory().dispose();
		for (int i = 0; i < pool.size; i++) {
			T resource = pool.get(i);
			if (resource instanceof Disposable) {
				((Disposable) resource).dispose();
			}

			pool.clear();
		}
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		super.read(json, jsonData);
		initOnStartCount = jsonData.getInt(INIT_ON_START_COUNT_TAG);
	}

	@Override
	public void write(Json json) {
		super.write(json);
		json.writeValue(INIT_ON_START_COUNT_TAG, initOnStartCount);
	}

	private static class CreateResourceWorker<T> implements AsyncResourceCallback<DependencyMap>, Poolable {
		private TemplateResourceReference<T> reference;
		private int dependenciesCount;
		private AsyncResourceCallback<T> callback;

		public static <T> void createResource(TemplateResourceReference<T> reference, AsyncResourceCallback<T> callback) {
			@SuppressWarnings("unchecked")
			CreateResourceWorker<T> instance = PoolService.obtain(CreateResourceWorker.class);
			instance.reference = reference;
			instance.callback = callback;
			instance.createResource();
			PoolService.free(instance);
		}

		@Override
		public void handleResource(DependencyMap dependencyMap) {
			createResource(dependencyMap);
			PoolService.free(this);
		}

		@Override
		public void handleException(Throwable exception) {
			notifyException(exception);
			PoolService.free(this);
		}

		@Override
		public void handleProgress(float progress) {
			notifyProgress(getProportionalProgress(progress));
		}

		private float getProportionalProgress(float progress) {
			if (progress == 0) {
				return 0;
			} else {
				return (progress / (dependenciesCount + 1)) * dependenciesCount;
			}
		}

		private void createResource() {
			try {
				createResourceSafely();
			} catch (Exception exception) {
				notifyException(exception);
			}
		}

		private void createResourceSafely() {
			ResourceFactory<T> resourceFactory = reference.getResourceFactory();
			IntArray dependentResourceIds = resourceFactory.getDependentResourceIds(reference.owningContext);
			if (dependentResourceIds == null) {
				createResource(null);
			} else {
				dependenciesCount = dependentResourceIds.size;
				reference.getOwningContext().obtainResources(dependentResourceIds, this);
			}
		}

		private void createResource(DependencyMap dependencyMap) {
			try {
				ResourceFactory<T> factory = reference.getResourceFactory();
				T createdResource = factory.create(dependencyMap);
				notifyProgress(1);
				handleCreatedResource(createdResource);
				if (dependencyMap != null) {
					dependencyMap.free();
				}
			} catch (Exception exception) {
				notifyException(exception);
				if (dependencyMap != null) {
					reference.getOwningContext().rollback(dependencyMap);
					dependencyMap.free();
				}
			}
		}

		private void handleCreatedResource(T resource) {
			if (callback == null) {
				reference.addPooledResource(resource);
			} else {
				reference.addUsedResource(resource);
				try {
					callback.handleResource(resource);
				} catch (Exception e) {
				}
			}
		}

		private void notifyProgress(float progress) {
			if (callback == null) {
				return;
			}

			try {
				callback.handleProgress(progress);
			} catch (Exception e) {
			}
		}

		private void notifyException(Throwable exception) {
			if (callback == null) {
				return;
			}

			try {
				callback.handleException(exception);
			} catch (Exception e) {
			}
		}

		@Override
		public void reset() {
			reference = null;
			callback = null;
		}
	}
}
