package com.gurella.studio.editor.inspector.material;

import static org.eclipse.ui.forms.widgets.ExpandableComposite.NO_TITLE_FOCUS_BOX;
import static org.eclipse.ui.forms.widgets.ExpandableComposite.SHORT_TITLE_BAR;

import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
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
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.SphereShapeBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.gurella.engine.graphics.material.MaterialDescriptor;
import com.gurella.engine.utils.Values;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.common.AssetsFolderLocator;
import com.gurella.studio.editor.inspector.InspectableContainer;
import com.gurella.studio.editor.inspector.InspectorView;
import com.gurella.studio.editor.swtgdx.GdxContext;
import com.gurella.studio.editor.swtgdx.SwtLwjglGraphics;
import com.gurella.studio.editor.swtgdx.SwtLwjglInput;
import com.gurella.studio.editor.utils.UiUtils;

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

	private SwtLwjglInput input;

	private PerspectiveCamera camera;
	private MaterialInputController materialInputController;

	private ModelBatch modelBatch;
	private Environment environment;
	private Color backgroundColor = new Color(0.501960f, 0.501960f, 0.501960f, 1f);

	private MaterialDescriptor materialDescriptor;

	public MaterialInspectableContainer(InspectorView parent, IFile target) {
		super(parent, target);
		setText(target.getName());

		materialDescriptor = editorContext.load(target);

		Composite body = getBody();
		body.setLayout(new GridLayout());
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		toolkit.adapt(this);

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
		scrolledComposite.setMinSize(200, 100);

		IFolder assetsFolder = AssetsFolderLocator.getAssetsFolder(editorContext.project);

		/////////////////////////
		Section group = toolkit.createSection(content,
				ExpandableComposite.TWISTIE | SHORT_TITLE_BAR | NO_TITLE_FOCUS_BOX);
		group.setText("Diffuse");
		toolkit.adapt(group);
		group.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));

		group.setLayout(new GridLayout());
		ColorTextureAttributeEditor attributeEditor = new ColorTextureAttributeEditor(group, materialDescriptor,
				() -> materialDescriptor.diffuseColor, c -> materialDescriptor.diffuseColor = c,
				() -> materialDescriptor.diffuseTexture, this::refreshMaterial, assetsFolder);
		attributeEditor.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		group.setClient(attributeEditor);

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
				() -> materialDescriptor.specularTexture, this::refreshMaterial, assetsFolder);
		attributeEditor.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
		group.setClient(client);

		/////////////
		group = toolkit.createSection(content, ExpandableComposite.TWISTIE | SHORT_TITLE_BAR | NO_TITLE_FOCUS_BOX);
		group.setText("Emissive");
		toolkit.adapt(group);
		group.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));

		group.setLayout(new GridLayout());
		attributeEditor = new ColorTextureAttributeEditor(group, materialDescriptor,
				() -> materialDescriptor.emissiveColor, c -> materialDescriptor.emissiveColor = c,
				() -> materialDescriptor.emissiveTexture, this::refreshMaterial, assetsFolder);
		attributeEditor.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		group.setClient(attributeEditor);

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
		camera = new PerspectiveCamera(67, size.x, size.y);
		camera.position.set(0f, 0.5f, -1f);
		camera.lookAt(0, 0, 0);
		camera.near = 0.1f;
		camera.far = 1000;
		camera.update();

		glCanvas.addListener(SWT.Resize, e -> updateSizeByParent());

		sphereButton = toolkit.createButton(canvasComposite, "S", SWT.PUSH);
		sphereButton.addListener(SWT.Selection, e -> updateModelType(ModelShape.sphere));
		sphereButton.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));

		boxButton = toolkit.createButton(canvasComposite, "B", SWT.PUSH);
		boxButton.addListener(SWT.Selection, e -> updateModelType(ModelShape.box));
		boxButton.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));

		materialInputController = new MaterialInputController();
		input = new SwtLwjglInput(glCanvas);
		input.setInputProcessor(materialInputController);

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1f));
		environment.set(new DepthTestAttribute());
		DirectionalLightsAttribute directionalAttribute = new DirectionalLightsAttribute();
		directionalAttribute.lights.add(new DirectionalLight().set(0.6f, 0.6f, 0.6f, -1f, -0.8f, -0.2f));
		environment.set(directionalAttribute);

		GdxContext.run(editorContext.editorId, this::initGlData);
		addDisposeListener(e -> GdxContext.run(editorContext.editorId, this::onDispose));
		render();
	}

	private void initGlData() {
		glCanvas.setCurrent();

		modelBatch = new ModelBatch(
				new DefaultShaderProvider(getDefaultVertexShader(), getDefaultFragmentShader()));
		builder = new ModelBuilder();

		wall = createWall();
		wallInstance = new ModelInstance(wall);
		Matrix4 transform = wallInstance.transform;
		transform.idt().rotate(0, 1, 0, 45).translate(-0.6f, -0.6f, 0.6f);

		material = materialDescriptor.getMaterial();
		model = createModel();
		instance = new ModelInstance(model);
		materialInputController.instance = instance;
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
			synchronized (SwtLwjglGraphics.glMutex) {
				glCanvas.setCurrent();
				model.dispose();
				model = createModel();
				instance = new ModelInstance(model);
				materialInputController.instance = instance;
			}
		}
	}

	private void updateShininess(String text) {
		materialDescriptor.shininess = Values.isBlank(text) ? 1 : Float.valueOf(text).floatValue();
		refreshMaterial();
	}

	private void onDispose() {
		editorContext.unload(materialDescriptor);
		wall.dispose();
		model.dispose();
		modelBatch.dispose();
		glCanvas.dispose();
	}

	private void updateSizeByParent() {
		Point size = glCanvas.getSize();
		camera.viewportWidth = size.x;
		camera.viewportHeight = size.y;
		camera.update(true);
	}

	private void render() {
		if (glCanvas.isDisposed()) {
			return;
		}

		input.update();
		synchronized (SwtLwjglGraphics.glMutex) {
			glCanvas.setCurrent();
			Point size = glCanvas.getSize();
			Color color = backgroundColor;
			Gdx.gl20.glClearColor(color.r, color.g, color.b, color.a);
			Gdx.gl20.glEnable(GL20.GL_DEPTH_TEST);
			Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
			Gdx.gl20.glViewport(0, 0, size.x, size.y);

			modelBatch.begin(camera);
			modelBatch.render(wallInstance, environment);
			modelBatch.render(instance, environment);
			modelBatch.end();
		}

		getDisplay().timerExec(60, this::render);
	}

	void refreshMaterial() {
		synchronized (SwtLwjglGraphics.glMutex) {
			editorContext.save(materialDescriptor);
			materialDescriptor.updateMaterial(material);
			glCanvas.setCurrent();
			Matrix4 transform = new Matrix4(instance.transform);
			model.dispose();
			model = createModel();
			instance = new ModelInstance(model);
			instance.transform.set(transform);
			materialInputController.instance = instance;
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

	private Model createSphere() {
		float radius = 0.8f;
		VertexAttributes attributes = materialDescriptor.createVertexAttributes(true, true);
		builder.begin();
		MeshPartBuilder partBuilder = builder.part("sphere", GL20.GL_TRIANGLES, attributes, material);
		SphereShapeBuilder.build(partBuilder, radius, radius, radius, 90, 90);
		Model result = builder.end();
		calculateTangents(result);
		return removeDisposables(result);
	}

	private Model createBox() {
		VertexAttributes attributes = materialDescriptor.createVertexAttributes(true, true);
		builder.begin();
		MeshPartBuilder partBuilder = builder.part("box", GL20.GL_TRIANGLES, attributes, material);
		BoxShapeBuilder.build(partBuilder, 0.6f, 0.6f, 0.6f);
		Model result = builder.end();
		calculateTangents(result);
		return removeDisposables(result);
	}

	// http://www.terathon.com/code/tangent.html
	private static void calculateTangents(Model result) {
		Array<Mesh> meshes = result.meshes;
		Vector3 tangent = new Vector3();
		Vector3 bitangent = new Vector3();

		for (Mesh mesh : meshes) {
			int numVertices = mesh.getNumVertices();
			final int vertexSize = mesh.getVertexSize() / 4;
			float[] vertices = mesh.getVertices(new float[vertexSize * numVertices]);

			int numIndices = mesh.getNumIndices();
			short[] indices = new short[numIndices];
			mesh.getIndices(indices);

			VertexAttributes vertexAttributes = mesh.getVertexAttributes();
			int offsetPosition = vertexAttributes.getOffset(Usage.Position, -1);
			int offsetTex = vertexAttributes.getOffset(Usage.TextureCoordinates, -1);
			int offsetTangent = vertexAttributes.getOffset(Usage.Tangent, -1);
			int offsetBiNormal = vertexAttributes.getOffset(Usage.BiNormal, -1);
			int offsetNormal = vertexAttributes.getOffset(Usage.Normal, -1);
			int stride = vertexAttributes.vertexSize / 4;

			if (offsetPosition < 0 || offsetTex < 0 || offsetNormal < 0 || (offsetTangent < 0 && offsetBiNormal < 0))
				continue;

			for (int i = 0; i < numIndices; i += 3) {
				int vert = indices[i];
				Vector3 v0pos = new Vector3(vertices[(vert * stride) + offsetPosition],
						vertices[(vert * stride) + offsetPosition + 1], vertices[(vert * stride) + offsetPosition + 2]);// Vertices[Indices[i]];
				vert = indices[i + 1];
				Vector3 v1pos = new Vector3(vertices[(vert * stride) + offsetPosition],
						vertices[(vert * stride) + offsetPosition + 1], vertices[(vert * stride) + offsetPosition + 2]);// Vertices[Indices[i+1]];
				vert = indices[i + 2];
				Vector3 v2pos = new Vector3(vertices[(vert * stride) + offsetPosition],
						vertices[(vert * stride) + offsetPosition + 1], vertices[(vert * stride) + offsetPosition + 2]);// Vertices[Indices[i+2]];

				vert = indices[i];
				Vector2 v0tex = new Vector2(vertices[(vert * stride) + offsetTex],
						vertices[(vert * stride) + offsetTex + 1]);
				vert = indices[i + 1];
				Vector2 v1tex = new Vector2(vertices[(vert * stride) + offsetTex],
						vertices[(vert * stride) + offsetTex + 1]);
				vert = indices[i + 2];
				Vector2 v2tex = new Vector2(vertices[(vert * stride) + offsetTex],
						vertices[(vert * stride) + offsetTex + 1]);

				Vector3 Edge1 = new Vector3(v1pos).sub(v0pos);
				Vector3 Edge2 = new Vector3(v2pos).sub(v0pos);

				float DeltaU1 = v1tex.x - v0tex.x;
				float DeltaV1 = v1tex.y - v0tex.y;
				float DeltaU2 = v2tex.x - v0tex.x;
				float DeltaV2 = v2tex.y - v0tex.y;

				float f = 1.0f / (DeltaU1 * DeltaV2 - DeltaU2 * DeltaV1);

				tangent.x = f * (DeltaV2 * Edge1.x - DeltaV1 * Edge2.x);
				tangent.y = f * (DeltaV2 * Edge1.y - DeltaV1 * Edge2.y);
				tangent.z = f * (DeltaV2 * Edge1.z - DeltaV1 * Edge2.z);

				if (offsetTangent >= 0) {
					vert = indices[i];
					vertices[(vert * stride) + offsetTangent] += tangent.x;
					vertices[(vert * stride) + offsetTangent + 1] += tangent.y;
					vertices[(vert * stride) + offsetTangent + 2] += tangent.z;
					vert = indices[i + 1];
					vertices[(vert * stride) + offsetTangent] += tangent.x;
					vertices[(vert * stride) + offsetTangent + 1] += tangent.y;
					vertices[(vert * stride) + offsetTangent + 2] += tangent.z;
					vert = indices[i + 2];
					vertices[(vert * stride) + offsetTangent] += tangent.x;
					vertices[(vert * stride) + offsetTangent + 1] += tangent.y;
					vertices[(vert * stride) + offsetTangent + 2] += tangent.z;
				}

				if (offsetBiNormal >= 0) {
					bitangent.x = f * (-DeltaU2 * Edge1.x - DeltaU1 * Edge2.x);
					bitangent.y = f * (-DeltaU2 * Edge1.y - DeltaU1 * Edge2.y);
					bitangent.z = f * (-DeltaU2 * Edge1.z - DeltaU1 * Edge2.z);

					vert = indices[i];
					vertices[(vert * stride) + offsetBiNormal] += bitangent.x;
					vertices[(vert * stride) + offsetBiNormal + 1] += bitangent.y;
					vertices[(vert * stride) + offsetBiNormal + 2] += bitangent.z;
					vert = indices[i + 1];
					vertices[(vert * stride) + offsetBiNormal] += bitangent.x;
					vertices[(vert * stride) + offsetBiNormal + 1] += bitangent.y;
					vertices[(vert * stride) + offsetBiNormal + 2] += bitangent.z;
					vert = indices[i + 2];
					vertices[(vert * stride) + offsetBiNormal] += bitangent.x;
					vertices[(vert * stride) + offsetBiNormal + 1] += bitangent.y;
					vertices[(vert * stride) + offsetBiNormal + 2] += bitangent.z;
				}
			}

			Vector3 normal = bitangent;
			if (offsetTangent >= 0) {
				for (int i = 0; i < numIndices; i++) {
					int vert = indices[i];

					float nx = vertices[(vert * stride) + offsetNormal];
					float ny = vertices[(vert * stride) + offsetNormal + 1];
					float nz = vertices[(vert * stride) + offsetNormal + 2];
					normal.set(nx, ny, nz);

					float tx = vertices[(vert * stride) + offsetTangent];
					float ty = vertices[(vert * stride) + offsetTangent + 1];
					float tz = vertices[(vert * stride) + offsetTangent + 2];
					tangent.set(tx, ty, tz);

					float dot = normal.dot(tangent);
					tangent.sub(normal).scl(dot).nor();

					vertices[(vert * stride) + offsetTangent] = tangent.x;
					vertices[(vert * stride) + offsetTangent + 1] = tangent.y;
					vertices[(vert * stride) + offsetTangent + 2] = tangent.z;
				}
			}

			// System.out.println(Arrays.toString(vertices));
			mesh.setVertices(vertices);
		}
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
