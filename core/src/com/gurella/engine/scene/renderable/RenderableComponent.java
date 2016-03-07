package com.gurella.engine.scene.renderable;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.event.EventService;
import com.gurella.engine.graphics.GenericBatch;
import com.gurella.engine.scene.BaseSceneElement;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.scene.layer.Layer;
import com.gurella.engine.scene.movement.TransformComponent;
import com.gurella.engine.subscriptions.scene.NodeComponentActivityListener;
import com.gurella.engine.subscriptions.scene.movement.NodeTransformChangedListener;
import com.gurella.engine.subscriptions.scene.renderable.SceneRenderableChangedListener;

//TODO PolygonSpriteComponent, DecalComponent, ImmediateModeComponent, SvgComponent
@BaseSceneElement
public abstract class RenderableComponent extends SceneNodeComponent2
		implements NodeComponentActivityListener, NodeTransformChangedListener, Poolable {
	private static final Array<SceneRenderableChangedListener> listeners = new Array<SceneRenderableChangedListener>();
	private static final Object lock = new Object();

	private transient int nodeId;

	//TODO LayerComponent ??
	public Layer layer = Layer.DEFAULT;
	transient TransformComponent transformComponent;

	private transient boolean changeDispatched = true;

	@Override
	protected void onActivate() {
		SceneNode2 node = getNode();
		nodeId = node.getInstanceId();
		transformComponent = node.getActiveComponent(TransformComponent.class);
		if (transformComponent == null) {
			updateDefaultTransform();
		} else {
			updateTransform();
		}
	}

	static void notifyChanged(RenderableComponent component) {
		synchronized (lock) {
			if (!component.changeDispatched) {
				component.changeDispatched = true;
				EventService.getSubscribers(component.nodeId, SceneRenderableChangedListener.class, listeners);
				for (int i = 0; i < listeners.size; i++) {
					listeners.get(i).onRenderableChanged(component);
				}
			}
		}
	}

	@Override
	protected void onDeactivate() {
		nodeId = -1;
		transformComponent = null;
	}

	@Override
	public void onNodeTransformChanged() {
		notifyChanged(this);
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

	@Override
	public void reset() {
		nodeId = -1;
		layer = Layer.DEFAULT;
		transformComponent = null;
		changeDispatched = true;
	}

	protected abstract void updateDefaultTransform();

	protected abstract void updateTransform();

	protected abstract void render(GenericBatch batch);

	public abstract void getBounds(BoundingBox bounds);

	public abstract boolean getIntersection(Ray ray, Vector3 intersection);
}
