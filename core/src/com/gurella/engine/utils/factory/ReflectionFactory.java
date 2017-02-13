package com.gurella.engine.utils.factory;

import com.badlogic.gdx.utils.reflect.Constructor;
import com.gurella.engine.utils.Reflection;

public class ReflectionFactory<T> implements Factory<T> {
	private final Constructor constructor;

	public ReflectionFactory(Class<T> type) {
		this.constructor = Reflection.getDeclaredConstructor(type, (Class[]) null);
		if (constructor == null) {
			throw new RuntimeException("Missing no-arg constructor: " + type.getName());
		}
	}

	@Override
	public T create() {
		try {
			@SuppressWarnings("unchecked")
			T result = (T) constructor.newInstance((Object[]) null);
			return result;
		} catch (Exception e) {
			return null;
		}
	}
}
