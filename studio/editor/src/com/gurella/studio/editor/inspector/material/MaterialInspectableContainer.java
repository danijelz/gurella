package com.gurella.studio.editor.inspector.material;

import static org.eclipse.ui.forms.widgets.ExpandableComposite.NO_TITLE_FOCUS_BOX;
import static org.eclipse.ui.forms.widgets.ExpandableComposite.SHORT_TITLE_BAR;

import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DirectionalLightsAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Disposable;
import com.gurella.engine.base.resource.ResourceService;
import com.gurella.engine.graphics.material.MaterialDescriptor;
import com.gurella.engine.utils.Values;
import com.gurella.studio.editor.GurellaStudioPlugin;
import com.gurella.studio.editor.common.UiUtils;
import com.gurella.studio.editor.inspector.InspectableContainer;
import com.gurella.studio.editor.inspector.InspectorView;
import com.gurella.studio.editor.inspector.ModelInspectableContainer;
import com.gurella.studio.editor.scene.Compass;
import com.gurella.studio.editor.swtgl.LwjglGL20;
import com.gurella.studio.editor.swtgl.SwtLwjglGraphics;
import com.gurella.studio.editor.swtgl.SwtLwjglInput;

public class MaterialInspectableContainer extends InspectableContainer<IFile> {
	private ModelBuilder builder;
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

	private MaterialDescriptor materialDescriptor;

	public MaterialInspectableContainer(InspectorView parent, IFile target) {
		super(parent, target);

		materialDescriptor = ResourceService.load(target.getLocation().toString());

		Composite body = getBody();
		body.setLayout(new GridLayout(2, false));
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		toolkit.adapt(this);

		Section group = toolkit.createSection(body, ExpandableComposite.TWISTIE | SHORT_TITLE_BAR | NO_TITLE_FOCUS_BOX);
		group.setText("Diffuse");
		toolkit.adapt(group);
		group.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));

		group.setLayout(new GridLayout());
		ColorTextureAttributeEditor attributeEditor = new ColorTextureAttributeEditor(group, materialDescriptor,
				() -> materialDescriptor.diffuseColor, c -> materialDescriptor.diffuseColor = c,
				() -> materialDescriptor.diffuseTexture, this::refreshMaterial);
		attributeEditor.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		group.setClient(attributeEditor);
		group.setExpanded(true);

		/////////////

		group = toolkit.createSection(body, ExpandableComposite.TWISTIE | SHORT_TITLE_BAR | NO_TITLE_FOCUS_BOX);
		group.setText("Blend");
		toolkit.adapt(group);
		group.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));

		group.setLayout(new GridLayout());
		BlendAttributeEditor blendAttributeEditor = new BlendAttributeEditor(group, materialDescriptor,
				() -> materialDescriptor.blend, this::refreshMaterial);
		blendAttributeEditor.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		group.setClient(blendAttributeEditor);
		group.setExpanded(true);
		//////////

		/////////////

		group = toolkit.createSection(body, ExpandableComposite.TWISTIE | SHORT_TITLE_BAR | NO_TITLE_FOCUS_BOX);
		group.setText("Specular");
		toolkit.adapt(group);
		group.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));

		group.setLayout(new GridLayout());
		attributeEditor = new ColorTextureAttributeEditor(group, materialDescriptor,
				() -> materialDescriptor.specularColor, c -> materialDescriptor.specularColor = c,
				() -> materialDescriptor.specularTexture, this::refreshMaterial);
		attributeEditor.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		group.setClient(attributeEditor);
		group.setExpanded(true);
		//////////

		/////////////

		group = toolkit.createSection(body, ExpandableComposite.TWISTIE | SHORT_TITLE_BAR | NO_TITLE_FOCUS_BOX);
		group.setText("Emissive");
		toolkit.adapt(group);
		group.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));

		group.setLayout(new GridLayout());
		attributeEditor = new ColorTextureAttributeEditor(group, materialDescriptor,
				() -> materialDescriptor.emissiveColor, c -> materialDescriptor.emissiveColor = c,
				() -> materialDescriptor.emissiveTexture, this::refreshMaterial);
		attributeEditor.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		group.setClient(attributeEditor);
		group.setExpanded(true);
		//////////

		Label separator = toolkit.createLabel(body, "", SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		toolkit.createLabel(body, "Shininess:");
		Text shininess = UiUtils.createFloatWidget(body);
		shininess.addModifyListener(e -> updateShininess(shininess.getText()));

		GLData glData = new GLData();
		glData.redSize = 8;
		glData.greenSize = 8;
		glData.blueSize = 8;
		glData.alphaSize = 8;
		glData.depthSize = 16;
		glData.stencilSize = 0;
		glData.samples = 0;
		glData.doubleBuffer = false;
		SwtLwjglGraphics graphics = (SwtLwjglGraphics) Gdx.graphics;
		glData.shareContext = graphics.getGlCanvas();

		glCanvas = new GLCanvas(body, SWT.FLAT, glData);
		glCanvas.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));

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

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.8f, 0.8f, 1f));
		environment.set(new DepthTestAttribute());
		DirectionalLightsAttribute directionalAttribute = new DirectionalLightsAttribute();
		directionalAttribute.lights.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
		environment.set(directionalAttribute);

		synchronized (ModelInspectableContainer.mutex) {
			glCanvas.setCurrent();
			Gdx.gl20 = gl20;
			modelBatch = new ModelBatch();
			material = materialDescriptor.createMaterial();
			builder = new ModelBuilder();
			model = createModel();
			instance = new ModelInstance(model);
			compass = new Compass(cam);
		}

		addDisposeListener(e -> onDispose());
		render();
	}

	private void updateShininess(String text) {
		materialDescriptor.shininess = Values.isBlank(text) ? 0 : Float.valueOf(text).floatValue();
		refreshMaterial();
	}

	private void onDispose() {
		synchronized (ModelInspectableContainer.mutex) {
			model.dispose();
			modelBatch.dispose();
			compass.dispose();
			//ResourceService.unload(materialDescriptor);
			materialDescriptor.destroy();
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
			compass.render(modelBatch);
			modelBatch.end();
		}

		getDisplay().timerExec(60, this::render);
	}

	void refreshMaterial() {
		synchronized (ModelInspectableContainer.mutex) {
			material = materialDescriptor.createMaterial();
			glCanvas.setCurrent();
			Gdx.gl20 = gl20;
			model.dispose();
			model = createModel();
			instance = new ModelInstance(model);
		}
	}

	private Model createModel() {
		return createBox();
	}

	private Model createBox() {
		VertexAttributes attributes = materialDescriptor.createVertexAttributes(true, true);

		builder.begin();
		builder.part("box", GL20.GL_TRIANGLES, attributes, material).box(0.8f, 0.8f, 0.8f);
		Model result = builder.end();

		Iterator<Disposable> disposables = result.getManagedDisposables().iterator();
		while (disposables.hasNext()) {
			Disposable disposable = disposables.next();
			if (disposable instanceof Texture) {
				disposables.remove();
			}
		}

		return result;
	}

	private Model createSphere() {
		float radius = 0.8f;
		VertexAttributes attributes = materialDescriptor.createVertexAttributes(true, true);

		builder.begin();
		builder.part("sphere", GL20.GL_TRIANGLES, attributes, material).sphere(radius, radius, radius, 30, 30);
		Model result = builder.end();

		Iterator<Disposable> disposables = result.getManagedDisposables().iterator();
		while (disposables.hasNext()) {
			Disposable disposable = disposables.next();
			if (disposable instanceof Texture) {
				disposables.remove();
			}
		}

		return result;
	}
}
