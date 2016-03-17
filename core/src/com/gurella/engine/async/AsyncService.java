package com.gurella.engine.async;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import com.badlogic.gdx.utils.async.AsyncResult;
import com.badlogic.gdx.utils.async.AsyncTask;
import com.gurella.engine.disposable.DisposablesService;
import com.gurella.engine.pool.PoolService;

public final class AsyncService {
	private static final AsyncExecutor executor = DisposablesService.add(new AsyncExecutor(1));

	private AsyncService() {
	}

	public static <T> AsyncResult<T> submit(final AsyncTask<T> task) {
		return executor.submit(task);
	}

	public static <T> void submit(final AsyncTask<T> task, final AsyncCallback<T> callback) {
		@SuppressWarnings("unchecked")
		CallbackTask<T> callbackTask = PoolService.obtain(CallbackTask.class);
		callbackTask.task = task;
		callbackTask.callback = callback;
		executor.submit(callbackTask);
	}

	private static class CallbackTask<T> implements AsyncTask<Void>, Poolable {
		private AsyncTask<T> task;
		private AsyncCallback<T> callback;

		@Override
		public Void call() throws Exception {
			T value;
			try {
				value = task.call();
			} catch (Exception e) {
				callback.onException(e);
				return null;
			}
			callback.onSuccess(value);
			PoolService.free(this);
			return null;
		}

		@Override
		public void reset() {
			task = null;
			callback = null;
		}
	}
}
