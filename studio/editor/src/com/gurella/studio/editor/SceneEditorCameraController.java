package com.gurella.studio.editor;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.gurella.engine.event.EventService;
import com.gurella.studio.editor.subscription.EditorCameraSwitch;
import com.gurella.studio.editor.subscription.EditorCameraSwitch.CameraType;
import com.gurella.studio.editor.subscription.EditorMouseListener;

class SceneEditorCameraController extends CameraInputController {
	private final int editorId;

	public SceneEditorCameraController(Camera camera, int editorId) {
		super(new SceneCameraGestureListener(editorId), camera);
		this.editorId = editorId;
	}

	@Override
	protected boolean process(float deltaX, float deltaY, int button) {
		if (camera instanceof OrthographicCamera) {
			if (button == rotateButton) {
				camera.translate(deltaX * 10, deltaY * 10, 0);
			} else if (button == translateButton) {
				camera.translate(deltaX, deltaY, 0);
			} else if (button == forwardButton) {
				zoom(-deltaY * translateUnits);
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
	public boolean zoom(float amount) {
		if (camera instanceof OrthographicCamera) {
			if (!alwaysScroll && activateKey != 0 && !activatePressed) {
				return false;
			}
			OrthographicCamera orthographicCamera = (OrthographicCamera) camera;
			orthographicCamera.zoom += amount;
			if (orthographicCamera.zoom < 0) {
				orthographicCamera.zoom = 0;
			}
			if (autoUpdate) {
				camera.update();
			}
			return true;
		} else {
			return super.zoom(amount);
		}
	}

	@Override
	public boolean keyUp(int keycode) {
		if (super.keyUp(keycode)) {
			return true;
		}

		if (keycode == Keys.NUM_2 || keycode == Keys.NUMPAD_2) {
			EventService.post(editorId, EditorCameraSwitch.class, l -> l.switchCamera(CameraType.camera2d));
			return true;
		} else if (keycode == Keys.NUM_3 || keycode == Keys.NUMPAD_3) {
			EventService.post(editorId, EditorCameraSwitch.class, l -> l.switchCamera(CameraType.camera3d));
			return true;
		} else if (keycode == Keys.ALT_LEFT || keycode == Keys.ALT_RIGHT) {
			EventService.post(editorId, EditorMouseListener.class, l -> l.onMouseMenu(0, 0));
			return true;
		} else {
			return false;
		}
	}

	private static class SceneCameraGestureListener extends CameraGestureListener {
		private final int editorId;

		public SceneCameraGestureListener(int editorId) {
			this.editorId = editorId;
		}

		@Override
		public boolean tap(float x, float y, int count, int button) {
			if (count != 1) {
				return false;
			}

			switch (button) {
			case Buttons.RIGHT:
				EventService.post(editorId, EditorMouseListener.class, l -> l.onMouseMenu(x, y));
				return false;
			case Buttons.LEFT:
				EventService.post(editorId, EditorMouseListener.class, l -> l.onMouseSelection(x, y));
				return false;
			default:
				return false;
			}
		}
	}
}
