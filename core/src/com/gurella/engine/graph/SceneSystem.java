package com.gurella.engine.graph;

import com.badlogic.gdx.utils.ObjectIntMap;
import com.gurella.engine.utils.IndexedType;
import com.gurella.engine.utils.ReflectionUtils;

public abstract class SceneSystem extends SceneGraphElement {
	private static final ObjectIntMap<Class<? extends SceneSystem>> baseSystemTypes = new ObjectIntMap<Class<? extends SceneSystem>>(); 
	private static IndexedType<SceneSystem> SYSTEM_TYPE_INDEXER = new IndexedType<SceneSystem>();

	public final int systemType;
	public final int implementationSystemType;

	public SceneSystem() {
		systemType = getSystemType(getClass());
		implementationSystemType = SYSTEM_TYPE_INDEXER.getType(getClass());
	}

	public static int getSystemType(SceneSystem system) {
		return getSystemType(system.getClass());
	}

	public static int getSystemType(Class<? extends SceneSystem> systemClass) {
		int type = baseSystemTypes.get(systemClass, -1);
		if (type != -1) {
			return type;
		}
		
		type = SYSTEM_TYPE_INDEXER.findType(systemClass, -1);
		if (type != -1) {
			return type;
		}
		
		Class<? extends SceneSystem> baseSystemType = findBaseSystemType(systemClass);
		if(baseSystemType == null) {
			return SYSTEM_TYPE_INDEXER.getType(systemClass);
		} else {
			type = SYSTEM_TYPE_INDEXER.getType(baseSystemType);
			baseSystemTypes.put(systemClass, type);
			return type;
		}
	}

	private static Class<? extends SceneSystem> findBaseSystemType(Class<? extends SceneSystem> systemClass) {
		Class<?> temp = systemClass;
		while (temp != null && !SceneSystem.class.equals(systemClass) && !Object.class.equals(systemClass)) {
			BaseSceneElementType annotation = ReflectionUtils.getDeclaredAnnotation(temp, BaseSceneElementType.class);
			if(annotation != null) {
				@SuppressWarnings("unchecked")
				Class<? extends SceneSystem> casted = (Class<? extends SceneSystem>) temp;
				return casted;
			}
			
			temp = temp.getSuperclass();
		}
		return null;
	}
	
	public static int getImplementationSystemType(SceneSystem system) {
		return getImplementationSystemType(system.getClass());
	}
	
	public static int getImplementationSystemType(Class<? extends SceneSystem> systemClass) {
		int type = SYSTEM_TYPE_INDEXER.findType(systemClass, -1);
		if (type != -1) {
			return type;
		}
		
		Class<? extends SceneSystem> baseSystemType = findBaseSystemType(systemClass);
		if(baseSystemType == null) {
			return SYSTEM_TYPE_INDEXER.getType(systemClass);
		} else {
			int baseType = SYSTEM_TYPE_INDEXER.getType(baseSystemType);
			baseSystemTypes.put(systemClass, baseType);
			return type;
		}
	}

	public int getSystemType() {
		return systemType;
	}
	
	public int getImplementationSystemType() {
		return implementationSystemType;
	}

	@Override
	final void activate() {
		if (graph != null) {
			graph.activateSystem(this);
		}
	}

	@Override
	final void deactivate() {
		if (graph != null) {
			graph.deactivateSystem(this);
		}
	}

	@Override
	public final void detach() {
		if (graph != null) {
			graph.removeSystem(this);
		}
	}

	@Override
	public final void reset() {
		resettedSignal.dispatch();
		clearSignals();
		initialized = false;
	}

	@Override
	public final void dispose() {
		detach();
		disposedSignal.dispatch();
		INDEXER.removeIndexed(this);
	}
	
	@Override
	public final void setEnabled(boolean enabled) {
		if(this.enabled == enabled) {
			return;
		} else {
			this.enabled = enabled;
			if(!enabled && active) {
				deactivate();
			} else if(enabled && !active) {
				activate();
			}
		}
	}
}
