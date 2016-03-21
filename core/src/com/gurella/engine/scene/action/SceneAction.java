package com.gurella.engine.scene.action;

import com.badlogic.gdx.utils.Pool.Poolable;

//TODO unused
public abstract class SceneAction implements Poolable {
	abstract public boolean act(float delta);

	public void restart() {
	}

	@Override
	public void reset() {
		restart();
	}
}
