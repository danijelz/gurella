package com.gurella.engine.scene.renderable;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.base.model.PropertyDescriptor;
import com.gurella.engine.editor.property.PropertyEditorDescriptor;
import com.gurella.engine.editor.property.PropertyEditorDescriptor.EditorType;
import com.gurella.engine.event.Event;
import com.gurella.engine.event.EventService;
import com.gurella.engine.graphics.render.GenericBatch;
import com.gurella.engine.scene.BaseSceneElement;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.scene.transform.TransformComponent;
import com.gurella.engine.subscriptions.scene.NodeComponentActivityListener;
import com.gurella.engine.subscriptions.scene.renderable.SceneRenderableChangedListener;
import com.gurella.engine.subscriptions.scene.transform.NodeTransformChangedListener;

//TODO PolygonSpriteComponent, ImmediateModeComponent, SvgComponent
//TODO input data should be moved to InputResponsiveRenderableComponent
@BaseSceneElement
public abstract class RenderableComponent extends SceneNodeComponent2 implements NodeComponentActivityListener,
		NodeTransformChangedListener, Event<SceneRenderableChangedListener>, Poolable {
	@PropertyDescriptor(nullable = false)
	@PropertyEditorDescriptor(factory = LayerPropertyEditorFactory.class, type = EditorType.simple)
	Layer layer = Layer.DEFAULT;

	@PropertyEditorDescriptor(index = 1000, group = "input", descriptiveName = "loose")
	public boolean looseInput;

	private transient int sceneId;

	transient boolean visible;
	transient TransformComponent transformComponent;
	private transient boolean dirty = true;
	private transient boolean boundsDirty = true;
	private transient float minx, miny, minz, maxx, maxy, maxz;

	protected abstract void updateGeometry();

	protected abstract void doRender(GenericBatch batch);

	protected abstract void calculateBounds(BoundingBox bounds);

	@Override
	protected void componentActivated() {
		sceneId = getScene().getInstanceId();
		transformComponent = getNode().getComponent(TransformComponent.class);
	}

	@Override
	protected void componentDeactivated() {
		sceneId = -1;
		transformComponent = null;
		dirty = true;
	}

	@Override
	public void onNodeTransformChanged() {
		setDirty();
	}

	void setDirty() {
		if (!dirty) {
			dirty = true;
			boundsDirty = true;
			EventService.post(sceneId, this);
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
		setDirty();
	}

	protected boolean update() {
		if (dirty) {
			updateGeometry();
			dirty = false;
			return true;
		} else {
			return false;
		}
	}

	public final void render(GenericBatch batch) {
		update();
		doRender(batch);
	}

	public final void getBounds(BoundingBox bounds) {
		if (boundsDirty) {
			update();
			calculateBounds(bounds);
			Vector3 min = bounds.min;
			minx = min.x;
			miny = min.y;
			minz = min.z;
			Vector3 max = bounds.max;
			maxx = max.x;
			maxy = max.y;
			maxz = max.z;
		} else {
			bounds.set(bounds.min.set(minx, miny, minz), bounds.max.set(maxx, maxy, maxz));
			boundsDirty = false;
		}
	}

	public boolean isVisible() {
		return visible;
	}

	public Layer getLayer() {
		return layer == null ? Layer.DEFAULT : layer;
	}

	public void setLayer(Layer layer) {
		this.layer = layer == null ? Layer.DEFAULT : layer;
	}

	@Override
	public Class<SceneRenderableChangedListener> getSubscriptionType() {
		return SceneRenderableChangedListener.class;
	}

	@Override
	public void dispatch(SceneRenderableChangedListener listener) {
		listener.onRenderableChanged(this);
	}

	@Override
	public void reset() {
		sceneId = -1;
		layer = Layer.DEFAULT;
		transformComponent = null;
		dirty = true;
		boundsDirty = true;
		visible = false;
		looseInput = false;
	}
}
