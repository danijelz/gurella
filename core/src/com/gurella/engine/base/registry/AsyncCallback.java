package com.gurella.engine.base.registry;

import com.badlogic.gdx.utils.Pool.Poolable;

public interface AsyncCallback<T> {
	void onSuccess(T value);

	void onException(Throwable exception);

	void onProgress(float progress);

	public static class SimpleAsyncCallback<T> implements AsyncCallback<T>, Poolable {
		private float cachedProgress;
		private T cachedValue;
		private Throwable cachedException;
		private boolean done;

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
		public void onProgress(float progress) {
			this.cachedProgress = progress;
		}

		public float getProgress() {
			return cachedProgress;
		}

		public boolean isDone() {
			return done;
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
			done = false;
		}
	}
}
