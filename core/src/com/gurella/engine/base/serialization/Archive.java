package com.gurella.engine.base.serialization;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.base.resource.AsyncCallback;

//TODO unused
public class Archive<T> implements Poolable {
	private String fileName;
	private AsyncCallback<T> callback;
	
	private Array<ExternalDependency> externalDependencies = new Array<ExternalDependency>();

	@Override
	public void reset() {
		externalDependencies.clear();
	}

	static class ExternalDependency {
		String typeName;
		String fileName;
	}
}
