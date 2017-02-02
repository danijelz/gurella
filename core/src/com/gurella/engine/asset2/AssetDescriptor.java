package com.gurella.engine.asset2;

import com.badlogic.gdx.utils.ObjectSet;
import com.gurella.engine.asset2.loader.AssetLoader;
import com.gurella.engine.asset2.persister.AssetPersister;
import com.gurella.engine.asset2.properties.AssetProperties;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.Values;

public class AssetDescriptor<TYPE> {
	public final Class<TYPE> assetType;
	public final boolean containsObjectReferences; // TODO ex composite
	private final ObjectSet<String> extensions = new ObjectSet<String>();
	private final ArrayExt<AssetLoaderConfig<TYPE>> loaders = new ArrayExt<AssetLoaderConfig<TYPE>>();
	private final ArrayExt<AssetPersisterConfig<TYPE>> persisters = new ArrayExt<AssetPersisterConfig<TYPE>>();
	// TODO AssetReloader, MissingValueProvider

	public AssetDescriptor(Class<TYPE> assetType, boolean containsObjectReferences, String... extensions) {
		this.assetType = assetType;
		this.containsObjectReferences = containsObjectReferences;
		for (int i = 0; i < extensions.length; i++) {
			String extension = extensions[i];
			if (Values.isNotBlank(extension)) {
				this.extensions.add(extension);
			}

		}
	}

	public static class AssetLoaderConfig<TYPE> {
		public final boolean extensible = true;
		public final String[] fileExtensions = null;
		public final AssetLoader<?, TYPE, ? extends AssetProperties<TYPE>> loader = null;
	}

	public static class AssetPersisterConfig<TYPE> {
		public final boolean extensible = true;
		public final ObjectSet<String> extensions = new ObjectSet<String>();
		public final AssetPersister<TYPE> persister = null;
	}
}
