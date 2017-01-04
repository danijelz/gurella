package com.gurella.engine.utils.priority;

import static com.gurella.engine.utils.Reflection.getAnnotation;

import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.ObjectMap;

public class PriorityManager {
	private static final ObjectIntMap<Class<?>> globalPriorities = new ObjectIntMap<Class<?>>();
	private static final ObjectMap<Class<?>, ObjectIntMap<Class<?>>> typePriorities = new ObjectMap<Class<?>, ObjectIntMap<Class<?>>>();

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

		ObjectIntMap<Class<?>> priorities = typePriorities.get(type);
		if (priorities != null && priorities.containsKey(interfaceType)) {
			return priorities.get(interfaceType, 0);
		} else {
			return globalPriorities.get(type, 0);
		}
	}

	private static void initPriorities(Class<?> type) {
		if (globalPriorities.containsKey(type)) {
			return;
		}

		Class<?> superType = type.getSuperclass();
		initPriorities(superType);
		copyPriorities(type, superType);

		Priorities priorities = getAnnotation(type, Priorities.class);
		if (priorities != null) {
			Priority[] values = priorities.value();
			for (int i = 0, n = values.length; i < n; i++) {
				appendPriority(type, values[i]);
			}
		}

		Priority priority = getAnnotation(type, Priority.class);
		if (priority != null) {
			appendPriority(type, priority);
		}

		if (!globalPriorities.containsKey(type)) {
			globalPriorities.put(type, 0);
		}
	}

	private static void appendPriority(Class<?> type, Priority priority) {
		Class<?> priorityType = priority.type();
		if (priorityType == null || Object.class == priorityType) {
			globalPriorities.put(priorityType, priority.value());
		} else {
			getTypePriorities(type).put(priorityType, priority.value());
		}
	}

	private static ObjectIntMap<Class<?>> getTypePriorities(Class<?> type) {
		ObjectIntMap<Class<?>> priorities = typePriorities.get(type);
		if (priorities == null) {
			priorities = new ObjectIntMap<Class<?>>();
			typePriorities.put(type, priorities);
		}
		return priorities;
	}

	private static void copyPriorities(Class<?> type, Class<?> superType) {
		globalPriorities.put(type, globalPriorities.get(superType, 0));
		ObjectIntMap<Class<?>> priorities = typePriorities.get(superType);
		if (priorities != null) {
			typePriorities.put(type, new ObjectIntMap<Class<?>>(priorities));
		}
	}
}
