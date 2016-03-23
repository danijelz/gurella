package com.gurella.engine.action;

import com.badlogic.gdx.Gdx;

public class DelayAction extends SceneAction {
	private float duration;
	private float time;

	public DelayAction(float duration) {
		this.duration = duration;
	}

	@Override
	public boolean act() {
		if (time >= duration) {
			return true;
		}

		time += Gdx.graphics.getDeltaTime();
		return time >= duration;
	}

	@Override
	public boolean isComplete() {
		return time >= duration;
	}

	@Override
	public void restart() {
		super.restart();
		time = 0;
	}
}
