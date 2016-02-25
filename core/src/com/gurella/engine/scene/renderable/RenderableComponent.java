package com.gurella.engine.scene.renderable;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.gurella.engine.event.Listener1;
import com.gurella.engine.event.Signal1;
import com.gurella.engine.graphics.GenericBatch;
import com.gurella.engine.resource.model.TransientProperty;
import com.gurella.engine.scene.BaseSceneElementType;
import com.gurella.engine.scene.SceneNode;
import com.gurella.engine.scene.SceneNodeComponent;
import com.gurella.engine.scene.layer.Layer;
import com.gurella.engine.scene.movement.TransformComponent;

//TODO PolygonSpriteComponent, DecalComponent, ImmediateModeComponent, SvgComponent
@BaseSceneElementType
public abstract class RenderableComponent extends SceneNodeComponent {
	//TODO LayerComponent ??
	public Layer layer = Layer.DEFAULT;

	TransformComponent transformComponent;

	private final TransformComponentActivatedListener transformComponentActivatedListener = new TransformComponentActivatedListener();
	private final TransformComponentDeactivatedListener transformComponentDeactivatedListener = new TransformComponentDeactivatedListener();
	private final TransformDirtyListener transformDirtyListener = new TransformDirtyListener();

	@TransientProperty
	public final Signal1<RenderableComponent> dirtySignal = new Signal1<RenderableComponent>();

	@Override
	protected void activated() {
		super.activated();
		SceneNode node = getNode();
		node.componentActivatedSignal.addListener(transformComponentActivatedListener);
		node.componentDeactivatedSignal.addListener(transformComponentDeactivatedListener);
		transformComponent = node.getActiveComponent(TransformComponent.class);
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
	protected void deactivated() {
		super.deactivated();
		SceneNode node = getNode();
		node.componentActivatedSignal.removeListener(transformComponentActivatedListener);
		node.componentDeactivatedSignal.removeListener(transformComponentDeactivatedListener);
		detachTransformDirtyListener();
		transformComponent = null;
	}

	private void detachTransformDirtyListener() {
		if (transformComponent != null) {
			transformComponent.dirtySignal.removeListener(transformDirtyListener);
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

	private class TransformComponentActivatedListener implements Listener1<SceneNodeComponent> {
		@Override
		public void handle(SceneNodeComponent component) {
			if (component instanceof TransformComponent) {
				setTransformComponent((TransformComponent) component);
			}
		}
	}

	private class TransformComponentDeactivatedListener implements Listener1<SceneNodeComponent> {
		@Override
		public void handle(SceneNodeComponent component) {
			if (component instanceof TransformComponent) {
				setTransformComponent(null);
			}
		}
	}

	private class TransformDirtyListener implements Listener1<TransformComponent> {
		@Override
		public void handle(TransformComponent component) {
			updateTransform();
			fireDirty();
		}
	}
}
