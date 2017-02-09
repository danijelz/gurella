package com.gurella.studio.editor.camera;

import com.badlogic.gdx.Input.Keys;
import com.gurella.engine.utils.plugin.Plugin;
import com.badlogic.gdx.InputAdapter;

class KeyboardCameraSelector extends InputAdapter implements Plugin {
	private CameraManager cameraManager;

	KeyboardCameraSelector(CameraManager cameraManager) {
		this.cameraManager = cameraManager;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == Keys.NUM_2 || keycode == Keys.NUMPAD_2) {
			cameraManager.switchCamera(CameraType.camera2d);
			return true;
		} else if (keycode == Keys.NUM_3 || keycode == Keys.NUMPAD_3) {
			cameraManager.switchCamera(CameraType.camera3d);
			return true;
		} else {
			return false;
		}
	}
}
