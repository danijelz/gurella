package com.gurella.engine.graph.manager;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Predicate;
import com.gurella.engine.graph.SceneNode;
import com.gurella.engine.graph.SceneNodeComponent;

public class NodeComponentsPredicate implements Predicate<SceneNode>, Poolable {
	private boolean activeComponents;
	private final Bits all = new Bits();
	private final Bits exclude = new Bits();
	private final Bits one = new Bits();

	private NodeComponentsPredicate() {
	}

	@Override
	public boolean evaluate(SceneNode node) {
		Bits nodeComponentBits = activeComponents ? node.activeComponentBits : node.componentBits;

		if (!nodeComponentBits.containsAll(all)) {
			return false;
		}

		if (!one.isEmpty() && !one.intersects(nodeComponentBits)) {
			return false;
		}

		if (!exclude.isEmpty() && exclude.intersects(nodeComponentBits)) {
			return false;
		}

		return true;
	}

	@Override
	public void reset() {
		activeComponents = false;
		all.clear();
		exclude.clear();
		one.clear();
	}

	@SafeVarargs
	public static Builder all(boolean activeComponents, Class<? extends SceneNodeComponent>... types) {
		return new Builder(activeComponents).all(types);
	}

	@SafeVarargs
	public static Builder exclude(boolean activeComponents, Class<? extends SceneNodeComponent>... types) {
		return new Builder(activeComponents).exclude(types);
	}

	@SafeVarargs
	public static Builder one(boolean activeComponents, Class<? extends SceneNodeComponent>... types) {
		return new Builder(activeComponents).one(types);
	}

	public static class Builder implements Poolable {
		private boolean activeComponents;
		private final Array<Class<? extends SceneNodeComponent>> all = new Array<Class<? extends SceneNodeComponent>>();
		private final Array<Class<? extends SceneNodeComponent>> exclude = new Array<Class<? extends SceneNodeComponent>>();
		private final Array<Class<? extends SceneNodeComponent>> one = new Array<Class<? extends SceneNodeComponent>>();

		private Builder(boolean activeComponents) {
			this.activeComponents = activeComponents;
		}

		public Builder all(@SuppressWarnings("unchecked") Class<? extends SceneNodeComponent>... types) {
			for (Class<? extends SceneNodeComponent> t : types) {
				all.add(t);
			}
			return this;
		}

		public Builder one(@SuppressWarnings("unchecked") Class<? extends SceneNodeComponent>... types) {
			for (Class<? extends SceneNodeComponent> t : types) {
				one.add(t);
			}
			return this;
		}

		public Builder exclude(@SuppressWarnings("unchecked") Class<? extends SceneNodeComponent>... types) {
			for (Class<? extends SceneNodeComponent> t : types) {
				exclude.add(t);
			}
			return this;
		}

		public NodeComponentsPredicate build() {
			NodeComponentsPredicate aspect = new NodeComponentsPredicate();
			aspect.activeComponents = activeComponents;
			associate(all, aspect.all);
			associate(exclude, aspect.exclude);
			associate(one, aspect.one);
			return aspect;
		}

		private static void associate(Array<Class<? extends SceneNodeComponent>> types, Bits componentBits) {
			for (int i = 0; i < types.size; i++) {
				componentBits.set(SceneNodeComponent.getComponentType(types.get(i)));
			}
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;

			Builder builder = (Builder) o;

			if (!all.equals(builder.all))
				return false;
			if (!exclude.equals(builder.exclude))
				return false;
			if (!one.equals(builder.one))
				return false;

			return true;
		}

		@Override
		public int hashCode() {
			int result = all.hashCode();
			result = 31 * result + exclude.hashCode();
			result = 31 * result + one.hashCode();
			return result;
		}

		@Override
		public void reset() {
			activeComponents = false;
			all.clear();
			exclude.clear();
			one.clear();
		}
	}
}
