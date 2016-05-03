package com.gurella.studio.editor.inspector.material;

import static org.eclipse.ui.forms.widgets.ExpandableComposite.NO_TITLE_FOCUS_BOX;
import static org.eclipse.ui.forms.widgets.ExpandableComposite.SHORT_TITLE_BAR;

import java.util.Arrays;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.IExpansionListener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DirectionalLightsAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.gurella.engine.base.resource.ResourceService;
import com.gurella.engine.graphics.material.MaterialDescriptor;
import com.gurella.engine.utils.Values;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.common.UiUtils;
import com.gurella.studio.editor.inspector.InspectableContainer;
import com.gurella.studio.editor.inspector.InspectorView;
import com.gurella.studio.editor.scene.Compass;
import com.gurella.studio.editor.swtgl.LwjglGL20;
import com.gurella.studio.editor.swtgl.SwtLwjglGraphics;
import com.gurella.studio.editor.swtgl.SwtLwjglInput;

public class MaterialInspectableContainer extends InspectableContainer<IFile> {
	private ModelBuilder builder;

	private Model wall;
	private ModelInstance wallInstance;

	private Model model;
	private ModelInstance instance;
	private Material material;

	private GLCanvas glCanvas;
	private Button sphereButton;
	private Button boxButton;

	private ModelShape modelShape = ModelShape.sphere;

	private LwjglGL20 gl20 = new LwjglGL20();
	private SwtLwjglInput input;

	private PerspectiveCamera cam;
	//private CameraInputController camController;
	private ModelInputController modelInputController;

	private ModelBatch modelBatch;
	private Environment environment;
	private Color backgroundColor = new Color(0.501960f, 0.501960f, 0.501960f, 1f);
	private Compass compass;

	private MaterialDescriptor materialDescriptor;

