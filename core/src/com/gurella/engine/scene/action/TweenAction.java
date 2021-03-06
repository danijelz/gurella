package com.gurella.engine.scene.action;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;

public class TweenAction extends Action {
	Tween tween;
	float duration;
	Interpolation interpolation;
	boolean reverse;

	private float time;

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
	public boolean doAct() {
		float delta = Gdx.graphics.getDeltaTime();
		time += delta;
		boolean complete = time >= duration;
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
		return complete;
	}

	public void finish() {
		time = duration;
	}

	@Override
	public void restart() {
		super.restart();
		tween.update(reverse ? 1 : 0);
		time = 0;
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
}
