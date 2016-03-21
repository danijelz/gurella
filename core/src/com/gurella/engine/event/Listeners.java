package com.gurella.engine.event;

import static com.gurella.engine.utils.Reflection.getAnnotation;

import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.ObjectMap;

class Listeners {
	private static final ObjectMap<Class<?>, ObjectIntMap<Class<?>>> priorities = new ObjectMap<Class<?>, ObjectIntMap<Class<?>>>();

	static int getPriority(Class<?> eventType, Class<?> listenerType) {
		ObjectIntMap<Class<?>> prioritiesByEvent = priorities.get(eventType);
		if (prioritiesByEvent == null) {
			prioritiesByEvent = new ObjectIntMap<Class<?>>();
		}

		if (prioritiesByEvent.containsKey(listenerType)) {
			return prioritiesByEvent.get(listenerType, 0);
		}

		Class<?> type = listenerType;
		while (type != null && type != Object.class) {
			ObjectIntMap<Class<?>> prioritiesByType = priorities.get(eventType);
			if (prioritiesByType != null && prioritiesByType.containsKey(type)) {
				int value = prioritiesByType.get(listenerType, 0);
				prioritiesByEvent.put(listenerType, value);
				return value;
			}

			TypePriority typePriority = getAnnotation(type, TypePriority.class);
			if (typePriority != null && eventType.getClass().equals(typePriority.type())) {
				int value = typePriority.priority();
				prioritiesByEvent.put(listenerType, value);
				return value;
			}

			TypePriorities typePriorities = getAnnotation(type, TypePriorities.class);
			if (typePriorities != null) {
				TypePriority listenerPriority = findListenerPriority(typePriorities, eventType);
				if (listenerPriority != null) {
					int value = listenerPriority.priority();
					prioritiesByEvent.put(listenerType, value);
					return value;
				}
			}
			type = type.getSuperclass();
		}

		type = listenerType;
		while (type != null && type != Object.class) {
			Priority priority = getAnnotation(type, Priority.class);
			if (priority != null) {
				int value = priority.value();
				prioritiesByEvent.put(listenerType, value);
				return value;
			}
			type = type.getSuperclass();
		}

		prioritiesByEvent.put(listenerType, 0);
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
}
