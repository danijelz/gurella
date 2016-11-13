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
import com.gurella.studio.editor.preferences.PreferencesNode;
import com.gurella.studio.editor.subscription.EditorCloseListener;
import com.gurella.studio.editor.subscription.EditorPreCloseListener;
import com.gurella.studio.editor.subscription.EditorPreRenderUpdateListener;
import com.gurella.studio.editor.subscription.EditorResizeListener;
import com.gurella.studio.editor.subscription.ScenePreferencesLoadedListener;

public class CameraManager implements EditorPreCloseListener, EditorCloseListener, EditorPreRenderUpdateListener,
		EditorResizeListener, ScenePreferencesLoadedListener {
	private static final String preferencesPath = CameraManager.class.getName();

	private final int editorId;
	private final CameraProviderExtensionRegistry extensionRegistry;
	private PreferencesNode rootPreferences;

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
		updateCameraPreference(camera2d);
	}

	private void updateCameraPreference(CameraType cameraType) {
		if (rootPreferences != null) {
			rootPreferences.putInt("cameraType", cameraType.ordinal());
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
	public void scenePreferencesLoaded(PreferencesNode scenePreferences) {
		this.rootPreferences = scenePreferences.node(preferencesPath);
		rootPreferences.getInt("cameraType", camera3d.ordinal(), i -> initCameraSelection(i));
		final Vector3 vec = new Vector3();

		vec.set(orthographicCamera.position);
		PreferencesNode node2d = rootPreferences.node("camera2d");
		node2d.preferences(p -> vec.set(p.getFloat("position.x", vec.x), p.getFloat("position.y", vec.y), vec.z))
				.preferences(p -> orthographicCamera.zoom = p.getFloat("zoom", orthographicCamera.zoom));
		orthographicCamera.position.set(vec);

		vec.set(orthographicCamera.direction);
		node2d.preferences(p -> vec.set(p.getFloat("direction.x", vec.x), p.getFloat("direction.y", vec.y),
				p.getFloat("direction.z", vec.z)));
		orthographicCamera.direction.set(vec);

		vec.set(orthographicCamera.up);
		node2d.preferences(
				p -> vec.set(p.getFloat("up.x", vec.x), p.getFloat("up.y", vec.y), p.getFloat("up.z", vec.z)));
		orthographicCamera.up.set(vec);
		orthographicCamera.update();

		PreferencesNode node3d = rootPreferences.node("camera3d");
		vec.set(perspectiveCamera.position);
		node3d.preferences(p -> vec.set(p.getFloat("position.x", vec.x), p.getFloat("position.y", vec.y),
				p.getFloat("position.z", vec.z)));
		perspectiveCamera.position.set(vec);

		vec.set(perspectiveCamera.direction);
		node3d.preferences(p -> vec.set(p.getFloat("direction.x", vec.x), p.getFloat("direction.y", vec.y),
				p.getFloat("direction.z", vec.z)));
		perspectiveCamera.direction.set(vec);

		vec.set(perspectiveCamera.up);
		node3d.preferences(
				p -> vec.set(p.getFloat("up.x", vec.x), p.getFloat("up.y", vec.y), p.getFloat("up.z", vec.z)));
		perspectiveCamera.up.set(vec);
		perspectiveCamera.update();
	}

	private void initCameraSelection(int cameraOrdinal) {
		CameraType[] values = CameraType.values();
		CameraType selected = values.length > cameraOrdinal ? values[cameraOrdinal] : camera3d;
		switchCamera(selected);
	}

	@Override
	public void onEditorPreClose() {
		if (rootPreferences == null) {
			return;
		}

		Vector3 pos = orthographicCamera.position;
		Vector3 dir = orthographicCamera.direction;
		Vector3 up = orthographicCamera.up;
		rootPreferences.node("camera2d").putFloat("position.x", pos.x).putFloat("position.y", pos.y)
				.putFloat("zoom", orthographicCamera.zoom).putFloat("direction.x", dir.x).putFloat("direction.y", dir.y)
				.putFloat("direction.z", dir.z).putFloat("up.x", up.x).putFloat("up.y", up.y).putFloat("up.z", up.z);

		pos = perspectiveCamera.position;
		dir = perspectiveCamera.direction;
		up = perspectiveCamera.up;
		rootPreferences.node("camera3d").putFloat("position.x", pos.x).putFloat("position.y", pos.y)
				.putFloat("position.z", pos.z).putFloat("direction.x", dir.x).putFloat("direction.y", dir.y)
				.putFloat("direction.z", dir.z).putFloat("up.x", up.x).putFloat("up.y", up.y).putFloat("up.z", up.z);
	}

	@Override
	public void onEditorClose() {
		Workbench.deactivate(cameraTypeSelector);
		Workbench.deactivate(inputController);
		Workbench.removeListener(extensionRegistry);
		EventService.unsubscribe(editorId, this);
	}
}
