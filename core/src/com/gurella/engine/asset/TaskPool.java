package com.gurella.engine.asset;

import com.badlogic.gdx.utils.Pool;

class TaskPool extends Pool<AssetLoadingTask<?>> {
	@Override
	protected AssetLoadingTask<Object> newObject() {
		return new AssetLoadingTask<Object>();
	}

	@SuppressWarnings("unchecked")
	<T> AssetLoadingTask<T> obtainTask() {
		return (AssetLoadingTask<T>) super.obtain();
	}
}