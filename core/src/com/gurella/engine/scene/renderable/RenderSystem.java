package com.gurella.engine.scene.renderable;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DirectionalLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.PointLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.SpotLightsAttribute;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntMap;
import com.gurella.engine.disposable.DisposablesService;
import com.gurella.engine.graphics.GenericBatch;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.scene.SceneService;
import com.gurella.engine.scene.camera.CameraComponent;
import com.gurella.engine.scene.layer.Layer;
import com.gurella.engine.scene.layer.Layer.LayerOrdinalComparator;
import com.gurella.engine.scene.layer.LayerMask;
import com.gurella.engine.scene.light.DirectionalLightComponent;
import com.gurella.engine.scene.light.PointLightComponent;
import com.gurella.engine.scene.light.SpotLightComponent;
import com.gurella.engine.scene.spatial.Spatial;
import com.gurella.engine.subscriptions.scene.ComponentActivityListener;
import com.gurella.engine.subscriptions.scene.update.RenderUpdateListener;

public class RenderSystem extends SceneService implements ComponentActivityListener, RenderUpdateListener {
	private GenericBatch batch;

	private Array<Layer> orderedLayers = new Array<Layer>();
	private IntMap<Array<CameraComponent<?>>> camerasByLayer = new IntMap<Array<CameraComponent<?>>>();

	private final LayerMask layerMask = new LayerMask();
	private final Array<Spatial> tempSpatials = new Array<Spatial>(256);

	private final Environment environment = new Environment();
	private final ColorAttribute ambientLight = new ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f, 1f, 1f);
	private final DepthTestAttribute depthTest = new DepthTestAttribute();
	private final DirectionalLightsAttribute directionalLights = new DirectionalLightsAttribute();
	private final PointLightsAttribute pointLights = new PointLightsAttribute();
	private final SpotLightsAttribute spotLights = new SpotLightsAttribute();


	@Override
	protected void init() {
		batch = DisposablesService.add(new GenericBatch());

		environment.set(depthTest);
		environment.set(directionalLights);
		environment.set(pointLights);
		environment.set(spotLights);
	}

	@Override
	public void onRenderUpdate() {
		for (int i = 0, n = orderedLayers.size; i < n; i++) {
			Layer layer = orderedLayers.get(i);
			render(layer);
		}
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
		// TODO Auto-generated method stub
		return environment;
	}

	private void renderSpatials(Layer layer, Camera camera) {
		layerMask.reset();
		getScene().spatialPartitioningSystem.getSpatials(camera.frustum, tempSpatials, layerMask.allowed(layer));
		for (int i = 0; i < tempSpatials.size; i++) {
			Spatial spatial = tempSpatials.get(i);
			spatial.renderableComponent.render(batch);
		}
		tempSpatials.clear();
	}

	@Override
	public void componentActivated(SceneNodeComponent2 component) {
		if (component instanceof CameraComponent) {
			CameraComponent<?> cameraComponent = (CameraComponent<?>) component;
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
		} else if (component instanceof DirectionalLightComponent) {
			directionalLights.lights.add(((DirectionalLightComponent) component).getLight());
		} else if (component instanceof PointLightComponent) {
			pointLights.lights.add(((PointLightComponent) component).getLight());
		} else if (component instanceof SpotLightComponent) {
			spotLights.lights.add(((SpotLightComponent) component).getLight());
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
			CameraComponent<?> cameraComponent = (CameraComponent<?>) component;
			boolean layersUpdated = false;

			Array<Layer> renderingLayers = cameraComponent.renderingLayers;
			for (int i = 0, n = renderingLayers.size; i < n; i++) {
				Layer layer = renderingLayers.get(i);
				layersUpdated |= removeCameraComponent(layer, cameraComponent);
			}

			if (layersUpdated) {
				orderedLayers.sort(LayerOrdinalComparator.instance);
			}
		} else if (component instanceof DirectionalLightComponent) {
			directionalLights.lights.removeValue(((DirectionalLightComponent) component).getLight(), true);
		} else if (component instanceof PointLightComponent) {
			pointLights.lights.removeValue(((PointLightComponent) component).getLight(), true);
		} else if (component instanceof SpotLightComponent) {
			spotLights.lights.removeValue(((SpotLightComponent) component).getLight(), true);
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
