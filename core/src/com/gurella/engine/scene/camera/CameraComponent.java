package com.gurella.engine.scene.camera;

import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.base.model.PropertyDescriptor;
import com.gurella.engine.base.model.ValueRange;
import com.gurella.engine.base.model.ValueRange.FloatRange;
import com.gurella.engine.base.model.ValueRange.IntegerRange;
import com.gurella.engine.editor.property.PropertyEditorDescriptor;
import com.gurella.engine.event.EventService;
import com.gurella.engine.graphics.render.RenderTarget;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.scene.BaseSceneElement;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.scene.camera.debug.CameraDebugRenderer;
import com.gurella.engine.scene.debug.DebugRenderable;
import com.gurella.engine.scene.renderable.Layer;
import com.gurella.engine.scene.transform.TransformComponent;
import com.gurella.engine.subscriptions.application.ApplicationResizeListener;
import com.gurella.engine.subscriptions.scene.NodeComponentActivityListener;
import com.gurella.engine.subscriptions.scene.transform.NodeTransformChangedListener;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.Values;

@BaseSceneElement
public abstract class CameraComponent<T extends Camera> extends SceneNodeComponent2
		implements ApplicationResizeListener, NodeComponentActivityListener, NodeTransformChangedListener,
		DebugRenderable, Poolable {
	private static final Vector3 initialDirection = new Vector3(0, 0, -1);
	private static final Vector3 initialUp = new Vector3(0, 1, 0);

	private int ordinal;

	// TODO notify render system for layer changes
	@PropertyDescriptor()
	@PropertyEditorDescriptor(descriptiveName = "renderingLayers")
	private final Array<Layer> _renderingLayers = new Array<Layer>();
	public final transient ImmutableArray<Layer> renderingLayers = new ImmutableArray<Layer>(_renderingLayers);
	private RenderTarget renderTarget;

	@PropertyEditorDescriptor(group = "Clear color", descriptiveName = "enable")
	public boolean clearColor = true;
	@PropertyEditorDescriptor(group = "Clear color", descriptiveName = "value")
	public final Color clearColorValue = new Color(0, 0, 0, 1);

	@PropertyEditorDescriptor(group = "Clear depth", descriptiveName = "enable")
	public boolean clearDepth = true;
	@PropertyEditorDescriptor(group = "Clear depth", descriptiveName = "value")
	@ValueRange(floatRange = @FloatRange(min = 0, max = 1))
	public float clearDepthValue = 1;

	@PropertyEditorDescriptor(group = "Clear stencil", descriptiveName = "enable")
	public boolean clearStencil = false;
	@PropertyEditorDescriptor(group = "Clear stencil", descriptiveName = "value")
	@ValueRange(integerRange = @IntegerRange(min = 0, max = 255))
	public int clearStencilValue = 1;

	public final transient T camera;
	public final CameraViewport viewport;

	private transient TransformComponent transformComponent;

	private Matrix4 tempTransform = new Matrix4();

	public CameraComponent() {
		camera = createCamera();
		viewport = new CameraViewport(camera);
		//_renderingLayers.add(Layer.DEFAULT);
	}

	abstract T createCamera();

	@Override
	public void resize(int width, int height) {
		if (renderTarget == null) {
			viewport.update(width, height);
		}
	}

	@Override
	protected void componentActivated() {
		if (renderTarget == null) {
			Graphics graphics = Gdx.graphics;
			viewport.update(graphics.getWidth(), graphics.getHeight());
		} else {
			viewport.update(renderTarget.getWidth(), renderTarget.getHeight());
		}

		transformComponent = getNode().getComponent(TransformComponent.class, false);
		if (transformComponent == null) {
			updateDefaultTransform();
		} else {
			updateTransform();
		}
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
		return camera.near;
	}

	public void setNear(float near) {
		camera.near = near;
	}

	public float getFar() {
		return camera.far;
	}

	public void setFar(float far) {
		camera.far = far;
	}

	public int getOrdinal() {
		return ordinal;
	}

	public void setOrdinal(int ordinal) {
		if (this.ordinal == ordinal) {
			return;
		}

		this.ordinal = ordinal;

		if (isActive()) {
			CameraOrdinalChangedEvent event = PoolService.obtain(CameraOrdinalChangedEvent.class);
			event.cameraComponent = this;
			EventService.post(getScene().getInstanceId(), event);
			PoolService.free(event);
		}
	}

	public ImmutableArray<Layer> getRenderingLayers() {
		return renderingLayers;
	}

	public void setRenderingLayers(Array<Layer> layers) {
		_renderingLayers.clear();
		_renderingLayers.addAll(layers);
	}

	public void addRenderingLayer(Layer layer) {
		if (!_renderingLayers.contains(layer, true)) {
			_renderingLayers.add(layer);
		}
	}

	public void removeRenderingLayer(Layer layer) {
		_renderingLayers.removeValue(layer, true);
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
	public void debugRender(RenderContext context) {
		CameraDebugRenderer.render(context, this);
	}

	public Matrix4 getTransform(Matrix4 out) {
		return transformComponent == null ? out.idt() : transformComponent.getWorldTransform(out);
	}

	@Override
	public void reset() {
		ordinal = 0;
		_renderingLayers.clear();
		transformComponent = null;
		viewport.reset();
	}

	public static final class OrdinalComparator implements Comparator<CameraComponent<?>> {
		public static final OrdinalComparator instance = new OrdinalComparator();

		public OrdinalComparator() {
		}

		@Override
		public int compare(CameraComponent<?> o1, CameraComponent<?> o2) {
			return Values.compare(o1.ordinal, o2.ordinal);
		}
	}
}
