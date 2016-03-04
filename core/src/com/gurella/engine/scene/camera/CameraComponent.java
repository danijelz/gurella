package com.gurella.engine.scene.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.resource.model.ResourceProperty;
import com.gurella.engine.scene.BaseSceneElement;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.scene.layer.Layer;
import com.gurella.engine.scene.movement.TransformComponent;
import com.gurella.engine.subscriptions.application.ApplicationResizeListener;
import com.gurella.engine.subscriptions.scene.NodeComponentActivityListener;
import com.gurella.engine.subscriptions.scene.movement.NodeTransformChangedListener;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.ImmutableArray;

@BaseSceneElement
public abstract class CameraComponent<T extends Camera> extends SceneNodeComponent2
		implements ApplicationResizeListener, NodeComponentActivityListener, NodeTransformChangedListener, Poolable {
	private static final Vector3 initialDirection = new Vector3(0, 0, -1);
	private static final Vector3 initialUp = new Vector3(0, 1, 0);

	/** the near clipping plane distance, has to be positive **/
	public float near = 1;
	/** the far clipping plane distance, has to be positive **/
	public float far = 1000;

	private int ordinal;
	// TODO notify render system for layer changes
	@ResourceProperty
	public final ArrayExt<Layer> renderingLayers = new ArrayExt<Layer>();

	public final transient T camera;
	public final transient CameraViewport viewport;
	transient TransformComponent transformComponent;

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
		transformComponent = getNode().getActiveComponent(TransformComponent.class);
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
		transformComponent = null;
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

	@Override
	public void onNodeTransformChanged() {
		updateTransform();
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

	private void setTransformComponent(TransformComponent transformComponent) {
		this.transformComponent = transformComponent;
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

	@Override
	public void reset() {
		near = 1;
		far = 1000;
		ordinal = 0;
		renderingLayers.clear();
		transformComponent = null;
	}
}
