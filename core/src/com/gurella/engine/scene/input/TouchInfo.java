package com.gurella.engine.scene.input;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.scene.renderable.RenderableComponent;

public class TouchInfo implements Poolable {
	public int pointer;
	public int button;

	public int screenX;
	public int screenY;
	
	public RenderableComponent renderable;
	public final Vector3 intersection = new Vector3();

	void set(int pointer, int button, int screenX, int screenY) {
		this.pointer = pointer;
		this.button = button;
		this.screenX = screenX;
		this.screenY = screenY;
	}

	@Override
	public void reset() {
		pointer = 0;
		button = 0;
		screenX = 0;
		screenY = 0;
		renderable = null;
		intersection.setZero();
	}
}
