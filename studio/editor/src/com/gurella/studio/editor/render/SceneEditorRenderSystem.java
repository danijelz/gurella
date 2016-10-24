package com.gurella.studio.editor.render;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DirectionalLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.PointLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.SpotLightsAttribute;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntMap;
import com.gurella.engine.event.EventService;
import com.gurella.engine.graphics.render.GenericBatch;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.scene.camera.CameraComponent;
import com.gurella.engine.scene.debug.DebugRenderable;
import com.gurella.engine.scene.debug.DebugRenderable.RenderContext;
import com.gurella.engine.scene.light.DirectionalLightComponent;
import com.gurella.engine.scene.light.PointLightComponent;
import com.gurella.engine.scene.light.SpotLightComponent;
import com.gurella.engine.scene.renderable.Layer;
import com.gurella.engine.scene.renderable.LayerMask;
import com.gurella.engine.scene.spatial.Spatial;
import com.gurella.engine.subscriptions.scene.ComponentActivityListener;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.studio.editor.subscription.SceneLoadedListener;

public class SceneEditorRenderSystem implements ComponentActivityListener, SceneLoadedListener, Disposable {
	private int editorId;

	private Scene scene;
	private int sceneId = -1;

	private Array<Layer> orderedLayers = new Array<Layer>();
	private IntMap<Array<CameraComponent<?>>> camerasByLayer = new IntMap<Array<CameraComponent<?>>>();
	private IntMap<Array<DebugRenderable>> debugRenderablesByNode = new IntMap<Array<DebugRenderable>>();

