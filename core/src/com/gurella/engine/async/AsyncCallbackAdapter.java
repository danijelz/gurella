package com.gurella.engine.async;

public class AsyncCallbackAdapter<T> implements AsyncCallback<T> {

	@Override
	public void onSuccess(T value) {
	}

	@Override
	public void onException(Throwable exception) {
	}

	@Override
	public void onCanceled(String message) {
	}

	@Override
	public void onProgress(float progress) {
	}
}
