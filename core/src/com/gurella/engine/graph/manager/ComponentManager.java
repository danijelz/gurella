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
	private IntMap<FamilyComponents<?>> families = new IntMap<FamilyComponents<?>>();

	@Override
	public void componentActivated(SceneNodeComponent component) {
		for (FamilyComponents<?> familyComponents : families.values()) {
			familyComponents.add(component);
		}
	}

	@Override
	public void componentDeactivated(SceneNodeComponent component) {
		for (FamilyComponents<?> familyComponents : families.values()) {
			familyComponents.remove(component);
		}
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
		Array<SceneNodeComponent> groupComponents = componentGroup == null ? null : componentGroup.components;
		componentGroupKeyPool.free(tempKey);
		return groupComponents;
	}

	public <T extends SceneNodeComponent> void registerComponentFamily(ComponentFamily<T> family) {
		if (families.containsKey(family.id)) {
			return;
		}

		FamilyComponents<T> familyComponents = new FamilyComponents<T>();
		families.put(family.id, familyComponents);

		Array<SceneNodeComponent> components = getGraph().allComponents;
		for (int i = 0; i < components.size; i++) {
			familyComponents.add(components.get(i));
		}
	}

	public void unregisterComponentType(Class<? extends SceneNodeComponent> componentClass) {
		unregisterComponentType(SceneNodeComponent.getComponentType(componentClass));
	}

	public void unregisterComponentType(int componentType) {
		components.remove(componentType);
	}

	public void registerComponentGroup(Class<? extends SceneNodeComponent> componentClass,
			Comparator<SceneNodeComponent> comparator) {
		registerComponentGroup(SceneNodeComponent.getComponentType(componentClass), comparator);
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
		unregisterComponentGroup(SceneNodeComponent.getComponentType(componentClass), comparator);
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

	public static abstract class ComponentFamily<T extends SceneNodeComponent> {
		private static int INDEXER = 0;

		public final int id;
		public final Comparator<? super T> comparator;

		public ComponentFamily() {
			this.id = INDEXER++;
			comparator = null;
		}

		public ComponentFamily(Comparator<? super T> comparator) {
			id = INDEXER++;
			this.comparator = comparator;
		}

		protected abstract boolean belongsToFamily(SceneNodeComponent component);
	}

	private static class FamilyComponents<T extends SceneNodeComponent> implements Poolable {
		ComponentFamily<T> family;
		Array<T> components = new Array<T>();

		private void add(SceneNodeComponent component) {
			if (family.belongsToFamily(component)) {
				@SuppressWarnings("unchecked")
				T casted = (T) component;
				components.add(casted);
			}
			Comparator<? super T> comparator = family.comparator;
			if (comparator != null) {
				components.sort(comparator);
			}
		}

		private void remove(SceneNodeComponent component) {
			@SuppressWarnings("unchecked")
			T casted = (T) component;
			components.removeValue(casted, true);
		}

		@Override
		public void reset() {
			family = null;
			components.clear();
		}
	}
}
