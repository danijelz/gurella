package com.gurella.studio.editor.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.gurella.engine.event.EventService;
import com.gurella.engine.plugin.Workbench;
import com.gurella.studio.editor.subscription.EditorActiveCameraProvider;
import com.gurella.studio.editor.subscription.EditorCameraSelectionListener;
import com.gurella.studio.editor.subscription.EditorPreCloseListener;
import com.gurella.studio.editor.subscription.EditorPreRenderUpdateListener;
import com.gurella.studio.editor.subscription.EditorResizeListener;

public class CameraManager implements EditorPreCloseListener, EditorActiveCameraProvider, EditorPreRenderUpdateListener,
		EditorResizeListener {

	private final int editorId;

	private final PerspectiveCamera perspectiveCamera;
	private final CameraController perspectiveCameraController;

	private final OrthographicCamera orthographicCamera;
	private final CameraController orthographicCameraController;

	private Camera camera;
	private CameraController inputController;

	private KeyboardCameraSelector cameraTypeSelector;

	@SuppressWarnings("unused")
	private CameraMenuContributor cameraMenuContributor;

	public CameraManager(int editorId) {
		this.editorId = editorId;

		Graphics graphics = Gdx.graphics;
		perspectiveCamera = new PerspectiveCamera(67, graphics.getWidth(), graphics.getHeight());
		perspectiveCamera.position.set(0f, 0f, 3f);
		perspectiveCamera.lookAt(0, 0, 0);
		perspectiveCamera.near = 0.1f;
		perspectiveCamera.far = 10000;
		perspectiveCamera.update();
		perspectiveCameraController = new CameraController(perspectiveCamera);

		orthographicCamera = new OrthographicCamera(graphics.getWidth(), graphics.getHeight());
		orthographicCamera.far = 10000;
		orthographicCamera.update();
		orthographicCameraController = new CameraController(orthographicCamera);

		camera = perspectiveCamera;
		inputController = perspectiveCameraController;
		Workbench.activate(inputController);

		cameraTypeSelector = new KeyboardCameraSelector(this);
		Workbench.activate(cameraTypeSelector);

		cameraMenuContributor = new CameraMenuContributor(editorId, this);

		EventService.subscribe(editorId, this);
		notifyCameraChange();
	}

	void switchCamera(CameraType cameraType) {
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

	boolean is2d() {
		return camera == orthographicCamera;
	}

	private void set2d() {
		Workbench.deactivate(inputController);
		camera = orthographicCamera;
		inputController = orthographicCameraController;
		notifyCameraChange();
		Workbench.activate(inputController);
	}

	private void notifyCameraChange() {
		EventService.post(editorId, EditorCameraSelectionListener.class, l -> l.cameraChanged(camera));
	}

	boolean is3d() {
		return camera == perspectiveCamera;
	}

	private void set3d() {
		Workbench.deactivate(inputController);
		camera = perspectiveCamera;
		inputController = perspectiveCameraController;
		notifyCameraChange();
		Workbench.activate(inputController);
	}

	@Override
	public Camera getActiveCamera() {
		return camera;
	}

	@Override
	public void onPreRenderUpdate() {
		inputController.update();
	}

	@Override
	public void resize(int width, int height) {
		perspectiveCamera.viewportWidth = width;
		perspectiveCamera.viewportHeight = height;
		perspectiveCamera.update();

		orthographicCamera.viewportWidth = width;
		orthographicCamera.viewportHeight = height;
		orthographicCamera.update();
	}

	@Override
	public void onEditorPreClose() {
		Workbench.deactivate(cameraTypeSelector);
		Workbench.deactivate(inputController);
		EventService.unsubscribe(editorId, this);
	}
}
