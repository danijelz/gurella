package com.gurella.engine.scene.layer;

import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.Pool.Poolable;

//TODO unused
public class LayerMask implements Poolable {
	private boolean allAlowed = true;
	private final Bits allowed = new Bits();
	private final Bits ignored = new Bits();

	public LayerMask allowed(Layer layer) {
		ignored.clear(layer.id);
		allowed.set(layer.id);
		allAlowed = false;
		return this;
	}

	public LayerMask ignored(Layer layer) {
		allowed.clear(layer.id);
		ignored.set(layer.id);
		allAlowed = allowed.nextSetBit(0) < 0;
		return this;
	}

	public boolean isValid(Layer layer) {
		return ignored.get(layer.id) ? false : allAlowed ? true : allowed.get(layer.id);
	}

	@Override
	public void reset() {
		allAlowed = true;
		allowed.clear();
		ignored.clear();
	}
}
