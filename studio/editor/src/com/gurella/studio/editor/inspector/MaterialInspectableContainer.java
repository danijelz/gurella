package com.gurella.studio.editor.inspector;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.gurella.engine.base.resource.ResourceService;
import com.gurella.engine.graphics.material.MaterialDescriptor;
import com.gurella.engine.math.geometry.shape.Sphere;
import com.gurella.studio.editor.scene.Compass;
import com.gurella.studio.editor.swtgl.LwjglGL20;
import com.gurella.studio.editor.swtgl.SwtLwjglInput;

public class MaterialInspectableContainer extends InspectableContainer<IFile> {
	private MaterialDescriptor materialDescriptor;

	private Sphere sphere;
	private Model model;
	private ModelInstance instance;
	private Material material;

	private GLCanvas glCanvas;

	private LwjglGL20 gl20 = new LwjglGL20();
	private SwtLwjglInput input;

	private PerspectiveCamera cam;
	private CameraInputController camController;
	private ModelBatch modelBatch;
	private Environment environment;
	private Color backgroundColor = new Color(0.501960f, 0.501960f, 0.501960f, 1f);
	private Compass compass;

	public MaterialInspectableContainer(InspectorView parent, IFile target) {
		super(parent, target);
		
		materialDescriptor = ResourceService.load(target.getLocation().toString());

		Composite body = getBody();

		GLData glData = new GLData();
		glData.redSize = 8;
		glData.greenSize = 8;
		glData.blueSize = 8;
		glData.alphaSize = 8;
		glData.depthSize = 16;
		glData.stencilSize = 0;
		glData.samples = 0;
		glData.doubleBuffer = false;

		glCanvas = new GLCanvas(body, SWT.FLAT, glData);
		glCanvas.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		Point size = glCanvas.getSize();
		cam = new PerspectiveCamera(67, size.x, size.y);
		cam.position.set(0f, 1f, -1f);
		cam.lookAt(0, 0, 0);
		cam.near = 0.1f;
		cam.far = 1000;
		cam.update();

		glCanvas.addListener(SWT.Resize, e -> updateSizeByParent());

		camController = new CameraInputController(cam);
		input = new SwtLwjglInput(glCanvas);
		input.setInputProcessor(camController);

		modelBatch = new ModelBatch();
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1f));
		environment.set(new DepthTestAttribute());
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		synchronized (ModelInspectableContainer.mutex) {
			glCanvas.setCurrent();
			sphere = new Sphere();
			sphere.setRadius(1.2f);
			material = materialDescriptor.createMaterial();
			ModelBuilder builder = new ModelBuilder();
			float radius = sphere.getRadius();
			model = builder.createSphere(radius, radius, radius, 30, 30, material, Usage.Position | Usage.Normal);
			instance = new ModelInstance(model);
			compass = new Compass(cam);
		}

		addDisposeListener(e -> onDispose());
		render();
	}

	private void onDispose() {
		synchronized (ModelInspectableContainer.mutex) {
			model.dispose();
			modelBatch.dispose();
			compass.dispose();
			glCanvas.dispose();
		}
	}

	private void updateSizeByParent() {
		Point size = glCanvas.getSize();
		cam.viewportWidth = size.x;
		cam.viewportHeight = size.y;
		cam.update(true);
	}

	private void render() {
		input.update();
		camController.update();
		synchronized (ModelInspectableContainer.mutex) {
			if (glCanvas.isDisposed()) {
				return;
			}

			glCanvas.setCurrent();
			Gdx.gl20 = gl20;
			Point size = glCanvas.getSize();
			Color color = backgroundColor;
			gl20.glClearColor(color.r, color.g, color.b, color.a);
			gl20.glEnable(GL20.GL_DEPTH_TEST);
			gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
			gl20.glViewport(0, 0, size.x, size.y);
			modelBatch.begin(cam);
			modelBatch.render(instance, environment);
			modelBatch.end();
		}

		getDisplay().timerExec(60, this::render);
	}
	
	private void refreshMaterial() {
		material = materialDescriptor.createMaterial();
		
		synchronized (ModelInspectableContainer.mutex) {
			glCanvas.setCurrent();
			model.dispose();
			ModelBuilder builder = new ModelBuilder();
			float radius = sphere.getRadius();
			model = builder.createSphere(radius, radius, radius, 30, 30, material, Usage.Position | Usage.Normal);
			instance = new ModelInstance(model);
		}
	}
	
	private class TextureAttributeEditor extends Composite {
		public TextureAttributeEditor(Composite parent) {
			super(parent, SWT.NONE);
		}
		
	}
}
