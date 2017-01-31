package com.gurella.engine.async;

public interface AsyncCallback<T> {
	void onSuccess(T value);

	void onException(Throwable exception);

	void onCanceled(String message);

	void onProgress(float progress);
}
