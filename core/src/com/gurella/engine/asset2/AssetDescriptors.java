package com.gurella.engine.asset2;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.gurella.engine.asset2.loader.AssetLoader;
import com.gurella.engine.asset2.persister.AssetPersister;
import com.gurella.engine.asset2.properties.AssetProperties;
import com.gurella.engine.utils.Values;

public class AssetDescriptors {
	private final ObjectMap<Class<?>, AssetDescriptor<?>> descriptorByType = new ObjectMap<Class<?>, AssetDescriptor<?>>();
	private final ObjectMap<String, Array<AssetDescriptor<?>>> descriptorsByExtension = new ObjectMap<String, Array<AssetDescriptor<?>>>();
	private final ObjectSet<String> allExtensions = new ObjectSet<String>();

	public <T> void registerAssetType(Class<T> assetType, boolean containsReferences, String... extensions) {
		if (descriptorByType.containsKey(assetType)) {
			throw new IllegalArgumentException("assetType " + assetType.getSimpleName() + " allready registered.");
		}

		AssetDescriptor<T> descriptor = new AssetDescriptor<T>(assetType, containsReferences, extensions);
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

	public <T> void registerLoader(Class<T> assetType, AssetLoader<?, T, ? extends AssetProperties<T>> loader,
			boolean extensible, String... extensions) {
		@SuppressWarnings("unchecked")
		AssetDescriptor<T> descriptor = (AssetDescriptor<T>) descriptorByType.get(assetType);

		if (descriptor == null) {
			throw new IllegalArgumentException("assetType " + assetType.getSimpleName() + " not registered.");
		} else {
			descriptor.registerLoader(assetType, loader, extensible, extensions);
		}
	}

	private <T> AssetDescriptor<T> getDescriptor(final Class<T> assetType) {
		AssetDescriptor<?> descriptor = descriptorByType.get(assetType);
		if (descriptor != null) {
			@SuppressWarnings("unchecked")
			AssetDescriptor<T> casted = (AssetDescriptor<T>) descriptor;
			return casted;
		}

		@SuppressWarnings("unchecked")
		AssetDescriptor<T> casted = (AssetDescriptor<T>) descriptor;
		return casted;
	}

	public <T> AssetLoader<?, T, ? extends AssetProperties<T>> getLoader(final Class<T> assetType) {
		return getLoader(assetType, null);
	}

	public <T> AssetLoader<?, T, ? extends AssetProperties<T>> getLoader(final Class<T> assetType,
			final String fileName) {
		AssetDescriptor<T> descriptor = getDescriptor(assetType);
		return descriptor == null ? null : descriptor.getLoader(assetType, fileName);
	}

	public <T> AssetPersister<T> getPersister(final Class<T> assetType) {
		return getPersister(assetType, null);
	}

	public <T> AssetPersister<T> getPersister(final Class<T> assetType, final String fileName) {
		AssetDescriptor<T> descriptor = getDescriptor(assetType);
		return descriptor == null ? null : descriptor.getPersister(assetType, fileName);
	}

	public boolean hasValidExtension(Class<?> assetType, String fileName) {
		return false;
	}

	public boolean isValidExtension(Class<?> assetType, String extension) {
		return false;
	}

	public <T> AssetDescriptor<T> getAssetDescriptor(final Class<T> assetType) {
		return null;
	}

	public <T> AssetDescriptor<T> getAssetDescriptor(final String fileName) {
		return null;
	}

	public <T> AssetDescriptor<T> getAssetDescriptor(final Class<T> assetType, final String fileName) {
		return null;
	}

	public <T> Class<T> getAssetType(final String fileName) {
		return null;
	}

	public <T> Class<T> getAssetType(final Object asset) {
		return null;
	}
}
