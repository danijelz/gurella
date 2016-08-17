package com.gurella.engine.scene.action;

public class RepeatAction extends SceneAction {
	public static final int infiniteRepeatCount = -1;

	SceneAction delegate;
	int repeatCount;

	private int executedCount;
	private boolean finished;

	RepeatAction() {
	}

	public RepeatAction(SceneAction delegate, int repeatCount) {
		this.delegate = delegate;
		this.repeatCount = repeatCount;
	}

	public RepeatAction(SceneAction delegate) {
		this.delegate = delegate;
		this.repeatCount = infiniteRepeatCount;
	}

	@Override
	public boolean act() {
		if (isComplete()) {
			return true;
		}

		if (delegate.act()) {
			if (repeatCount > 0) {
				executedCount++;
			}
			if (finished || executedCount == repeatCount) {
				return true;
			}
			delegate.restart();
		}

		return false;
	}

	public void finish() {
		finished = true;
	}

	@Override
	public boolean isComplete() {
		return finished || (repeatCount > 0 && executedCount == repeatCount);
	}

	@Override
	public void restart() {
		super.restart();
		executedCount = 0;
		finished = false;
	}
}
