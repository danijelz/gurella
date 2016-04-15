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
import com.badlogic.gdx.utils.Disposable;
import com.gurella.engine.scene.Scene;

public class SceneRenderer2 implements Disposable {
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

	private Scene scene;

	public SceneRenderer2() {
		perspectiveCamera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		perspectiveCamera.position.set(0f, 4f, 2f);
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
		renderScene();
		modelBatch.end();
	}

	private void renderScene() {
		if (scene == null) {
			return;
		}

		// TODO Auto-generated method stub

	}

	public void setScene(Scene scene) {
		this.scene = scene;
	}

	@Override
	public void dispose() {
		modelBatch.dispose();
	}
}
