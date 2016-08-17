package com.gurella.engine.scene.action;

public class RunnableAction extends SceneAction {
	Runnable runnable;

	RunnableAction() {
	}

	RunnableAction(Runnable runnable) {
		this.runnable = runnable;
	}

	@Override
	public boolean doAct() {
		runnable.run();
		return true;
	}

	@Override
	public void reset() {
		super.reset();
		runnable = null;
	}
}
