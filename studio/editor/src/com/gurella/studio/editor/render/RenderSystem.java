package com.gurella.studio.editor.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DirectionalLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.PointLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.SpotLightsAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.gurella.engine.event.EventService;
import com.gurella.engine.graphics.render.GenericBatch;
import com.gurella.engine.plugin.Workbench;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNodeComponent;
import com.gurella.engine.scene.camera.CameraComponent;
import com.gurella.engine.scene.camera.CameraComponent.OrdinalComparator;
import com.gurella.engine.scene.debug.DebugRenderable;
import com.gurella.engine.scene.debug.DebugRenderable.DebugRenderContext;
import com.gurella.engine.scene.light.DirectionalLightComponent;
import com.gurella.engine.scene.light.PointLightComponent;
import com.gurella.engine.scene.light.SpotLightComponent;
import com.gurella.engine.scene.renderable.Layer;
import com.gurella.engine.scene.renderable.LayerMask;
import com.gurella.engine.scene.renderable.RenderSystem.LayerOrdinalComparator;
import com.gurella.engine.scene.spatial.Spatial;
import com.gurella.engine.subscriptions.scene.ComponentActivityListener;
import com.gurella.engine.subscriptions.scene.update.PreRenderUpdateListener;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.SceneProviderExtension;
import com.gurella.studio.editor.camera.CameraProviderExtension;
import com.gurella.studio.editor.subscription.EditorFocusListener;
import com.gurella.studio.editor.subscription.EditorCloseListener;
import com.gurella.studio.editor.subscription.EditorRenderUpdateListener;
import com.gurella.studio.editor.tool.ToolManager;

