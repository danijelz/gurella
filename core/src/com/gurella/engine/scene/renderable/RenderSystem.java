package com.gurella.engine.scene.renderable;

import com.badlogic.gdx.graphics.Camera;
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
import com.gurella.engine.scene.spatial.Spatial;
import com.gurella.engine.subscriptions.scene.ComponentActivityListener;
import com.gurella.engine.subscriptions.scene.update.RenderUpdateListener;

public class RenderSystem extends SceneService implements ComponentActivityListener, RenderUpdateListener {
	private GenericBatch batch;
	private Array<Layer> orderedLayers = new Array<Layer>();
	private IntMap<Array<CameraComponent<?>>> camerasByLayer = new IntMap<Array<CameraComponent<?>>>();
	private final LayerMask layerMask = new LayerMask();

	private final Array<Spatial> tempSpatials = new Array<Spatial>(256);

	@Override
	protected void init() {
		batch = DisposablesService.add(new GenericBatch());
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
		try {
			renderSpatials(layer, camera);
		} catch (Exception e) {
			throw e instanceof RuntimeException ? (RuntimeException) e : new GdxRuntimeException(e);
		} finally {
			batch.end();
		}
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

			if (cameraComponent.renderingLayers.size > 0) {
				for (Layer layer : cameraComponent.renderingLayers) {
					layersUpdated |= addCameraComponent(layer, cameraComponent);
				}
			} else {
				layersUpdated |= addCameraComponent(Layer.DEFAULT, cameraComponent);
			}

			if (layersUpdated) {
				orderedLayers.sort(LayerOrdinalComparator.instance);
			}
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
			for (Layer layer : cameraComponent.renderingLayers) {
				layersUpdated |= removeCameraComponent(layer, cameraComponent);
			}

			if (layersUpdated) {
				orderedLayers.sort(LayerOrdinalComparator.instance);
			}
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
