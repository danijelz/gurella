package com.gurella.engine.utils.adapter;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.gurella.engine.utils.ImmutableArray;

public class Adapters {
	private static final ObjectMap<Class<?>, ObjectMap<Class<?>, AdapterFactory>> factories = new ObjectMap<Class<?>, ObjectMap<Class<?>, AdapterFactory>>();

	private Adapters() {
	}

	public static <T> T adapt(Object source, Class<T> adapter) {
		if (source == null) {
			return null;
		}

		if (ClassReflection.isInstance(adapter, source)) {
			@SuppressWarnings("unchecked")
			T casted = (T) source;
			return casted;
		}

		if (source instanceof Adaptable) {
			Adaptable adaptable = (Adaptable) source;
			T result = adaptable.getAdapter(adapter);
			if (result != null) {
				if (!ClassReflection.isInstance(adapter, result)) {
					throw new IllegalStateException();
				}
				@SuppressWarnings("cast")
				T casted = (T) result;
				return casted;
			}
		}

		Class<?> adaptable = source.getClass();
		ObjectMap<Class<?>, AdapterFactory> adaptableFactories = factories.get(adaptable);
		if (adaptableFactories == null) {
			return null;
		}

		AdapterFactory factory = adaptableFactories.get(adapter);
		if (factory == null) {
			return null;
		}

		T result = factory.getAdapter(source, adapter);
		if (result == null || ClassReflection.isInstance(adapter, result)) {
			return result;
		} else {
			throw new IllegalStateException();
		}
	}

	public void registerAdapters(AdapterFactory factory, Class<?> adaptable) {
		ObjectMap<Class<?>, AdapterFactory> adaptableFactories = factories.get(adaptable);
		if (adaptableFactories == null) {
			adaptableFactories = new ObjectMap<Class<?>, AdapterFactory>();
			factories.put(adaptable, adaptableFactories);
		}

		ImmutableArray<Class<?>> adapters = factory.getAdapterList();
		for (int i = 0, n = adapters.size(); i < n; i++) {
			adaptableFactories.put(adapters.get(i), factory);
		}
	}

	public void unregisterAdapters(AdapterFactory factory, Class<?> adaptable) {
		ObjectMap<Class<?>, AdapterFactory> adaptableFactories = factories.get(adaptable);
		if (adaptableFactories == null) {
			return;
		}

		ImmutableArray<Class<?>> adapters = factory.getAdapterList();
		for (int i = 0, n = adapters.size(); i < n; i++) {
			Class<?> type = adapters.get(1);
			if (adaptableFactories.get(type) == factory) {
				adaptableFactories.remove(type);
			}
		}
	}
}
