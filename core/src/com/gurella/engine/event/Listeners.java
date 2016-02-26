package com.gurella.engine.event;

import static com.gurella.engine.utils.Reflection.getAnnotation;

import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.ObjectMap;

class Listeners {
	private static final ObjectMap<Class<?>, ObjectIntMap<Class<?>>> priorities = new ObjectMap<Class<?>, ObjectIntMap<Class<?>>>();

	static void initListenerPriotiy(Class<?> eventType, Class<?> listenerType) {
		ObjectIntMap<Class<?>> prioritiesByEvent = priorities.get(eventType);
		if (prioritiesByEvent == null) {
			prioritiesByEvent = new ObjectIntMap<Class<?>>();
		}

		if (prioritiesByEvent.containsKey(listenerType)) {
			return;
		}

		TypePriority listenerPriority = findListenerPriority(eventType, listenerType);
		if (listenerPriority != null) {
			prioritiesByEvent.put(listenerType, listenerPriority.priority());
			return;
		}

		Priority priority = findPriority(listenerType);
		prioritiesByEvent.put(listenerType, priority == null ? 0 : priority.value());
	}

	private static TypePriority findListenerPriority(Class<?> eventType, Class<?> listenerType) {
		Class<?> type = listenerType;
		while (type != null && type != Object.class) {
			TypePriorities typePriorities = getAnnotation(listenerType, TypePriorities.class);
			if (typePriorities != null) {
				TypePriority listenerPriority = findListenerPriority(typePriorities, eventType);
				if (listenerPriority != null) {
					return listenerPriority;
				}
			}
			type = type.getSuperclass();
		}
		return null;
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

	private static Priority findPriority(Class<?> listenerType) {
		Class<?> type = listenerType;
		while (type != null && type != Object.class) {
			Priority priority = getAnnotation(type, Priority.class);
			if (priority == null) {
				return priority;
			}
			type = type.getSuperclass();
		}
		return null;
	}

	static int getPriority(Class<?> eventType, Class<?> listenerType) {
		ObjectIntMap<Class<?>> prioritiesByEvent = priorities.get(eventType);
		return prioritiesByEvent.get(listenerType, 0);
	}
}
