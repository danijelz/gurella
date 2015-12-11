package com.gurella.studio.sceneview;

import java.nio.FloatBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Scaling;
import com.gurella.engine.application.Application;
import com.gurella.engine.application.events.UpdateEvent;
import com.gurella.engine.event.EventService;
import com.gurella.engine.event.Listener1;
import com.gurella.engine.resource.ResourceReference;
import com.gurella.engine.resource.SharedResourceReference;
import com.gurella.engine.resource.factory.ModelResourceFactory;
import com.gurella.engine.resource.model.ResourceId;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneGraph;
import com.gurella.engine.scene.SceneNode;
import com.gurella.engine.scene.SceneNodeComponent;
import com.gurella.engine.scene.camera.CameraComponent;
import com.gurella.engine.scene.camera.PerspectiveCameraComponent;
import com.gurella.engine.scene.movement.TransformComponent;
import com.gurella.engine.scene.renderable.ModelComponent;
import com.gurella.engine.scene.renderable.TextureComponent;
import com.gurella.studio.project.ProjectHeaderContainer.SceneSelectionChangedEvent;

public class SceneView extends Container<Image> {
	private GL20 gl = Gdx.gl;
	private final AssetManager assetManager = new AssetManager();
	Texture t;

	private ShapeRenderer shapeRenderer = new ShapeRenderer();
	private FrameBuffer frameBuffer;
	private TextureRegion region;
	private Image image;

	private Application application;
	private Scene view;
	private SceneGraph graph;
	private CameraComponent cameraComponent;
	private CameraInputController cameraInputController;
	private InputProcessor inputProcessor;
	private InputMultiplexer inputMultiplexer = new InputMultiplexer();

	private Scene scene;
	private IntMap<SharedResourceReference<? extends SceneNode>> sceneNodes;
	private IntMap<SharedResourceReference<? extends SceneNodeComponent>> sceneNodeComponents;

	public SceneView() {
		assetManager.load("data/grid.png", Texture.class);

		while (!assetManager.update()) {
		}

		t = assetManager.get("data/grid.png");
		t.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);

		frameBuffer = new FrameBuffer(Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true, true);
		region = new TextureRegion(frameBuffer.getColorBufferTexture());
		region.flip(false, true);

