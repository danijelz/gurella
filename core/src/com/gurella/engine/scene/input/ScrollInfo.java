package com.gurella.engine.scene.input;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.scene.renderable.RenderableComponent;

public class ScrollInfo implements Poolable {
	public int screenX;
	public int screenY;
	public int amount;

	public RenderableComponent renderable;
	public final Vector3 intersection = new Vector3();

	void set(int screenX, int screenY, int amount, RenderableComponent renderable, Vector3 intersection) {
		this.screenX = screenX;
		this.screenY = screenY;
		this.amount = amount;
		this.renderable = renderable;
		this.intersection.set(intersection);
	}

	@Override
	public void reset() {
		amount = 0;
		screenX = 0;
		screenY = 0;
		renderable = null;
		intersection.setZero();
	}
}
