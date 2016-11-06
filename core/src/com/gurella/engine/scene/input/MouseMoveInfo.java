package com.gurella.engine.scene.input;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.scene.renderable.RenderableComponent;

public class MouseMoveInfo implements Poolable {
	public int screenX;
	public int screenY;
	
	public RenderableComponent renderable;
	public final Vector3 intersection = new Vector3();

	void set(int screenX, int screenY) {
		this.screenX = screenX;
		this.screenY = screenY;
	}

	@Override
	public void reset() {
		screenX = 0;
		screenY = 0;
		renderable = null;
		intersection.setZero();
	}
}