	private final Environment environment = new Environment();
	private final ColorAttribute ambientLight = new ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1f);
	private final DepthTestAttribute depthTest = new DepthTestAttribute();
	private final DirectionalLightsAttribute directionalLights = new DirectionalLightsAttribute();
	private final PointLightsAttribute pointLights = new PointLightsAttribute();
	private final SpotLightsAttribute spotLights = new SpotLightsAttribute();

	private final LayerMask layerMask = new LayerMask();
	private final Array<Spatial> tempSpatials = new Array<Spatial>(256);

	public SceneEditorRenderSystem(int editorId) {
		this.editorId = editorId;
		layerMask.allowed(Layer.DEFAULT);
		layerMask.allowed(Layer.SKY);

		environment.set(ambientLight);
		environment.set(depthTest);
		environment.set(directionalLights);
		environment.set(pointLights);
		environment.set(spotLights);

		EventService.subscribe(editorId, this);
	}

	@Override
	public void sceneLoaded(Scene scene) {
		this.scene = scene;
		sceneId = scene.getInstanceId();
		EventService.subscribe(scene.getInstanceId(), this);
	}

	@Override
	public void componentActivated(SceneNodeComponent2 component) {
		if (component instanceof CameraComponent) {
			addCameraComponent((CameraComponent<?>) component);
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

	private void addCameraComponent(CameraComponent<?> cameraComponent) {
		boolean layersUpdated = false;
		ImmutableArray<Layer> renderingLayers = cameraComponent.renderingLayers;
		int layersSize = renderingLayers.size();

		if (layersSize > 0) {
			for (int i = 0, n = layersSize; i < n; i++) {
				Layer layer = renderingLayers.get(i);
				layersUpdated |= addCameraComponent(layer, cameraComponent);
			}
		} else {
			layersUpdated |= addCameraComponent(Layer.DEFAULT, cameraComponent);
		}

		if (layersUpdated) {
			orderedLayers.sort();
		}
	}

	private void addDebugRenderable(SceneNodeComponent2 component) {
		int nodeId = component.getNodeId();
		Array<DebugRenderable> renderables = debugRenderablesByNode.get(nodeId);
		if (renderables == null) {
			renderables = new Array<>();
			debugRenderablesByNode.put(nodeId, renderables);
		}
		renderables.add((DebugRenderable) component);
	}

	private boolean addCameraComponent(Layer layer, CameraComponent<?> cameraComponent) {
		int layerId = layer.id;
		if (!camerasByLayer.containsKey(layerId)) {
			camerasByLayer.put(layerId, new Array<CameraComponent<?>>());
		}

		Array<CameraComponent<?>> layerCameras = camerasByLayer.get(layerId);
		layerCameras.add(cameraComponent);
		layerCameras.sort();
		return addLayer(layer);
	}

	private boolean addLayer(Layer layer) {
		if (mustAddLayer(layer)) {
			orderedLayers.add(layer);
			return true;
		} else {
			return false;
		}
	}

	private boolean mustAddLayer(Layer layer) {
		int layerId = layer.id;
		return camerasByLayer.containsKey(layerId) && !orderedLayers.contains(layer, true);
	}

	@Override
	public void componentDeactivated(SceneNodeComponent2 component) {
		if (component instanceof CameraComponent) {
			removeCameraComponent((CameraComponent<?>) component);
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

	private void removeCameraComponent(CameraComponent<?> cameraComponent) {
		ImmutableArray<Layer> renderingLayers = cameraComponent.renderingLayers;
		int layersSize = renderingLayers.size();

		if (layersSize > 0) {
			for (int i = 0, n = layersSize; i < n; i++) {
				Layer layer = renderingLayers.get(i);
				removeCameraComponent(layer, cameraComponent);
			}
		} else {
			removeCameraComponent(Layer.DEFAULT, cameraComponent);
		}
	}

	private void removeCameraComponent(Layer layer, CameraComponent<?> cameraComponent) {
		int layerId = layer.id;
		Array<CameraComponent<?>> layerCameras = camerasByLayer.get(layerId);
		layerCameras.removeValue(cameraComponent, true);

		if (layerCameras.size < 1) {
			camerasByLayer.remove(layerId);
			orderedLayers.removeValue(layer, true);
		}
	}

	private void removeDebugRenderable(SceneNodeComponent2 component) {
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

	public void renderScene(RenderContext context) {
		if (scene == null) {
			return;
		}

		EventService.post(sceneId, PreRenderUpdateEvent.instance);

		GenericBatch batch = context.batch;
		Camera camera = context.camera;
		SceneNodeComponent2 focusedComponent = context.focusedComponent;

		batch.begin(camera);
		batch.setEnvironment(environment);

		scene.spatialSystem.getSpatials(camera.frustum, tempSpatials, layerMask);
		int focusedComponentNodeId = focusedComponent instanceof DebugRenderable ? focusedComponent.getNodeId() : -1;
		boolean focusedComponnentRendered = focusedComponent == null ? true : false;

		for (int i = 0; i < tempSpatials.size; i++) {
			Spatial spatial = tempSpatials.get(i);
			spatial.renderableComponent.render(batch);
			debugRender(context, spatial, focusedComponent);
			focusedComponnentRendered |= spatial.nodeId == focusedComponentNodeId;
		}

		if (!focusedComponnentRendered && focusedComponent instanceof DebugRenderable) {
			DebugRenderable debugRenderable = (DebugRenderable) focusedComponent;
			debugRenderable.debugRender(context);
		}

		tempSpatials.clear();
		batch.end();
	}

	private void debugRender(RenderContext context, Spatial spatial, SceneNodeComponent2 focusedComponent) {
		Array<DebugRenderable> renderables = debugRenderablesByNode.get(spatial.nodeId);
		if (renderables != null) {
			for (int j = 0; j < renderables.size; j++) {
				DebugRenderable debugRenderable = renderables.get(j);
				if (debugRenderable == focusedComponent) {
					debugRenderable.debugRender(context);
				}
			}
		}
	}

	@Override
	public void dispose() {
		EventService.unsubscribe(sceneId, this);
		EventService.unsubscribe(editorId, this);
	}
}