	public MaterialInspectableContainer(InspectorView parent, IFile target) {
		super(parent, target);

		materialDescriptor = ResourceService.load(target.getLocation().toString());

		Composite body = getBody();
		body.setLayout(new GridLayout());
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		toolkit.adapt(this);

		/*Composite root = toolkit.createComposite(body);
		toolkit.adapt(root);
		GridData layoutData = new GridData(GridData.FILL, GridData.FILL, true, true);
		layoutData.heightHint = 500;
		root.setLayoutData(layoutData);
		root.setLayout(new GridLayout());*/

		ScrolledComposite scrolledComposite = new ScrolledComposite(body, SWT.V_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		GridData layoutData = new GridData(GridData.FILL, GridData.BEGINNING, true, false);
		scrolledComposite.setLayoutData(layoutData);
		toolkit.adapt(scrolledComposite);

		Composite content = toolkit.createComposite(scrolledComposite);
		toolkit.adapt(content);
		content.setLayout(new GridLayout(2, false));
		scrolledComposite.setContent(content);
		//content.setSize(content.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		scrolledComposite.setMinSize(200, 100);

		//////////////////////////////

		IExpansionListener expansionListener = new ExpansionAdapter() {
			@Override
			public void expansionStateChanged(ExpansionEvent e) {
				//content.setSize(content.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			}
		};

		/////////////////////////
		Section group = toolkit.createSection(content,
				ExpandableComposite.TWISTIE | SHORT_TITLE_BAR | NO_TITLE_FOCUS_BOX);
		group.setText("Diffuse");
		toolkit.adapt(group);
		group.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));

		group.setLayout(new GridLayout());
		ColorTextureAttributeEditor attributeEditor = new ColorTextureAttributeEditor(group, materialDescriptor,
				() -> materialDescriptor.diffuseColor, c -> materialDescriptor.diffuseColor = c,
				() -> materialDescriptor.diffuseTexture, this::refreshMaterial);
		attributeEditor.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		group.setClient(attributeEditor);
		//group.setExpanded(true);
		group.addExpansionListener(expansionListener);

		/////////////
		group = toolkit.createSection(content, ExpandableComposite.TWISTIE | SHORT_TITLE_BAR | NO_TITLE_FOCUS_BOX);
		group.setText("Blend");
		toolkit.adapt(group);
		group.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));

		group.setLayout(new GridLayout());
		BlendAttributeEditor blendAttributeEditor = new BlendAttributeEditor(group, materialDescriptor,
				() -> materialDescriptor.blend, this::refreshMaterial);
		blendAttributeEditor.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		group.setClient(blendAttributeEditor);
		//group.setExpanded(true);
		group.addExpansionListener(expansionListener);
		//////////

		/////////////
		group = toolkit.createSection(content, ExpandableComposite.TWISTIE | SHORT_TITLE_BAR | NO_TITLE_FOCUS_BOX);
		group.setText("Specular");
		toolkit.adapt(group);
		group.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
		group.setLayout(new GridLayout());
		
		Composite client = toolkit.createComposite(group);
		client.setLayout(new GridLayout(2, false));
		
		toolkit.createLabel(client, "Shininess:");
		Text shininess = UiUtils.createFloatWidget(client);
		shininess.addModifyListener(e -> updateShininess(shininess.getText()));

		attributeEditor = new ColorTextureAttributeEditor(client, materialDescriptor,
				() -> materialDescriptor.specularColor, c -> materialDescriptor.specularColor = c,
				() -> materialDescriptor.specularTexture, this::refreshMaterial);
		attributeEditor.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
		group.setClient(client);
		//group.setExpanded(true);
		group.addExpansionListener(expansionListener);
		//////////

		/////////////
		group = toolkit.createSection(content, ExpandableComposite.TWISTIE | SHORT_TITLE_BAR | NO_TITLE_FOCUS_BOX);
		group.setText("Emissive");
		toolkit.adapt(group);
		group.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));

		group.setLayout(new GridLayout());
		attributeEditor = new ColorTextureAttributeEditor(group, materialDescriptor,
				() -> materialDescriptor.emissiveColor, c -> materialDescriptor.emissiveColor = c,
				() -> materialDescriptor.emissiveTexture, this::refreshMaterial);
		attributeEditor.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		group.setClient(attributeEditor);
		//group.setExpanded(true);
		group.addExpansionListener(expansionListener);
		//////////

		Composite canvasComposite = toolkit.createComposite(body);
		toolkit.adapt(canvasComposite);
		layoutData = new GridData(GridData.FILL, GridData.FILL, true, true);
		layoutData.heightHint = 300;
		canvasComposite.setLayoutData(layoutData);
		canvasComposite.setLayout(new GridLayout(2, false));

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

		glCanvas = new GLCanvas(canvasComposite, SWT.FLAT, glData);
		layoutData = new GridData(GridData.FILL, GridData.FILL, true, true);
		layoutData.minimumHeight = 300;
		layoutData.heightHint = 300;
		layoutData.verticalSpan = 2;
		glCanvas.setLayoutData(layoutData);

		Point size = glCanvas.getSize();
		cam = new PerspectiveCamera(67, size.x, size.y);
		cam.position.set(0f, 0.5f, -1f);
		cam.lookAt(0, 0, 0);
		cam.near = 0.1f;
		cam.far = 1000;
		cam.update();

		glCanvas.addListener(SWT.Resize, e -> updateSizeByParent());

		sphereButton = toolkit.createButton(canvasComposite, "S", SWT.PUSH);
		sphereButton.addListener(SWT.Selection, e -> updateModelType(ModelShape.sphere));
		sphereButton.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));

		boxButton = toolkit.createButton(canvasComposite, "B", SWT.PUSH);
		boxButton.addListener(SWT.Selection, e -> updateModelType(ModelShape.box));
		boxButton.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));

		//camController = new CameraInputController(cam);
		modelInputController = new ModelInputController();
		input = new SwtLwjglInput(glCanvas);
		//input.setInputProcessor(camController);
		input.setInputProcessor(modelInputController);

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1f));
		environment.set(new DepthTestAttribute());
		DirectionalLightsAttribute directionalAttribute = new DirectionalLightsAttribute();
		directionalAttribute.lights.add(new DirectionalLight().set(0.6f, 0.6f, 0.6f, -1f, -0.8f, -0.2f));
		environment.set(directionalAttribute);

		synchronized (GurellaStudioPlugin.glMutex) {
			glCanvas.setCurrent();
			Gdx.gl20 = gl20;

			DefaultShaderProvider provider = new DefaultShaderProvider(getDefaultVertexShader(), getDefaultFragmentShader());
			modelBatch = new ModelBatch(provider);
			builder = new ModelBuilder();

			wall = createWall();
			wallInstance = new ModelInstance(wall);
			Matrix4 transform = wallInstance.transform;
			transform.idt().rotate(0, 1, 0, 45).translate(-0.6f, -0.6f, 0.6f);

			material = materialDescriptor.createMaterial();
			model = createModel();
			instance = new ModelInstance(model);
			modelInputController.instance = instance;

			compass = new Compass(cam);
		}

		addDisposeListener(e -> onDispose());
		render();
	}

	public static String getDefaultVertexShader() {
		return Gdx.files.classpath("com/gurella/engine/graphics/gl/standard.vertex.glsl").readString();
	}

	public static String getDefaultFragmentShader() {
		return Gdx.files.classpath("com/gurella/engine/graphics/gl/standard.fragment.glsl").readString();
	}

	private void updateModelType(ModelShape newShape) {
		if (modelShape != newShape) {
			modelShape = newShape;
			synchronized (GurellaStudioPlugin.glMutex) {
				glCanvas.setCurrent();
				Gdx.gl20 = gl20;
				model.dispose();
				model = createModel();
				instance = new ModelInstance(model);
				modelInputController.instance = instance;
			}
		}
	}

	private void updateShininess(String text) {
		materialDescriptor.shininess = Values.isBlank(text) ? 1 : Float.valueOf(text).floatValue();
		refreshMaterial();
	}

	private void onDispose() {
		synchronized (GurellaStudioPlugin.glMutex) {
			wall.dispose();
			model.dispose();
			modelBatch.dispose();
			compass.dispose();
			//TODO ResourceService.unload(materialDescriptor);
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
		//camController.update();
		synchronized (GurellaStudioPlugin.glMutex) {
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

			gl20.glViewport(0, 0, size.x, size.y);
			modelBatch.begin(cam);
			modelBatch.render(wallInstance, environment);
			modelBatch.render(instance, environment);
			compass.render(modelBatch);
			modelBatch.end();
		}

		getDisplay().timerExec(60, this::render);
	}

	void refreshMaterial() {
		synchronized (GurellaStudioPlugin.glMutex) {
			material = materialDescriptor.createMaterial();
			glCanvas.setCurrent();
			Gdx.gl20 = gl20;
			Matrix4 transform = new Matrix4(instance.transform);
			model.dispose();
			model = createModel();
			instance = new ModelInstance(model);
			instance.transform.set(transform);
			modelInputController.instance = instance;
		}
	}

	private Model createModel() {
		switch (modelShape) {
		case sphere:
			return createSphere();
		case box:
			return createBox();
		default:
			return createSphere();
		}
	}

	private Model createBox() {
		VertexAttributes attributes = materialDescriptor.createVertexAttributes(true, true);
		builder.begin();
		builder.part("box", GL20.GL_TRIANGLES, attributes, material).box(0.6f, 0.6f, 0.6f);
		Model result = builder.end();
		if(materialDescriptor.isNormalTextureEnabled()) {
			calculateTangents(result);
		} else {
			calculateTangents(result);
		}
		return removeDisposables(result);
	}

	private static void calculateTangents(Model result) {
		Array<Mesh> meshes = result.meshes;
		for(Mesh mesh : meshes) {
			int numVertices = mesh.getNumVertices();
			float[] vertices = mesh.getVertices(new float[numVertices]);
			//System.out.println(Arrays.toString(vertices));
		}
		// TODO Auto-generated method stub
		
	}

	private Model createSphere() {
		float radius = 0.8f;
		VertexAttributes attributes = materialDescriptor.createVertexAttributes(true, true);
		builder.begin();
		builder.part("sphere", GL20.GL_TRIANGLES, attributes, material).sphere(radius, radius, radius, 90, 90);
		Model result = builder.end();
		if(materialDescriptor.isNormalTextureEnabled()) {
			calculateTangents(result);
		} else {
			calculateTangents(result);
		}
		return removeDisposables(result);
	}

	private Model createWall() {
		builder.begin();
		int usage = Usage.Position | Usage.Normal;

		Material wallMaterialGrey = new Material();
		wallMaterialGrey.set(ColorAttribute.createDiffuse(0.1f, 0.1f, 0.1f, 1));

		Material wallMaterialWhite = new Material();
		wallMaterialWhite.set(ColorAttribute.createDiffuse(1, 1, 1, 1));

		Vector3 normal = new Vector3(0, 0, -1);
		for (int i = 0; i < 30; i++) {
			for (int j = 0; j < 30; j++) {
				float x = i * 0.2f;
				float y = j * 0.2f;

				Vector3 corner00 = new Vector3(x, y, 0);
				Vector3 corner10 = new Vector3(x, y + 0.2f, 0);
				Vector3 corner11 = new Vector3(x + 0.2f, y + 0.2f, 0);
				Vector3 corner01 = new Vector3(x + 0.2f, y, 0);
				Material wallMaterial = (i + j) % 2 == 0 ? wallMaterialGrey : wallMaterialWhite;
				builder.part("box", GL20.GL_TRIANGLES, usage, wallMaterial).rect(corner00, corner10, corner11, corner01,
						normal);
			}
		}

		normal = new Vector3(-1, 0, 0);
		for (int i = 0; i < 30; i++) {
			for (int j = 0; j < 30; j++) {
				float y = i * 0.2f;
				float z = j * -0.2f;

				Vector3 corner00 = new Vector3(0, y, z);
				Vector3 corner10 = new Vector3(0, y, z - 0.2f);
				Vector3 corner11 = new Vector3(0, y + 0.2f, z - 0.2f);
				Vector3 corner01 = new Vector3(0, y + 0.2f, z);
				Material wallMaterial = (i + j) % 2 == 1 ? wallMaterialGrey : wallMaterialWhite;
				builder.part("box", GL20.GL_TRIANGLES, usage, wallMaterial).rect(corner00, corner10, corner11, corner01,
						normal);
			}
		}

		normal = new Vector3(0, 1, 0);
		for (int i = 0; i < 30; i++) {
			for (int j = 0; j < 30; j++) {
				float x = i * 0.2f;
				float z = j * -0.2f;

				Vector3 corner00 = new Vector3(x, 0, z);
				Vector3 corner10 = new Vector3(x + 0.2f, 0, z);
				Vector3 corner11 = new Vector3(x + 0.2f, 0, z - 0.2f);
				Vector3 corner01 = new Vector3(x, 0, z - 0.2f);
				Material wallMaterial = (i + j) % 2 == 1 ? wallMaterialGrey : wallMaterialWhite;
				builder.part("box", GL20.GL_TRIANGLES, usage, wallMaterial).rect(corner00, corner10, corner11, corner01,
						normal);
			}
		}

		Model result = builder.end();
		return removeDisposables(result);
	}

	private static Model removeDisposables(Model result) {
		Iterator<Disposable> disposables = result.getManagedDisposables().iterator();
		while (disposables.hasNext()) {
			Disposable disposable = disposables.next();
			if (disposable instanceof Texture) {
				disposables.remove();
			}
		}
		return result;
	}

	private static enum ModelShape {
		sphere, box;
	}
}
