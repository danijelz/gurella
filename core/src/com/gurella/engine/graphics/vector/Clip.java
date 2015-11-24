package com.gurella.engine.graphics.vector;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;

class Clip implements Poolable {
	GlCall call;
	ClipOperation clipOperation = ClipOperation.union;
	
	static Clip obtain(GlCall call, ClipOperation clipOperation) {
		Clip clip = Pools.obtain(Clip.class);
		clip.call = call;
		clip.clipOperation = clipOperation;
		return clip;
	}
	
	@Override
	public void reset() {
		call = null;
		clipOperation = ClipOperation.union;
	}
	
	void free() {
		Pools.free(this);
	}
}