public class RenderSystem implements ComponentActivityListener, SceneProviderExtension, EditorCloseListener,
		EditorFocusListener, EditorRenderUpdateListener, CameraProviderExtension {
	private int editorId;

	private Scene scene;
	private int sceneId = -1;

	private final Array<CameraComponent<?>> cameras = new Array<CameraComponent<?>>();
	private IntMap<Array<DebugRenderable>> debugRenderablesByNode = new IntMap<Array<DebugRenderable>>();
	private EditorFocusData focusData = new EditorFocusData(null, null);

	private final Environment environment = new Environment();
	private final ColorAttribute ambientLight = new ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1f);
	private final DepthTestAttribute depthTest = new DepthTestAttribute();
	private final DirectionalLightsAttribute directionalLights = new DirectionalLightsAttribute();
	private final PointLightsAttribute pointLights = new PointLightsAttribute();
	private final SpotLightsAttribute spotLights = new SpotLightsAttribute();

	private final LayerMask layerMask = new LayerMask();
	private final Array<Spatial> tempSpatials = new Array<Spatial>(256);

	private GenericBatch batch;
	private DebugRenderContext renderContext = new DebugRenderContext();
	private Color backgroundColor = new Color(0.501960f, 0.501960f, 0.501960f, 1f);

	private Grid3d grid3d;
	private Grid2d grid2d;
	private Compass compass;
	private InfoRenderer infoRenderer;
	private ToolManager toolManager;

	private Camera camera;

	@SuppressWarnings("deprecation")
	public RenderSystem(int editorId) {
		this.editorId = editorId;

		environment.set(ambientLight);
		environment.set(depthTest);
		environment.set(directionalLights);
		environment.set(pointLights);
		environment.set(spotLights);

		layerMask.allowed(Layer.DEFAULT);
		layerMask.allowed(Layer.SKY);

		batch = new GenericBatch();
		grid3d = new Grid3d(editorId);
		grid2d = new Grid2d(editorId);
		compass = new Compass(editorId);
		infoRenderer = new InfoRenderer(editorId);

		toolManager = new ToolManager(editorId);

		DefaultShader.defaultCullFace = 0;

		EventService.subscribe(editorId, this);
		Workbench.activate(this);
	}

	@Override
	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	@Override
	public void setScene(Scene scene) {
		this.scene = scene;
		sceneId = scene.getInstanceId();
		EventService.subscribe(sceneId, this);
	}

	@Override
	public void componentActivated(SceneNodeComponent component) {
		if (component instanceof CameraComponent) {
			cameras.add((CameraComponent<?>) component);
			cameras.sort(OrdinalComparator.instance);
		} else if (component instanceof DebugRenderable) {
			addDebugRenderable(component);
		} else if (component instanceof DirectionalLightComponent) {
			directionalLights.lights.add(((DirectionalLightComponent) component).getLight());
		} else if (component instanceof PointLightComponent) {
			pointLights.lights.add(((PointLightComponent) component).getLight());
		} else if (component instanceof SpotLightComponent) {
			spotLights.lights.add(((SpotLightComponent) component).getLight());
		}
	}

	private void addDebugRenderable(SceneNodeComponent component) {
		int nodeId = component.getNodeId();
		Array<DebugRenderable> renderables = debugRenderablesByNode.get(nodeId);
		if (renderables == null) {
			renderables = new Array<>();
			debugRenderablesByNode.put(nodeId, renderables);
		}
		renderables.add((DebugRenderable) component);
	}

	@Override
	public void componentDeactivated(SceneNodeComponent component) {
		if (component instanceof CameraComponent) {
			cameras.removeValue((CameraComponent<?>) component, true);
		} else if (component instanceof DebugRenderable) {
			removeDebugRenderable(component);
		} else if (component instanceof DirectionalLightComponent) {
			directionalLights.lights.removeValue(((DirectionalLightComponent) component).getLight(), true);
		} else if (component instanceof PointLightComponent) {
			pointLights.lights.removeValue(((PointLightComponent) component).getLight(), true);
		} else if (component instanceof SpotLightComponent) {
			spotLights.lights.removeValue(((SpotLightComponent) component).getLight(), true);
		}
	}

	private void removeDebugRenderable(SceneNodeComponent component) {
		int nodeId = component.getNodeId();
		Array<DebugRenderable> renderables = debugRenderablesByNode.get(nodeId);
		if (renderables == null) {
			return;
		}

		renderables.removeValue((DebugRenderable) component, true);
		if (renderables.size < 1) {
			debugRenderablesByNode.remove(nodeId);
		}
	}

	@Override
	public void onRenderUpdate() {
		if (camera == null) {
			return;
		}

		synchronized (GurellaStudioPlugin.glMutex) {
			updateGlState();
			if (camera instanceof PerspectiveCamera) {
				grid3d.render(batch);
			} else {
				grid2d.render(batch);
			}
			renderScene();
			compass.render(batch);
			infoRenderer.renderInfo(camera, batch);
			toolManager.render(batch);
		}
	}

	protected void updateGlState() {
		Color color = backgroundColor;
		Gdx.gl.glClearColor(color.r, color.g, color.b, color.a);
		Gdx.gl.glClearDepthf(1);
		Gdx.gl.glClearStencil(0);
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT);
		Graphics graphics = Gdx.graphics;
		Gdx.gl.glViewport(0, 0, graphics.getWidth(), graphics.getHeight());
	}

	private void renderScene() {
		if (scene == null) {
			return;
		}

		EventService.post(sceneId, PreRenderUpdateListener.class, l -> l.onPreRenderUpdate());

		batch.begin(camera);
		batch.setEnvironment(environment);

		scene.spatialSystem.getSpatials(camera.frustum, tempSpatials, layerMask);
		tempSpatials.sort(LayerOrdinalComparator.instance);

		SceneNodeComponent focusedComponent = focusData.focusedComponent;
		int focusedComponentNodeId = focusedComponent instanceof DebugRenderable ? focusedComponent.getNodeId() : -1;
		boolean focusedComponnentRendered = focusedComponent == null ? true : false;

		renderContext.batch = batch;
		renderContext.camera = camera;

		for (int i = 0; i < tempSpatials.size; i++) {
			Spatial spatial = tempSpatials.get(i);
			spatial.renderable.render(batch);
			debugRender(renderContext, spatial, focusedComponent);
			focusedComponnentRendered |= spatial.nodeId == focusedComponentNodeId;
		}
		
		if (!focusedComponnentRendered && focusedComponent instanceof DebugRenderable && focusedComponent.isActive()) {
			((DebugRenderable) focusedComponent).debugRender(renderContext);
		}

		tempSpatials.clear();
		batch.end();
	}

	private void debugRender(DebugRenderContext context, Spatial spatial, SceneNodeComponent focusedComponent) {
		Array<DebugRenderable> renderables = debugRenderablesByNode.get(spatial.nodeId);
		if (renderables == null) {
			return;
		}

		for (int j = 0, n = renderables.size; j < n; j++) {
			DebugRenderable debugRenderable = renderables.get(j);
			if (debugRenderable == focusedComponent) {
				debugRenderable.debugRender(context);
			}
		}
	}

	@Override
	public void focusChanged(EditorFocusData focusData) {
		this.focusData = focusData;
	}

	@Override
	public void onEditorClose() {
		Workbench.deactivate(this);
		EventService.unsubscribe(sceneId, this);
		EventService.unsubscribe(editorId, this);
		batch.dispose();
	}
}
