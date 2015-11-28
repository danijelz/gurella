package com.gurella.engine.graph.manager;

import static com.gurella.engine.graph.SceneNodeComponent.getComponentSubtypes;
import static com.gurella.engine.graph.SceneNodeComponent.getComponentType;

import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Predicate;
import com.gurella.engine.graph.SceneNode;
import com.gurella.engine.graph.SceneNodeComponent;
import com.gurella.engine.utils.ImmutableBits;


public class ComponentBitsPredicate implements Predicate<SceneNode>, Poolable {
	private boolean activeComponents;
	private final Bits all = new Bits();
	private final Bits exclude = new Bits();
	private final Bits any = new Bits();

	private ComponentBitsPredicate() {
	}

	@Override
	public boolean evaluate(SceneNode node) {
		ImmutableBits nodeComponentBits = activeComponents ? node.activeComponentBits : node.componentBits;

		int componentId = all.nextSetBit(0);
		while (componentId != -1) {
			if (!nodeComponentBits.get(componentId)
					&& !nodeComponentBits.intersects(getComponentSubtypes(componentId))) {
				return false;
			}
			componentId = all.nextSetBit(componentId);
		}

		if (!any.isEmpty()) {
			componentId = any.nextSetBit(0);
			while (componentId != -1) {
				if (nodeComponentBits.get(componentId)
						|| nodeComponentBits.intersects(getComponentSubtypes(componentId))) {
					break;
				}
				componentId = any.nextSetBit(componentId);
			}
			if (componentId == -1) {
				return false;
			}
		}

		if (!exclude.isEmpty()) {
			componentId = exclude.nextSetBit(0);
			while (componentId != -1) {
				if (nodeComponentBits.get(componentId)
						|| nodeComponentBits.intersects(getComponentSubtypes(componentId))) {
					return false;
				}
				componentId = exclude.nextSetBit(componentId);
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

		public ComponentBitsPredicate build() {
			ComponentBitsPredicate predicate = new ComponentBitsPredicate();
			predicate.activeComponents = activeComponents;
			predicate.all.or(all);
			predicate.exclude.or(exclude);
			predicate.any.or(any);
			return predicate;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;

			Builder builder = (Builder) o;

			if (activeComponents != builder.activeComponents)
				return false;
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
			final int prime = 31;
			int result = 1;
			result = prime * result + (activeComponents ? 1231 : 1237);
			result = prime * result + ((all == null) ? 0 : all.hashCode());
			result = prime * result + ((any == null) ? 0 : any.hashCode());
			result = prime * result + ((exclude == null) ? 0 : exclude.hashCode());
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
