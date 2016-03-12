package com.gurella.engine.scene.tag;

import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.Pool.Poolable;

public final class TagFamily {
	private static int INDEXER = 0;

	public final int id;
	private final Bits all = new Bits();
	private final Bits exclude = new Bits();
	private final Bits any = new Bits();

	public TagFamily() {
		id = INDEXER++;
	}

	public boolean matches(TagComponent component) {
		Bits tags = component._tags;

		if (!tags.containsAll(all)) {
			return false;
		}

		if (!any.isEmpty() && !any.intersects(tags)) {
			return false;
		}

		if (!exclude.isEmpty() && exclude.intersects(tags)) {
			return false;
		}

		return true;
	}

	public static Builder all(Tag... tags) {
		return new Builder().all(tags);
	}

	public static Builder exclude(Tag... tags) {
		return new Builder().exclude(tags);
	}

	public static Builder any(Tag... tags) {
		return new Builder().any(tags);
	}

	public static class Builder implements Poolable {
		private final Bits all = new Bits();
		private final Bits exclude = new Bits();
		private final Bits any = new Bits();

		private Builder() {
		}

		public TagFamily.Builder all(Tag... tags) {
			for (Tag tag : tags) {
				all.set(tag.id);
			}
			return this;
		}

		public TagFamily.Builder any(Tag... tags) {
			for (Tag tag : tags) {
				any.set(tag.id);
			}
			return this;
		}

		public TagFamily.Builder exclude(Tag... tags) {
			for (Tag tag : tags) {
				exclude.set(tag.id);
			}
			return this;
		}

		public TagFamily build() {
			TagFamily family = new TagFamily();
			family.all.or(all);
			family.exclude.or(exclude);
			family.any.or(any);
			return family;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;

			Builder builder = (Builder) o;
			if (!all.equals(builder.all)) {
				return false;
			}
			if (!exclude.equals(builder.exclude)) {
				return false;
			}
			if (!any.equals(builder.any)) {
				return false;
			}

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
			all.clear();
			exclude.clear();
			any.clear();
		}
	}
}