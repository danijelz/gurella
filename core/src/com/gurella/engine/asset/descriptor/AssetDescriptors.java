package com.gurella.engine.asset.descriptor;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.gurella.engine.asset.Assets;
import com.gurella.engine.asset.loader.AssetLoader;
import com.gurella.engine.asset.loader.AssetProperties;
import com.gurella.engine.asset.persister.AssetPersister;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.Values;

public class AssetDescriptors extends DefaultAssetDescriptors {
	private final ObjectMap<Class<?>, AssetDescriptor<?>> descriptorByType = new ObjectMap<Class<?>, AssetDescriptor<?>>();
	private final ObjectMap<Class<?>, AssetDescriptor<?>> resolvedDescriptors = new ObjectMap<Class<?>, AssetDescriptor<?>>();
	private final ObjectMap<String, Array<AssetDescriptor<?>>> descriptorsByExtension = new ObjectMap<String, Array<AssetDescriptor<?>>>();

	public AssetDescriptors() {
		for (int i = 0, n = _descriptors.size; i < n; i++) {
			register(_descriptors.get(i));
		}
	}

	public <T> void register(AssetDescriptor<T> descriptor) {
		Class<T> assetType = descriptor.assetType;
		if (descriptorByType.containsKey(assetType)) {
			throw new IllegalArgumentException("assetType " + assetType.getName() + " allready registered.");
		}

		descriptorByType.put(assetType, descriptor);

		ImmutableArray<String> extensions = descriptor.extensions;
		for (int i = 0, n = extensions.size(); i < n; i++) {
			String extension = extensions.get(i);
			if (Values.isNotBlank(extension)) {
				extension = extension.toLowerCase();
				Array<AssetDescriptor<?>> descriptors = descriptorsByExtension.get(extension);
				if (descriptors == null) {
					descriptors = new Array<AssetDescriptor<?>>(2);
					descriptorsByExtension.put(extension, descriptors);
				}
				descriptors.add(descriptor);
			}
		}

		resolvedDescriptors.clear();
	}

	public <T> void registerAssetType(Class<T> assetType, boolean validForSubtypes, boolean hasReferences,
			String... extensions) {
		if (descriptorByType.containsKey(assetType)) {
			throw new IllegalArgumentException("assetType " + assetType.getName() + " allready registered.");
		}

		AssetDescriptor<T> descriptor = new AssetDescriptor<T>(assetType, validForSubtypes, hasReferences, extensions);
		descriptorByType.put(assetType, descriptor);

		for (int i = 0; i < extensions.length; i++) {
			String extension = extensions[i];
			if (Values.isNotBlank(extension)) {
				extension = extension.toLowerCase();
				Array<AssetDescriptor<?>> descriptors = descriptorsByExtension.get(extension);
				if (descriptors == null) {
					descriptors = new Array<AssetDescriptor<?>>(2);
					descriptorsByExtension.put(extension, descriptors);
				}
				descriptors.add(descriptor);
			}
		}

		resolvedDescriptors.clear();
	}

	public <T> void registerLoader(Class<T> assetType, AssetLoader<?, T, ? extends AssetProperties> loader,
			String... extensions) {
		AssetDescriptor<T> descriptor = getAssetDescriptor(assetType);
		if (descriptor == null) {
			throw new IllegalArgumentException("assetType " + assetType.getName() + " not registered.");
		}
		descriptor.registerLoader(loader, extensions);

		for (int i = 0; i < extensions.length; i++) {
			String extension = extensions[i];
			if (Values.isNotBlank(extension)) {
				extension = extension.toLowerCase();
				Array<AssetDescriptor<?>> descriptors = descriptorsByExtension.get(extension);
				if (descriptors == null) {
					descriptors = new Array<AssetDescriptor<?>>(2);
					descriptorsByExtension.put(extension, descriptors);
				}
				descriptors.add(descriptor);
			}
		}
	}

	public <T> AssetLoader<?, T, ? extends AssetProperties> getLoader(final Class<T> assetType) {
		return getLoader(null, assetType);
	}

	public <A, T> AssetLoader<A, T, AssetProperties> getLoader(final String fileName, final Class<T> assetType) {
		AssetDescriptor<T> descriptor = getAssetDescriptor(assetType);
		return descriptor == null ? null : descriptor.<A> getLoader(fileName);
	}