		image = new Image(new TextureRegionDrawable(region), Scaling.none);
		image.addListener(new InputListener() {
			@Override
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				inputProcessor = Gdx.input.getInputProcessor();
				if (inputProcessor != inputMultiplexer) {
					inputMultiplexer.addProcessor(0, inputProcessor);
					Gdx.input.setInputProcessor(inputMultiplexer);
				}
			}

			@Override
			public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
				Gdx.input.setInputProcessor(inputProcessor);
				inputMultiplexer.removeProcessor(inputProcessor);
			}
		});
		setActor(image);

		application = new Application(null);
		view = new Scene(application, "");
		graph = view.graph;
		view.start(null);
		addPlane();
		addCamera();
		graph.update();

		EventService.addListener(SceneSelectionChangedEvent.class, new SceneSelectionChangedListener());
	}

	private void clearScene() {
		this.scene = null;
		sceneNodes = null;
		sceneNodeComponents = null;
	}

	public void presentScene(Scene selectedScene) {
		scene = selectedScene;
		sceneNodes = scene.getSceneNodes();
		sceneNodeComponents = scene.getSceneNodeComponents();
	}

	private void addPlane() {
		SceneNode node = new SceneNode();

		TransformComponent transformComponent = new TransformComponent();
		node.addComponent(transformComponent);

		ModelComponent modelComponent = new ModelComponent();
		ModelBuilder builder = new ModelBuilder();

		Model model = builder.createRect(-20, 0, 20, 20, 0, 20, 20, 0, -20, -20, 0, -20, 0, 1, 0,
				new Material(TextureAttribute.createDiffuse(t), new BlendingAttribute(1f)),
				Usage.Position | Usage.TextureCoordinates);

		Mesh mesh = model.meshes.get(0);
		FloatBuffer verticesBuffer = mesh.getVerticesBuffer();
		if (verticesBuffer.get(3) == 1f) {
			verticesBuffer.put(3, 20f);
		}

		if (verticesBuffer.get(4) == 1f) {
			verticesBuffer.put(4, 20f);
		}

		if (verticesBuffer.get(8) == 1f) {
			verticesBuffer.put(8, 20f);
		}

		if (verticesBuffer.get(9) == 1f) {
			verticesBuffer.put(9, 20f);
		}

		if (verticesBuffer.get(13) == 1f) {
			verticesBuffer.put(13, 20f);
		}

		if (verticesBuffer.get(14) == 1f) {
			verticesBuffer.put(14, 20f);
		}

		if (verticesBuffer.get(18) == 1f) {
			verticesBuffer.put(18, 20f);
		}

		if (verticesBuffer.get(19) == 1f) {
			verticesBuffer.put(19, 20f);
		}

		modelComponent.setModel(model);
		node.addComponent(modelComponent);

		graph.addNode(node);
	}

	private void addCamera() {
		SceneNode node = new SceneNode();

		TransformComponent transformComponent = new TransformComponent();
		transformComponent.setTranslation(1, 3, 10);
		node.addComponent(transformComponent);

		cameraComponent = new PerspectiveCameraComponent();
		node.addComponent(cameraComponent);

		cameraInputController = new CameraInputController(cameraComponent.camera);
		inputMultiplexer.addProcessor(cameraInputController);

		/*
		 * cameraComponent = new OrtographicCameraComponent(); node.addComponent(cameraComponent);
		 */

		graph.addNode(node);
	}

	@Override
	public void act(float delta) {
		updateView();
		graph.update();

		frameBuffer.bind();
		gl.glClearColor(0, 0, 0.3f, 1);
		gl.glClearStencil(0);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		EventService.notify(UpdateEvent.instance);

		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setProjectionMatrix(cameraComponent.camera.combined);
		shapeRenderer.setColor(0, 0, 1, 1);
		shapeRenderer.line(0, -10, 0, 0, 10, 0);
		shapeRenderer.setColor(0, 1, 0, 1);
		shapeRenderer.line(-10, 0, 0, 10, 0, 0);
		shapeRenderer.setColor(1, 0, 0, 1);
		shapeRenderer.line(0, 0, -10, 0, 0, 10);
		shapeRenderer.end();

		frameBuffer.end();

		super.act(delta);
	}

	private class SceneSelectionChangedListener implements Listener1<Scene> {
		@Override
		public void handle(Scene selectedScene) {
			if (selectedScene == null) {
				clearScene();
			} else {
				presentScene(selectedScene);
			}
		}
	}

	private ObjectMap<ResourceReference<? extends SceneNode>, SceneNode> nodesMap = new ObjectMap<ResourceReference<? extends SceneNode>, SceneNode>();
	private ObjectMap<ResourceReference<? extends SceneNodeComponent>, SceneRenderableComponent> componentsMap = new ObjectMap<ResourceReference<? extends SceneNodeComponent>, SceneRenderableComponent>();

	private void updateView() {
		if (scene == null) {
			return;
		}

		IntArray initialNodes = scene.getInitialNodes();
		for (int i = 0; i < initialNodes.size; i++) {
			ResourceReference<? extends SceneNode> nodeReference = sceneNodes.get(initialNodes.get(i));
			SceneNode node = nodesMap.get(nodeReference);
			TransformComponent transformComponent;
			SceneRenderableComponent sceneRenderableComponent;

			if (node == null) {
				node = new SceneNode();
				transformComponent = new TransformComponent();
				node.addComponent(transformComponent);
				sceneRenderableComponent = new SceneRenderableComponent();
				node.addComponent(sceneRenderableComponent);
				graph.addNode(node);
				nodesMap.put(nodeReference, node);
			} else {
				sceneRenderableComponent = node.getComponent(SceneRenderableComponent.class);
				transformComponent = node.getComponent(TransformComponent.class);
			}

			updateNode(nodeReference, transformComponent, sceneRenderableComponent);
		}
	}

	private void updateNode(ResourceReference<? extends SceneNode> nodeReference, TransformComponent transformComponent,
			SceneRenderableComponent sceneRenderableComponent) {
		ModelResourceFactory<? extends SceneNode> nodeFactory = (ModelResourceFactory<? extends SceneNode>) nodeReference
				.getResourceFactory();
		Array<ResourceId> componentIds = nodeFactory.getPropertyValue("components");

		if (componentIds != null) {
			for (int j = 0; j < componentIds.size; j++) {
				int componentId = componentIds.get(j).getId();
				ResourceReference<? extends SceneNodeComponent> componentReference = sceneNodeComponents
						.get(componentId);

				if (TransformComponent.class.equals(componentReference.getResourceType())) {
					@SuppressWarnings("unchecked")
					ModelResourceFactory<TransformComponent> resourceFactory = (ModelResourceFactory<TransformComponent>) componentReference
							.getResourceFactory();
					resourceFactory.init(transformComponent, null);
				}

				if (TextureComponent.class.equals(componentReference.getResourceType())) {
					sceneRenderableComponent.addTexture((ResourceReference<TextureComponent>) componentReference);
				}
			}
		}
	}
}
