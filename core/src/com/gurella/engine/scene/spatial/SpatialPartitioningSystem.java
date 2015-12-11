package com.gurella.engine.scene.spatial;

import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntMap.Values;
import com.gurella.engine.application.CommonUpdateOrder;
import com.gurella.engine.application.UpdateEvent;
import com.gurella.engine.application.UpdateListener;
import com.gurella.engine.event.EventService;
import com.gurella.engine.event.Listener0;
import com.gurella.engine.event.Listener1;
import com.gurella.engine.scene.BaseSceneElementType;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneGraphListener;
import com.gurella.engine.scene.SceneNodeComponent;
import com.gurella.engine.scene.SceneSystem;
import com.gurella.engine.scene.layer.Layer;
import com.gurella.engine.scene.layer.LayerComponent;
import com.gurella.engine.scene.renderable.RenderableComponent;

//TODO attach listeners
@BaseSceneElementType
public abstract class SpatialPartitioningSystem<T extends Spatial> extends SceneSystem implements SceneGraphListener {
	protected IntMap<T> allSpatials = new IntMap<T>();
	protected IntMap<T> dirtySpatials = new IntMap<T>();
	protected IntMap<T> addedSpatials = new IntMap<T>();
	protected IntMap<T> removedSpatials = new IntMap<T>();

	private SceneStartListener sceneStartListener = new SceneStartListener();
	private SceneStopListener sceneStopListener = new SceneStopListener();

	private UpdateListenerImpl updateListener = new UpdateListenerImpl();

	protected IntMap<T> spatialsByRenderableComponent = new IntMap<T>();
	private SpatialDirtyListener spatialDirtyListener = new SpatialDirtyListener();

	private synchronized void initSpatials() {
		doInitSpatials();
	}

	protected abstract void doInitSpatials();

	public synchronized void add(T spatial) {
		addedSpatials.put(spatial.nodeId, spatial);
		allSpatials.put(spatial.nodeId, spatial);
		spatialsByRenderableComponent.put(spatial.renderableComponent.id, spatial);
		spatial.renderableComponent.dirtySignal.addListener(spatialDirtyListener);
	}

	public synchronized void remove(T spatial) {
		if (allSpatials.remove(spatial.nodeId) != null) {
			removeSpatial(spatial);
		}
	}

	private void removeSpatial(T spatial) {
		spatial.renderableComponent.dirtySignal.removeListener(spatialDirtyListener);
		spatialsByRenderableComponent.remove(spatial.renderableComponent.id);
		removedSpatials.put(spatial.nodeId, spatial);
		addedSpatials.remove(spatial.nodeId);
		dirtySpatials.remove(spatial.nodeId);
	}

	private synchronized void updateSpatials() {
		if (removedSpatials.size > 0 || addedSpatials.size > 0 || dirtySpatials.size > 0) {
			doUpdateSpatials();

			removedSpatials.clear();
			addedSpatials.clear();
			dirtySpatials.clear();
		}
	}

	protected abstract void doUpdateSpatials();

	public void getSpatials(BoundingBox bounds, Array<Spatial> out, Layer... layers) {
		updateSpatials();
		doGetSpatials(bounds, out, layers);
	}

	protected abstract void doGetSpatials(BoundingBox bounds, Array<Spatial> out, Layer... layers);

	public void getSpatials(Frustum frustum, Array<Spatial> out, Layer... layers) {
		updateSpatials();
		doGetSpatials(frustum, out, layers);
	}

	protected abstract void doGetSpatials(Frustum frustum, Array<Spatial> out, Layer... layers);

	public void getSpatials(Ray ray, Array<Spatial> out, Layer... layers) {
		updateSpatials();
		doGetSpatials(ray, out, layers);
	}

	protected abstract void doGetSpatials(Ray ray, Array<Spatial> out, Layer... layers);

	public synchronized void clear() {
		Values<T> values = allSpatials.values();
		while (values.hasNext) {
			T spatial = values.next();
			removeSpatial(spatial);
		}
		doClear();
		allSpatials.clear();
	}

	protected abstract void doClear();

	public synchronized void markDirty(T spatial) {
		int nodeId = spatial.nodeId;
		if (!addedSpatials.containsKey(nodeId)) {
			dirtySpatials.put(nodeId, spatial);
		}
	}

	protected abstract T createSpatial(RenderableComponent drawableComponent);

	@Override
	public void componentAdded(SceneNodeComponent component) {
	}

	@Override
	public void componentRemoved(SceneNodeComponent component) {
	}

	@Override
	public void componentActivated(SceneNodeComponent component) {
		if (component instanceof RenderableComponent) {
			T spatial = createSpatial((RenderableComponent) component);
			add(spatial);
		} else if (component instanceof LayerComponent) {
			T spatial = allSpatials.get(component.getNode().id);
			if (spatial != null) {
				spatial.layer = ((LayerComponent) component).getLayer();
			}
		}
	}

	@Override
	public void componentDeactivated(SceneNodeComponent component) {
		if (component instanceof RenderableComponent) {
			T spatial = allSpatials.get(component.getNode().id);
			if (spatial != null) {
				remove(spatial);
			}
		} else if (component instanceof LayerComponent) {
			T spatial = allSpatials.get(component.getNode().id);
			if (spatial != null) {
				spatial.layer = Layer.DEFAULT;
			}
		}
	}

	@Override
	protected void attached() {
		Scene scene = getScene();
		scene.startSignal.addListener(sceneStartListener);
		scene.stopSignal.addListener(sceneStopListener);
	}

	@Override
	protected void detached() {
		Scene scene = getScene();
		scene.startSignal.removeListener(sceneStartListener);
		scene.stopSignal.removeListener(sceneStopListener);
	}

	private class SceneStartListener implements Listener0 {
		@Override
		public void handle() {
			initSpatials();
			EventService.addListener(UpdateEvent.class, updateListener);
		}
	}

	private class SceneStopListener implements Listener0 {
		@Override
		public void handle() {
			EventService.removeListener(UpdateEvent.class, updateListener);
			clear();
		}
	}

	private class UpdateListenerImpl implements UpdateListener {
		@Override
		public int getOrdinal() {
			return CommonUpdateOrder.CLEANUP;
		}

		@Override
		public void update() {
			updateSpatials();
		}
	}

	private class SpatialDirtyListener implements Listener1<RenderableComponent> {
		@Override
		public void handle(RenderableComponent renderableComponent) {
			markDirty(spatialsByRenderableComponent.get(renderableComponent.id));
		}
	}
}
