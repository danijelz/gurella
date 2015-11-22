package com.gurella.engine.resource;

import com.badlogic.gdx.utils.Pool.Poolable;

public interface AsyncResourceCallback<T> {
	void handleResource(T resource);

	void handleException(Throwable exception);
	
	void handleProgress(float progress);
	
	public static class SimpleAsyncResourceCallback<T> implements AsyncResourceCallback<T>, Poolable {
		private float cachedProgress;
		private T cachedResource;
		private Throwable cachedException;
		private boolean done;
		
		@Override
		public void handleResource(T resource) {
			this.cachedResource = resource;
			done = true;
		}

		@Override
		public void handleException(Throwable exception) {
			this.cachedException = exception;
			done = true;
		}

		@Override
		public void handleProgress(float progress) {
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
		
		public T getResource() {
			return cachedResource;
		}
		
		public Throwable getException() {
			return cachedException;
		}

		@Override
		public void reset() {
			cachedProgress = 0;
			cachedResource = null;
			cachedException = null;
			done = false;
		}
	}
}