	public <A, T> AssetLoader<A, T, AssetProperties> getLoader(final String fileName) {
		AssetDescriptor<T> descriptor = getAssetDescriptor(fileName);
		return descriptor == null ? null : descriptor.<A> getLoader(fileName);
	}

	public <T> AssetPersister<T> getPersister(final Class<T> assetType) {
		return getPersister(null, assetType);
	}

	public <T> AssetPersister<T> getPersister(final String fileName, final Class<T> assetType) {
		AssetDescriptor<T> descriptor = getAssetDescriptor(assetType);
		return descriptor == null ? null : descriptor.getPersister(fileName);
	}

	public boolean hasValidExtension(String fileName, Class<?> assetType) {
		String extension = Assets.getFileExtension(fileName);
		return isValidExtension(extension, assetType);
	}

	public boolean isValidExtension(String extension, Class<?> assetType) {
		if (Values.isBlank(extension)) {
			return false;
		}
		AssetDescriptor<?> descriptor = getAssetDescriptor(assetType);
		return descriptor != null && descriptor.isValidExtension(extension);
	}

	public boolean isValidExtension(String extension, Class<?>... assetTypes) {
		if (Values.isBlank(extension) || Values.isEmptyArray(assetTypes)) {
			return false;
		}

		String lowerCaseExtension = extension.toLowerCase();
		for (Class<?> assetType : assetTypes) {
			AssetDescriptor<?> descriptor = getAssetDescriptor(assetType);
			if (descriptor != null && descriptor.isValidExtension(lowerCaseExtension)) {
				return true;
			}
		}

		return false;
	}

	public boolean hasAssetType(Object obj) {
		return obj != null && getAssetDescriptor(obj.getClass()) != null;
	}

	public boolean isAssetType(Class<?> assetType) {
		return assetType != null && getAssetDescriptor(assetType) != null;
	}

	public <T> AssetDescriptor<T> getAssetDescriptor(final Class<? extends T> assetType) {
		AssetDescriptor<?> descriptor = descriptorByType.get(assetType);
		if (descriptor != null) {
			return Values.cast(descriptor);
		}

		if (resolvedDescriptors.containsKey(assetType)) {
			return Values.cast(resolvedDescriptors.get(assetType));
		}

		for (Entry<Class<?>, AssetDescriptor<?>> entry : descriptorByType.entries()) {
			AssetDescriptor<?> temp = entry.value;
			if (temp.validForSubtypes && ClassReflection.isAssignableFrom(temp.assetType, assetType)) {
				resolvedDescriptors.put(assetType, temp);
				@SuppressWarnings("unchecked")
				AssetDescriptor<T> casted = (AssetDescriptor<T>) temp;
				return casted;
			}
		}

		return null;
	}

	public <T> AssetDescriptor<T> getAssetDescriptor(final String fileName) {
		String extension = Assets.getFileExtension(fileName);
		return getExtensionAssetDescriptor(extension);
	}

	public <T> AssetDescriptor<T> getExtensionAssetDescriptor(String extension) {
		if (Values.isBlank(extension)) {
			return null;
		}

		Array<AssetDescriptor<?>> descriptors = descriptorsByExtension.get(extension.toLowerCase());
		if (descriptors == null || descriptors.size != 1) {
			return null;
		} else {
			@SuppressWarnings("unchecked")
			AssetDescriptor<T> descriptor = (AssetDescriptor<T>) descriptors.get(0);
			return descriptor;
		}
	}

	public <T> Class<T> getAssetType(final String fileName) {
		AssetDescriptor<T> descriptor = getAssetDescriptor(fileName);
		return descriptor == null ? null : descriptor.assetType;
	}

	public <T> Class<T> getExtensionAssetType(final String extension) {
		AssetDescriptor<T> descriptor = getExtensionAssetDescriptor(extension);
		return descriptor == null ? null : descriptor.assetType;
	}

	public <T> Class<T> getAssetType(final Class<? extends T> assetType) {
		AssetDescriptor<T> descriptor = getAssetDescriptor(assetType);
		return descriptor == null ? null : descriptor.assetType;
	}

	public <T> Class<T> getAssetType(final Object asset) {
		@SuppressWarnings("unchecked")
		Class<T> assetType = (Class<T>) asset.getClass();
		AssetDescriptor<T> descriptor = getAssetDescriptor(assetType);
		return descriptor == null ? null : descriptor.assetType;
	}
}
