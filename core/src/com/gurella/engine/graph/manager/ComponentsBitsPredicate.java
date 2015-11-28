package com.gurella.engine.graph.manager;

import static com.gurella.engine.graph.SceneNodeComponent.getComponentSubtypes;
import static com.gurella.engine.graph.SceneNodeComponent.getComponentType;

import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Predicate;
import com.gurella.engine.graph.SceneNode;
import com.gurella.engine.graph.SceneNodeComponent;
import com.gurella.engine.utils.ImmutableBits;

public class ComponentsBitsPredicate implements Predicate<SceneNode>, Poolable {
	private boolean activeComponents;
	private final Bits all = new Bits();
	private final Bits exclude = new Bits();
	private final Bits any = new Bits();

	private ComponentsBitsPredicate() {
	}

	@Override
	public boolean evaluate(SceneNode node) {
		ImmutableBits nodeComponentBits = activeComponents ? node.activeComponentBits : node.componentBits;

		int setBit = all.nextSetBit(0);
		while (setBit != -1) {
			if (!nodeComponentBits.get(setBit) && !nodeComponentBits.intersects(getComponentSubtypes(setBit))) {
				return false;
			}
			setBit = all.nextSetBit(setBit);
		}

		if (!any.isEmpty()) {
			boolean exists = false;
			setBit = any.nextSetBit(0);
			while (setBit != -1) {
				if (nodeComponentBits.get(setBit) || nodeComponentBits.intersects(getComponentSubtypes(setBit))) {
					exists = true;
					break;
				}
				setBit = any.nextSetBit(setBit);
			}
			if (!exists) {
				return false;
			}
		}

		if (!exclude.isEmpty()) {
			setBit = exclude.nextSetBit(0);
			while (setBit != -1) {
				if (nodeComponentBits.get(setBit) || nodeComponentBits.intersects(getComponentSubtypes(setBit))) {
					return false;
				}
				setBit = exclude.nextSetBit(setBit);
			}
		}

		return true;
	}

	@Override
	public void reset() {
		activeComponents = false;
		all.clear();
		exclude.clear();
		any.clear();
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
	public static Builder any(boolean activeComponents, Class<? extends SceneNodeComponent>... types) {
		return new Builder(activeComponents).any(types);
	}

	public static class Builder implements Poolable {
		private boolean activeComponents;
		private final Bits all = new Bits();
		private final Bits exclude = new Bits();
		private final Bits any = new Bits();

		private Builder(boolean activeComponents) {
			this.activeComponents = activeComponents;
		}

		public Builder all(@SuppressWarnings("unchecked") Class<? extends SceneNodeComponent>... types) {
			for (Class<? extends SceneNodeComponent> type : types) {
				all.set(getComponentType(type));
			}
			return this;
		}

		public Builder any(@SuppressWarnings("unchecked") Class<? extends SceneNodeComponent>... types) {
			for (Class<? extends SceneNodeComponent> type : types) {
				any.set(getComponentType(type));
			}
			return this;
		}

		public Builder exclude(@SuppressWarnings("unchecked") Class<? extends SceneNodeComponent>... types) {
			for (Class<? extends SceneNodeComponent> type : types) {
				exclude.set(getComponentType(type));
			}
			return this;
		}

		public ComponentsBitsPredicate build() {
			ComponentsBitsPredicate aspect = new ComponentsBitsPredicate();
			aspect.activeComponents = activeComponents;
			aspect.all.or(all);
			aspect.exclude.or(exclude);
			aspect.any.or(any);
			return aspect;
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
			if (!any.equals(builder.any))
				return false;

			return true;
		}

		@Override
		public int hashCode() {
			int result = all.hashCode();
			result = 31 * result + exclude.hashCode();
			result = 31 * result + any.hashCode();
			return result;
		}

		@Override
		public void reset() {
			activeComponents = false;
			all.clear();
			exclude.clear();
			any.clear();
		}
	}
}
