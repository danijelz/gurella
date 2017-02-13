package com.gurella.engine.asset;

import com.badlogic.gdx.utils.Pool;

class TaskPool extends Pool<AssetLoadingTask<?>> {
	int created;
	
	@Override
	protected AssetLoadingTask<Object> newObject() {
		created++;
		return new AssetLoadingTask<Object>();
	}

	@SuppressWarnings("unchecked")
	<T> AssetLoadingTask<T> obtainTask() {
		return (AssetLoadingTask<T>) super.obtain();
	}
	
	@Override
	public String toString() {
		return created + "/" + getFree();
	}
}