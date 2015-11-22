package com.gurella.engine.graph.manager;

import java.util.Comparator;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.graph.GraphListenerSystem;
import com.gurella.engine.graph.SceneGraph;
import com.gurella.engine.graph.SceneNodeComponent;

public class ComponentManager extends GraphListenerSystem {
	private IntMap<Array<SceneNodeComponent>> components = new IntMap<Array<SceneNodeComponent>>();
	private IntMap<ObjectMap<ComponentGroupKey, ComponentGroup>> componentGroups = new IntMap<ObjectMap<ComponentGroupKey, ComponentGroup>>();

	private ComponentGroupKeyPool componentGroupKeyPool = new ComponentGroupKeyPool();
	private ComponentGroupPool componentGroupPool = new ComponentGroupPool();

	@Override
	public void componentActivated(SceneNodeComponent component) {
		int componentType = component.getComponentType();

		Array<SceneNodeComponent> componentsByType = components.get(componentType);
		if (componentsByType != null) {
			componentsByType.add(component);
		}

		ObjectMap<ComponentGroupKey, ComponentGroup> groupsByType = componentGroups.get(componentType);
		if (groupsByType != null) {
			for (ComponentGroup componentGroup : groupsByType.values()) {
				componentGroup.components.add(component);
				componentGroup.sort();
			}
		}
	}

	@Override
	public void componentDeactivated(SceneNodeComponent component) {
		int componentType = component.getComponentType();

		Array<SceneNodeComponent> array = components.get(componentType);
		if (array != null) {
			array.removeValue(component, true);
		}

		ObjectMap<ComponentGroupKey, ComponentGroup> groupsByType = componentGroups.get(componentType);
		if (groupsByType != null) {
			for (ComponentGroup componentGroup : groupsByType.values()) {
				componentGroup.components.removeValue(component, true);
			}
		}
	}

	@Override
	public void componentAdded(SceneNodeComponent component) {
	}

	@Override
	public void componentRemoved(SceneNodeComponent component) {
	}

	public <T extends SceneNodeComponent> Array<T> getComponents(Class<T> componentClass) {
		return this.<T> getComponents(SceneNodeComponent.getImplementationComponentType(componentClass));
	}

	@SuppressWarnings("unchecked")
	public <T extends SceneNodeComponent> Array<T> getComponents(int componentType) {
		return (Array<T>) components.get(componentType);
	}

	public Array<SceneNodeComponent> getComponents(Class<? extends SceneNodeComponent> componentClass,
			Comparator<SceneNodeComponent> comparator) {
		return getComponents(SceneNodeComponent.getImplementationComponentType(componentClass), comparator);
	}

	public Array<SceneNodeComponent> getComponents(int componentType, Comparator<SceneNodeComponent> comparator) {
		if (comparator == null) {
			return null;
		}

		ObjectMap<ComponentGroupKey, ComponentGroup> groupsByType = componentGroups.get(componentType);
		if (groupsByType == null) {
			return null;
		}

		ComponentGroupKey tempKey = componentGroupKeyPool.obtain(componentType, comparator);
		ComponentGroup componentGroup = groupsByType.get(tempKey);
		Array<SceneNodeComponent> groupComponents = componentGroup == null
				? null
				: componentGroup.components;
		componentGroupKeyPool.free(tempKey);
		return groupComponents;
	}

	public void registerComponentType(Class<? extends SceneNodeComponent> componentClass) {
		registerComponentType(SceneNodeComponent.getImplementationComponentType(componentClass));
	}

	public void registerComponentType(int componentType) {
		Array<SceneNodeComponent> componentsByType = components.get(componentType);

		if (componentsByType == null) {
			componentsByType = new Array<SceneNodeComponent>();
			components.put(componentType, componentsByType);
			SceneGraph graph = getGraph();
			for (SceneNodeComponent component : graph.activeComponents) {
				if (componentType == component.getComponentType()) {
					componentsByType.add(component);
				}
			}
		}
	}

