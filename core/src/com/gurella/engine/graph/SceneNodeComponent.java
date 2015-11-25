package com.gurella.engine.graph;

import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.IntMap.Values;
import com.gurella.engine.graph.script.ScriptComponent;
import com.gurella.engine.utils.IndexedType;
import com.gurella.engine.utils.ReflectionUtils;

public class SceneNodeComponent extends SceneGraphElement {
	private static final ObjectIntMap<Class<? extends SceneNodeComponent>> baseComponentTypes = new ObjectIntMap<Class<? extends SceneNodeComponent>>(); 
	private static final IndexedType<SceneNodeComponent> COMPONENT_TYPE_INDEXER = new IndexedType<SceneNodeComponent>();

	public final int baseComponentType;
	public final int componentType;
	SceneNode node;

	public SceneNodeComponent() {
		baseComponentType = getBaseComponentType(getClass());
		componentType = COMPONENT_TYPE_INDEXER.getType(getClass());
	}

	public static int getBaseComponentType(SceneNodeComponent component) {
		return getBaseComponentType(component.getClass());
	}

	public static int getBaseComponentType(Class<? extends SceneNodeComponent> componentClass) {
		int type = baseComponentTypes.get(componentClass, -1);
		if (type != -1) {
			return type;
		}
		
		type = COMPONENT_TYPE_INDEXER.findType(componentClass, -1);
		if (type != -1) {
			return type;
		}
		
		Class<? extends SceneNodeComponent> baseComponentType = findBaseComponentType(componentClass);
		if(baseComponentType == null) {
			return COMPONENT_TYPE_INDEXER.getType(componentClass);
		} else {
			type = COMPONENT_TYPE_INDEXER.getType(baseComponentType);
			baseComponentTypes.put(componentClass, type);
			return type;
		}
	}

	private static Class<? extends SceneNodeComponent> findBaseComponentType(Class<? extends SceneNodeComponent> componentClass) {
		Class<?> temp = componentClass;
		while (temp != null && !SceneNodeComponent.class.equals(componentClass) && !ScriptComponent.class.equals(componentClass) && !Object.class.equals(componentClass)) {
			BaseSceneElementType annotation = ReflectionUtils.getDeclaredAnnotation(temp, BaseSceneElementType.class);
			if(annotation != null) {
				@SuppressWarnings("unchecked")
				Class<? extends SceneNodeComponent> casted = (Class<? extends SceneNodeComponent>) temp;
				return casted;
			}
			
			temp = temp.getSuperclass();
		}
		return null;
	}
	
	public static int getComponentType(SceneNodeComponent component) {
		return getComponentType(component.getClass());
	}
	
	public static int getComponentType(Class<? extends SceneNodeComponent> componentClass) {
		int type = COMPONENT_TYPE_INDEXER.findType(componentClass, -1);
		if (type != -1) {
			return type;
		}
		
		Class<? extends SceneNodeComponent> baseComponentType = findBaseComponentType(componentClass);
		if(baseComponentType == null) {
			return COMPONENT_TYPE_INDEXER.getType(componentClass);
		} else {
			int baseType = COMPONENT_TYPE_INDEXER.getType(baseComponentType);
			baseComponentTypes.put(componentClass, baseType);
			return type;
		}
	}

	public static Bits getBitsFor(Class<? extends SceneNodeComponent>... componentClasses) {
		Bits bits = new Bits();

		for (int i = 0; i < componentClasses.length; i++) {
			bits.set(getBaseComponentType(componentClasses[i]));
		}

		return bits;
	}

	public static Bits getBitsFor(SceneNodeComponent... components) {
		Bits bits = new Bits();

		for (int i = 0; i < components.length; i++) {
			bits.set(getBaseComponentType(components[i]));
		}

		return bits;
	}

	public SceneNode getNode() {
		return node;
	}

	@Override
	final void activate() {
		if (graph != null) {
			graph.activateComponent(this);
		}
	}

	@Override
	final void deactivate() {
		if (graph != null) {
			graph.deactivateComponent(this);
		}
	}

	@Override
	public final void detach() {
		if (graph != null) {
			graph.removeComponent(this);
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
	
	public final boolean isHierarchyEnabled() {
		return this.enabled && node != null && node.isHierarchyEnabled();
	}
	
	@Override
	public final void setEnabled(boolean enabled) {
		if(this.enabled == enabled) {
			return;
		} else {
			this.enabled = enabled;
			boolean hierarchyEnabled = isHierarchyEnabled();
			if(!hierarchyEnabled && active) {
				deactivate();
			} else if(hierarchyEnabled && !active) {
				activate();
			}
		}
	}
	
	public <T extends SceneNodeComponent> T getNodeComponent(Class<T> componentClass) {
		return node.getComponent(componentClass);
	}

	public <T extends SceneNodeComponent> T getActiveNodeComponent(Class<T> componentClass) {
		return node.getActiveComponent(componentClass);
	}

	public <T extends SceneNodeComponent> T getNodeComponent(int nodeComponentType) {
		return node.getComponent(nodeComponentType);
	}

	public <T extends SceneNodeComponent> T getActiveNodeComponent(int nodeComponentType) {
		return node.getActiveComponent(nodeComponentType);
	}

	public Values<SceneNodeComponent> getNodeComponents() {
		return node.getComponents();
	}
}
