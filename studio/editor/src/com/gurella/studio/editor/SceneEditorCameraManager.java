package com.gurella.studio.editor;

import com.badlogic.gdx.InputEventQueue;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.gurella.engine.event.EventService;
import com.gurella.studio.editor.subscription.EditorCameraChangedListener;
import com.gurella.studio.editor.subscription.EditorCameraSwitch;
import com.gurella.studio.editor.subscription.EditorPreCloseListener;

//TODO unused
public class SceneEditorCameraManager implements EditorCameraSwitch, EditorPreCloseListener {
	private final InputEventQueue inputQueue = new InputEventQueue();

	private final int editorId;

	private PerspectiveCamera perspectiveCamera;
	private SceneEditorCameraController perspectiveCameraController;

	private OrthographicCamera orthographicCamera;
	private SceneEditorCameraController orthographicCameraController;

	private Camera camera;
	private SceneEditorCameraController inputController;

	public SceneEditorCameraManager(int editorId) {
		this.editorId = editorId;
		EventService.subscribe(editorId, this);
	}

	@Override
	public void switchCamera(CameraType cameraType) {
		switch (cameraType) {
		case camera2d:
			if (!is2d()) {
				set2d();
			}
			return;
		case camera3d:
			if (!is3d()) {
				set3d();
			}
			return;
		default:
			return;
		}
	}

	Camera getCamera() {
		return camera;
	}

	boolean is2d() {
		return camera == orthographicCamera;
	}

	void set2d() {
		camera = orthographicCamera;
		inputController = orthographicCameraController;
		inputQueue.setProcessor(inputController);
		notifyCameraChange();
	}

	private void notifyCameraChange() {
		EventService.post(editorId, EditorCameraChangedListener.class, l -> l.cameraChanged(camera));
	}

	boolean is3d() {
		return camera == perspectiveCamera;
	}

	void set3d() {
		camera = perspectiveCamera;
		inputController = perspectiveCameraController;
		inputQueue.setProcessor(inputController);
		notifyCameraChange();
	}

	@Override
	public void onEditorPreClose() {
		EventService.unsubscribe(editorId, this);
	}
}
