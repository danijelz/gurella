package com.gurella.engine.asset2;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.gurella.engine.asset2.loader.AssetLoader;
import com.gurella.engine.asset2.properties.AssetProperties;

public class AssetDescriptors {
	private final ObjectMap<Class<?>, AssetInfo<?>> infoByType = new ObjectMap<Class<?>, AssetInfo<?>>();
	private final ObjectMap<String, Array<AssetInfo<?>>> infosByExtension = new ObjectMap<String, Array<AssetInfo<?>>>();
	private final ObjectSet<String> allExtensions = new ObjectSet<String>();

	public <T> AssetLoader<?, T, AssetProperties<T>> getLoader(final Class<T> type) {
		return getLoader(type, null);
	}

	public <T> AssetLoader<?, T, AssetProperties<T>> getLoader(final Class<T> type, final String fileName) {
		//TODO
		return null;
	}

	public static boolean isValidExtension(Class<?> assetType, String extension) {
		return false;
	}

	public static <T> AssetDescriptor<T> getAssetDescriptor(final Class<T> assetType) {
		return null;
	}

	public static <T> AssetDescriptor<T> getAssetDescriptor(final String fileName) {
		return null;
	}

	public static <T> AssetDescriptor<T> getAssetDescriptor(final Class<T> assetType, final String fileName) {
		return null;
	}

	public static <T> Class<T> getAssetType(final String fileName) {
		return null;
	}

	public static <T> Class<T> getAssetType(final Object asset) {
		return null;
	}

	private static class AssetInfo<TYPE> {
		final Array<AssetDescriptor<TYPE>> descriptors = new Array<AssetDescriptor<TYPE>>();
		AssetLoader<?, ?, ?> defaultLoader;
		final ObjectMap<String, AssetLoader<?, ?, ?>> loadersByExtension = new ObjectMap<String, AssetLoader<?, ?, ?>>();
		final ObjectSet<String> allExtensions = new ObjectSet<String>();
	}
}
