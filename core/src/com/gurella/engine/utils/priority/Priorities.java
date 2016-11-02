package com.gurella.engine.utils.priority;

import static com.gurella.engine.utils.Reflection.getAnnotation;

import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.ObjectMap;

public class Priorities {
	private static final ObjectIntMap<Class<?>> globalPriorities = new ObjectIntMap<Class<?>>();
	private static final ObjectMap<Class<?>, ObjectIntMap<Class<?>>> interfacePriorities = new ObjectMap<Class<?>, ObjectIntMap<Class<?>>>();

	public static int getPriority(Object obj) {
		return getPriority(obj.getClass());
	}

	public static int getPriority(Class<?> objType) {
		if (globalPriorities.containsKey(objType)) {
			return globalPriorities.get(objType, 0);
		}
		return initPriorities(objType, null);
	}

	public static int getPriority(Object obj, Class<?> interfaceType) {
		return getPriority(obj.getClass(), interfaceType);
	}

	public static int getPriority(Class<?> objType, Class<?> interfaceType) {
		ObjectIntMap<Class<?>> prioritiesByEvent = interfacePriorities.get(objType);
		if (prioritiesByEvent == null) {
			prioritiesByEvent = new ObjectIntMap<Class<?>>();
			interfacePriorities.put(objType, prioritiesByEvent);
		}

		if (prioritiesByEvent.containsKey(interfaceType)) {
			return prioritiesByEvent.get(interfaceType, 0);
		}

		Class<?> type = interfaceType;
		while (type != null && type != Object.class) {
			ObjectIntMap<Class<?>> prioritiesByType = interfacePriorities.get(objType);
			if (prioritiesByType != null && prioritiesByType.containsKey(type)) {
				int value = prioritiesByType.get(interfaceType, 0);
				prioritiesByEvent.put(interfaceType, value);
				return value;
			}

			TypePriority typePriority = getAnnotation(type, TypePriority.class);
			if (typePriority != null && objType.getClass().equals(typePriority.type())) {
				int value = typePriority.priority();
				prioritiesByEvent.put(interfaceType, value);
				return value;
			}

			TypePriorities typePriorities = getAnnotation(type, TypePriorities.class);
			if (typePriorities != null) {
				TypePriority listenerPriority = findListenerPriority(typePriorities, objType);
				if (listenerPriority != null) {
					int value = listenerPriority.priority();
					prioritiesByEvent.put(interfaceType, value);
					return value;
				}
			}
			type = type.getSuperclass();
		}

		type = interfaceType;
		while (type != null && type != Object.class) {
			Priority priority = getAnnotation(type, Priority.class);
			if (priority != null) {
				int value = priority.value();
				prioritiesByEvent.put(interfaceType, value);
				globalPriorities.put(objType, value);
				return value;
			}
			type = type.getSuperclass();
		}

		prioritiesByEvent.put(interfaceType, 0);
		return 0;
	}

	private static TypePriority findListenerPriority(TypePriorities typePriorities, Class<?> eventType) {
		if (typePriorities == null) {
			return null;
		}

		TypePriority[] values = typePriorities.value();
		for (int i = 0; i < values.length; i++) {
			TypePriority typePriority = values[i];
			if (eventType.getClass().equals(typePriority.type())) {
				return typePriority;
			}
		}

		return null;
	}

	private static int initPriorities(Class<?> objType, Class<?> interfaceType) {
		return 0;
	}
}
