package com.gurella.engine.asset;

import static com.gurella.engine.asset.AssetLoadingPhase.finished;

import com.gurella.engine.async.AsyncCallback;
import com.gurella.engine.async.CompositeAsyncCallback;

class AssetLoadingTaskCallback<T> extends CompositeAsyncCallback<T> {
	boolean isActive() {
		for (int i = 0, n = callbacks.size(); i < n; i++) {
			AsyncCallback<? super T> callback = callbacks.get(i);
			if (callback instanceof AssetLoadingTask) {
				AssetLoadingTask<?> dependentTask = (AssetLoadingTask<?>) callback;
				if (dependentTask.phase != finished) {
					return true;
				}
			}
		}

		return false;
	}

	int getReferences() {
		int count = 0;
		for (int i = 0, n = callbacks.size(); i < n; i++) {
			AsyncCallback<? super T> concurrentCallback = callbacks.get(i);
			if (!(concurrentCallback instanceof AssetLoadingTask)) {
				count++;
			}
		}

		return count;
	}

	int getReservations() {
		int count = 0;
		for (int i = 0, n = callbacks.size(); i < n; i++) {
			AsyncCallback<? super T> concurrentCallback = callbacks.get(i);
			if (concurrentCallback instanceof AssetLoadingTask) {
				count++;
			}
		}

		return count;
	}
}