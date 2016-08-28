package com.gurella.engine.scene.renderable;

import static com.gurella.engine.editor.ui.event.EditorEventType.Selection;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.base.model.PropertyDescriptor;
import com.gurella.engine.editor.property.PropertyEditorContext;
import com.gurella.engine.editor.property.PropertyEditorDescriptor;
import com.gurella.engine.editor.property.PropertyEditorFactory;
import com.gurella.engine.editor.ui.EditorButton;
import com.gurella.engine.editor.ui.EditorComposite;
import com.gurella.engine.editor.ui.EditorUiFactory;
import com.gurella.engine.editor.ui.event.EditorEvent;
import com.gurella.engine.editor.ui.event.EditorEventListener;
import com.gurella.engine.event.EventService;
import com.gurella.engine.graphics.render.GenericBatch;
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

	transient boolean visible;
	transient TransformComponent transformComponent;
	private transient boolean dirty = true;

	@PropertyDescriptor(nullable = false)
	public Layer layer = Layer.DEFAULT;

	@PropertyEditorDescriptor(factory = InputEventsPropertyEditorFactory.class, complex = false)
	public byte inputEvents;// TODO flags to disable input events (tap, touch, doubleTuch, longPress,
							// mouseMove, scroll, drag)

	protected abstract void updateGeometry();

	protected abstract void doRender(GenericBatch batch);

	protected abstract void doGetBounds(BoundingBox bounds);

	protected abstract boolean doGetIntersection(Ray ray, Vector3 intersection);

	@Override
	protected void onActivate() {
		sceneId = getScene().getInstanceId();
		transformComponent = getNode().getComponent(TransformComponent.class);
	}

	@Override
	protected void onDeactivate() {
		sceneId = -1;
		transformComponent = null;
		dirty = true;
	}

	@Override
	public void onNodeTransformChanged() {
		setDirty();
	}

	public void setDirty() {
		if (!dirty) {
			dirty = true;
			notifyChanged(this);
		}
	}

	private static void notifyChanged(RenderableComponent component) {
		synchronized (mutex) {
			EventService.getSubscribers(component.sceneId, SceneRenderableChangedListener.class, listeners);
			for (int i = 0; i < listeners.size; i++) {
				listeners.get(i).onRenderableChanged(component);
			}
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

	protected void update() {
		if (dirty) {
			updateGeometry();
			dirty = false;
		}
	}

	public final void render(GenericBatch batch) {
		update();
		doRender(batch);
	}

	public final void getBounds(BoundingBox bounds) {
		update();
		doGetBounds(bounds);
	}

	public final boolean getIntersection(Ray ray, Vector3 intersection) {
		update();
		return doGetIntersection(ray, intersection);
	}

	public boolean isVisible() {
		return visible;
	}

	@Override
	public void reset() {
		sceneId = -1;
		layer = Layer.DEFAULT;
		transformComponent = null;
		dirty = true;
	}

	static class InputEventsPropertyEditorFactory implements PropertyEditorFactory<Byte> {
		@Override
		public void buildUi(EditorComposite parent, PropertyEditorContext<Byte> context) {
			createCheck(context, parent, "tap", (byte) 1);
			createCheck(context, parent, "tap", (byte) 2);
		}

		private static void createCheck(PropertyEditorContext<Byte> context, EditorComposite parent, String text,
				byte index) {
			EditorUiFactory uiFactory = parent.getUiFactory();
			EditorButton check = uiFactory.createCheckBox(parent, text);
			byte byteValue = context.getPropertyValue().byteValue();
			check.setSelection((byteValue & (1 << index)) != 0);
			check.addListener(Selection, new InputEventsSelectionListener(context, check, index));
		}
	}

	private static class InputEventsSelectionListener implements EditorEventListener {
		private PropertyEditorContext<Byte> contex;
		private EditorButton check;
		private byte index;

		public InputEventsSelectionListener(PropertyEditorContext<Byte> contex, EditorButton check, byte index) {
			this.contex = contex;
			this.check = check;
			this.index = index;
		}

		@Override
		public void handleEvent(EditorEvent event) {
			byte byteValue = contex.getPropertyValue().byteValue();
			if (check.getSelection()) {
				byteValue = (byte) (byteValue | (1 << index));
			} else {
				byteValue = (byte) (byteValue & ~(1 << index));
			}
			contex.setPropertyValue(Byte.valueOf(byteValue));
		}
	}
}
