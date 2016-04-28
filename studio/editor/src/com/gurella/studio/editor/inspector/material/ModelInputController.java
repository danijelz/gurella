package com.gurella.studio.editor.inspector.material;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Quaternion;

public class ModelInputController extends InputAdapter {
	protected int button = -1;
	private float startX, startY;

	public ModelInstance instance;

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		startX = screenX;
		startY = screenY;
		this.button = button;
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (button == this.button) {
			this.button = -1;
			return true;
		}
		return false;
	}

	protected boolean process(float deltaX, float deltaY) {
		if (instance == null) {
			return false;
		}

		instance.transform.rotate(new Quaternion().setEulerAnglesRad(deltaX, deltaY, 0));
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		boolean result = super.touchDragged(screenX, screenY, pointer);
		if (result || this.button < 0) {
			return result;
		}
		final float deltaX = (screenX - startX) / Gdx.graphics.getWidth();
		final float deltaY = (startY - screenY) / Gdx.graphics.getHeight();
		startX = screenX;
		startY = screenY;
		return process(deltaX, deltaY);
	}
}
