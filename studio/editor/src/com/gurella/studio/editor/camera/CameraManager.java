package com.gurella.studio.editor.camera;

import static com.gurella.studio.editor.camera.CameraType.camera2d;
import static com.gurella.studio.editor.camera.CameraType.camera3d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;
import com.gurella.engine.event.EventService;
import com.gurella.engine.plugin.Workbench;
import com.gurella.studio.editor.preferences.PreferencesExtension;
import com.gurella.studio.editor.preferences.PreferencesNode;
import com.gurella.studio.editor.preferences.PreferencesStore;
import com.gurella.studio.editor.subscription.EditorCloseListener;
import com.gurella.studio.editor.subscription.EditorPreCloseListener;
import com.gurella.studio.editor.subscription.EditorPreRenderUpdateListener;
import com.gurella.studio.editor.subscription.EditorResizeListener;

public class CameraManager implements EditorPreCloseListener, EditorCloseListener, EditorPreRenderUpdateListener,
		EditorResizeListener, PreferencesExtension {

	private final int editorId;
	private final CameraProviderExtensionRegistry extensionRegistry;
	private PreferencesNode preferences;

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
		Workbench.activate(this);
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
		updateCameraPreference(camera2d);
	}

	private void updateCameraPreference(CameraType cameraType) {
		if (preferences != null) {
			preferences.putInt("cameraType", cameraType.ordinal());
		}
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
		updateCameraPreference(camera3d);
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
	public void setPreferencesStore(PreferencesStore preferencesStore) {
		if (preferencesStore == null) {
			preferences = null;
			return;
		}

		preferences = preferencesStore.sceneNode().node(CameraManager.class);
		preferences.getInt("cameraType", camera3d.ordinal(), i -> initCameraSelection(i));

		PreferencesNode node2d = preferences.node("camera2d");
		Vector3 position = orthographicCamera.position;
		position.x = node2d.getFloat("position.x", 0);
		position.y = node2d.getFloat("position.y", 0);
		orthographicCamera.zoom = node2d.getFloat("zoom", 1);
		Vector3 direction = orthographicCamera.direction;
		direction.x = node2d.getFloat("direction.x", 0);
		direction.y = node2d.getFloat("direction.y", 0);
		direction.z = node2d.getFloat("direction.z", -1);
		Vector3 up = orthographicCamera.up;
		up.x = node2d.getFloat("up.x", 0);
		up.y = node2d.getFloat("up.y", 1);
		up.z = node2d.getFloat("up.z", 0);
		orthographicCamera.update();

		PreferencesNode node3d = preferences.node("camera3d");
		position = perspectiveCamera.position;
		position.x = node3d.getFloat("position.x", 0);
		position.y = node3d.getFloat("position.y", 0);
		position.z = node3d.getFloat("position.z", 3);
		direction = perspectiveCamera.direction;
		direction.x = node3d.getFloat("direction.x", 0);
		direction.y = node3d.getFloat("direction.y", 0);
		direction.z = node3d.getFloat("direction.z", -1);
		up = perspectiveCamera.up;
		up.x = node3d.getFloat("up.x", 0);
		up.y = node3d.getFloat("up.y", 1);
		up.z = node3d.getFloat("up.z", 0);
		perspectiveCamera.update();
	}

	private void initCameraSelection(int cameraOrdinal) {
		CameraType[] values = CameraType.values();
		CameraType selected = values.length > cameraOrdinal ? values[cameraOrdinal] : camera3d;
		switchCamera(selected);
	}

	@Override
	public void onEditorPreClose() {
		if (preferences == null) {
			return;
		}

		Vector3 pos = orthographicCamera.position;
		Vector3 dir = orthographicCamera.direction;
		Vector3 up = orthographicCamera.up;
		preferences.node("camera2d").putFloat("position.x", pos.x).putFloat("position.y", pos.y)
				.putFloat("zoom", orthographicCamera.zoom).putFloat("direction.x", dir.x).putFloat("direction.y", dir.y)
				.putFloat("direction.z", dir.z).putFloat("up.x", up.x).putFloat("up.y", up.y).putFloat("up.z", up.z);

		pos = perspectiveCamera.position;
		dir = perspectiveCamera.direction;
		up = perspectiveCamera.up;
		preferences.node("camera3d").putFloat("position.x", pos.x).putFloat("position.y", pos.y)
				.putFloat("position.z", pos.z).putFloat("direction.x", dir.x).putFloat("direction.y", dir.y)
				.putFloat("direction.z", dir.z).putFloat("up.x", up.x).putFloat("up.y", up.y).putFloat("up.z", up.z);
	}

	@Override
	public void onEditorClose() {
		Workbench.deactivate(cameraTypeSelector);
		Workbench.deactivate(inputController);
		Workbench.removeListener(extensionRegistry);
		EventService.unsubscribe(editorId, this);
		Workbench.deactivate(this);
	}
}
