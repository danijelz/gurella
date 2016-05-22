package com.gurella.studio.editor;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputEventQueue;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.event.EventService;
import com.gurella.engine.input.InputService;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.scene.spatial.Spatial;
import com.gurella.engine.subscriptions.application.ApplicationDebugUpdateListener;
import com.gurella.engine.subscriptions.scene.update.PreRenderUpdateListener;
import com.gurella.engine.utils.Values;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.scene.Compass;
import com.gurella.studio.editor.scene.GridModelInstance;
import com.gurella.studio.editor.scene.SceneCameraInputController;
import com.gurella.studio.editor.scene.StudioRenderSystem;
import com.gurella.studio.editor.subscriptions.SceneEditorMouseSelectionListener;

final class SceneEditorApplicationListener extends ApplicationAdapter
		implements EditorMessageListener, SceneEditorMouseSelectionListener {
	private final InputEventQueue inputQueue = new InputEventQueue();

	private PerspectiveCamera perspectiveCamera;
	private SceneCameraInputController perspectiveCameraController;

	private OrthographicCamera orthographicCamera;
	private SceneCameraInputController orthographicCameraController;

	private Camera selectedCamera;
	private SceneCameraInputController selectedController;

	private ModelBatch modelBatch;
	private Environment environment;
	private Color backgroundColor = new Color(0.501960f, 0.501960f, 0.501960f, 1f);

	private GridModelInstance gridModelInstance;
	private Compass compass;

	private Scene scene;
	private StudioRenderSystem renderSystem;

	SceneNode2 selectedNode;

	private final Array<Object> tempArray = new Array<>(64);

	@Override
	public void create() {
		perspectiveCamera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		perspectiveCamera.position.set(0f, 1f, -3f);
		perspectiveCamera.lookAt(0, 0, 0);
		perspectiveCamera.near = 0.1f;
		perspectiveCamera.far = 1000;

		perspectiveCamera.update();
		selectedCamera = perspectiveCamera;

		perspectiveCameraController = new SceneCameraInputController(perspectiveCamera);
		selectedController = perspectiveCameraController;

		modelBatch = new ModelBatch();
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1f));
		environment.set(new DepthTestAttribute());
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		gridModelInstance = new GridModelInstance();
		compass = new Compass(perspectiveCamera);

		inputQueue.setProcessor(selectedController);
		InputService.addInputProcessor(inputQueue);
	}

	public void presentScene(Scene scene) {
		if (this.scene != null) {
			EventService.unsubscribe(this.scene.getInstanceId(), renderSystem);
			renderSystem.dispose();
		}

		this.scene = scene;
		renderSystem = new StudioRenderSystem(scene);

		if (scene != null) {
			EventService.subscribe(scene.getInstanceId(), renderSystem);
		}

		debugUpdate();
	}

	@Override
	public void resize(int width, int height) {
		perspectiveCamera.viewportWidth = width;
		perspectiveCamera.viewportHeight = height;
		perspectiveCamera.update();
		compass.resize(width, height);
	}

	@Override
	public void render() {
		debugUpdate();
		inputQueue.drain();
		preRender();
		renderScene();
	}

	private void debugUpdate() {
		Array<ApplicationDebugUpdateListener> listeners = Values.cast(tempArray);
		EventService.getSubscribers(ApplicationDebugUpdateListener.class, listeners);
		for (int i = 0; i < listeners.size; i++) {
			listeners.get(i).debugUpdate();
		}
		listeners.clear();
	}

	private void preRender() {
		if (scene == null) {
			return;
		}

		Array<PreRenderUpdateListener> listeners = Values.cast(tempArray);
		EventService.getSubscribers(scene.getInstanceId(), PreRenderUpdateListener.class, listeners);
		for (int i = 0; i < listeners.size; i++) {
			listeners.get(i).onPreRenderUpdate();
		}
		listeners.clear();
	}

	public void renderScene() {
		synchronized (GurellaStudioPlugin.glMutex) {
			selectedController.update();
			Color color = backgroundColor;
			Gdx.gl.glClearColor(color.r, color.g, color.b, color.a);
			Gdx.gl.glClearStencil(0);
			Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT);
			Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			modelBatch.begin(selectedCamera);
			modelBatch.render(gridModelInstance, environment);
			compass.render(modelBatch);
			modelBatch.end();
			if (scene != null) {
				renderSystem.renderScene(selectedCamera);
			}
		}
	}

	@Override
	public void handleMessage(Object source, Object message) {
		if (renderSystem != null) {
			renderSystem.handleMessage(source, message);
		}
	}

	@Override
	public void onMouseSelection(float x, float y) {
		if (scene == null) {
			return;
		}

		selectedCamera.update(true);
		Ray pickRay = selectedCamera.getPickRay(x, y);
		Array<Spatial> spatials = Values.cast(tempArray);
		scene.spatialPartitioningSystem.getSpatials(pickRay, spatials, null);

		if (spatials.size > 0) {
			Spatial spatial = spatials.get(0);
			SceneNode2 node = spatial.renderableComponent.getNode();
		}

		spatials.clear();
	}

	@Override
	public void dispose() {
		SceneEditorUtils.unsubscribe(this);
		if (this.scene != null) {
			EventService.unsubscribe(this.scene.getInstanceId(), renderSystem);
			renderSystem.dispose();
		}
	}
}