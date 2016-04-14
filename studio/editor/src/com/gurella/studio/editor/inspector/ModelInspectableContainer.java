package com.gurella.studio.editor.inspector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.model.data.ModelData;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.TextureProvider;
import com.gurella.studio.editor.GurellaStudioPlugin;
import com.gurella.studio.editor.model.ModelEditorContainer;
import com.gurella.studio.editor.swtgl.LwjglGL20;
import com.gurella.studio.editor.swtgl.SwtLwjglInput;
import com.gurella.studio.editor.utils.FolderRelativeFileHandleResolver;

public class ModelInspectableContainer extends InspectableContainer<IFile> {
	private ModelEditorContainer<Object> propertiesContainer;
	private GLCanvas glCanvas;

	private LwjglGL20 gl20 = new LwjglGL20();
	private SwtLwjglInput input;

	private PerspectiveCamera cam;
	private CameraInputController camController;
	private ModelBatch modelBatch;
	private Environment environment;
	private ModelInstance modelInstance;

	public ModelInspectableContainer(InspectorView parent, IFile target) {
		super(parent, target);
		Composite body = getBody();
		body.setLayout(new GridLayout(1, false));
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		toolkit.adapt(this);

		FolderRelativeFileHandleResolver resolver = new FolderRelativeFileHandleResolver((IFolder) target.getParent());
		ModelLoader<?> loader = new ObjLoader(resolver);
		FileHandle fileHandle = new FileHandle(target.getLocation().toFile());
		ModelData modelData = loader.loadModelData(fileHandle);
		final Model model = new Model(modelData, new TextureProvider() {
			@Override
			public Texture load(String fileName) {
				Texture result = new Texture(new FileHandle(fileName));
				result.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
				result.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
				return result;
			}
		});
		modelInstance = new ModelInstance(model);

		GLData glData = new GLData();
		glData.redSize = 8;
		glData.greenSize = 8;
		glData.blueSize = 8;
		glData.alphaSize = 8;
		glData.depthSize = 16;
		glData.stencilSize = 0;
		glData.samples = 0;
		glData.doubleBuffer = false; // TODO
		
		glCanvas = new GLCanvas(body, SWT.FLAT, glData);
		glCanvas.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		Point size = glCanvas.getSize();
		cam = new PerspectiveCamera(67, size.x, size.y);
		cam.position.set(1f, 1f, 1f);
		cam.lookAt(0, 0, 0);
		cam.near = 1f;
		cam.far = 300f;
		cam.update();
		
		glCanvas.addListener(SWT.Resize, e -> updateSizeByParent());
		
		camController = new CameraInputController(cam);
		input = new SwtLwjglInput(glCanvas);
		input.setInputProcessor(camController);

		modelBatch = new ModelBatch();
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f, 1f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		render();
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
		glCanvas.setCurrent();

		Point size = glCanvas.getSize();
		gl20.glClearColor(1, 1, 1, 1);
		gl20.glViewport(0, 0, size.x, size.y);
		gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		modelBatch.begin(cam);
		modelBatch.render(modelInstance, environment);
		modelBatch.end();

		glCanvas.getDisplay().timerExec(60, this::render);
	}
}
