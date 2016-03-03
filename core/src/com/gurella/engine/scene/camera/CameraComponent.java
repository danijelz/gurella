package com.gurella.engine.scene.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.gurella.engine.event.Listener1;
import com.gurella.engine.resource.model.DefaultValue;
import com.gurella.engine.resource.model.ResourceProperty;
import com.gurella.engine.resource.model.TransientProperty;
import com.gurella.engine.scene.BaseSceneElement;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.scene.layer.Layer;
import com.gurella.engine.scene.movement.TransformComponent;
import com.gurella.engine.subscriptions.application.ApplicationResizeListener;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.Values;

@BaseSceneElement
public abstract class CameraComponent<T extends Camera> extends SceneNodeComponent2
		implements Comparable<CameraComponent<?>>, ApplicationResizeListener {
	private static final Vector3 initialDirection = new Vector3(0, 0, -1);
	private static final Vector3 initialUp = new Vector3(0, 1, 0);

	/** the near clipping plane distance, has to be positive **/
	@DefaultValue(floatValue = 1)
	public float near = 1;
	/** the far clipping plane distance, has to be positive **/
	@DefaultValue(floatValue = 1000)
	public float far = 1000;

	private int ordinal;
	// TODO notify render system for layer changes
	@ResourceProperty
	public final ArrayExt<Layer> renderingLayers = new ArrayExt<Layer>();

	@TransientProperty
	public T camera;
	@TransientProperty
	public CameraViewport viewport;
	@TransientProperty
	private TransformComponent transformComponent;

	private final TransformComponentActivatedListener transformComponentActivatedListener = new TransformComponentActivatedListener();
	private final TransformComponentDeactivatedListener transformComponentDeactivatedListener = new TransformComponentDeactivatedListener();
	private final TransformDirtyListener transformDirtyListener = new TransformDirtyListener();

	public CameraComponent() {
		camera = createCamera();
		viewport = new CameraViewport(camera);
	}
	
	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}

	abstract T createCamera();
	
	@Override
	protected void onActivate() {
		initCamera();
		viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		SceneNode2 node = getNode();
		node.componentActivatedSignal.addListener(transformComponentActivatedListener);
		node.componentDeactivatedSignal.addListener(transformComponentDeactivatedListener);
		transformComponent = node.getActiveComponent(TransformComponent.class);
		attachTransformDirtyListener();
		if (transformComponent == null) {
			updateDefaultTransform();
		} else {
			updateTransform();
		}
	}

	void initCamera() {
		camera.near = near;
		camera.far = far;
		viewport.update();
	}
	
	@Override
	protected void onDeactivate() {
		super.deactivated();
		SceneNode2 node = getNode();
		node.componentActivatedSignal.removeListener(transformComponentActivatedListener);
		node.componentDeactivatedSignal.removeListener(transformComponentDeactivatedListener);
		detachTransformDirtyListener();
		transformComponent = null;
	}

	public float getNear() {
		return near;
	}

	public void setNear(float near) {
		this.near = near;
		camera.near = near;
	}

	public float getFar() {
		return far;
	}

	public void setFar(float far) {
		this.far = far;
		camera.far = far;
	}

	public int getOrdinal() {
		return ordinal;
	}

	public void setOrdinal(int ordinal) {
		this.ordinal = ordinal;
		// TODO notify RenderSystem
	}

	public ImmutableArray<Layer> getRenderingLayers() {
		return renderingLayers.immutable();
	}

	public void setRenderingLayers(Layer... layers) {
		renderingLayers.clear();
		renderingLayers.addAll(layers);
		// TODO notify RenderSystem
	}

	public void addRenderingLayer(Layer layer) {
		if (!renderingLayers.contains(layer, true)) {
			renderingLayers.add(layer);
			// TODO notify RenderSystem
		}
	}

	public void removeRenderingLayer(Layer layer) {
		if (renderingLayers.removeValue(layer, true)) {
			// TODO notify RenderSystem
		}
	}

	@Override
	public int compareTo(CameraComponent<?> other) {
		return Values.compare(ordinal, other.ordinal);
	}

	private void attachTransformDirtyListener() {
		if (transformComponent != null) {
			transformComponent.dirtySignal.addListener(transformDirtyListener);
		}
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
	}

	protected void updateDefaultTransform() {
		camera.position.setZero();
		camera.direction.set(initialDirection);
		camera.up.set(initialUp);
		camera.update(true);
	}

	private Matrix4 tempTransform = new Matrix4();

	protected void updateTransform() {
		camera.position.setZero();
		camera.direction.set(initialDirection);
		camera.up.set(initialUp);
		camera.transform(transformComponent.getWorldTransform(tempTransform));
		camera.normalizeUp();
		camera.update(true);
	}

	private class TransformComponentActivatedListener implements Listener1<SceneNodeComponent2> {
		@Override
		public void handle(SceneNodeComponent2 component) {
			if (component instanceof TransformComponent) {
				setTransformComponent((TransformComponent) component);
			}
		}
	}

	private class TransformComponentDeactivatedListener implements Listener1<SceneNodeComponent2> {
		@Override
		public void handle(SceneNodeComponent2 component) {
			if (component instanceof TransformComponent) {
				setTransformComponent(null);
			}
		}
	}

	private class TransformDirtyListener implements Listener1<TransformComponent> {
		@Override
		public void handle(TransformComponent component) {
			updateTransform();
		}
	}
}
