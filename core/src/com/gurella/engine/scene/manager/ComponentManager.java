package com.gurella.engine.scene.manager;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.Predicate;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.scene.SceneSystem2;
import com.gurella.engine.subscriptions.scene.ComponentActivityListener;
import com.gurella.engine.utils.ImmutableArray;

public class ComponentManager extends SceneSystem2 implements ComponentActivityListener {
	private IntMap<FamilyComponents<?>> families = new IntMap<FamilyComponents<?>>();

	@Override
	public void componentActivated(SceneNodeComponent2 component) {
		for (FamilyComponents<?> familyComponents : families.values()) {
			familyComponents.handle(component);
		}
	}

	@Override
	public void componentDeactivated(SceneNodeComponent2 component) {
		for (FamilyComponents<?> familyComponents : families.values()) {
			familyComponents.remove(component);
		}
	}

	public <T extends SceneNodeComponent2> ImmutableArray<T> getComponents(ComponentFamily family) {
		@SuppressWarnings("unchecked")
		FamilyComponents<T> familyComponents = (FamilyComponents<T>) families.get(family.id);
		return familyComponents == null ? ImmutableArray.<T> empty() : familyComponents.immutableComponents;
	}

	public <T extends SceneNodeComponent2> void registerComponentFamily(ComponentFamily family) {
		if (families.containsKey(family.id)) {
			return;
		}

		@SuppressWarnings("unchecked")
		FamilyComponents<T> familyComponents = Pools.obtain(FamilyComponents.class);
		familyComponents.family = family;
		families.put(family.id, familyComponents);

		ImmutableArray<SceneNodeComponent2> components = getScene().components;
		for (int i = 0; i < components.size(); i++) {
			familyComponents.handle(components.get(i));
		}
	}

	public <T extends SceneNodeComponent2> void unregisterComponentFamily(ComponentFamily family) {
		@SuppressWarnings("unchecked")
		FamilyComponents<T> familyComponents = (FamilyComponents<T>) families.remove(family.id);
		if (familyComponents != null) {
			Pools.free(familyComponents);
		}
	}

	public static final class ComponentFamily {
		private static int INDEXER = 0;

		public final int id;
		public final Predicate<? super SceneNodeComponent2> predicate;

		public ComponentFamily(Predicate<? super SceneNodeComponent2> predicate) {
			id = INDEXER++;
			this.predicate = predicate;
		}
	}

	private static class FamilyComponents<T extends SceneNodeComponent2> implements Poolable {
		private ComponentFamily family;
		private final Array<T> components = new Array<T>();
		private final ImmutableArray<T> immutableComponents = new ImmutableArray<T>(components);

		private void handle(SceneNodeComponent2 component) {
			if (family.predicate.evaluate(component)) {
				@SuppressWarnings("unchecked")
				T casted = (T) component;
				components.add(casted);
			}
		}

		private void remove(SceneNodeComponent2 component) {
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
