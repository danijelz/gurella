package com.gurella.engine.asset.persister;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.gurella.engine.application.ApplicationConfig;
import com.gurella.engine.asset.persister.object.JsonObjectPersister;
import com.gurella.engine.asset.properties.AssetProperties;
import com.gurella.engine.graphics.material.MaterialDescriptor;
import com.gurella.engine.managedobject.ManagedObject;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNode;
import com.gurella.engine.utils.Values;

public class AssetPersisters {
	private static final ObjectMap<Class<?>, AssetPersister<?>> persisters = new ObjectMap<Class<?>, AssetPersister<?>>();

	static {
		FileHandleResolver resolver = new InternalFileHandleResolver();
		setPersister(Scene.class, new JsonObjectPersister<Scene>(resolver, Scene.class));
		setPersister(SceneNode.class, new JsonObjectPersister<SceneNode>(resolver, SceneNode.class));
		setPersister(MaterialDescriptor.class,
				new JsonObjectPersister<MaterialDescriptor>(resolver, MaterialDescriptor.class));
		setPersister(ManagedObject.class, new JsonObjectPersister<ManagedObject>(resolver, ManagedObject.class));
		setPersister(ApplicationConfig.class,
				new JsonObjectPersister<ApplicationConfig>(resolver, ApplicationConfig.class));
		Class<AssetProperties<?>> propertiesClass = Values.cast(AssetProperties.class);
		setPersister(propertiesClass, new JsonObjectPersister<AssetProperties<?>>(resolver, propertiesClass));
	}

	private AssetPersisters() {
	}

	private static <T> void setPersister(Class<T> type, AssetPersister<T> persister) {
		persisters.put(type, persister);
	}

	public static <T> AssetPersister<T> get(T asset) {
		@SuppressWarnings("unchecked")
		AssetPersister<T> persister = (AssetPersister<T>) persisters.get(asset.getClass());
		if (persister == null) {
			for (Entry<Class<?>, AssetPersister<?>> entry : persisters.entries()) {
				if (ClassReflection.isInstance(entry.key, asset)) {
					@SuppressWarnings("unchecked")
					AssetPersister<T> casted = (AssetPersister<T>) entry.value;
					persisters.put(asset.getClass(), casted);
					return casted;
				}
			}
		}

		return null;
	}
}
