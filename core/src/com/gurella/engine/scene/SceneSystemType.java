package com.gurella.engine.scene;

import com.badlogic.gdx.utils.ObjectIntMap;
import com.gurella.engine.utils.Reflection;
import com.gurella.engine.utils.TypeRegistry;

//TODO test
public class SceneSystemType {
	private static final ObjectIntMap<Class<? extends SceneSystem2>> baseSystemTypes = new ObjectIntMap<Class<? extends SceneSystem2>>();
	private static final TypeRegistry<SceneSystem2> registry = new TypeRegistry<SceneSystem2>();

	private SceneSystemType() {
	}

	public static int getBaseSystemType(SceneSystem2 system) {
		return getBaseSystemType(system.getClass());
	}

	public static int getBaseSystemType(Class<? extends SceneSystem2> systemClass) {
		int type = baseSystemTypes.get(systemClass, TypeRegistry.invalidId);
		if (type != TypeRegistry.invalidId) {
			return type;
		}

		type = registry.findId(systemClass);
		if (type != TypeRegistry.invalidId) {
			return type;
		}

		Class<? extends SceneSystem2> baseSystemType = findBaseSystemType(systemClass);
		if (baseSystemType == null) {
			return registry.getId(systemClass);
		} else {
			type = registry.getId(baseSystemType);
			baseSystemTypes.put(systemClass, type);
			return type;
		}
	}

	private static Class<? extends SceneSystem2> findBaseSystemType(Class<? extends SceneSystem2> systemClass) {
		Class<?> temp = systemClass;
		while (temp != null && !SceneSystem2.class.equals(systemClass) && !Object.class.equals(systemClass)) {
			BaseSceneElement annotation = Reflection.getDeclaredAnnotation(temp, BaseSceneElement.class);
			if (annotation != null) {
				@SuppressWarnings("unchecked")
				Class<? extends SceneSystem2> casted = (Class<? extends SceneSystem2>) temp;
				return casted;
			}

			temp = temp.getSuperclass();
		}
		return null;
	}

	public static int getSystemType(SceneSystem2 system) {
		return getSystemType(system.getClass());
	}

	public static int getSystemType(Class<? extends SceneSystem2> systemClass) {
		int type = registry.findId(systemClass);
		if (type != TypeRegistry.invalidId) {
			return type;
		}

		Class<? extends SceneSystem2> baseSystemType = findBaseSystemType(systemClass);
		if (baseSystemType == null) {
			return registry.getId(systemClass);
		} else {
			int baseType = registry.getId(baseSystemType);
			baseSystemTypes.put(systemClass, baseType);
			return type;
		}
	}
}
