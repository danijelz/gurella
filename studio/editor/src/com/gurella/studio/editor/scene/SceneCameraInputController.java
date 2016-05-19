package com.gurella.studio.editor.scene;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.gurella.studio.editor.SceneEditorUtils;
import com.gurella.studio.editor.subscriptions.SceneEditorMouseSelectionListener;

public class SceneCameraInputController extends CameraInputController {
	public SceneCameraInputController(Camera camera) {
		super(new SceneCameraGestureListener(), camera);
	}

	protected static class SceneCameraGestureListener extends CameraGestureListener {
		@Override
		public boolean tap(float x, float y, int count, int button) {
			if (count != 1) {
				return false;
			}

			switch (button) {
			case Buttons.RIGHT:
				return false;
			case Buttons.LEFT:
				SceneEditorUtils.notify(SceneEditorMouseSelectionListener.class, l -> l.onMouseSelection(x, y));
				return false;
			default:
				return false;
			}
		}
	}
}
