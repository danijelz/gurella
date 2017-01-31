package com.gurella.engine.async;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.utils.Values;

public class SimpleAsyncCallback<T> implements AsyncCallback<T>, Poolable {
	private float cachedProgress;
	private T value;
	private Throwable exception;
	private String message;
	private CallbackState state = CallbackState.processing;

	public static <T> SimpleAsyncCallback<T> obtain() {
		return Values.cast(PoolService.obtain(SimpleAsyncCallback.class));
	}

	@Override
	public void onSuccess(T value) {
		this.value = value;
		state = CallbackState.successful;
	}

	@Override
	public void onException(Throwable exception) {
		this.exception = exception;
		state = CallbackState.failed;
	}

	@Override
	public void onCanceled(String message) {
		this.message = message == null ? "" : message;
		state = CallbackState.cancelled;
	}

	@Override
	public void onProgress(float progress) {
		this.cachedProgress = progress;
	}

	public float getProgress() {
		return cachedProgress;
	}

	public boolean isDone() {
		return state != CallbackState.processing;
	}

	public boolean isCancelled() {
		return state == CallbackState.cancelled;
	}

	public boolean isFailed() {
		return state == CallbackState.failed;
	}

	public boolean isSuccessful() {
		return state == CallbackState.successful;
	}

	public T getValue() {
		if (!isSuccessful()) {
			throw new RuntimeException("Invalid state.");
		}
		return value;
	}

	public T getValueAndFree() {
		if (!isSuccessful()) {
			throw new RuntimeException("Invalid state.");
		}
		T temp = value;
		free();
		return temp;
	}

	public Throwable getException() {
		if (!isFailed()) {
			throw new RuntimeException("Invalid state.");
		}
		return exception;
	}

	public Throwable getExceptionAndFree() {
		if (!isFailed()) {
			throw new RuntimeException("Invalid state.");
		}
		Throwable temp = exception;
		free();
		return temp;
	}

	public String getMessage() {
		if (!isCancelled()) {
			throw new RuntimeException("Invalid state.");
		}
		return message;
	}

	public String getCancellationMessageAndFree() {
		if (!isCancelled()) {
			throw new RuntimeException("Invalid state.");
		}
		String temp = message;
		free();
		return temp;
	}

	@Override
	public void reset() {
		cachedProgress = 0;
		value = null;
		exception = null;
		message = null;
		state = CallbackState.processing;
	}

	public void free() {
		PoolService.free(this);
	}

	private enum CallbackState {
		processing, successful, cancelled, failed;
	}
}