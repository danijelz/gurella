package com.gurella.studio.editor.scene;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.gurella.engine.scene.Scene;
import com.gurella.studio.editor.inspector.ModelInspectableContainer;
import com.gurella.studio.editor.swtgl.LwjglGL20;
import com.gurella.studio.editor.swtgl.SwtLwjglInput;

public class SceneRenderer {
	private GLCanvas glCanvas;
	private SwtLwjglInput input;
	private GL20 gl20 = new LwjglGL20();
	private GL30 gl30;

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

	public SceneRenderer(Composite parent) {
		GLData glData = new GLData();
		glData.redSize = 8;
		glData.greenSize = 8;
		glData.blueSize = 8;
		glData.alphaSize = 8;
		glData.depthSize = 16;
		glData.stencilSize = 0;
		glData.samples = 0;
		glData.doubleBuffer = false;

		glCanvas = new GLCanvas(parent, SWT.FLAT, glData);
		glCanvas.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		Gdx.gl20 = gl20;
		Gdx.gl30 = gl30;

		Point size = glCanvas.getSize();
		perspectiveCamera = new PerspectiveCamera(67, size.x, size.y);
		perspectiveCamera.position.set(0f, 4f, 4f);
		perspectiveCamera.lookAt(0, 0, 0);
		perspectiveCamera.near = 0.1f;
		perspectiveCamera.far = 1000;
		perspectiveCamera.update();

		perspectiveCameraController = new CameraInputController(perspectiveCamera);
		input = new SwtLwjglInput(glCanvas);
		input.setInputProcessor(perspectiveCameraController);

		modelBatch = new ModelBatch();
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1f));
		environment.set(new DepthTestAttribute());
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
		
		gridModelInstance = new GridModelInstance();

		parent.addDisposeListener(e -> onDispose());
		render();
	}

	private void render() {
		if (glCanvas.isDisposed()) {
			return;
		}

		input.update();
		selectedController.update();

		synchronized (ModelInspectableContainer.mutex) {
			Gdx.gl20 = gl20;
			Gdx.gl30 = gl30;
			glCanvas.setCurrent();
			Point size = glCanvas.getSize();
			Color color = backgroundColor;
			gl20.glClearColor(color.r, color.g, color.b, color.a);
			Gdx.gl.glClearStencil(0);
			gl20.glEnable(GL20.GL_DEPTH_TEST);
			gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT);
			gl20.glViewport(0, 0, size.x, size.y);
			modelBatch.begin(selectedCamera);
			modelBatch.render(gridModelInstance, environment);
			renderScene();
			modelBatch.end();
		}

		glCanvas.getDisplay().timerExec(60, this::render);
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

	private void onDispose() {
		synchronized (ModelInspectableContainer.mutex) {
			glCanvas.dispose();
			modelBatch.dispose();
		}
	}
}
