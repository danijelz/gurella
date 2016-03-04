package com.gurella.engine.scene.spatial;

import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntMap.Values;
import com.gurella.engine.scene.BaseSceneElement;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.scene.SceneSystem2;
import com.gurella.engine.scene.layer.Layer;
import com.gurella.engine.scene.layer.LayerComponent;
import com.gurella.engine.scene.layer.LayerMask;
import com.gurella.engine.scene.renderable.RenderableComponent;
import com.gurella.engine.subscriptions.scene.ComponentActivityListener;
import com.gurella.engine.subscriptions.scene.SceneActivityListener;
import com.gurella.engine.subscriptions.scene.renderable.SceneRenderableChanged;

//TODO attach listeners -> SceneSystem
@BaseSceneElement
public abstract class SpatialPartitioningSystem<T extends Spatial> extends SceneSystem2
		implements ComponentActivityListener, SceneActivityListener, SceneRenderableChanged {
	protected IntMap<T> allSpatials = new IntMap<T>();
	protected IntMap<T> dirtySpatials = new IntMap<T>();
	protected IntMap<T> addedSpatials = new IntMap<T>();
	protected IntMap<T> removedSpatials = new IntMap<T>();

	protected IntMap<T> spatialsByRenderableComponent = new IntMap<T>();

	private synchronized void initSpatials() {
		doInitSpatials();
	}

	protected abstract void doInitSpatials();

	public synchronized void add(T spatial) {
		addedSpatials.put(spatial.nodeId, spatial);
		allSpatials.put(spatial.nodeId, spatial);
		spatialsByRenderableComponent.put(spatial.renderableComponent.getInstanceId(), spatial);
	}

	public synchronized void remove(T spatial) {
		if (allSpatials.remove(spatial.nodeId) != null) {
			removeSpatial(spatial);
		}
	}

	private void removeSpatial(T spatial) {
		spatialsByRenderableComponent.remove(spatial.renderableComponent.getInstanceId());
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

	public void getSpatials(BoundingBox bounds, Array<Spatial> out, LayerMask mask) {
		updateSpatials();
		doGetSpatials(bounds, out, mask);
	}

	protected abstract void doGetSpatials(BoundingBox bounds, Array<Spatial> out, LayerMask mask);

	public void getSpatials(Frustum frustum, Array<Spatial> out, LayerMask mask) {
		updateSpatials();
		doGetSpatials(frustum, out, mask);
	}

	protected abstract void doGetSpatials(Frustum frustum, Array<Spatial> out, LayerMask mask);

	public void getSpatials(Ray ray, Array<Spatial> out, LayerMask mask) {
		updateSpatials();
		doGetSpatials(ray, out, mask);
	}

	protected abstract void doGetSpatials(Ray ray, Array<Spatial> out, LayerMask mask);

	public final synchronized void clearSpatials() {
		Values<T> values = allSpatials.values();
		while (values.hasNext) {
			T spatial = values.next();
			removeSpatial(spatial);
		}
		doClearSpatials();
		allSpatials.clear();
	}

	protected abstract void doClearSpatials();

	@Override
	public void onRenderableChanged(RenderableComponent component) {
		markDirty(spatialsByRenderableComponent.get(component.getInstanceId()));
	}

	public synchronized void markDirty(T spatial) {
		int nodeId = spatial.nodeId;
		if (!addedSpatials.containsKey(nodeId)) {
			dirtySpatials.put(nodeId, spatial);
		}
	}

	protected abstract T createSpatial(RenderableComponent drawableComponent);

	@Override
	public void componentActivated(SceneNodeComponent2 component) {
		if (component instanceof RenderableComponent) {
			T spatial = createSpatial((RenderableComponent) component);
			add(spatial);
		} else if (component instanceof LayerComponent) {
			T spatial = allSpatials.get(component.getNodeId());
			if (spatial != null) {
				spatial.layer = ((LayerComponent) component).getLayer();
			}
		}
	}

	@Override
	public void componentDeactivated(SceneNodeComponent2 component) {
		if (component instanceof RenderableComponent) {
			T spatial = allSpatials.get(component.getNodeId());
			if (spatial != null) {
				remove(spatial);
			}
		} else if (component instanceof LayerComponent) {
			T spatial = allSpatials.get(component.getNodeId());
			if (spatial != null) {
				spatial.layer = Layer.DEFAULT;
			}
		}
	}

	@Override
	public void sceneStarted() {
		// TODO Auto-generated method stub
		initSpatials();
	}

	@Override
	public void sceneStopped() {
		// TODO Auto-generated method stub
		clearSpatials();
	}
}
