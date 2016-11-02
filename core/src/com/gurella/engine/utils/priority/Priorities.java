package com.gurella.engine.utils.priority;

import static com.gurella.engine.utils.Reflection.getAnnotation;

import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.ObjectMap;

public class Priorities {
	private static final ObjectIntMap<Class<?>> globalPriorities = new ObjectIntMap<Class<?>>();
	private static final ObjectMap<Class<?>, ObjectIntMap<Class<?>>> interfacePriorities = new ObjectMap<Class<?>, ObjectIntMap<Class<?>>>();

	static {
		globalPriorities.put(Object.class, 0);
	}

	public static int getPriority(Object obj) {
		return getPriority(obj.getClass());
	}

	public static int getPriority(Class<?> type) {
		if (!globalPriorities.containsKey(type)) {
			synchronized (globalPriorities) {
				initPriorities(type);
			}
		}

		return globalPriorities.get(type, 0);
	}

	public static int getPriority(Object obj, Class<?> interfaceType) {
		return getPriority(obj.getClass(), interfaceType);
	}

	public static int getPriority(Class<?> type, Class<?> interfaceType) {
		if (!globalPriorities.containsKey(type)) {
			synchronized (globalPriorities) {
				initPriorities(type);
			}
		}

		ObjectIntMap<Class<?>> priorities = interfacePriorities.get(type);
		return priorities == null ? 0 : priorities.get(interfaceType, 0);
	}

	private static void initPriorities(Class<?> type) {
		if (globalPriorities.containsKey(type)) {
			return;
		}

		Class<?> superType = type.getSuperclass();
		initPriorities(superType);
		copyPriorities(type, superType);

		TypePriorities typePriorities = getAnnotation(type, TypePriorities.class);
		if (typePriorities != null) {
			ObjectIntMap<Class<?>> priorities = getInterfacePriorities(type);
			TypePriority[] values = typePriorities.value();
			for (int i = 0; i < values.length; i++) {
				TypePriority typePriority = values[i];
				priorities.put(typePriority.type(), typePriority.priority());
			}
		}

		TypePriority typePriority = getAnnotation(type, TypePriority.class);
		if (typePriority != null) {
			ObjectIntMap<Class<?>> priorities = getInterfacePriorities(type);
			priorities.put(typePriority.type(), typePriority.priority());
		}

		Priority priority = getAnnotation(type, Priority.class);
		globalPriorities.put(type, priority == null ? 0 : priority.value());
	}

	private static ObjectIntMap<Class<?>> getInterfacePriorities(Class<?> type) {
		ObjectIntMap<Class<?>> priorities = interfacePriorities.get(type);
		if (priorities == null) {
			priorities = new ObjectIntMap<Class<?>>();
			interfacePriorities.put(type, priorities);
		}
		return priorities;
	}

	private static void copyPriorities(Class<?> type, Class<?> superType) {
		globalPriorities.put(type, globalPriorities.get(superType, 0));
		ObjectIntMap<Class<?>> priorities = interfacePriorities.get(superType);
		if (priorities != null) {
			interfacePriorities.put(type, new ObjectIntMap<Class<?>>(priorities));
		}
	}
}
