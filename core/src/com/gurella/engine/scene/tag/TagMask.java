package com.gurella.engine.scene.tag;

import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Predicate;

public class TagMask implements Predicate<TagComponent>, Poolable {
	private boolean allAlowed = true;
	private final Bits allowed = new Bits();
	private final Bits ignored = new Bits();

	public TagMask allowed(Tag layer) {
		ignored.clear(layer.id);
		allowed.set(layer.id);
		allAlowed = false;
		return this;
	}

	public TagMask ignored(Tag layer) {
		allowed.clear(layer.id);
		ignored.set(layer.id);
		allAlowed = allowed.nextSetBit(0) < 0;
		return this;
	}

	@Override
	public boolean evaluate(TagComponent component) {
		return isValid(component._tags);
	}

	public boolean isValid(Bits tags) {
		if (tags == null || ignored.intersects(tags)) {
			return false;
		}

		return allAlowed || tags.containsAll(allowed);
	}

	@Override
	public void reset() {
		allAlowed = true;
		allowed.clear();
		ignored.clear();
	}
}
