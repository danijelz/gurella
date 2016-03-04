package com.gurella.engine.resource;

import com.badlogic.gdx.utils.ObjectIntMap;
import com.gurella.engine.utils.TypeRegistry;
import com.gurella.engine.scene.BaseSceneElement;
import com.gurella.engine.utils.Reflection;

public abstract class SceneSystem extends SceneElement {
	private static final ObjectIntMap<Class<? extends SceneSystem>> baseSystemTypes = new ObjectIntMap<Class<? extends SceneSystem>>();
	private static TypeRegistry<SceneSystem> SYSTEM_TYPE_INDEXER = new TypeRegistry<SceneSystem>();

	public final int baseSystemType;
	public final int systemType;

	public SceneSystem() {
		baseSystemType = getBaseSystemType(getClass());
		systemType = SYSTEM_TYPE_INDEXER.getId(getClass());
	}

	public static int getBaseSystemType(SceneSystem system) {
		return getBaseSystemType(system.getClass());
	}

	public static int getBaseSystemType(Class<? extends SceneSystem> systemClass) {
		int type = baseSystemTypes.get(systemClass, -1);
		if (type != -1) {
			return type;
		}

		type = SYSTEM_TYPE_INDEXER.findId(systemClass);
		if (type != -1) {
			return type;
		}

		Class<? extends SceneSystem> baseSystemType = findBaseSystemType(systemClass);
		if (baseSystemType == null) {
			return SYSTEM_TYPE_INDEXER.getId(systemClass);
		} else {
			type = SYSTEM_TYPE_INDEXER.getId(baseSystemType);
			baseSystemTypes.put(systemClass, type);
			return type;
		}
	}

	private static Class<? extends SceneSystem> findBaseSystemType(Class<? extends SceneSystem> systemClass) {
		Class<?> temp = systemClass;
		while (temp != null && !SceneSystem.class.equals(systemClass) && !Object.class.equals(systemClass)) {
			BaseSceneElement annotation = Reflection.getDeclaredAnnotation(temp, BaseSceneElement.class);
			if (annotation != null) {
				@SuppressWarnings("unchecked")
				Class<? extends SceneSystem> casted = (Class<? extends SceneSystem>) temp;
				return casted;
			}

			temp = temp.getSuperclass();
		}
		return null;
	}

	public static int getSystemType(SceneSystem system) {
		return getSystemType(system.getClass());
	}

	public static int getSystemType(Class<? extends SceneSystem> systemClass) {
		int type = SYSTEM_TYPE_INDEXER.findId(systemClass);
		if (type != -1) {
			return type;
		}

		Class<? extends SceneSystem> baseSystemType = findBaseSystemType(systemClass);
		if (baseSystemType == null) {
			return SYSTEM_TYPE_INDEXER.getId(systemClass);
		} else {
			int baseType = SYSTEM_TYPE_INDEXER.getId(baseSystemType);
			baseSystemTypes.put(systemClass, baseType);
			return type;
		}
	}

	public int getBaseSystemType() {
		return baseSystemType;
	}

	public int getSystemType() {
		return systemType;
	}

	@Override
	final void activate() {
		if (scene != null) {
			//scene.activateSystem(this);
		}
	}

	@Override
	final void deactivate() {
		if (scene != null) {
			//scene.deactivateSystem(this);
		}
	}

	@Override
	public final void detach() {
		if (scene != null) {
			//scene.removeSystem(this);
		}
	}

	@Override
	public final void reset() {
		clearSignals();
		initialized = false;
	}

	@Override
	public final void dispose() {
		detach();
		INDEXER.remove(this);
	}

	@Override
	public final void setEnabled(boolean enabled) {
		if (this.enabled != enabled) {
			this.enabled = enabled;
			if (!enabled && active) {
				deactivate();
			} else if (enabled && !active) {
				activate();
			}
		}
	}
}
