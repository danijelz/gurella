package com.gurella.engine.base.model;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.utils.Reflection;

public class ModelDefaults {
	private static final ObjectMap<Class<?>, Object> defaults = new ObjectMap<Class<?>, Object>();

	public static <T> T getDefault(Class<T> type) {
		if (defaults.containsKey(type)) {
			@SuppressWarnings("unchecked")
			T casted = (T) defaults.get(type);
			return casted;
		}

		T defaultValue = Reflection.newInstanceSilently(type);
		defaults.put(type, defaultValue);
		return defaultValue;
	}

	public static <T, P> P getDefault(Class<T> type, String propertyName) {
		T defaultModelInstance = getDefault(type);
		if (defaultModelInstance == null) {
			return null;
		}

		Model<T> model = Models.getModel(type);
		Property<P> property = model.getProperty(propertyName);
		if (property == null) {
			throw new GdxRuntimeException("Invalid property.");
		}

		return property.getValue(defaultModelInstance);
	}

	public static <T, P> P getDefault(Class<T> type, Property<P> property) {
		T defaultModelInstance = getDefault(type);
		if (defaultModelInstance == null) {
			return null;
		}
		return property.getValue(defaultModelInstance);
	}
}
