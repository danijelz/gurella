package com.gurella.engine.graph.renderable;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.gurella.engine.application.UpdateOrder;
import com.gurella.engine.application2.Application;
import com.gurella.engine.graph.SceneNodeComponent;
import com.gurella.engine.graph.SceneProcessorManager;
import com.gurella.engine.graph.camera.CameraComponent;
import com.gurella.engine.graph.layer.Layer;
import com.gurella.engine.graph.layer.Layer.LayerOrdinalComparator;
import com.gurella.engine.graph.spatial.Spatial;
import com.gurella.engine.graphics.GenericBatch;

public class RenderSystem extends SceneProcessorManager {
	private final GenericBatch batch = Application.DISPOSABLE_MANAGER.add(new GenericBatch());
	private Array<Layer> orderedLayers = new Array<Layer>();
	private IntMap<Array<CameraComponent<?>>> camerasByLayer = new IntMap<Array<CameraComponent<?>>>();

	private final Array<Spatial> tempSpatials = new Array<Spatial>(256);

	@Override
	public void update() {
		for (Layer layer : orderedLayers) {
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
		renderSpatials(layer, camera);
		batch.end();
	}

	private void renderSpatials(Layer layer, Camera camera) {
		getGraph().spatialPartitioningManager.getSpatials(camera.frustum, tempSpatials, layer);
		for (int i = 0; i < tempSpatials.size; i++) {
			Spatial spatial = tempSpatials.get(i);
			spatial.renderableComponent.render(batch);
		}
		tempSpatials.clear();
	}

	@Override
	public int getOrdinal() {
		return UpdateOrder.RENDER;
	}

	@Override
	public void componentActivated(SceneNodeComponent component) {
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
	public void componentDeactivated(SceneNodeComponent component) {
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
