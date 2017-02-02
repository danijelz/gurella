package com.gurella.engine.asset2;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.gurella.engine.asset2.loader.AssetLoader;
import com.gurella.engine.asset2.properties.AssetProperties;
import com.gurella.engine.utils.Values;

public class AssetDescriptors {
	private final ObjectMap<Class<?>, AssetDescriptor<?>> descriptorByType = new ObjectMap<Class<?>, AssetDescriptor<?>>();
	private final ObjectMap<String, Array<AssetDescriptor<?>>> descriptorsByExtension = new ObjectMap<String, Array<AssetDescriptor<?>>>();
	private final ObjectSet<String> allExtensions = new ObjectSet<String>();

	public <T> void register(Class<T> assetType, boolean containsObjectReferences, String... extensions) {
		if (descriptorByType.containsKey(assetType)) {
			throw new IllegalArgumentException("assetType " + assetType.getSimpleName() + " allready registered.");
		}

		AssetDescriptor<T> descriptor = new AssetDescriptor<T>(assetType, containsObjectReferences, extensions);
		descriptorByType.put(assetType, descriptor);

		for (int i = 0; i < extensions.length; i++) {
			String extension = extensions[i];
			if (Values.isNotBlank(extension)) {
				Array<AssetDescriptor<?>> descriptors = descriptorsByExtension.get(extension);
				if (descriptors == null) {
					descriptors = new Array<AssetDescriptor<?>>(4);
					descriptorsByExtension.put(extension, descriptors);
				}
				descriptors.add(descriptor);
				allExtensions.add(extension);
			}
		}
	}

	public <T> AssetLoader<?, T, AssetProperties<T>> getLoader(final Class<T> type) {
		return getLoader(type, null);
	}

	public <T> AssetLoader<?, T, AssetProperties<T>> getLoader(final Class<T> type, final String fileName) {
		// TODO
		return null;
	}

	public static boolean hasValidExtension(Class<?> assetType, String fileName) {
		return false;
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
