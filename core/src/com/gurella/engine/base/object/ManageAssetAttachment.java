package com.gurella.engine.base.object;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.async.AsyncCallback;
import com.gurella.engine.base.resource.ResourceService;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.subscriptions.base.object.ObjectDestroyedListener;

public class ManageAssetAttachment<T> extends Attachment<T> implements Poolable {
	static <T> ManageAssetAttachment<T> obtain(T asset) {
		@SuppressWarnings("unchecked")
		ManageAssetAttachment<T> attachment = PoolService.obtain(ManageAssetAttachment.class);
		attachment.value = asset;
		return attachment;
	}

	static <T> void loadAsync(ManagedObject object, String fileName, Class<T> assetType, AsyncCallback<T> callback) {
		ResourceService.loadAsync(fileName, assetType, Callback.obtain(object, callback), 0);
	}

	@Override
	protected void attach() {
	}

	@Override
	protected void detach() {
	}

	@Override
	public void reset() {
		ResourceService.unload(value);
		value = null;
	}

	static class Callback<T> implements AsyncCallback<T>, ObjectDestroyedListener, Poolable {
		private final Object mutex = new Object();
		ManagedObject object;
		AsyncCallback<T> delegate;
		boolean objectDestroyed;

		static <T> Callback<T> obtain(ManagedObject object, AsyncCallback<T> delegate) {
			@SuppressWarnings("unchecked")
			Callback<T> callback = PoolService.obtain(Callback.class);
			callback.object = object;
			object.subscribeTo(callback);
			callback.delegate = delegate;
			return callback;
		}

		@Override
		public void onSuccess(T value) {
			synchronized (mutex) {
				if (objectDestroyed) {
					ResourceService.unload(value);
				} else {
					object.bindAsset(value);
					delegate.onSuccess(value);
				}
				PoolService.free(this);
			}
		}

		@Override
		public void onException(Throwable exception) {
			synchronized (mutex) {
				if (!objectDestroyed) {
					delegate.onException(exception);
					PoolService.free(this);
				}
			}
		}

		@Override
		public void onCancled(String message) {
			synchronized (mutex) {
				if (!objectDestroyed) {
					delegate.onCancled(message);
					PoolService.free(this);
				}
			}
		}

		@Override
		public void onProgress(float progress) {
			synchronized (mutex) {
				if (!objectDestroyed) {
					delegate.onProgress(progress);
				}
			}
		}

		@Override
		public void objectDestroyed() {
			synchronized (mutex) {
				objectDestroyed = true;
			}
		}

		@Override
		public void reset() {
			objectDestroyed = false;
			object = null;
			delegate = null;
		}
	}
}
