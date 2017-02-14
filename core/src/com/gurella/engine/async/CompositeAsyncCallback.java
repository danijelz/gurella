package com.gurella.engine.async;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.utils.ImmutableArray;

public class CompositeAsyncCallback<T> implements AsyncCallback<T>, Poolable {
	private final Array<AsyncCallback<? super T>> _callbacks = new Array<AsyncCallback<? super T>>();
	public final ImmutableArray<AsyncCallback<? super T>> callbacks = new ImmutableArray<AsyncCallback<? super T>>(
			_callbacks);

	public CompositeAsyncCallback() {
	}

	@SuppressWarnings("unchecked")
	public static <T> CompositeAsyncCallback<T> obtain() {
		return PoolService.obtain(CompositeAsyncCallback.class);
	}

	public static <T> CompositeAsyncCallback<T> obtain(AsyncCallback<? super T> callback) {
		@SuppressWarnings("unchecked")
		CompositeAsyncCallback<T> compositeCallback = PoolService.obtain(CompositeAsyncCallback.class);
		compositeCallback.add(callback);
		return compositeCallback;
	}

	public static <T> CompositeAsyncCallback<T> obtain(AsyncCallback<? super T> callback1,
			AsyncCallback<? super T> callback2) {
		@SuppressWarnings("unchecked")
		CompositeAsyncCallback<T> compositeCallback = PoolService.obtain(CompositeAsyncCallback.class);
		compositeCallback.add(callback1);
		compositeCallback.add(callback2);
		return compositeCallback;
	}

	public static <T> CompositeAsyncCallback<T> obtain(AsyncCallback<? super T>... callbacks) {
		@SuppressWarnings("unchecked")
		CompositeAsyncCallback<T> compositeCallback = PoolService.obtain(CompositeAsyncCallback.class);
		compositeCallback.addAll(callbacks);
		return compositeCallback;
	}

	public void add(AsyncCallback<? super T> callback) {
		_callbacks.add(callback);
	}

	public void remove(AsyncCallback<? super T> callback) {
		_callbacks.removeValue(callback, true);
	}

	public void addAll(AsyncCallback<? super T>... callbacks) {
		_callbacks.addAll(callbacks);
	}

	@Override
	public void onSuccess(T value) {
		for (int i = 0, n = _callbacks.size; i < n; i++) {
			_callbacks.get(i).onSuccess(value);
		}
	}

	@Override
	public void onException(Throwable exception) {
		for (int i = 0, n = _callbacks.size; i < n; i++) {
			_callbacks.get(i).onException(exception);
		}
	}

	@Override
	public void onCanceled(String message) {
		for (int i = 0, n = _callbacks.size; i < n; i++) {
			_callbacks.get(i).onCanceled(message);
		}
	}

	@Override
	public void onProgress(float progress) {
		for (int i = 0, n = _callbacks.size; i < n; i++) {
			_callbacks.get(i).onProgress(progress);
		}
	}

	@Override
	public void reset() {
		_callbacks.clear();
	}

	public void free() {
		PoolService.free(this);
	}
}
