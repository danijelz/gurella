package com.gurella.studio.editor.camera;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.gurella.engine.utils.plugin.Plugin;
import com.gurella.engine.utils.priority.Priority;

@Priority(Integer.MAX_VALUE)
class CameraController extends CameraInputController implements Plugin {
	public CameraController(Camera camera) {
		super(camera);
		forwardKey = Keys.UP;
		backwardKey = Keys.DOWN;
		rotateRightKey = Keys.RIGHT;
		rotateLeftKey = Keys.LEFT;
	}

	@Override
	protected boolean process(float deltaX, float deltaY, int button) {
		if (camera instanceof OrthographicCamera) {
			if (button == rotateButton) {
				float zoom = ((OrthographicCamera) camera).zoom;
				camera.translate(-deltaX * 620 * zoom, -deltaY * 620 * zoom, 0);
			} else if (button == translateButton) {
				((OrthographicCamera) camera).rotate(deltaY * 100);
			} else if (button == forwardButton) {
				zoom(-deltaY * 0.01f);
			}

			if (autoUpdate) {
				camera.update();
			}
			return true;
		} else {
			return super.process(deltaX, deltaY, button);
		}
	}

	@Override
	public boolean scrolled(int amount) {
		if (camera instanceof OrthographicCamera) {
			return zoom(amount * 0.01f);
		} else {
			return super.scrolled(amount);
		}
	}

	@Override
	public boolean zoom(float amount) {
		if (camera instanceof OrthographicCamera) {
			if (!alwaysScroll && activateKey != 0 && !activatePressed) {
				return false;
			}
			float temp = amount;
			OrthographicCamera orthographicCamera = (OrthographicCamera) camera;
			if (orthographicCamera.zoom < 0.0001f) {
				temp *= 0.0001f;
			} else if (orthographicCamera.zoom < 0.001f) {
				temp *= 0.001f;
			} else if (orthographicCamera.zoom < 0.01f) {
				temp *= 0.01f;
			} else if (orthographicCamera.zoom < 0.1f) {
				temp *= 0.1f;
			}

			orthographicCamera.zoom += temp;
			if (orthographicCamera.zoom < Float.MIN_VALUE) {
				orthographicCamera.zoom = Float.MIN_VALUE;
			}

			if (autoUpdate) {
				camera.update();
			}

			return true;
		} else {
			return super.zoom(amount);
		}
	}
}
