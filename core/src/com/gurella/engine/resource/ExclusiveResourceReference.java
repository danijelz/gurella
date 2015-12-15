package com.gurella.engine.resource;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.pools.SynchronizedPools;

public class ExclusiveResourceReference<T> extends FactoryResourceReference<T> {
	private T resource;
	private boolean locked;
	private boolean initialized;
	private boolean resourceInCreation;
	private Throwable cretionException;
	private AsyncResourceCallback<T> activeCallback;

	protected ExclusiveResourceReference() {
	}

	public ExclusiveResourceReference(int id, String name, boolean persistent, boolean initOnStart,
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
			activeCallback = callback;
			createResource();
		} else if (callback != null) {
			callback.handleResource(resource);
		}
	}

	private synchronized boolean handleCallbackConcurrently(AsyncResourceCallback<T> callback) {
		if (cretionException != null) {
			callback.handleException(cretionException);
			return false;
		}

		if (locked) {
			callback.handleException(new IllegalStateException("Exclusive resource is locked: " + getId()));
			return false;
		}

		locked = true;

		return resource == null;
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
			DependenciesResolverCallback callback = DependenciesResolverCallback.obtain(this,
					dependentResourceIds.size);
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

		if (activeCallback == null) {
			return;
		}

		try {
			activeCallback.handleException(exception);
			activeCallback = null;
		} catch (Exception e) {
		}
	}

	private void notifyProgress(float progress) {
		if (activeCallback == null) {
			return;
		}

		try {
			activeCallback.handleProgress(progress);
		} catch (Exception e) {
		}
	}

	private void notifySucess() {
		if (activeCallback == null) {
			return;
		}

		try {
			activeCallback.handleResource(resource);
			activeCallback = null;
		} catch (Exception e) {
		}
	}

	@Override
	protected synchronized final void release(T resourceToRelease) {
		if (!locked || this.resource != resourceToRelease) {
			return;
		}

		locked = false;

		if (isPersistent()) {
			resetResource();
		} else {
			disposeResource();
		}
	}

	private void resetResource() {
		if (resource instanceof Poolable) {
			((Poolable) resource).reset();
		}
	}

	private void disposeResource() {
		if (resource instanceof Disposable) {
			((Disposable) resource).dispose();
			resource = null;
		}
	}

	@Override
	public synchronized void dispose() {
		if (locked) {
			throw new IllegalStateException("Resource in use.");
		}

		disposeResource();
		getResourceFactory().dispose();
		cretionException = null;
	}

	private static class DependenciesResolverCallback implements AsyncResourceCallback<DependencyMap>, Poolable {
		private ExclusiveResourceReference<?> reference;
		private int dependenciesCount;

		private static DependenciesResolverCallback obtain(ExclusiveResourceReference<?> reference,
				int dependenciesCount) {
			DependenciesResolverCallback callback = SynchronizedPools.obtain(DependenciesResolverCallback.class);
			callback.reference = reference;
			callback.dependenciesCount = dependenciesCount;
			return callback;
		}

		@Override
		public void handleResource(DependencyMap resource) {
			reference.createResource(resource);
			SynchronizedPools.free(this);
		}

		@Override
		public void handleException(Throwable exception) {
			reference.handleCreationException(exception);
			SynchronizedPools.free(this);
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
