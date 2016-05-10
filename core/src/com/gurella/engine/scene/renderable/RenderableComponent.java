package com.gurella.engine.scene.renderable;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.base.model.PropertyDescriptor;
import com.gurella.engine.event.EventService;
import com.gurella.engine.graphics.GenericBatch;
import com.gurella.engine.scene.BaseSceneElement;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.scene.layer.Layer;
import com.gurella.engine.scene.transform.TransformComponent;
import com.gurella.engine.subscriptions.scene.NodeComponentActivityListener;
import com.gurella.engine.subscriptions.scene.movement.NodeTransformChangedListener;
import com.gurella.engine.subscriptions.scene.renderable.SceneRenderableChangedListener;

//TODO PolygonSpriteComponent, DecalComponent, ImmediateModeComponent, SvgComponent
@BaseSceneElement
public abstract class RenderableComponent extends SceneNodeComponent2
		implements NodeComponentActivityListener, NodeTransformChangedListener, Poolable {
	private static final Array<SceneRenderableChangedListener> listeners = new Array<SceneRenderableChangedListener>();
	private static final Object mutex = new Object();

	private transient int sceneId;

	// TODO LayerComponent ??
	@PropertyDescriptor(nullable = false)
	public Layer layer = Layer.DEFAULT;
	transient TransformComponent transformComponent;

	private transient boolean dirty = true;

	@Override
	protected void onActivate() {
		sceneId = getScene().getInstanceId();
		transformComponent = getNode().getActiveComponent(TransformComponent.class);
		if (transformComponent == null) {
			updateDefaultTransform();
		} else {
			updateTransform();
		}
	}

	void notifyChanged(RenderableComponent component) {
		synchronized (mutex) {
			EventService.getSubscribers(sceneId, SceneRenderableChangedListener.class, listeners);
			for (int i = 0; i < listeners.size; i++) {
				listeners.get(i).onRenderableChanged(component);
			}
		}
	}

	@Override
	protected void onDeactivate() {
		sceneId = -1;
		transformComponent = null;
	}

	@Override
	public void onNodeTransformChanged() {
		updateTransform();
		notifyChanged(this);
	}
	
	public void setDirty() {
		if(!dirty) {
			dirty = true;
			notifyChanged(this);
		}
	}

	@Override
	public void nodeComponentActivated(SceneNodeComponent2 component) {
		if (component instanceof TransformComponent) {
			setTransformComponent((TransformComponent) component);
		}
	}

	@Override
	public void nodeComponentDeactivated(SceneNodeComponent2 component) {
		if (component instanceof TransformComponent) {
			setTransformComponent(null);
		}
	}

	public TransformComponent getTransformComponent() {
		return transformComponent;
	}

	private void setTransformComponent(TransformComponent transformComponent) {
		this.transformComponent = transformComponent;
		if (transformComponent == null) {
			updateDefaultTransform();
		} else {
			updateTransform();
		}

		notifyChanged(this);
	}
	
	//TODO call when dirty
	protected void update() {
		if(dirty) {
			//updateGeometry
			dirty = false;
		}
	}

	@Override
	public void reset() {
		sceneId = -1;
		layer = Layer.DEFAULT;
		transformComponent = null;
		dirty = true;
	}

	protected abstract void updateDefaultTransform();

	protected abstract void updateTransform();

	protected abstract void render(GenericBatch batch);

	public abstract void getBounds(BoundingBox bounds);

	public abstract boolean getIntersection(Ray ray, Vector3 intersection);
}
