package com.gurella.engine.base.metamodel;

import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.utils.ReflectionUtils;

public class Defaults {
	private static final ObjectMap<Class<?>, Object> defaults = new ObjectMap<Class<?>, Object>();

	public static <T> T getDefault(Class<T> type) {
		if (defaults.containsKey(type)) {
			@SuppressWarnings("unchecked")
			T casted = (T) defaults.get(type);
			return casted;
		}

		T defaultValue = ReflectionUtils.newInstanceSilently(type);
		defaults.put(type, defaultValue);
		return defaultValue;
	}
}
