package com.gurella.engine.asset.persister;

import com.badlogic.gdx.utils.ObjectMap;

public class AssetPersisters {
	private static final ObjectMap<Class<?>, AssetPersister<?>> persisters = new ObjectMap<Class<?>, AssetPersister<?>>();
	
	private AssetPersisters() {
	}

	public static <T> AssetPersister<T> get(T asset) {
		return null;
	}
}
