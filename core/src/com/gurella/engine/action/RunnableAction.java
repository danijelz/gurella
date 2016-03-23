package com.gurella.engine.action;

public class RunnableAction extends SceneAction {
	private Runnable runnable;
	private boolean ran;

	@Override
	public boolean act() {
		if (!ran) {
			runnable.run();
		}
		return true;
	}

	@Override
	public void restart() {
		super.restart();
		ran = false;
	}

	@Override
	public void reset() {
		super.reset();
		runnable = null;
	}

	@Override
	public boolean isComplete() {
		return ran;
	}
}
