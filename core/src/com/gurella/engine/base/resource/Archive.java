package com.gurella.engine.base.resource;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;

//TODO unused
public class Archive<T> {
	private static final ObjectMap<Class<?>, ObjectSet<?>> archived = new ObjectMap<Class<?>, ObjectSet<?>>();

	T rootValue;
	ObjectSet<?> allValues;
}
