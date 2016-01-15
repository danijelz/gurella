package com.gurella.engine.base.model;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;

public class GdxArrayModelResolver implements ModelResolver {
	public static final GdxArrayModelResolver instance = new GdxArrayModelResolver();

	private static final ObjectMap<Class<?>, GdxArrayModel<?>> modelsByType = new ObjectMap<Class<?>, GdxArrayModel<?>>();

	private GdxArrayModelResolver() {
	}

	@Override
	public <T> Model<T> resolve(Class<T> type) {
		synchronized (modelsByType) {
			GdxArrayModel<?> instance = modelsByType.get(type);
			if (instance == null && ClassReflection.isAssignableFrom(Array.class, type)) {
				@SuppressWarnings({ "rawtypes", "unchecked" })
				GdxArrayModel<?> raw = new GdxArrayModel(type);
				instance = raw;
				modelsByType.put(type, instance);
			}
			@SuppressWarnings("unchecked")
			Model<T> casted = (Model<T>) instance;
			return casted;
		}
	}
}
