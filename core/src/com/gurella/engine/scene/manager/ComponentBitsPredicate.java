package com.gurella.engine.scene.manager;

import static com.gurella.engine.scene.ComponentType.getSubtypes;
import static com.gurella.engine.scene.ComponentType.getType;

import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Predicate;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.utils.ImmutableBits;

public class ComponentBitsPredicate implements Predicate<SceneNode2>, Poolable {
	private final Bits all = new Bits();
	private final Bits exclude = new Bits();
	private final Bits any = new Bits();

	private ComponentBitsPredicate() {
	}

	@Override
	public boolean evaluate(SceneNode2 node) {
		ImmutableBits componentBits = node.componentBits;
		int componentId;

		if (!all.isEmpty() && !componentBits.containsAll(all)) {
			for (componentId = all.nextSetBit(0); componentId >= 0; componentId = all.nextSetBit(componentId + 1)) {
				if (!componentBits.get(componentId) || !componentBits.intersects(getSubtypes(componentId))) {
					return false;
				}
			}
		}

		if (!any.isEmpty() && !componentBits.intersects(any)) {
			boolean pass = false;
			for (componentId = any.nextSetBit(0); componentId >= 0; componentId = any.nextSetBit(componentId + 1)) {
				if (componentBits.intersects(getSubtypes(componentId))) {
					pass = true;
				}
			}

			if (!pass) {
				return false;
			}
		}

		if (!exclude.isEmpty()) {
			if (componentBits.intersects(exclude)) {
				return false;
			}

			for (componentId = exclude.nextSetBit(0); componentId >= 0; componentId = exclude
					.nextSetBit(componentId + 1)) {
				if (componentBits.get(componentId) || componentBits.intersects(getSubtypes(componentId))) {
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public void reset() {
		all.clear();
		exclude.clear();
		any.clear();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		ComponentBitsPredicate other = (ComponentBitsPredicate) o;
		if (!all.equals(other.all)) {
			return false;
		}
		if (!exclude.equals(other.exclude)) {
			return false;
		}
		if (!any.equals(other.any)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((all == null) ? 0 : all.hashCode());
		result = prime * result + ((any == null) ? 0 : any.hashCode());
		result = prime * result + ((exclude == null) ? 0 : exclude.hashCode());
		return result;
	}

	@SafeVarargs
	public static Builder all(Class<? extends SceneNodeComponent2>... types) {
		return new Builder().all(types);
	}

	@SafeVarargs
	public static Builder exclude(Class<? extends SceneNodeComponent2>... types) {
		return new Builder().exclude(types);
	}

	@SafeVarargs
	public static Builder any(Class<? extends SceneNodeComponent2>... types) {
		return new Builder().any(types);
	}

	public static class Builder implements Poolable {
		private final Bits all = new Bits();
		private final Bits exclude = new Bits();
		private final Bits any = new Bits();

		public Builder all(Class<? extends SceneNodeComponent2>... types) {
			for (Class<? extends SceneNodeComponent2> type : types) {
				all.set(getType(type));
			}
			return this;
		}

		public Builder any(Class<? extends SceneNodeComponent2>... types) {
			for (Class<? extends SceneNodeComponent2> type : types) {
				int componentType = getType(type);
				getSubtypes(componentType).orBits(any);
			}
			return this;
		}

		public Builder exclude(Class<? extends SceneNodeComponent2>... types) {
			for (Class<? extends SceneNodeComponent2> type : types) {
				int componentType = getType(type);
				getSubtypes(componentType).orBits(exclude);
			}
			return this;
		}

		public ComponentBitsPredicate build() {
			ComponentBitsPredicate predicate = new ComponentBitsPredicate();
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
			result = prime * result + ((all == null) ? 0 : all.hashCode());
			result = prime * result + ((any == null) ? 0 : any.hashCode());
			result = prime * result + ((exclude == null) ? 0 : exclude.hashCode());
			return result;
		}

		@Override
		public void reset() {
			all.clear();
			exclude.clear();
			any.clear();
		}
	}
}
