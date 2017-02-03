package com.gurella.engine.asset2;

import com.badlogic.gdx.utils.Array;
import com.gurella.engine.application.ApplicationConfig;
import com.gurella.engine.asset2.loader.object.SelializedJsonLoader;
import com.gurella.engine.asset2.properties.AssetProperties;
import com.gurella.engine.graphics.material.MaterialDescriptor;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNode;
import com.gurella.engine.utils.Values;

public class DefaultAssetDescriptors {
	public static Array<AssetDescriptor<?>> create() {
		Array<AssetDescriptor<?>> array = new Array<AssetDescriptor<?>>();
		array.add(createJsonLoaderDescriptor(Scene.class, "gscn"));
		array.add(createJsonLoaderDescriptor(SceneNode.class, "pref"));
		array.add(createJsonLoaderDescriptor(MaterialDescriptor.class, "gmat"));
		array.add(createJsonLoaderDescriptor(ApplicationConfig.class, "gcfg"));
		Class<AssetProperties<?>> propsClass = Values.<Class<AssetProperties<?>>> cast(AssetProperties.class);
		array.add(createJsonLoaderDescriptor(propsClass, "gprop"));

		// TODO add loaders
		return array;
	}

	private static <T> AssetDescriptor<T> createJsonLoaderDescriptor(Class<T> type, String extension) {
		SelializedJsonLoader<T> loader = new SelializedJsonLoader<T>(type);
		return new AssetDescriptor<T>(type, true, true, loader, extension);
	}
}
