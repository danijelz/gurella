package com.gurella.engine.asset2;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.gurella.engine.asset2.loader.AssetLoader;
import com.gurella.engine.asset2.persister.AssetPersister;
import com.gurella.engine.asset2.properties.AssetProperties;
import com.gurella.engine.utils.Values;

public class AssetDescriptors {
	private final ObjectMap<Class<?>, AssetDescriptor<?>> descriptorByType = new ObjectMap<Class<?>, AssetDescriptor<?>>();
	private final ObjectMap<Class<?>, AssetDescriptor<?>> resolvedDescriptors = new ObjectMap<Class<?>, AssetDescriptor<?>>();
	private final ObjectMap<String, Array<AssetDescriptor<?>>> descriptorsByExtension = new ObjectMap<String, Array<AssetDescriptor<?>>>();
	private final ObjectSet<String> allExtensions = new ObjectSet<String>();

	public <T> void registerAssetType(Class<T> assetType, boolean allowedForSubtypes, boolean containsReferences,
			String... extensions) {
		if (descriptorByType.containsKey(assetType)) {
			throw new IllegalArgumentException("assetType " + assetType.getName() + " allready registered.");
		}

		AssetDescriptor<T> descriptor = new AssetDescriptor<T>(assetType, allowedForSubtypes, containsReferences,
				extensions);
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
			boolean allowedForSubtypes, String... extensions) {
		AssetDescriptor<T> descriptor = getAssetDescriptor(assetType);
		if (descriptor == null) {
			throw new IllegalArgumentException("assetType " + assetType.getName() + " not registered.");
		} else {
			descriptor.registerLoader(loader, allowedForSubtypes, extensions);
		}
	}

	public <T> AssetLoader<?, T, ? extends AssetProperties<T>> getLoader(final Class<T> assetType) {
		return getLoader(assetType, null);
	}

	public <T> AssetLoader<?, T, ? extends AssetProperties<T>> getLoader(final Class<T> assetType,
			final String fileName) {
		AssetDescriptor<T> descriptor = getAssetDescriptor(assetType);
		return descriptor == null ? null : descriptor.getLoader(assetType, fileName);
	}

	public <T> AssetPersister<T> getPersister(final Class<T> assetType) {
		return getPersister(assetType, null);
	}

	public <T> AssetPersister<T> getPersister(final Class<T> assetType, final String fileName) {
		AssetDescriptor<T> descriptor = getAssetDescriptor(assetType);
		return descriptor == null ? null : descriptor.getPersister(assetType, fileName);
	}

	public boolean hasValidExtension(Class<?> assetType, String fileName) {
		String extension = Assets.getFileExtension(fileName);
		return isValidExtension(assetType, extension);
	}

	public boolean isValidExtension(Class<?> assetType, String extension) {
		if (Values.isBlank(extension)) {
			return false;
		}
		AssetDescriptor<?> descriptor = getAssetDescriptor(assetType);
		return descriptor == null ? null : descriptor.extensions.contains(extension);
	}

	public <T> AssetDescriptor<T> getAssetDescriptor(final Class<T> assetType) {
		AssetDescriptor<?> descriptor = descriptorByType.get(assetType);
		if (descriptor != null) {
			return Values.cast(descriptor);
		}

		if (resolvedDescriptors.containsKey(assetType)) {
			return Values.cast(resolvedDescriptors.get(assetType));
		}

		for (Entry<Class<?>, AssetDescriptor<?>> entry : descriptorByType.entries()) {
			AssetDescriptor<?> temp = entry.value;
			if (temp.allowedForSubtypes && ClassReflection.isAssignableFrom(temp.assetType, assetType)) {
				resolvedDescriptors.put(assetType, temp);
				@SuppressWarnings("unchecked")
				AssetDescriptor<T> casted = (AssetDescriptor<T>) temp;
				return casted;
			}
		}

		return null;
	}

	public <T> AssetDescriptor<T> getAssetDescriptor(final String fileName) {
		return null;
	}

	public <T> Class<T> getAssetType(final String fileName) {
		AssetDescriptor<T> descriptor = getAssetDescriptor(fileName);
		return descriptor == null ? null : descriptor.assetType;
	}

	public <T> Class<T> getAssetType(final Object asset) {
		@SuppressWarnings("unchecked")
		Class<T> assetType = (Class<T>) asset.getClass();
		AssetDescriptor<T> descriptor = getAssetDescriptor(assetType);
		return descriptor == null ? null : descriptor.assetType;
	}
}
