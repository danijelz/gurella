package com.gurella.engine.scene.renderable;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DirectionalLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.PointLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.SpotLightsAttribute;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntMap;
import com.gurella.engine.event.EventService;
import com.gurella.engine.graphics.render.GenericBatch;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.scene.SceneService2;
import com.gurella.engine.scene.camera.CameraComponent;
import com.gurella.engine.scene.camera.PerspectiveCameraComponent;
import com.gurella.engine.scene.layer.Layer;
import com.gurella.engine.scene.layer.Layer.LayerOrdinalComparator;
import com.gurella.engine.scene.layer.LayerMask;
import com.gurella.engine.scene.light.DirectionalLightComponent;
import com.gurella.engine.scene.light.PointLightComponent;
import com.gurella.engine.scene.light.SpotLightComponent;
import com.gurella.engine.scene.spatial.Spatial;
import com.gurella.engine.scene.spatial.SpatialSystem;
import com.gurella.engine.subscriptions.scene.ComponentActivityListener;
import com.gurella.engine.subscriptions.scene.renderable.RenderableVisibilityListener;
import com.gurella.engine.subscriptions.scene.update.RenderUpdateListener;
import com.gurella.engine.utils.IdentitySet;

public class RenderSystem extends SceneService2 implements ComponentActivityListener, RenderUpdateListener {
	private GenericBatch batch;

	private final Array<Layer> orderedLayers = new Array<Layer>();
	private final IntMap<Array<CameraComponent<?>>> camerasByLayer = new IntMap<Array<CameraComponent<?>>>();

	private final LayerMask layerMask = new LayerMask();
	private final Array<Spatial> tempSpatials = new Array<Spatial>(256);

	private IdentitySet<RenderableComponent> lastVisibleRenderables = new IdentitySet<RenderableComponent>(256);
	private IdentitySet<RenderableComponent> currentVisibleRenderables = new IdentitySet<RenderableComponent>(256);
	private final Array<RenderableVisibilityListener> visibilityListeners = new Array<RenderableVisibilityListener>();

