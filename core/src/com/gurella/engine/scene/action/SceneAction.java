package com.gurella.engine.scene.action;

import com.badlogic.gdx.utils.Pool.Poolable;

public abstract class SceneAction implements Poolable {
	abstract public boolean act();

	abstract public boolean isComplete();

	public void restart() {
	}

	@Override
	public void reset() {
		restart();
	}
}
