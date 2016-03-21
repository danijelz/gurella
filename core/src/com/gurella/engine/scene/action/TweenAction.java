package com.gurella.engine.scene.action;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;

public abstract class TweenAction extends SceneAction {
	private float duration, time;
	private Interpolation interpolation;
	private boolean reverse, began, complete;

	public TweenAction() {
	}

	public TweenAction(float duration) {
		this.duration = duration;
	}

	public TweenAction(float duration, Interpolation interpolation) {
		this.duration = duration;
		this.interpolation = interpolation;
	}

	@Override
	public boolean act() {
		float delta = Gdx.graphics.getDeltaTime();
		if (complete) {
			return true;
		}
		if (!began) {
			begin();
			began = true;
		}
		time += delta;
		complete = time >= duration;
		float percent;
		if (complete) {
			percent = 1;
		} else {
			percent = time / duration;
			if (interpolation != null) {
				percent = interpolation.apply(percent);
			}
		}
		update(reverse ? 1 - percent : percent);
		if (complete) {
			end();
		}
		return complete;
	}

	protected void begin() {
	}

	protected void end() {
	}

	protected abstract void update(float percent);

	public void finish() {
		time = duration;
	}

	@Override
	public void restart() {
		super.restart();
		time = 0;
		began = false;
		complete = false;
	}

	@Override
	public void reset() {
		super.reset();
		reverse = false;
		interpolation = null;
	}

	public float getTime() {
		return time;
	}

	public void setTime(float time) {
		this.time = time;
	}

	public float getDuration() {
		return duration;
	}

	public void setDuration(float duration) {
		this.duration = duration;
	}

	public Interpolation getInterpolation() {
		return interpolation;
	}

	public void setInterpolation(Interpolation interpolation) {
		this.interpolation = interpolation;
	}

	public boolean isReverse() {
		return reverse;
	}

	public void setReverse(boolean reverse) {
		this.reverse = reverse;
	}

	public boolean isComplete() {
		return complete;
	}
}
