package com.gurella.engine.scene.renderable;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.event.Listener1;
import com.gurella.engine.event.Signal1;
import com.gurella.engine.graphics.GenericBatch;
import com.gurella.engine.resource.model.TransientProperty;
import com.gurella.engine.scene.BaseSceneElement;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.scene.layer.Layer;
import com.gurella.engine.scene.movement.TransformComponent;
import com.gurella.engine.subscriptions.scene.NodeComponentActivityListener;

//TODO PolygonSpriteComponent, DecalComponent, ImmediateModeComponent, SvgComponent
@BaseSceneElement
public abstract class RenderableComponent extends SceneNodeComponent2
		implements NodeComponentActivityListener, Poolable {
	//TODO LayerComponent ??
	public Layer layer = Layer.DEFAULT;

	TransformComponent transformComponent;

	private final TransformDirtyListener transformDirtyListener = new TransformDirtyListener();

	@TransientProperty
	public final Signal1<RenderableComponent> dirtySignal = new Signal1<RenderableComponent>();

	@Override
	protected void onActivate() {
		transformComponent = getNode().getActiveComponent(TransformComponent.class);
		attachTransformDirtyListener();
		if (transformComponent == null) {
			updateDefaultTransform();
		} else {
			updateTransform();
		}
		fireDirty();
	}

	void fireDirty() {
		dirtySignal.dispatch(this);
	}

	private void attachTransformDirtyListener() {
		if (transformComponent != null) {
			transformComponent.dirtySignal.addListener(transformDirtyListener);
		}
	}

	@Override
	protected void onDeactivate() {
		detachTransformDirtyListener();
		transformComponent = null;
	}

	private void detachTransformDirtyListener() {
		if (transformComponent != null) {
			transformComponent.dirtySignal.removeListener(transformDirtyListener);
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

	public void setTransformComponent(TransformComponent transformComponent) {
		detachTransformDirtyListener();
		this.transformComponent = transformComponent;
		attachTransformDirtyListener();

		if (transformComponent == null) {
			updateDefaultTransform();
		} else {
			updateTransform();
		}

		fireDirty();
	}

	protected abstract void updateDefaultTransform();

	protected abstract void updateTransform();

	protected abstract void render(GenericBatch batch);

	public abstract void getBounds(BoundingBox bounds);

	public abstract boolean getIntersection(Ray ray, Vector3 intersection);

	private class TransformDirtyListener implements Listener1<TransformComponent> {
		@Override
		public void handle(TransformComponent component) {
			updateTransform();
			fireDirty();
		}
	}
}
