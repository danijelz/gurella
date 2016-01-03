package com.gurella.engine.resource;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.utils.SynchronizedPools;

public class DelegatingResourceReference<T> extends ResourceReference<T> {
	private static final String DELEGATE_ID_TAG = "delegateId";

	private int delegateId;
	private ResourceReference<T> delegate;

	private T resource;
	private int referenceCount;
	private boolean initialized;
	private boolean resourceInCreation;
	private Throwable cretionException;
	private Array<AsyncResourceCallback<T>> pendingCallbacks = new Array<AsyncResourceCallback<T>>();

	protected DelegatingResourceReference() {
	}

	public DelegatingResourceReference(int id, boolean persistent, boolean initOnStart, ResourceReference<T> delegate) {
		super(id, delegate.getName(), persistent, initOnStart);
		this.delegateId = delegate.getId();
	}

	public DelegatingResourceReference(int id, String name, boolean persistent, boolean initOnStart, int delegateId) {
		super(id, name, persistent, initOnStart);
		this.delegateId = delegateId;
	}

	private ResourceReference<T> getDelegate() {
		if (delegate == null) {
			delegate = getOwningContext().getReference(delegateId);
		}
		return delegate;
	}

	@Override
	public ResourceFactory<T> getResourceFactory() {
		return getDelegate().getResourceFactory();
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
		if (!handleCallbackConcurrently(callback)) {
			createResource();
		}
	}

	private synchronized boolean handleCallbackConcurrently(AsyncResourceCallback<T> callback) {
		if (cretionException != null) {
			callback.handleException(cretionException);
			return true;
		}

		referenceCount++;

		if (resource == null) {
			pendingCallbacks.add(callback);
			if (!resourceInCreation) {
				resourceInCreation = true;
				return false;
			} else {
				return true;
			}
		} else {
			callback.handleResource(resource);
			return false;
		}
	}

	private void createResource() {
		getDelegate().obtain(DelegateAsyncResourceCallback.getInstance(this));
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

	private void notifySucess(T createdResource) {
		handleCreatedResource(createdResource);
		notifySucess();
	}

	private synchronized void handleCreatedResource(T createdResource) {
		resource = createdResource;
		resourceInCreation = false;
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

	private void notifyException(Throwable exception) {
		handleCreationException(exception);
		notifyException();
	}

	private synchronized void handleCreationException(Throwable exception) {
		cretionException = exception;
		resourceInCreation = false;
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
				getDelegate().release(resourceToRelease);
				resource = null;
			}
		}
	}

	private void resetResource() {
		if (resource instanceof Poolable) {
			((Poolable) resource).reset();
		}
	}

	@Override
	public void write(Json json) {
		super.write(json);
		json.writeValue(DELEGATE_ID_TAG, delegateId);
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		super.read(json, jsonData);
		delegateId = jsonData.getInt(DELEGATE_ID_TAG);
	}

	@Override
	public void dispose() {
		if (referenceCount > 0) {
			throw new IllegalStateException("Resource in use.");
		}
		release(resource);
		cretionException = null;
	}

	private static class DelegateAsyncResourceCallback<T> implements AsyncResourceCallback<T> {
		DelegatingResourceReference<T> reference;

		public static <T> DelegateAsyncResourceCallback<T> getInstance(DelegatingResourceReference<T> reference) {
			@SuppressWarnings("unchecked")
			DelegateAsyncResourceCallback<T> instance = SynchronizedPools.obtain(DelegateAsyncResourceCallback.class);
			instance.reference = reference;
			return instance;
		}

		@Override
		public void handleResource(T resource) {
			reference.notifySucess(resource);
			SynchronizedPools.free(this);
		}

		@Override
		public void handleException(Throwable exception) {
			reference.notifyException(exception);
			SynchronizedPools.free(this);
		}

		@Override
		public void handleProgress(float progress) {
			reference.notifyProgress(progress);
		}
	}
}
