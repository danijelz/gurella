package com.gurella.studio.editor.scene;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.gurella.studio.editor.SceneEditor;
import com.gurella.studio.editor.subscription.SceneEditorMouseListener;

public class SceneCameraInputController extends CameraInputController {
	public SceneCameraInputController(Camera camera, int editorId) {
		super(new SceneCameraGestureListener(editorId), camera);
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
				SceneEditor.post(editorId, SceneEditorMouseListener.class, l -> l.onMouseMenu(x, y));
				return false;
			case Buttons.LEFT:
				SceneEditor.post(editorId, SceneEditorMouseListener.class, l -> l.onMouseSelection(x, y));
				return false;
			default:
				return false;
			}
		}
	}
}
