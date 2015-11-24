package com.gurella.engine.graphics.vector;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.Pool.Poolable;

class CanvasLayer implements Poolable {
	Effect effect;//TODO apply effect
	final Rectangle bounds = new Rectangle();
	
	public static CanvasLayer obtain() {
		return Pools.obtain(CanvasLayer.class);
	}
	
	@Override
	public void reset() {
		effect = null;
		bounds.set(0, 0, 0, 0);
	}

	public void free() {
		Pools.free(this);
	}
}
