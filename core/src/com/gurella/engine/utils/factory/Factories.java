package com.gurella.engine.utils.factory;

import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.utils.Reflection;
import com.gurella.engine.utils.Values;

public final class Factories {
	private static final ObjectMap<Class<?>, Factory<?>> factoriesByType = new ObjectMap<Class<?>, Factory<?>>();

	private Factories() {
	}

	public static <T> Factory<T> getFactory(Class<T> type) {
		if (!factoriesByType.containsKey(type)) {
			FactoryDescriptor descriptor = Reflection.getAnnotation(type, FactoryDescriptor.class);
			if (descriptor == null) {
				factoriesByType.put(type, null);
				return null;
			} else {
				Factory<T> factory = Values.cast(Reflection.newInstance(descriptor.value()));
				factoriesByType.put(type, factory);
			}
		}
		return Values.cast(factoriesByType.get(type));
	}
}
