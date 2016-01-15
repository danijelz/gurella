package com.gurella.engine.base.model;

import java.util.Collection;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;

public class CollectionModelResolver implements ModelResolver {
	public static final CollectionModelResolver instance = new CollectionModelResolver();

	private static final ObjectMap<Class<?>, CollectionModel<?>> modelsByType = new ObjectMap<Class<?>, CollectionModel<?>>();

	private CollectionModelResolver() {
	}

	@Override
	public <T> Model<T> resolve(Class<T> type) {
		synchronized (modelsByType) {
			CollectionModel<?> instance = modelsByType.get(type);
			if (instance == null && ClassReflection.isAssignableFrom(Collection.class, type)) {
				@SuppressWarnings({ "rawtypes", "unchecked" })
				CollectionModel<?> raw = new CollectionModel(type);
				instance = raw;
				modelsByType.put(type, instance);
			}
			@SuppressWarnings("unchecked")
			Model<T> casted = (Model<T>) instance;
			return casted;
		}
	}
}
