package com.gurella.studio.editor.scene;

import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.subscriptions.application.ApplicationDebugRenderListener;

public class SceneRenderer implements Disposable {
	private PerspectiveCamera perspectiveCamera;
	private CameraInputController perspectiveCameraController;

	private OrthographicCamera orthographicCamera;
	private CameraInputController orthographicCameraController;

	private Camera selectedCamera;
	private CameraInputController selectedController;

	private ModelBatch modelBatch;
	private Environment environment;
	private Color backgroundColor = new Color(0.501960f, 0.501960f, 0.501960f, 1f);

	private GridModelInstance gridModelInstance;
	private Compass compass;

	private Scene scene;

	private final Array<ApplicationDebugRenderListener> listeners = new Array<>(64);

	public SceneRenderer() {
		perspectiveCamera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		perspectiveCamera.position.set(0f, 1f, -3f);
		perspectiveCamera.lookAt(0, 0, 0);
		perspectiveCamera.near = 0.1f;
		perspectiveCamera.far = 1000;

		perspectiveCamera.update();
		selectedCamera = perspectiveCamera;

		perspectiveCameraController = new CameraInputController(perspectiveCamera);
		Gdx.input.setInputProcessor(perspectiveCameraController);
		selectedController = perspectiveCameraController;

		modelBatch = new ModelBatch();
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1f));
		environment.set(new DepthTestAttribute());
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		gridModelInstance = new GridModelInstance();
		compass = new Compass(perspectiveCamera);
	}

	public void render() {
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
		renderScene();
		//TODO input should be handled 
		Gdx.input.setInputProcessor(perspectiveCameraController);
	}

	private void renderScene() {
		if (scene != null) {
			EventService.getSubscribers(ApplicationDebugRenderListener.class, listeners);
			for (int i = 0; i < listeners.size; i++) {
				listeners.get(i).debugRender(selectedCamera);
			}
			listeners.clear();
		}
	}

	public void setScene(Scene scene) {
		this.scene = scene;
	}

	@Override
	public void dispose() {
		modelBatch.dispose();
	}

	public void resize(int width, int height) {
		perspectiveCamera.viewportWidth = width;
		perspectiveCamera.viewportHeight = height;
		perspectiveCamera.update();
		compass.resize(width, height);
	}
}
