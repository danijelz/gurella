package com.gurella.engine.async;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.utils.Values;

public class SimpleAsyncCallback<T> implements AsyncCallback<T>, Poolable {
	private float cachedProgress;
	private T cachedValue;
	private Throwable cachedException;
	private String cancledMessage;
	private boolean done;

	public static <T> SimpleAsyncCallback<T> obtain() {
		return Values.cast(PoolService.obtain(SimpleAsyncCallback.class));
	}

	@Override
	public void onSuccess(T value) {
		this.cachedValue = value;
		done = true;
	}

	@Override
	public void onException(Throwable exception) {
		this.cachedException = exception;
		done = true;
	}

	@Override
	public void onCancled(String message) {
		cancledMessage = message == null ? "" : message;
		done = true;
	}

	@Override
	public void onProgress(float progress) {
		this.cachedProgress = progress;
	}

	public float getProgress() {
		return cachedProgress;
	}

	public boolean isDone() {
		return done;
	}

	public boolean isCancled() {
		return cancledMessage != null;
	}

	public boolean isDoneWithException() {
		return done && cachedException != null;
	}

	public boolean isDoneWithoutException() {
		return done && cachedException == null;
	}

	public T getValue() {
		return cachedValue;
	}

	public Throwable getException() {
		return cachedException;
	}

	@Override
	public void reset() {
		cachedProgress = 0;
		cachedValue = null;
		cachedException = null;
		cancledMessage = null;
		done = false;
	}

	public void free() {
		PoolService.free(this);
	}
}