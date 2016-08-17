package com.gurella.engine.scene.action;

public class RunnableAction extends Action {
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
