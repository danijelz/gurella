package com.gurella.engine.asset2;

import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.asset2.loader.AssetLoader;
import com.gurella.engine.asset2.properties.AssetProperties;

public class AssetDescriptors {
	private final ObjectMap<Class<?>, AssetDescriptor<?, ?>> descriptors = new ObjectMap<Class<?>, AssetDescriptor<?, ?>>();
	private final ObjectMap<Class<?>, AssetLoader<?, ?, ?>> loadersByType = new ObjectMap<Class<?>, AssetLoader<?, ?, ?>>();
	private final ObjectMap<String, AssetLoader<?, ?, ?>> loadersByExtension = new ObjectMap<String, AssetLoader<?, ?, ?>>();
	
	private static class AssetInfo<TYPE> {
		private final AssetDescriptor<TYPE, AssetProperties<TYPE>> descriptor;
		private final LoadersConfig loaders;
		
		public AssetInfo(AssetDescriptor<TYPE, AssetProperties<TYPE>> descriptor, AssetLoader<?, ?, ?> defaultLoader) {
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
