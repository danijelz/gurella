package com.gurella.studio.editor.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.gurella.engine.event.EventService;
import com.gurella.engine.plugin.Workbench;
import com.gurella.studio.editor.subscription.EditorPreCloseListener;
import com.gurella.studio.editor.subscription.EditorPreRenderUpdateListener;
import com.gurella.studio.editor.subscription.EditorResizeListener;

public class CameraManager implements EditorPreCloseListener, EditorPreRenderUpdateListener, EditorResizeListener {
	private final int editorId;
	private final CameraProviderExtensionRegistry extensionRegistry;

	private final PerspectiveCamera perspectiveCamera;
	private final CameraController perspectiveCameraController;

	private final OrthographicCamera orthographicCamera;
	private final CameraController orthographicCameraController;

	private Camera camera;
	private CameraController inputController;

	@SuppressWarnings("unused")
	private final CameraMenuContributor cameraMenuContributor;
	private final KeyboardCameraSelector cameraTypeSelector;

	public CameraManager(int editorId) {
		this.editorId = editorId;
		extensionRegistry = new CameraProviderExtensionRegistry(this);
		Workbench.addListener(extensionRegistry);

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

		cameraMenuContributor = new CameraMenuContributor(editorId, this);
		cameraTypeSelector = new KeyboardCameraSelector(this);
		Workbench.activate(cameraTypeSelector);

		EventService.subscribe(editorId, this);
	}

	void switchCamera(CameraType cameraType) {
		switch (cameraType) {
		case camera2d:
			set2d();
			return;
		case camera3d:
			set3d();
			return;
		default:
			return;
		}
	}

	boolean is2d() {
		return camera == orthographicCamera;
	}

	private void set2d() {
		if (is2d()) {
			return;
		}

		Workbench.deactivate(inputController);
		camera = orthographicCamera;
		inputController = orthographicCameraController;
		Workbench.activate(inputController);
		extensionRegistry.updateCamera(camera);
	}

	boolean is3d() {
		return camera == perspectiveCamera;
	}

	private void set3d() {
		if (is3d()) {
			return;
		}

		Workbench.deactivate(inputController);
		camera = perspectiveCamera;
		inputController = perspectiveCameraController;
		Workbench.activate(inputController);
		extensionRegistry.updateCamera(camera);
	}

	Camera getCamera() {
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
		Workbench.removeListener(extensionRegistry);
		EventService.unsubscribe(editorId, this);
	}
}
