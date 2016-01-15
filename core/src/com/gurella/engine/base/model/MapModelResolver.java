package com.gurella.engine.base.model;

import java.util.Map;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;

public class MapModelResolver implements ModelResolver {
	public static final MapModelResolver instance = new MapModelResolver();

	private static final ObjectMap<Class<?>, MapModel<?>> modelsByType = new ObjectMap<Class<?>, MapModel<?>>();

	private MapModelResolver() {
	}

	@Override
	public <T> Model<T> resolve(Class<T> type) {
		synchronized (modelsByType) {
			MapModel<?> instance = modelsByType.get(type);
			if (instance == null && ClassReflection.isAssignableFrom(Map.class, type)) {
				@SuppressWarnings({ "rawtypes", "unchecked" })
				MapModel<?> raw = new MapModel(type);
				instance = raw;
				modelsByType.put(type, instance);
			}
			@SuppressWarnings("unchecked")
			Model<T> casted = (Model<T>) instance;
			return casted;
		}
	}
}
