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
	final ObjectSet<String> extensions = new ObjectSet<String>();
	final ArrayExt<AssetLoaderConfig<TYPE>> loaders = new ArrayExt<AssetLoaderConfig<TYPE>>();
	final ArrayExt<AssetPersisterConfig<TYPE>> persisters = new ArrayExt<AssetPersisterConfig<TYPE>>();
	// TODO AssetReloader, MissingValueProvider

	public AssetDescriptor(Class<TYPE> assetType, /*boolean allowForSubtypes,*/ boolean containsReferences,
			String... extensions) {
		this.assetType = assetType;
		this.containsReferences = containsReferences;
		for (int i = 0; i < extensions.length; i++) {
			String extension = extensions[i];
			if (Values.isNotBlank(extension)) {
				this.extensions.add(extension);
			}
		}
	}

	public <T> void registerLoader(Class<T> assetType, AssetLoader<?, T, ? extends AssetProperties<T>> loader,
			boolean extensible, String... extensions) {

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
		final boolean extensible;
		final boolean defaultLoader;
		final ObjectSet<String> extensions = new ObjectSet<String>();
		final AssetLoader<?, TYPE, ? extends AssetProperties<TYPE>> loader;

		public AssetLoaderConfig(AssetLoader<?, TYPE, ? extends AssetProperties<TYPE>> loader, boolean extensible,
				String... extensions) {
			this.extensible = extensible;
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
