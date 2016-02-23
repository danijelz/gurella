package com.gurella.engine.resource;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.pool.PoolService;

public class SharedResourceReference<T> extends FactoryResourceReference<T> {
	private T resource;
	private int referenceCount;
	private boolean initialized;
	private boolean resourceInCreation;
	private Throwable cretionException;
	private final Array<AsyncResourceCallback<T>> pendingCallbacks = new Array<AsyncResourceCallback<T>>();

	protected SharedResourceReference() {
	}

	public SharedResourceReference(int id, String name, boolean persistent, boolean initOnStart,
			ResourceFactory<T> resourceFactory) {
		super(id, name, persistent, initOnStart, resourceFactory);
	}

	@Override
	protected void init() {
		if (isInitOnStart()) {
			if (handleInit()) {
				createResource();
			}
		}
	}

	private synchronized boolean handleInit() {
		if (!initialized) {
			initialized = true;
			if (resource == null && !resourceInCreation) {
				resourceInCreation = true;
				return true;
			}
		}

		return false;
	}

	@Override
	protected void obtain(AsyncResourceCallback<T> callback) {
		if (handleCallbackConcurrently(callback)) {
			createResource();
		}
	}

	private synchronized boolean handleCallbackConcurrently(AsyncResourceCallback<T> callback) {
		if (cretionException != null) {
			callback.handleException(cretionException);
			return false;
		}

		referenceCount++;

		if (resource == null) {
			pendingCallbacks.add(callback);
			if (!resourceInCreation) {
				resourceInCreation = true;
				return true;
			} else {
				return false;
			}
		} else {
			callback.handleResource(resource);
			return false;
		}
	}

	private void createResource() {
		try {
			createResourceSafely();
		} catch (Exception exception) {
			handleCreationException(exception);
		}
	}

	private void createResourceSafely() {
		IntArray dependentResourceIds = getResourceFactory().getDependentResourceIds(owningContext);
		if (dependentResourceIds == null) {
			createResource(null);
		} else {
			DependenciesResolverCallback callback = DependenciesResolverCallback
					.obtain(this, dependentResourceIds.size);
			getOwningContext().obtainResources(dependentResourceIds, callback);
		}
	}

	private void createResource(DependencyMap dependencyMap) {
		try {
			ResourceFactory<T> factory = getResourceFactory();
			T createdResource = factory.create(dependencyMap);
			handleCreatedResource(createdResource);
			notifyProgress(1);
			notifySucess();
			if (dependencyMap != null) {
				dependencyMap.free();
			}
		} catch (Exception exception) {
			handleCreationException(exception);
			if (dependencyMap != null) {
				getOwningContext().rollback(dependencyMap);
				dependencyMap.free();
			}
		}
	}

	private synchronized void handleCreatedResource(T createdResource) {
		resource = createdResource;
		resourceInCreation = false;
	}

	private void handleCreationException(Throwable exception) {
		synchronized (this) {
			cretionException = exception;
			resourceInCreation = false;
		}
		notifyException();
	}

	private void notifyProgress(float progress) {
		for (int i = 0; i < pendingCallbacks.size; i++) {
			try {
				AsyncResourceCallback<T> pendingCallback = pendingCallbacks.get(i);
				pendingCallback.handleProgress(progress);
			} catch (Exception e) {
			}
		}
	}

	private void notifySucess() {
		for (int i = 0; i < pendingCallbacks.size; i++) {
			try {
				AsyncResourceCallback<T> pendingCallback = pendingCallbacks.get(i);
				pendingCallback.handleResource(resource);
			} catch (Exception e) {
			}
		}

		pendingCallbacks.clear();
	}

	private void notifyException() {
		for (int i = 0; i < pendingCallbacks.size; i++) {
			try {
				AsyncResourceCallback<T> pendingCallback = pendingCallbacks.get(i);
				pendingCallback.handleException(cretionException);
			} catch (Exception e) {
			}
		}

		pendingCallbacks.clear();
	}

	@Override
	protected synchronized void release(T resourceToRelease) {
		if (referenceCount == 0 || this.resource != resourceToRelease) {
			return;
		}

		referenceCount--;
		if (referenceCount == 0) {
			if (isPersistent()) {
				resetResource();
			} else {
				disposeResource();
			}
		}
	}

	private void resetResource() {
		if (resource instanceof Poolable) {
			((Poolable) resource).reset();
		}
	}

	void disposeResource() {
		if (resource instanceof Disposable) {
			((Disposable) resource).dispose();
			resource = null;
		}
	}

	@Override
	public synchronized void dispose() {
		if (referenceCount > 0) {
			throw new IllegalStateException("Resource in use.");
		}
		disposeResource();
		getResourceFactory().dispose();
		cretionException = null;
	}

	private static class DependenciesResolverCallback implements AsyncResourceCallback<DependencyMap>, Poolable {
		private SharedResourceReference<?> reference;
		private int dependenciesCount;

		private static DependenciesResolverCallback obtain(SharedResourceReference<?> reference, int dependenciesCount) {
			DependenciesResolverCallback callback = PoolService.obtain(DependenciesResolverCallback.class);
			callback.reference = reference;
			callback.dependenciesCount = dependenciesCount;
			return callback;
		}

		@Override
		public void handleResource(DependencyMap resource) {
			reference.createResource(resource);
			PoolService.free(this);
		}

		@Override
		public void handleException(Throwable exception) {
			reference.handleCreationException(exception);
			PoolService.free(this);
		}

		@Override
		public void handleProgress(float progress) {
			reference.notifyProgress(getProportionalProgress(progress));
		}

		private float getProportionalProgress(float progress) {
			if (progress == 0) {
				return 0;
			} else {
				return (progress / (dependenciesCount + 1)) * dependenciesCount;
			}
		}

		@Override
		public void reset() {
			reference = null;
		}
	}
}
