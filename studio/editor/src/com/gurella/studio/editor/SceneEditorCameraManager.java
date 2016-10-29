package com.gurella.studio.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.InputEventQueue;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.gurella.engine.event.EventService;
import com.gurella.engine.input.InputService;
import com.gurella.studio.editor.subscription.EditorActiveCameraProvider;
import com.gurella.studio.editor.subscription.EditorCameraChangedListener;
import com.gurella.studio.editor.subscription.EditorCameraSwitch;
import com.gurella.studio.editor.subscription.EditorPreCloseListener;

public class SceneEditorCameraManager
		implements EditorCameraSwitch, EditorPreCloseListener, EditorActiveCameraProvider {
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

		Graphics graphics = Gdx.graphics;
		perspectiveCamera = new PerspectiveCamera(67, graphics.getWidth(), graphics.getHeight());
		perspectiveCamera.position.set(0f, 0f, 3f);
		perspectiveCamera.lookAt(0, 0, 0);
		perspectiveCamera.near = 0.1f;
		perspectiveCamera.far = 10000;
		perspectiveCamera.update();
		perspectiveCameraController = new SceneEditorCameraController(perspectiveCamera, editorId);

		orthographicCamera = new OrthographicCamera(graphics.getWidth(), graphics.getHeight());
		orthographicCamera.far = 10000;
		orthographicCamera.update();
		orthographicCameraController = new SceneEditorCameraController(orthographicCamera, editorId);

		camera = perspectiveCamera;
		inputController = perspectiveCameraController;

		inputQueue.setProcessor(inputController);
		InputService.addInputProcessor(inputQueue);
		EventService.subscribe(editorId, this);

		notifyCameraChange();
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

	private boolean is2d() {
		return camera == orthographicCamera;
	}

	private void set2d() {
		camera = orthographicCamera;
		inputController = orthographicCameraController;
		inputQueue.setProcessor(inputController);
		notifyCameraChange();
	}

	private void notifyCameraChange() {
		EventService.post(editorId, EditorCameraChangedListener.class, l -> l.cameraChanged(camera));
	}

	private boolean is3d() {
		return camera == perspectiveCamera;
	}

	private void set3d() {
		camera = perspectiveCamera;
		inputController = perspectiveCameraController;
		inputQueue.setProcessor(inputController);
		notifyCameraChange();
	}

	@Override
	public void onEditorPreClose() {
		InputService.removeInputProcessor(inputQueue);
		EventService.unsubscribe(editorId, this);
	}

	@Override
	public Camera getActiveCamera() {
		return camera;
	}

	void resize(int width, int height) {
		perspectiveCamera.viewportWidth = width;
		perspectiveCamera.viewportHeight = height;
		perspectiveCamera.update();

		orthographicCamera.viewportWidth = width;
		orthographicCamera.viewportHeight = height;
		orthographicCamera.update();
	}
}
