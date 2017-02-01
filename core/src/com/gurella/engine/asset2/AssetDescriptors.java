package com.gurella.engine.asset2;

import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.asset2.loader.AssetLoader;
import com.gurella.engine.asset2.properties.AssetProperties;

public class AssetDescriptors {
	private final ObjectMap<Class<?>, AssetDescriptor<?>> descriptors = new ObjectMap<Class<?>, AssetDescriptor<?>>();
	private final ObjectMap<Class<?>, AssetLoader<?, ?, ?>> loadersByType = new ObjectMap<Class<?>, AssetLoader<?, ?, ?>>();
	private final ObjectMap<String, AssetLoader<?, ?, ?>> loadersByExtension = new ObjectMap<String, AssetLoader<?, ?, ?>>();

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
		private final AssetDescriptor<TYPE> descriptor;
		private final LoadersConfig loaders;

		public AssetInfo(AssetDescriptor<TYPE> descriptor, AssetLoader<?, ?, ?> defaultLoader) {
			this.descriptor = descriptor;
			this.loaders = new LoadersConfig(defaultLoader);
		}
	}

	private static class LoadersConfig {
		final AssetLoader<?, ?, ?> defaultLoader;
		final ObjectMap<String, AssetLoader<?, ?, ?>> loadersByExtension = new ObjectMap<String, AssetLoader<?, ?, ?>>();

		LoadersConfig(AssetLoader<?, ?, ?> defaultLoader) {
			this.defaultLoader = defaultLoader;
		}
	}
}
