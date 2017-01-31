package com.gurella.engine.async;

import com.badlogic.gdx.utils.Array;
import com.gurella.engine.pool.PoolService;

public class CompositeAsyncCallback<T> implements AsyncCallback<T> {
	private final Array<AsyncCallback<T>> callbacks = new Array<AsyncCallback<T>>();

	CompositeAsyncCallback() {
	}

	@SuppressWarnings("unchecked")
	public static <T> CompositeAsyncCallback<T> obtain() {
		return PoolService.obtain(CompositeAsyncCallback.class);
	}

	public static <T> CompositeAsyncCallback<T> obtain(AsyncCallback<T> callback) {
		@SuppressWarnings("unchecked")
		CompositeAsyncCallback<T> compositeCallback = PoolService.obtain(CompositeAsyncCallback.class);
		compositeCallback.add(callback);
		return compositeCallback;
	}

	public static <T> CompositeAsyncCallback<T> obtain(AsyncCallback<T> callback1, AsyncCallback<T> callback2) {
		@SuppressWarnings("unchecked")
		CompositeAsyncCallback<T> compositeCallback = PoolService.obtain(CompositeAsyncCallback.class);
		compositeCallback.add(callback1);
		compositeCallback.add(callback2);
		return compositeCallback;
	}

	private void add(AsyncCallback<T> callback2) {
		callbacks.add(callback2);
	}

	@Override
	public void onSuccess(T value) {
		for (int i = 0, n = callbacks.size; i < n; i++) {
			callbacks.get(i).onSuccess(value);
		}
		callbacks.clear();
		PoolService.free(this);
	}

	@Override
	public void onException(Throwable exception) {
		for (int i = 0, n = callbacks.size; i < n; i++) {
			callbacks.get(i).onException(exception);
		}
		callbacks.clear();
		PoolService.free(this);
	}

	@Override
	public void onCanceled(String message) {
		for (int i = 0, n = callbacks.size; i < n; i++) {
			callbacks.get(i).onCanceled(message);
		}
		callbacks.clear();
		PoolService.free(this);
	}

	@Override
	public void onProgress(float progress) {
		for (int i = 0, n = callbacks.size; i < n; i++) {
			callbacks.get(i).onProgress(progress);
		}
	}
}
