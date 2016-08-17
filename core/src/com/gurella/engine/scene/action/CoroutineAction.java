package com.gurella.engine.scene.action;

public final class CoroutineAction extends SceneAction {
	Coroutine coroutine;
	private boolean complete = false;

	private long waitStartTime;
	private long waitDuration;

	CoroutineAction() {
	}

	public CoroutineAction(Coroutine coroutine) {
		this.coroutine = coroutine;
	}

	@Override
	public final boolean act() {
		if (complete) {
			return true;
		}

		if (waitDuration > 0) {
			if (System.currentTimeMillis() - waitStartTime >= waitDuration) {
				waitDuration = 0;
			} else {
				return false;
			}
		}

		long result = coroutine.act();
		if (result < 0f) {
			complete = true;
		} else if (result > 0) {
			waitDuration = result;
			waitStartTime = System.currentTimeMillis();
		}

		return complete;
	}

	@Override
	public boolean isComplete() {
		return complete;
	}

	@Override
	public void restart() {
		super.restart();
		complete = false;
		waitStartTime = 0;
		waitDuration = 0;
	}

	@Override
	public void reset() {
		super.reset();
		coroutine = null;
	}

	public interface Coroutine {
		public abstract long act();
	}
}
