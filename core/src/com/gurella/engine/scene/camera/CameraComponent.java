package com.gurella.engine.scene.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.base.model.ValueRange;
import com.gurella.engine.base.model.ValueRange.FloatRange;
import com.gurella.engine.base.model.ValueRange.IntegerRange;
import com.gurella.engine.editor.property.PropertyEditorDescriptor;
import com.gurella.engine.graphics.render.GenericBatch;
import com.gurella.engine.scene.BaseSceneElement;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.scene.camera.debug.CameraDebugRenderer;
import com.gurella.engine.scene.debug.DebugRenderable;
import com.gurella.engine.scene.renderable.Layer;
import com.gurella.engine.scene.transform.TransformComponent;
import com.gurella.engine.subscriptions.application.ApplicationResizeListener;
import com.gurella.engine.subscriptions.scene.NodeComponentActivityListener;
import com.gurella.engine.subscriptions.scene.transform.NodeTransformChangedListener;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.ImmutableArray;

@BaseSceneElement
public abstract class CameraComponent<T extends Camera> extends SceneNodeComponent2
		implements ApplicationResizeListener, NodeComponentActivityListener, NodeTransformChangedListener,
		DebugRenderable, Poolable {
	private static final Vector3 initialDirection = new Vector3(0, 0, -1);
	private static final Vector3 initialUp = new Vector3(0, 1, 0);

	public float near = 1;
	public float far = 1000;

	private int ordinal;

	@PropertyEditorDescriptor(group = "Clear color", descriptiveName = "enable")
	public boolean clearColor = true;
	@PropertyEditorDescriptor(group = "Clear color", descriptiveName = "value")
	public final Color clearColorValue = new Color(0, 0, 0, 1);

	@PropertyEditorDescriptor(group = "Clear depth", descriptiveName = "enable")
	public boolean clearDepth = true;
	@PropertyEditorDescriptor(group = "Clear depth", descriptiveName = "value")
	@ValueRange(floatRange = @FloatRange(min = 0, max = 1) )
	public float clearDepthValue = 1;

	@PropertyEditorDescriptor(group = "Clear stencil", descriptiveName = "enable")
	public boolean clearStencil = false;
	@PropertyEditorDescriptor(group = "Clear stencil", descriptiveName = "value")
	@ValueRange(integerRange = @IntegerRange(min = 0, max = 255) )
	public int clearStencilValue = 1;

	// TODO notify render system for layer changes
	public final ArrayExt<Layer> renderingLayers = new ArrayExt<Layer>();
	// TODO RenderTarget

	public final transient T camera;
	public final transient CameraViewport viewport;
	private transient TransformComponent transformComponent;

	private Matrix4 tempTransform = new Matrix4();

	public CameraComponent() {
		camera = createCamera();
		viewport = new CameraViewport(camera);
	}

	abstract T createCamera();

	@Override
	public void resize(int width, int height) {
		// TODO if(RenderTarget != nul)...
		viewport.update(width, height);
	}

	@Override
	protected void componentActivated() {
		initCamera();
		transformComponent = getNode().getComponent(TransformComponent.class, false);
		if (transformComponent == null) {
			updateDefaultTransform();
		} else {
			updateTransform();
		}
	}

	void initCamera() {
		camera.near = near;
		camera.far = far;
		// TODO if(RenderTarget != nul)...
		viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	@Override
	protected void componentDeactivated() {
		transformComponent = null;
	}

	@Override
	public void nodeComponentActivated(SceneNodeComponent2 component) {
		if (component instanceof TransformComponent) {
			this.transformComponent = (TransformComponent) component;
			updateTransform();
		}
	}

	@Override
	public void nodeComponentDeactivated(SceneNodeComponent2 component) {
		if (component instanceof TransformComponent) {
			this.transformComponent = null;
			updateDefaultTransform();
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

	private void updateDefaultTransform() {
		camera.position.setZero();
		camera.direction.set(initialDirection);
		camera.up.set(initialUp);
		camera.update(true);
	}

	private void updateTransform() {
		camera.position.setZero();
		camera.direction.set(initialDirection);
		camera.up.set(initialUp);
		camera.transform(transformComponent.getWorldTransform(tempTransform));
		camera.normalizeUp();
		camera.update(true);
	}

	@Override
	public void debugRender(GenericBatch batch) {
		CameraDebugRenderer.render(this);
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
