package com.gurella.engine.asset.descriptor;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedSet;
import com.gurella.engine.asset.Assets;
import com.gurella.engine.asset.loader.AssetLoader;
import com.gurella.engine.asset.loader.AssetProperties;
import com.gurella.engine.asset.persister.AssetPersister;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.Values;

public class AssetDescriptor<TYPE> {
	public final Class<TYPE> assetType;
	public final boolean hasReferences; // AssetType.composite
	public final boolean validForSubtypes;
	final OrderedSet<String> _extensions = new OrderedSet<String>();
	public final ImmutableArray<String> extensions = new ImmutableArray<String>(_extensions.orderedItems());

	private AssetLoader<?, TYPE, ? extends AssetProperties> defaultLoader;
	private final ObjectMap<String, AssetLoader<?, TYPE, ? extends AssetProperties>> loadersByExtension = new ObjectMap<String, AssetLoader<?, TYPE, ? extends AssetProperties>>();

	private AssetPersister<TYPE> defaultPersister;
	private final ObjectMap<String, AssetPersister<TYPE>> persistersByExtension = new ObjectMap<String, AssetPersister<TYPE>>();
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
			AssetLoader<?, TYPE, ? extends AssetProperties> loader, String extension) {
		this(assetType, validForSubtypes, hasReferences, extension);
		registerLoader(loader, extension);
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
			AssetLoader<?, TYPE, ? extends AssetProperties> loader, String... extensions) {
		this(assetType, validForSubtypes, hasReferences, extensions);
		registerLoader(loader, extensions);
	}

	public AssetDescriptor<TYPE> registerLoader(AssetLoader<?, TYPE, ? extends AssetProperties> loader,
			String... extensions) {
		for (int i = 0; i < extensions.length; i++) {
			String extension = extensions[i];
			registerLoader(loader, extension);
		}
		return this;
	}

	public AssetDescriptor<TYPE> registerLoader(AssetLoader<?, TYPE, ? extends AssetProperties> loader,
			String extension) {
		if (Values.isNotBlank(extension) && !"*".equals(extension)) {
			loadersByExtension.put(extension.toLowerCase(), loader);
			this._extensions.add(extension);
		} else {
			defaultLoader = loader;
		}
		return this;
	}

	public <A> AssetLoader<A, TYPE, AssetProperties> getLoader(String fileName) {
		if (Assets.hasFileExtension(fileName)) {
			String extension = Assets.getFileExtension(fileName).toLowerCase();
			AssetLoader<?, TYPE, ? extends AssetProperties> loader = loadersByExtension.get(extension);
			if (loader != null) {
				@SuppressWarnings("unchecked")
				AssetLoader<A, TYPE, AssetProperties> casted = (AssetLoader<A, TYPE, AssetProperties>) loader;
				return casted;
			}
		}
		@SuppressWarnings("unchecked")
		AssetLoader<A, TYPE, AssetProperties> casted = (AssetLoader<A, TYPE, AssetProperties>) defaultLoader;
		return casted;
	}

	public AssetPersister<TYPE> getPersister(String fileName) {
		if (Assets.hasFileExtension(fileName)) {
			String extension = Assets.getFileExtension(fileName).toLowerCase();
			AssetPersister<TYPE> persister = persistersByExtension.get(extension);
			if (persister != null) {
				return persister;
			}
		}
		return defaultPersister;
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
