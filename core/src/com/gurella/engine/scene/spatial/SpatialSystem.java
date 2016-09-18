package com.gurella.engine.scene.spatial;

import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntMap.Values;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Predicate;
import com.gurella.engine.scene.BaseSceneElement;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.scene.SceneService;
import com.gurella.engine.scene.renderable.RenderableComponent;
import com.gurella.engine.subscriptions.scene.ComponentActivityListener;
import com.gurella.engine.subscriptions.scene.SceneActivityListener;
import com.gurella.engine.subscriptions.scene.renderable.SceneRenderableChangedListener;

@BaseSceneElement
public abstract class SpatialSystem<T extends Spatial> extends SceneService
		implements ComponentActivityListener, SceneActivityListener, SceneRenderableChangedListener, Poolable {
	private Object mutex = new Object();

	protected IntMap<T> allSpatials = new IntMap<T>();
	protected IntMap<T> dirtySpatials = new IntMap<T>();
	protected IntMap<T> addedSpatials = new IntMap<T>();
	protected IntMap<T> removedSpatials = new IntMap<T>();

	protected IntMap<T> spatialsByRenderableComponent = new IntMap<T>();

	public abstract BoundingBox getBounds(BoundingBox out);

	private void initSpatials() {
		synchronized (mutex) {
			doInitSpatials();
		}
	}

	protected abstract void doInitSpatials();

	private void add(T spatial) {
		synchronized (mutex) {
			addedSpatials.put(spatial.nodeId, spatial);
			allSpatials.put(spatial.nodeId, spatial);
			spatialsByRenderableComponent.put(spatial.renderableComponent.getInstanceId(), spatial);
		}
	}

	private void remove(T spatial) {
		synchronized (mutex) {
			if (allSpatials.remove(spatial.nodeId) != null) {
				removeSpatial(spatial);
			}
		}
	}

	private void removeSpatial(T spatial) {
		spatialsByRenderableComponent.remove(spatial.renderableComponent.getInstanceId());
		removedSpatials.put(spatial.nodeId, spatial);
		addedSpatials.remove(spatial.nodeId);
		dirtySpatials.remove(spatial.nodeId);
	}

	private void updateSpatials() {
		if (removedSpatials.size > 0 || addedSpatials.size > 0 || dirtySpatials.size > 0) {
			doUpdateSpatials();

			removedSpatials.clear();
			addedSpatials.clear();
			dirtySpatials.clear();
		}
	}

	protected abstract void doUpdateSpatials();

	public final void getSpatials(BoundingBox bounds, Array<Spatial> out, Predicate<RenderableComponent> predicate) {
		synchronized (mutex) {
			updateSpatials();
			doGetSpatials(bounds, out, predicate);
		}
	}

	protected abstract void doGetSpatials(BoundingBox bounds, Array<Spatial> out,
			Predicate<RenderableComponent> predicate);

	public final void getSpatials(Frustum frustum, Array<Spatial> out, Predicate<RenderableComponent> predicate) {
		synchronized (mutex) {
			updateSpatials();
			doGetSpatials(frustum, out, predicate);
		}
	}

	protected abstract void doGetSpatials(Frustum frustum, Array<Spatial> out,
			Predicate<RenderableComponent> predicate);

	public final void getSpatials(Ray ray, Array<Spatial> out, Predicate<RenderableComponent> predicate) {
		synchronized (mutex) {
			updateSpatials();
			doGetSpatials(ray, out, predicate);
		}
	}

	protected abstract void doGetSpatials(Ray ray, Array<Spatial> out, Predicate<RenderableComponent> predicate);

	public final void getSpatials(Ray ray, float maxDistance, Array<Spatial> out,
			Predicate<RenderableComponent> predicate) {
		synchronized (mutex) {
			updateSpatials();
			doGetSpatials(ray, maxDistance, out, predicate);
		}
	}

	protected abstract void doGetSpatials(Ray ray, float maxDistance, Array<Spatial> out,
			Predicate<RenderableComponent> predicate);

	public final void clearSpatials() {
		synchronized (mutex) {
			Values<T> values = allSpatials.values();
			while (values.hasNext) {
				T spatial = values.next();
				removeSpatial(spatial);
			}
			doClearSpatials();
			allSpatials.clear();
		}
	}

	protected abstract void doClearSpatials();

	@Override
	public void onRenderableChanged(RenderableComponent component) {
		T spatial = spatialsByRenderableComponent.get(component.getInstanceId());
		if (spatial != null) {
			markDirty(spatial);
		}
	}

	private void markDirty(T spatial) {
		synchronized (mutex) {
			int nodeId = spatial.nodeId;
			if (!addedSpatials.containsKey(nodeId)) {
				dirtySpatials.put(nodeId, spatial);
			}
		}
	}

	protected abstract T createSpatial(RenderableComponent drawableComponent);

	@Override
	public void componentActivated(SceneNodeComponent2 component) {
		if (component instanceof RenderableComponent) {
			T spatial = createSpatial((RenderableComponent) component);
			add(spatial);
		}
	}

	@Override
	public void componentDeactivated(SceneNodeComponent2 component) {
		if (component instanceof RenderableComponent) {
			T spatial = allSpatials.get(component.getNodeId());
			if (spatial != null) {
				remove(spatial);
			}
		}
	}

	@Override
	public void sceneStarted() {
		initSpatials();
	}

	@Override
	public void sceneStopped() {
		clearSpatials();
	}
	
	@Override
	public void reset() {
		// TODO Auto-generated method stub
	}
}
