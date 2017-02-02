package com.gurella.engine.asset2;

import com.badlogic.gdx.utils.ObjectSet;
import com.gurella.engine.asset2.loader.AssetLoader;
import com.gurella.engine.asset2.persister.AssetPersister;
import com.gurella.engine.asset2.properties.AssetProperties;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.Values;

public class AssetDescriptor<TYPE> {
	public final Class<TYPE> assetType;
	public final boolean containsReferences; // TODO ex composite
	public final boolean allowedForSubtypes;
	final ObjectSet<String> extensions = new ObjectSet<String>();
	final ArrayExt<AssetLoaderConfig<TYPE>> loaders = new ArrayExt<AssetLoaderConfig<TYPE>>();
	final ArrayExt<AssetPersisterConfig<TYPE>> persisters = new ArrayExt<AssetPersisterConfig<TYPE>>();
	// TODO AssetReloader, MissingValueProvider

	public AssetDescriptor(Class<TYPE> assetType, boolean allowedForSubtypes, boolean containsReferences,
			String... extensions) {
		this.assetType = assetType;
		this.containsReferences = containsReferences;
		this.allowedForSubtypes = allowedForSubtypes;
		for (int i = 0; i < extensions.length; i++) {
			String extension = extensions[i];
			if (Values.isNotBlank(extension)) {
				this.extensions.add(extension);
			}
		}
	}

	public void registerLoader(AssetLoader<?, TYPE, ? extends AssetProperties<TYPE>> loader, boolean allowedForSubtypes,
			String... extensions) {
		loaders.add(new AssetLoaderConfig<TYPE>(loader, allowedForSubtypes, extensions));
	}

	public <T> AssetLoader<?, T, ? extends AssetProperties<T>> getLoader(Class<T> assetType, String fileName) {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> AssetPersister<T> getPersister(Class<T> assetType, String fileName) {
		// TODO Auto-generated method stub
		return null;
	}

	public static class AssetLoaderConfig<TYPE> {
		final boolean allowedForSubtypes;
		final boolean defaultLoader;
		final ObjectSet<String> extensions = new ObjectSet<String>();
		final AssetLoader<?, TYPE, ? extends AssetProperties<TYPE>> loader;

		public AssetLoaderConfig(AssetLoader<?, TYPE, ? extends AssetProperties<TYPE>> loader,
				boolean allowedForSubtypes, String... extensions) {
			this.allowedForSubtypes = allowedForSubtypes;
			this.loader = loader;

			boolean hasDefault = false;
			for (int i = 0; i < extensions.length; i++) {
				String extension = extensions[i];
				if (Values.isNotBlank(extension)) {
					this.extensions.add(extension);
				} else {
					hasDefault = true;
				}
			}
			defaultLoader = hasDefault;
		}
	}

	public static class AssetPersisterConfig<TYPE> {
		final boolean extensible = true;
		final boolean defaultLoader = true;
		final ObjectSet<String> extensions = new ObjectSet<String>();
		final AssetPersister<TYPE> persister = null;
	}
}
