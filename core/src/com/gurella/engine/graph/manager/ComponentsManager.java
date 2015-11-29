package com.gurella.engine.graph.manager;

import java.util.Comparator;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.Predicate;
import com.gurella.engine.graph.GraphListenerSystem;
import com.gurella.engine.graph.SceneNodeComponent;
import com.gurella.engine.utils.ImmutableArray;

public class ComponentsManager extends GraphListenerSystem {
	private IntMap<FamilyComponents<?>> families = new IntMap<FamilyComponents<?>>();

	@Override
	public void componentAdded(SceneNodeComponent component) {
		handleComponent(component);
	}

	@Override
	public void componentActivated(SceneNodeComponent component) {
		handleComponent(component);
	}

	private void handleComponent(SceneNodeComponent component) {
		for (FamilyComponents<?> familyComponents : families.values()) {
			familyComponents.handle(component);
		}
	}

	@Override
	public void componentDeactivated(SceneNodeComponent component) {
		handleComponent(component);
	}

	@Override
	public void componentRemoved(SceneNodeComponent component) {
		for (FamilyComponents<?> familyComponents : families.values()) {
			familyComponents.remove(component);
		}
	}

	public <T extends SceneNodeComponent> ImmutableArray<T> getComponents(ComponentFamily<T> family) {
		@SuppressWarnings("unchecked")
		FamilyComponents<T> familyComponents = (FamilyComponents<T>) families.get(family.id);
		return familyComponents == null ? ImmutableArray.<T> empty() : familyComponents.immutableComponents;
	}

	public <T extends SceneNodeComponent> void registerComponentFamily(ComponentFamily<T> family) {
		if (families.containsKey(family.id)) {
			return;
		}

		@SuppressWarnings("unchecked")
		FamilyComponents<T> familyComponents = Pools.obtain(FamilyComponents.class);
		familyComponents.family = family;
		families.put(family.id, familyComponents);

		ImmutableArray<SceneNodeComponent> components = getGraph().allComponents;
		for (int i = 0; i < components.size(); i++) {
			familyComponents.handle(components.get(i));
		}
	}

	public <T extends SceneNodeComponent> void unregisterComponentFamily(ComponentFamily<T> family) {
		@SuppressWarnings("unchecked")
		FamilyComponents<T> familyComponents = (FamilyComponents<T>) families.remove(family.id);
		if (familyComponents != null) {
			Pools.free(familyComponents);
		}
	}

	public static final class ComponentFamily<T extends SceneNodeComponent> {
		private static int INDEXER = 0;

		public final int id;
		public final Comparator<? super T> comparator;
		public final Predicate<? super SceneNodeComponent> predicate;

		public ComponentFamily(Predicate<? super SceneNodeComponent> predicate) {
			id = INDEXER++;
			comparator = null;
			this.predicate = predicate;
		}

		public ComponentFamily(Predicate<? super SceneNodeComponent> predicate, Comparator<? super T> comparator) {
			id = INDEXER++;
			this.comparator = comparator;
			this.predicate = predicate;
		}
	}

	private static class FamilyComponents<T extends SceneNodeComponent> implements Poolable {
		private ComponentFamily<T> family;
		private final Array<T> components = new Array<T>();
		private final ImmutableArray<T> immutableComponents = new ImmutableArray<T>(components);

		private void handle(SceneNodeComponent component) {
			if (family.predicate.evaluate(component)) {
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
