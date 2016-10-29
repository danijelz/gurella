package com.gurella.studio.editor.camera;

import static com.gurella.studio.editor.subscription.EditorCameraSwitch.CameraType.camera2d;
import static com.gurella.studio.editor.subscription.EditorCameraSwitch.CameraType.camera3d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.InputEventQueue;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.gurella.engine.event.EventService;
import com.gurella.engine.input.InputService;
import com.gurella.studio.editor.menu.ContextMenuActions;
import com.gurella.studio.editor.subscription.EditorActiveCameraProvider;
import com.gurella.studio.editor.subscription.EditorCameraChangedListener;
import com.gurella.studio.editor.subscription.EditorCameraSwitch;
import com.gurella.studio.editor.subscription.EditorContextMenuContributor;
import com.gurella.studio.editor.subscription.EditorInputUpdateListener;
import com.gurella.studio.editor.subscription.EditorPreCloseListener;
import com.gurella.studio.editor.subscription.EditorResizeListener;

public class CameraManager implements EditorCameraSwitch, EditorPreCloseListener, EditorActiveCameraProvider,
		EditorInputUpdateListener, EditorResizeListener, EditorContextMenuContributor {
	private static final String cameraMenuGroupName = "Camera";
	private static final String moveToMenuGroupName = "Move to";

	private final InputEventQueue inputQueue = new InputEventQueue();

	private final int editorId;

	private PerspectiveCamera perspectiveCamera;
	private CameraController perspectiveCameraController;

	private OrthographicCamera orthographicCamera;
	private CameraController orthographicCameraController;

	private Camera camera;
	private CameraController inputController;

	public CameraManager(int editorId) {
		this.editorId = editorId;

		Graphics graphics = Gdx.graphics;
		perspectiveCamera = new PerspectiveCamera(67, graphics.getWidth(), graphics.getHeight());
		perspectiveCamera.position.set(0f, 0f, 3f);
		perspectiveCamera.lookAt(0, 0, 0);
		perspectiveCamera.near = 0.1f;
		perspectiveCamera.far = 10000;
		perspectiveCamera.update();
		perspectiveCameraController = new CameraController(perspectiveCamera, editorId);

		orthographicCamera = new OrthographicCamera(graphics.getWidth(), graphics.getHeight());
		orthographicCamera.far = 10000;
		orthographicCamera.update();
		orthographicCameraController = new CameraController(orthographicCamera, editorId);

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
	public Camera getActiveCamera() {
		return camera;
	}

	@Override
	public void onInputUpdate() {
		inputQueue.drain();
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
	public void contribute(ContextMenuActions actions) {
		actions.addGroup(cameraMenuGroupName, -800);
		boolean is2dCamera = is2d();
		actions.addCheckAction(cameraMenuGroupName, "2d", 100, !is2dCamera, is2dCamera, () -> switchCamera(camera2d));
		boolean is3dCamera = is3d();
		actions.addCheckAction(cameraMenuGroupName, "3d", 200, !is3dCamera, is3dCamera, () -> switchCamera(camera3d));

		actions.addGroup(moveToMenuGroupName, -700);
		actions.addAction(moveToMenuGroupName, "Front", 100, () -> toFront());
		actions.addAction(moveToMenuGroupName, "Back", 200, () -> toBack());
		actions.addAction(moveToMenuGroupName, "Top", 300, () -> toTop());
		actions.addAction(moveToMenuGroupName, "Bottom", 400, () -> toBottom());
		actions.addAction(moveToMenuGroupName, "Right", 500, () -> toRight());
		actions.addAction(moveToMenuGroupName, "Left", 600, () -> toLeft());
	}

	private void toFront() {
		EventService.post(editorId, EditorActiveCameraProvider.class, l -> camera = l.getActiveCamera());
		camera.position.set(0, 0, 3);
		camera.direction.set(0, 0, -1);
		camera.up.set(0, 1, 0);
		camera.lookAt(0, 0, 0);
		camera.update(true);
	}

	private void toBack() {
		camera.position.set(0, 0, -3);
		camera.direction.set(0, 0, 1);
		camera.up.set(0, 1, 0);
		camera.lookAt(0, 0, 0);
		camera.update(true);
	}

	private void toTop() {
		camera.position.set(0, 3, 0);
		camera.direction.set(0, -1, 0);
		camera.up.set(0, 0, -1);
		camera.lookAt(0, 0, 0);
		camera.update(true);
	}

	private void toBottom() {
		camera.position.set(0, -3, 0);
		camera.direction.set(0, -1, 0);
		camera.up.set(0, 0, 1);
		camera.lookAt(0, 0, 0);
		camera.update(true);
	}

	private void toRight() {
		camera.position.set(3, 0, 0);
		camera.direction.set(-1, 0, 0);
		camera.up.set(0, 1, 0);
		camera.lookAt(0, 0, 0);
		camera.update(true);
	}

	private void toLeft() {
		camera.position.set(-3, 0, 0);
		camera.direction.set(1, 0, 0);
		camera.up.set(0, 1, 0);
		camera.lookAt(0, 0, 0);
		camera.update(true);
	}

	@Override
	public void onEditorPreClose() {
		InputService.removeInputProcessor(inputQueue);
		EventService.unsubscribe(editorId, this);
	}
}
