package com.gurella.engine.scene.action;

public class RepeatAction extends Action {
	public static final int infiniteRepeatCount = -1;

	Action delegate;
	int repeatCount;

	private int executedCount;

	RepeatAction() {
	}

	public RepeatAction(Action delegate, int repeatCount) {
		this.delegate = delegate;
		this.repeatCount = repeatCount;
	}

	public RepeatAction(Action delegate) {
		this.delegate = delegate;
		this.repeatCount = infiniteRepeatCount;
	}

	@Override
	public boolean doAct() {
		if (delegate.act()) {
			if (repeatCount > 0) {
				executedCount++;
			}
			if (executedCount == repeatCount) {
				return true;
			}
			delegate.restart();
		}

		return false;
	}

	@Override
	public void restart() {
		super.restart();
		executedCount = 0;
	}
}
