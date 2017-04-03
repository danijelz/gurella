package com.gurella.engine.utils;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.metatype.MetaType;
import com.gurella.engine.metatype.MetaTypes;
import com.gurella.engine.metatype.Property;

public class DefaultInstances {
	private static final ObjectMap<Class<?>, Object> defaults = new ObjectMap<Class<?>, Object>();

	private DefaultInstances() {
	}

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
		T defaultBean = getDefault(type);
		if (defaultBean == null) {
			return null;
		}

		MetaType<T> metaType = MetaTypes.getMetaType(type);
		Property<P> property = metaType.getProperty(propertyName);
		if (property == null) {
			throw new GdxRuntimeException("Invalid property.");
		}

		return property.getValue(defaultBean);
	}

	public static <T, P> P getDefault(Class<T> type, Property<P> property) {
		T defaultBean = getDefault(type);
		return defaultBean == null ? null : property.getValue(defaultBean);
	}
}