	private final Environment environment = new Environment();
	private final ColorAttribute ambientLight = new ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f, 1f, 1f);
	private final ColorAttribute fog = new ColorAttribute(ColorAttribute.Fog, 1f, 1f, 1f, 1f);
	private final DepthTestAttribute depthTest = new DepthTestAttribute();
	private final DirectionalLightsAttribute directionalLights = new DirectionalLightsAttribute();
	private final PointLightsAttribute pointLights = new PointLightsAttribute();
	private final SpotLightsAttribute spotLights = new SpotLightsAttribute();

	private SpatialSystem<?> spatialSystem;

	public RenderSystem(Scene scene) {
		super(scene);
	}

	@Override
	protected void serviceActivated() {
		if (batch == null) {
			batch = new GenericBatch();

			environment.set(depthTest);
			environment.set(directionalLights);
			environment.set(pointLights);
			environment.set(spotLights);
		}

		spatialSystem = scene.spatialSystem;
	}

	@Override
	protected void serviceDeactivated() {
		spatialSystem = null;
		batch.dispose();
		batch = null;
	}

	@Override
	public void onRenderUpdate() {
		for (int i = 0, n = orderedLayers.size; i < n; i++) {
			Layer layer = orderedLayers.get(i);
			render(layer);
		}

		for (RenderableComponent renderable : lastVisibleRenderables) {
			if (!currentVisibleRenderables.contains(renderable)) {
				notifyVisibilityChange(renderable, false);
			}
		}

		IdentitySet<RenderableComponent> temp = lastVisibleRenderables;
		lastVisibleRenderables = currentVisibleRenderables;
		currentVisibleRenderables = temp;
		currentVisibleRenderables.clear();
	}

	private void render(Layer layer) {
		for (CameraComponent<?> cameraComponent : camerasByLayer.get(layer.id)) {
			render(layer, cameraComponent);
		}
	}

	private void render(Layer layer, CameraComponent<?> cameraComponent) {
		Camera camera = cameraComponent.camera;
		cameraComponent.viewport.apply();
		batch.begin(camera);
		batch.setEnvironment(updateEnvironment(cameraComponent));

		try {
			renderSpatials(layer, camera);
		} catch (Exception e) {
			throw e instanceof RuntimeException ? (RuntimeException) e : new GdxRuntimeException(e);
		} finally {
			batch.end();
			batch.setEnvironment(null);
		}
	}

	private Environment updateEnvironment(CameraComponent<?> cameraComponent) {
		if (cameraComponent instanceof PerspectiveCameraComponent) {
			PerspectiveCameraComponent perspectiveCamera = (PerspectiveCameraComponent) cameraComponent;

			Color ambientLightColor = perspectiveCamera.ambientLight;
			if (ambientLightColor == null) {
				environment.remove(ambientLight.type);
			} else {
				ambientLight.color.set(ambientLightColor);
				environment.set(ambientLight);
			}

			Color fogColor = perspectiveCamera.ambientLight;
			if (fogColor == null) {
				environment.remove(fog.type);
			} else {
				fog.color.set(fogColor);
				environment.set(fog);
			}
		} else {
			environment.remove(ambientLight.type | fog.type);
		}
		return environment;
	}

	private void renderSpatials(Layer layer, Camera camera) {
		layerMask.reset();
		spatialSystem.getSpatials(camera.frustum, tempSpatials, layerMask.allowed(layer));

		for (int i = 0; i < tempSpatials.size; i++) {
			Spatial spatial = tempSpatials.get(i);
			RenderableComponent renderable = spatial.renderableComponent;

			if (!lastVisibleRenderables.contains(renderable) && !currentVisibleRenderables.add(renderable)) {
				notifyVisibilityChange(renderable, true);
			}

			renderable.render(batch);
		}

		tempSpatials.clear();
	}

	private void notifyVisibilityChange(RenderableComponent renderableComponent, boolean visible) {
		renderableComponent.visible = visible;
		int nodeId = renderableComponent.getNodeId();
		EventService.getSubscribers(nodeId, RenderableVisibilityListener.class, visibilityListeners);
		for (int i = 0; i < visibilityListeners.size; i++) {
			visibilityListeners.get(i).visibilityChanged(visible);
		}
		visibilityListeners.clear();
	}

	@Override
	public void componentActivated(SceneNodeComponent2 component) {
		if (component instanceof CameraComponent) {
			addCameraComponent((CameraComponent<?>) component);
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
		Array<Layer> renderingLayers = cameraComponent.renderingLayers;

		if (renderingLayers.size > 0) {
			for (int i = 0, n = renderingLayers.size; i < n; i++) {
				Layer layer = renderingLayers.get(i);
				layersUpdated |= addCameraComponent(layer, cameraComponent);
			}
		} else {
			layersUpdated |= addCameraComponent(Layer.DEFAULT, cameraComponent);
		}

		if (layersUpdated) {
			orderedLayers.sort(LayerOrdinalComparator.instance);
		}
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
		} else if (component instanceof DirectionalLightComponent) {
			directionalLights.lights.removeValue(((DirectionalLightComponent) component).getLight(), true);
		} else if (component instanceof PointLightComponent) {
			pointLights.lights.removeValue(((PointLightComponent) component).getLight(), true);
		} else if (component instanceof SpotLightComponent) {
			spotLights.lights.removeValue(((SpotLightComponent) component).getLight(), true);
		}
	}

	private void removeCameraComponent(CameraComponent<?> cameraComponent) {
		boolean layersUpdated = false;
		Array<Layer> renderingLayers = cameraComponent.renderingLayers;

		for (int i = 0, n = renderingLayers.size; i < n; i++) {
			Layer layer = renderingLayers.get(i);
			layersUpdated |= removeCameraComponent(layer, cameraComponent);
		}

		if (layersUpdated) {
			orderedLayers.sort(LayerOrdinalComparator.instance);
		}
	}

	private boolean removeCameraComponent(Layer layer, CameraComponent<?> cameraComponent) {
		int layerId = layer.id;
		Array<CameraComponent<?>> layerCameras = camerasByLayer.get(layerId);
		layerCameras.removeValue(cameraComponent, true);

		if (layerCameras.size < 1) {
			camerasByLayer.remove(layerId);
			orderedLayers.removeValue(layer, true);
			return true;
		} else {
			layerCameras.sort();
			return false;
		}
	}
}
