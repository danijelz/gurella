package com.gurella.engine.asset.descriptor;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedSet;
import com.gurella.engine.asset.Assets;
import com.gurella.engine.asset.loader.AssetLoader;
import com.gurella.engine.asset.loader.AssetProperties;
import com.gurella.engine.asset.persister.AssetPersister;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.Values;
import com.gurella.engine.utils.factory.Factory;

public class AssetDescriptor<TYPE> {
	public final Class<TYPE> assetType;
	public final boolean hasReferences;
	public final boolean validForSubtypes;
	final OrderedSet<String> _extensions = new OrderedSet<String>();
	public final ImmutableArray<String> extensions = new ImmutableArray<String>(_extensions.orderedItems());

	private Factory<? extends AssetLoader<TYPE, ? extends AssetProperties>> defaultLoaderFactory;
	private final ObjectMap<String, Factory<? extends AssetLoader<TYPE, ? extends AssetProperties>>> loadersByExtension = new ObjectMap<String, Factory<? extends AssetLoader<TYPE, ? extends AssetProperties>>>();

	private Factory<AssetPersister<TYPE>> defaultPersisterFactory;
	private final ObjectMap<String, Factory<AssetPersister<TYPE>>> persistersByExtension = new ObjectMap<String, Factory<AssetPersister<TYPE>>>();
	// TODO AssetReloader, MissingValueProvider

	public AssetDescriptor(Class<TYPE> assetType, boolean validForSubtypes, boolean hasReferences, String extension) {
		this.assetType = assetType;
		this.hasReferences = hasReferences;
		this.validForSubtypes = validForSubtypes;
		if (Values.isNotBlank(extension) && !"*".equals(extension)) {
			this._extensions.add(extension.toLowerCase());
		}
	}

	public AssetDescriptor(Class<TYPE> assetType, boolean validForSubtypes, boolean hasReferences,
			Factory<AssetLoader<TYPE, ? extends AssetProperties>> loaderFactory, String extension) {
		this(assetType, validForSubtypes, hasReferences, extension);
		registerLoaderFactory(loaderFactory, extension);
	}

	public AssetDescriptor(Class<TYPE> assetType, boolean validForSubtypes, boolean hasReferences,
			String... extensions) {
		this.assetType = assetType;
		this.hasReferences = hasReferences;
		this.validForSubtypes = validForSubtypes;
		for (int i = 0; i < extensions.length; i++) {
			String extension = extensions[i];
			if (Values.isNotBlank(extension)) {
				this._extensions.add(extension.toLowerCase());
			}
		}
	}

	public AssetDescriptor(Class<TYPE> assetType, boolean validForSubtypes, boolean hasReferences,
			Factory<AssetLoader<TYPE, ? extends AssetProperties>> loaderFactory, String... extensions) {
		this(assetType, validForSubtypes, hasReferences, extensions);
		registerLoaderFactory(loaderFactory, extensions);
	}

	public AssetDescriptor<TYPE> registerLoaderFactory(
			Factory<? extends AssetLoader<TYPE, ? extends AssetProperties>> loaderFactory, String... extensions) {
		for (int i = 0; i < extensions.length; i++) {
			String extension = extensions[i];
			registerLoaderFactory(loaderFactory, extension);
		}
		return this;
	}

	public AssetDescriptor<TYPE> registerLoaderFactory(
			Factory<? extends AssetLoader<TYPE, ? extends AssetProperties>> loaderFactory, String extension) {
		if (Values.isNotBlank(extension) && !"*".equals(extension)) {
			loadersByExtension.put(extension.toLowerCase(), loaderFactory);
			this._extensions.add(extension);
		} else {
			defaultLoaderFactory = loaderFactory;
		}
		return this;
	}

	public <P extends AssetProperties> Factory<AssetLoader<TYPE, P>> getLoaderFactory(String fileName) {
		if (Assets.hasFileExtension(fileName)) {
			String extension = Assets.getFileExtension(fileName).toLowerCase();
			@SuppressWarnings("unchecked")
			Factory<AssetLoader<TYPE, P>> loader = (Factory<AssetLoader<TYPE, P>>) loadersByExtension.get(extension);
			if (loader != null) {
				return loader;
			}
		}

		@SuppressWarnings("unchecked")
		Factory<AssetLoader<TYPE, P>> loader = (Factory<AssetLoader<TYPE, P>>) defaultLoaderFactory;
		return loader;
	}

	public Factory<AssetPersister<TYPE>> getPersisterFactory(String fileName) {
		if (Assets.hasFileExtension(fileName)) {
			String extension = Assets.getFileExtension(fileName).toLowerCase();
			Factory<AssetPersister<TYPE>> persister = persistersByExtension.get(extension);
			if (persister != null) {
				return persister;
			}
		}

		return defaultPersisterFactory;
	}

	public boolean isValidExtension(String extension) {
		if (Values.isBlank(extension)) {
			return false;
		}

		return _extensions.contains(extension.toLowerCase());
	}

	public String getSingleExtension() {
		return _extensions.size == 1 ? _extensions.orderedItems().get(0) : null;
	}
}
