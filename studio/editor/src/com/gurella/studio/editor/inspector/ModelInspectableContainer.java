package com.gurella.studio.editor.inspector;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.model.data.ModelData;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.TextureProvider;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.UBJsonReader;
import com.gurella.engine.asset.properties.G3dModelProperties;
import com.gurella.engine.asset.properties.ModelProperties;
import com.gurella.engine.asset.properties.ObjModelProperties;
import com.gurella.studio.editor.GurellaStudioPlugin;
import com.gurella.studio.editor.model.ModelEditorContainer;
import com.gurella.studio.editor.scene.Compass;
import com.gurella.studio.editor.swtgl.LwjglGL20;
import com.gurella.studio.editor.swtgl.SwtLwjglInput;
import com.gurella.studio.editor.utils.ContainerRelativeFileHandleResolver;

public class ModelInspectableContainer extends InspectableContainer<IFile> {
	public static final Object mutex = new Object();

	private ModelEditorContainer<ModelProperties> propertiesContainer;
	private GLCanvas glCanvas;

	private LwjglGL20 gl20 = new LwjglGL20();
	private SwtLwjglInput input;

	private PerspectiveCamera cam;
	private CameraInputController camController;
	private ModelBatch modelBatch;
	private Environment environment;
	private Model model;
	private ModelInstance modelInstance;
	private Color backgroundColor = new Color(0.501960f, 0.501960f, 0.501960f, 1f);
	private Compass compass;

	public ModelInspectableContainer(InspectorView parent, IFile target) {
		super(parent, target);
		Composite body = getBody();
		body.setLayout(new GridLayout(1, false));
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		toolkit.adapt(this);

		propertiesContainer = new ModelEditorContainer<>(getBody(), findProperties(target));
		GridData layoutData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		propertiesContainer.setLayoutData(layoutData);

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
		cam.position.set(1f, 1f, 1f);
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

		synchronized (mutex) {
			glCanvas.setCurrent();
			Gdx.gl20 = gl20;
			ModelLoader<?> loader = getLoader();
			FileHandle fileHandle = new FileHandle(target.getLocation().toFile());
			ModelData modelData = loader.loadModelData(fileHandle);
			model = new Model(modelData, new DirectTextureProvider());
			modelInstance = new ModelInstance(model);
		}
		
		compass = new Compass(cam);

		addDisposeListener(e -> onDispose());
		render();
	}

	private ModelProperties findProperties(IFile target) {
		// TODO Auto-generated method stub
		String fileExtension = target.getFileExtension();
		if ("obj".equals(fileExtension)) {
			return new ObjModelProperties();
		} else if ("g3db".equals(fileExtension) || "g3dj".equals(fileExtension)) {
			return new G3dModelProperties();
		} else {
			throw new IllegalArgumentException("No loader for: " + target.toString());
		}
	}

	private ModelLoader<?> getLoader() {
		ContainerRelativeFileHandleResolver resolver = new ContainerRelativeFileHandleResolver(target.getParent());
		String fileExtension = target.getFileExtension();
		if ("obj".equals(fileExtension)) {
			return new ObjLoader(resolver);
		} else if ("g3db".equals(fileExtension)) {
			return new G3dModelLoader(new UBJsonReader(), resolver);
		} else if ("g3dj".equals(fileExtension)) {
			return new G3dModelLoader(new JsonReader(), resolver);
		} else {
			throw new IllegalArgumentException("No loader for: " + target.toString());
		}
	}

	private void onDispose() {
		synchronized (mutex) {
			glCanvas.dispose();
			model.dispose();
			modelBatch.dispose();
		}
	}

	private void updateSizeByParent() {
		Point size = glCanvas.getSize();
		cam.viewportWidth = size.x;
		cam.viewportHeight = size.y;
		cam.update(true);
	}

	private void render() {
		if (glCanvas.isDisposed()) {
			return;
		}

		input.update();
		camController.update();
		synchronized (mutex) {
			glCanvas.setCurrent();
			Gdx.gl20 = gl20;
			Point size = glCanvas.getSize();
			Color color = backgroundColor;
			gl20.glClearColor(color.r, color.g, color.b, color.a);
			gl20.glEnable(GL20.GL_DEPTH_TEST);
			gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
			gl20.glViewport(0, 0, size.x, size.y);
			modelBatch.begin(cam);
			modelBatch.render(modelInstance, environment);
			compass.render(modelBatch);
			modelBatch.end();
		}

		glCanvas.getDisplay().timerExec(60, this::render);
	}

	private static class DirectTextureProvider implements TextureProvider {
		@Override
		public Texture load(String fileName) {
			Texture result = new Texture(new FileHandle(fileName));
			result.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
			result.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
			return result;
		}
	}
}