	public void unregisterComponentType(Class<? extends SceneNodeComponent> componentClass) {
		unregisterComponentType(SceneNodeComponent.getImplementationComponentType(componentClass));
	}

	public void unregisterComponentType(int componentType) {
		components.remove(componentType);
	}

	public void registerComponentGroup(Class<? extends SceneNodeComponent> componentClass,
			Comparator<SceneNodeComponent> comparator) {
		registerComponentGroup(SceneNodeComponent.getImplementationComponentType(componentClass), comparator);
	}

	public void registerComponentGroup(int componentType, Comparator<SceneNodeComponent> comparator) {
		if (comparator == null) {
			return;
		}

		ObjectMap<ComponentGroupKey, ComponentGroup> groupsByType = componentGroups.get(componentType);
		if (groupsByType == null) {
			groupsByType = new ObjectMap<ComponentGroupKey, ComponentGroup>();
			componentGroups.put(componentType, new ObjectMap<ComponentGroupKey, ComponentGroup>());
		}

		ComponentGroupKey key = componentGroupKeyPool.obtain(componentType, comparator);
		ComponentGroup componentGroup = groupsByType.get(key);
		if (componentGroup == null) {
			componentGroup = componentGroupPool.obtain(key);
			groupsByType.put(key, componentGroup);
			SceneGraph graph = getGraph();
			for (SceneNodeComponent component : graph.activeComponents) {
				if (componentType == component.getComponentType()) {
					componentGroup.components.add(component);
				}
			}
			componentGroup.components.sort(comparator);
		} else {
			componentGroupKeyPool.free(key);
		}
	}

	public void unregisterComponentGroup(Class<? extends SceneNodeComponent> componentClass,
			Comparator<SceneNodeComponent> comparator) {
		unregisterComponentGroup(SceneNodeComponent.getImplementationComponentType(componentClass), comparator);
	}

	public void unregisterComponentGroup(int componentType, Comparator<SceneNodeComponent> comparator) {
		if (comparator == null) {
			return;
		}

		ObjectMap<ComponentGroupKey, ComponentGroup> groupsByType = componentGroups.get(componentType);
		if (groupsByType == null) {
			return;
		}

		ComponentGroupKey tempKey = componentGroupKeyPool.obtain(componentType, comparator);
		ComponentGroup group = groupsByType.remove(tempKey);
		if (group != null) {
			componentGroupKeyPool.free(group.key);
			group.key = null;
			componentGroupPool.free(group);
		}

		componentGroupKeyPool.free(tempKey);
	}

	private static class ComponentGroupKey implements Poolable {
		int componentType;
		Comparator<SceneNodeComponent> comparator;

		@Override
		public void reset() {
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = prime * comparator.hashCode();
			return prime * result + componentType;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ComponentGroupKey other = (ComponentGroupKey) obj;

			return componentType == other.componentType && comparator.equals(other.comparator);
		}
	}

	private static class ComponentGroupKeyPool extends Pool<ComponentGroupKey> {
		@Override
		protected ComponentGroupKey newObject() {
			return new ComponentGroupKey();
		}

		public ComponentGroupKey obtain(int componentType, Comparator<SceneNodeComponent> comparator) {
			ComponentGroupKey key = obtain();
			key.componentType = componentType;
			key.comparator = comparator;
			return key;
		}
	}

	public static class ComponentGroup implements Poolable {
		ComponentGroupKey key;
		Array<SceneNodeComponent> components = new Array<SceneNodeComponent>();

		void sort() {
			components.sort(key.comparator);
		}

		@Override
		public void reset() {
		}
	}

	private static class ComponentGroupPool extends Pool<ComponentGroup> {
		@Override
		protected ComponentGroup newObject() {
			return new ComponentGroup();
		}

		public ComponentGroup obtain(ComponentGroupKey key) {
			ComponentGroup componentGroup = obtain();
			componentGroup.key = key;
			return componentGroup;
		}
	}
}
