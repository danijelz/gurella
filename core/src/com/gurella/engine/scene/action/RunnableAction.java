package com.gurella.engine.scene.action;

public class RunnableAction extends SceneAction {
	Runnable runnable;
	private boolean complete;

	@Override
	public boolean act() {
		if (!complete) {
			runnable.run();
		}
		return true;
	}

	@Override
	public void restart() {
		super.restart();
		complete = false;
	}

	@Override
	public void reset() {
		super.reset();
		runnable = null;
	}

	@Override
	public boolean isComplete() {
		return complete;
	}
}
