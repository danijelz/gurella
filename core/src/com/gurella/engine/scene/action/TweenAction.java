package com.gurella.engine.scene.action;

import com.badlogic.gdx.math.Interpolation;
import com.gurella.engine.application.Application;

public class TweenAction extends SceneAction {
	Tween tween;
	float duration;
	Interpolation interpolation;

	private float time;
	private boolean reverse, began, complete;

	TweenAction() {
	}

	public TweenAction(Tween tween, float duration) {
		this.tween = tween;
		this.duration = duration;
	}

	public TweenAction(Tween tween, float duration, Interpolation interpolation) {
		this.tween = tween;
		this.duration = duration;
		this.interpolation = interpolation;
	}

	@Override
	public boolean act() {
		if (complete) {
			return true;
		}

		if (!began) {
			begin();
			began = true;
		}

		float delta = Application.deltaTime;
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

		tween.update(reverse ? 1 - percent : percent);
		if (complete) {
			end();
		}

		return complete;
	}

	protected void begin() {
	}

	protected void end() {
	}

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

	@Override
	public boolean isComplete() {
		return complete;
	}
}
