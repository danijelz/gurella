package com.gurella.engine.scene.behaviour;

import java.util.Comparator;
import java.util.concurrent.Callable;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntMap.Values;
import com.gurella.engine.application.Application;
import com.gurella.engine.application.SceneTransition;
import com.gurella.engine.resource.AsyncResourceCallback;
import com.gurella.engine.resource.ResourceMap;
import com.gurella.engine.scene.SceneElement;
import com.gurella.engine.scene.SceneNode;
import com.gurella.engine.scene.SceneNodeComponent;
import com.gurella.engine.scene.behaviour.trigger.AfterRenderUpdateTrigger;
import com.gurella.engine.scene.behaviour.trigger.CleanupUpdateTrigger;
import com.gurella.engine.scene.behaviour.trigger.ComponentActivatedTrigger;
import com.gurella.engine.scene.behaviour.trigger.ComponentAddedTrigger;
import com.gurella.engine.scene.behaviour.trigger.ComponentDeactivatedTrigger;
import com.gurella.engine.scene.behaviour.trigger.ComponentRemovedTrigger;
import com.gurella.engine.scene.behaviour.trigger.DebugRenderUpdateTrigger;
import com.gurella.engine.scene.behaviour.trigger.InputUpdateTrigger;
import com.gurella.engine.scene.behaviour.trigger.NodeComponentActivatedTrigger;
import com.gurella.engine.scene.behaviour.trigger.NodeComponentAddedTrigger;
import com.gurella.engine.scene.behaviour.trigger.NodeComponentDeactivatedTrigger;
import com.gurella.engine.scene.behaviour.trigger.NodeComponentRemovedTrigger;
import com.gurella.engine.scene.behaviour.trigger.PauseTrigger;
import com.gurella.engine.scene.behaviour.trigger.PreRenderUpdateTrigger;
import com.gurella.engine.scene.behaviour.trigger.RenderUpdateTrigger;
import com.gurella.engine.scene.behaviour.trigger.ResumeTrigger;
import com.gurella.engine.scene.behaviour.trigger.SceneStartTrigger;
import com.gurella.engine.scene.behaviour.trigger.SceneStopTrigger;
import com.gurella.engine.scene.behaviour.trigger.ThinkUpdateTrigger;
import com.gurella.engine.scene.bullet.BulletPhysicsRigidBodyComponent;
import com.gurella.engine.scene.bullet.Collision;
import com.gurella.engine.scene.bullet.CollisionPair;
import com.gurella.engine.scene.event.EventSubscriptionCallback;
import com.gurella.engine.scene.input.DragSource;
import com.gurella.engine.scene.input.DragStartCondition;
import com.gurella.engine.scene.input.DropTarget;
import com.gurella.engine.scene.input.IntersectionTouchEvent;
import com.gurella.engine.scene.input.PointerActivityListener;
import com.gurella.engine.scene.input.TouchEvent;
import com.gurella.engine.scene.layer.Layer;
import com.gurella.engine.scene.manager.ComponentManager.ComponentFamily;
import com.gurella.engine.scene.manager.NodeManager.SceneNodeFamily;
import com.gurella.engine.scene.movement.TransformComponent;
import com.gurella.engine.scene.renderable.RenderableComponent;
import com.gurella.engine.scene.spatial.Spatial;
import com.gurella.engine.scene.tag.Tag;
import com.gurella.engine.utils.ImmutableArray;

//TODO move methods to parent classes
public abstract class BehaviourComponent extends SceneNodeComponent {
	private final Array<Releasable<?>> releasables = new Array<Releasable<?>>();

	private void addReleasable(Releasable<?> releasable) {
		releasables.add(releasable);
		if (isActive()) {
			releasable.attach();
		}
	}

	// TODO events
	protected void registerListener(Object listener) {
	}

	protected void unregisterListener(Object listener) {
	}

	protected void registerListener(SceneElement element, Object listener) {
	}

	protected void unregisterListener(SceneElement element, Object listener) {
	}

	// UPDATE EVENTS
	@EventSubscriptionCallback(trigger = InputUpdateTrigger.class, marker = true)
	public void onInput() {
	}

	@EventSubscriptionCallback(trigger = ThinkUpdateTrigger.class, marker = true)
	public void onThink() {
	}

	@EventSubscriptionCallback(trigger = PreRenderUpdateTrigger.class, marker = true)
	public void onPreRender() {
	}

	@EventSubscriptionCallback(trigger = RenderUpdateTrigger.class, marker = true)
	public void onRender() {
	}

	@EventSubscriptionCallback(trigger = DebugRenderUpdateTrigger.class, marker = true)
	public void onDebugRender() {
	}

	@EventSubscriptionCallback(trigger = AfterRenderUpdateTrigger.class, marker = true)
	public void onAfterRender() {
	}

	@EventSubscriptionCallback(trigger = CleanupUpdateTrigger.class, marker = true)
	public void onCleanup() {
	}

	// INPUT EVENTS
	@SuppressWarnings("unused")
	@EventSubscriptionCallback(marker = true)
	public void onTouchDown(IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(marker = true)
	public void onTouchUp(IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(marker = true)
	public void onTap(IntersectionTouchEvent touchEvent, int count) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(marker = true)
	public void onDragOverStart(IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(marker = true)
	public void onDragOverMove(IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(marker = true)
	public void onDragOverEnd(TouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(marker = true)
	public void onDragStart(IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(marker = true)
	public void onDragMove(TouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(marker = true)
	public void onDragEnd(TouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(marker = true)
	public void onLongPress(IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(marker = true)
	public void onDoubleTouch(IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(marker = true)
	public void onScrolled(int screenX, int screenY, int amount, Vector3 intersection) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(marker = true)
	public void onMouseOverStart(int screenX, int screenY, Vector3 intersection) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(marker = true)
	public void onMouseOverMove(int screenX, int screenY, Vector3 intersection) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(marker = true)
	public void onMouseOverEnd(int screenX, int screenY) {
	}

	// NODE DRAG AND DROP
	@SuppressWarnings("unused")
	@EventSubscriptionCallback(marker = true)
	public DragSource getDragSource(DragStartCondition dragStartCondition) {
		return null;
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(marker = true)
	public DropTarget getDropTarget(Array<DragSource> dragSources) {
		return null;
	}

	// RESOLVED GLOBAL INPUT
	@SuppressWarnings("unused")
	@EventSubscriptionCallback(id = "onTouchDownGlobal", marker = true)
	public void onTouchDown(RenderableComponent renderableComponent, IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(id = "onTouchUpGlobal", marker = true)
	public void onTouchUp(RenderableComponent renderableComponent, IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(id = "onTapGlobal", marker = true)
	public void onTap(RenderableComponent renderableComponent, IntersectionTouchEvent touchEvent, int count) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(id = "onDragOverStartGlobal", marker = true)
	public void onDragOverStart(RenderableComponent renderableComponent, IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(id = "onDragOverMoveGlobal", marker = true)
	public void onDragOverMove(RenderableComponent renderableComponent, IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(id = "onDragOverEndGlobal", marker = true)
	public void onDragOverEnd(RenderableComponent renderableComponent, TouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(id = "onDragStartGlobal", marker = true)
	public void onDragStart(RenderableComponent renderableComponent, IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(id = "onDragMoveGlobal", marker = true)
	public void onDragMove(RenderableComponent renderableComponent, TouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(id = "onDragEndGlobal", marker = true)
	public void onDragEnd(RenderableComponent renderableComponent, TouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(id = "onLongPressGlobal", marker = true)
	public void onLongPress(RenderableComponent renderableComponent, IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(id = "onDoubleTouchGlobal", marker = true)
	public void onDoubleTouch(RenderableComponent renderableComponent, IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(id = "onScrolledGlobal", marker = true)
	public void onScrolled(RenderableComponent renderableComponent, int screenX, int screenY, int amount,
			Vector3 intersection) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(id = "onMouseOverStartGlobal", marker = true)
	public void onMouseOverStart(RenderableComponent renderableComponent, int screenX, int screenY,
			Vector3 intersection) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(id = "onMouseOverMoveGlobal", marker = true)
	public void onMouseOverMove(RenderableComponent renderableComponent, int screenX, int screenY,
			Vector3 intersection) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(id = "onMouseOverEndGlobal", marker = true)
	public void onMouseOverEnd(RenderableComponent renderableComponent, int screenX, int screenY) {
	}

	// //GLOBAL INPUT
	@SuppressWarnings("unused")
	@EventSubscriptionCallback(marker = true)
	public void keyDown(int keycode) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(marker = true)
	public void keyUp(int keycode) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(marker = true)
	public void keyTyped(char character) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(marker = true)
	public void touchDown(TouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(marker = true)
	public void doubleTouchDown(TouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(marker = true)
	public void touchUp(TouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(marker = true)
	public void touchDragged(TouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(marker = true)
	public void mouseMoved(int screenX, int screenY) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(marker = true)
	public void scrolled(int screenX, int screenY, int amount) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(marker = true)
	public void tap(TouchEvent touchEvent, int count) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(marker = true)
	public void longPress(TouchEvent touchEvent) {
	}

	// BULLET PHYSICS EVENTS
	@SuppressWarnings("unused")
	@EventSubscriptionCallback(marker = true)
	public void onCollisionEnter(Collision collision) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(marker = true)
	public void onCollisionStay(Collision collision) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(marker = true)
	public void onCollisionExit(BulletPhysicsRigidBodyComponent rigidBodyComponent) {
	}

	// GLOBAL BULLET PHYSICS EVENTS
	@SuppressWarnings("unused")
	@EventSubscriptionCallback(id = "onCollisionEnterGlobal", marker = true)
	public void onCollisionEnter(CollisionPair collision) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(id = "onCollisionStayGlobal", marker = true)
	public void onCollisionStay(CollisionPair collision) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(id = "onCollisionExitGlobal", marker = true)
	public void onCollisionExit(BulletPhysicsRigidBodyComponent rigidBodyComponent1,
			BulletPhysicsRigidBodyComponent rigidBodyComponent2) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(marker = true)
	public void onPhysicsSimulationStart(btDynamicsWorld dynamicsWorld) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(marker = true)
	public void onPhysicsSimulationStep(btDynamicsWorld dynamicsWorld, float timeStep) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(marker = true)
	public void onPhysicsSimulationEnd(btDynamicsWorld dynamicsWorld) {
	}

	// TODO onJointBreak

	// COMPONENT LIFECYCLE EVENTS
	@Override
	protected void init() {
	}

	@Override
	protected void attached() {
	}

	@Override
	protected void activated() {
	}

	@Override
	protected void deactivated() {
	}

	@Override
	protected void resetted() {
	}

	@Override
	protected void detached() {
	}

	@Override
	protected void disposed() {
	}

	// OWNING NODE EVENTS TODO
	@SuppressWarnings("unused")
	@EventSubscriptionCallback(trigger = NodeComponentAddedTrigger.class, marker = true)
	public void nodeComponentAdded(SceneNodeComponent component) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(trigger = NodeComponentRemovedTrigger.class, marker = true)
	public void nodeComponentRemoved(SceneNodeComponent component) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(trigger = NodeComponentActivatedTrigger.class, marker = true)
	public void nodeComponentActivated(SceneNodeComponent component) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(trigger = NodeComponentDeactivatedTrigger.class, marker = true)
	public void nodeComponentDeactivated(SceneNodeComponent component) {
	}

	// TODO triggers
	@SuppressWarnings("unused")
	@EventSubscriptionCallback(marker = true)
	public void nodeParentChanged(SceneNode newParent) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(marker = true)
	public void nodeChildAdded(SceneNode child) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(marker = true)
	public void nodeChildRemoved(SceneNode child) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(trigger = ComponentAddedTrigger.class, marker = true)
	public void componentAdded(SceneNode node, SceneNodeComponent component) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(trigger = ComponentRemovedTrigger.class, marker = true)
	public void componentRemoved(SceneNode node, SceneNodeComponent component) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(trigger = ComponentActivatedTrigger.class, marker = true)
	public void componentActivated(SceneNode node, SceneNodeComponent component) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(trigger = ComponentDeactivatedTrigger.class, marker = true)
	public void componentDeactivated(SceneNode node, SceneNodeComponent component) {
	}

	// TODO triggers
	@SuppressWarnings("unused")
	@EventSubscriptionCallback(marker = true)
	public void parentChanged(SceneNode node, SceneNode newParent) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(marker = true)
	public void childAdded(SceneNode node, SceneNode child) {
	}

	@SuppressWarnings("unused")
	@EventSubscriptionCallback(marker = true)
	public void childRemoved(SceneNode node, SceneNode child) {
	}

	// SCENE EVENTS
	@EventSubscriptionCallback(trigger = SceneStartTrigger.class, marker = true)
	public void onSceneStart() {
	}

	@EventSubscriptionCallback(trigger = SceneStopTrigger.class, marker = true)
	public void onSceneStop() {
	}

	// APPLICATION EVENTS
	@EventSubscriptionCallback(trigger = PauseTrigger.class, marker = true)
	public void onPause() {
	}

	@EventSubscriptionCallback(trigger = ResumeTrigger.class, marker = true)
	public void onResume() {
	}

	public boolean isPaused() {
		Application application = getApplication();
		return application == null ? false : application.isPaused();
	}

	// TODO
	@SuppressWarnings("unused")
	@EventSubscriptionCallback(marker = true)
	public void onResize(int width, int height) {
	}

	// TODO transform events
	public void onTransformChanged(TransformComponent transformComponent) {
	}

	// TODO box2d physics events

	// TODO animation events

	// TODO tag events
	@EventSubscriptionCallback(marker = true)
	public void onNodeTagged(SceneNode node, Tag tag) {
	}

	@EventSubscriptionCallback(marker = true)
	public void onNodeUntagged(SceneNode node, Tag tag) {
	}

	// TODO layer events
	@EventSubscriptionCallback(marker = true)
	public void onNodeLayerChanged(SceneNode node, Layer oldLayer, Layer newLayer) {
	}

	@EventSubscriptionCallback(marker = true)
	public boolean onMessage(Object sender, Object messageType, Object messageData) {
		return false;
	}

	// //////////TODO METHODS

	public void loadScene(String sceneId, SceneTransition transition) {
	}

	public void nextScene(SceneTransition transition) {
	}

	public void previousScene(SceneTransition transition) {
	}

	// TODO InputMapper InputContext

	public void addInputProcessor(InputProcessor inputProcessor) {
	}

	public void removeInputProcessor(InputProcessor inputProcessor) {
	}

	// TODO getActiveComponent getComponentSafely
	public <T extends SceneNodeComponent> T getComponent(Class<T> componnetType) {
		return getNode().getComponent(componnetType);
	}

	public Array<? super SceneNodeComponent> getComponents(Array<? super SceneNodeComponent> out) {
		Values<SceneNodeComponent> components = getNode().getComponents();
		while (components.hasNext) {
			out.add(components.next());
		}
		return out;
	}

	public <T extends SceneNodeComponent> T getComponentInChildren(Class<T> componetType) {
		ImmutableArray<SceneNode> children = getNode().children;
		for (int i = 0; i < children.size(); i++) {
			T component = children.get(i).getComponent(componetType);
			if (component != null) {
				return component;
			}
		}
		return null;
	}

	public <T extends SceneNodeComponent> Array<T> getComponentsInChildren(Class<T> componnetType, Array<T> out) {
		return null;
	}

	public <T extends SceneNodeComponent> T getComponentInParent(Class<T> componnetType) {
		return null;
	}

	public <T extends SceneNodeComponent> Array<T> getComponentsInParent(Class<T> componnetType, Array<T> out) {
		return null;
	}

	public Array<SceneNodeComponent> getSceneComponents(Class<? extends SceneNodeComponent> componentClass,
			Comparator<SceneNodeComponent> comparator) {
		return null;
	}

	public <T> void runAssync(final AsyncRequest<T> request) {
	}

	public void runAction(final Action action) {
	}

	public SceneNode getParentNode() {
		SceneNode node = getNode();
		return node == null ? null : node.getParent();
	}

	public <T> T obtainResource(int resourceId) {
		return null;
	}

	public void obtainResources(IntArray resourceIds, AsyncResourceCallback<ResourceMap> callback) {
	}

	public <T> void obtainResourceAsync(int resourceId, AsyncResourceCallback<T> callback) {
	}

	public void obtainResourcesAsync(IntArray resourceIds, AsyncResourceCallback<ResourceMap> callback) {
	}

	public <T> T load(String fileName, Class<T> type) {
		return null;
	}

	public <T> void loadAsync(String fileName, Class<T> type, AsyncResourceCallback<T> callback) {
	}

	public boolean releaseResource(Object resource) {
		return false;
	}

	public <T extends Disposable> T manageDisposable(T disposable) {
		return null;
	}

	public void dispose(Disposable disposable) {
	}

	public SceneNodeFamily[] registerNodeGroupsOnInit() {
		return null;
	}

	public void registerNodeFamily(SceneNodeFamily nodeFamily) {

	}

	public void unregisterNodeFamily(SceneNodeFamily nodeFamily) {

	}

	public Array<SceneNode> getNodes(SceneNodeFamily nodeFamily) {
		return null;
	}

	public <T extends SceneNodeComponent> void registerComponentFamily(ComponentFamily<T> componentFamily) {
	}

	public <T extends SceneNodeComponent> void unregisterComponentFamily(ComponentFamily<?> componentFamily) {
	}

	public <T extends SceneNodeComponent> ImmutableArray<T> getComponents(ComponentFamily<T> componentFamily) {
		return null;
	}

	public void addTag(Tag tag) {
	}

	public void removeTag(Tag tag) {
	}

	public void setLayer(Layer layer) {
	}

	public void getSpatials(BoundingBox bounds, Array<Spatial> out, Layer... layers) {
	}

	public void getSpatials(Frustum frustum, Array<Spatial> out, Layer... layers) {
	}

	public void getSpatials(Ray ray, Array<Spatial> out, Layer... layers) {
	}

	public void addPointerActivityListener(PointerActivityListener pointerActivityListener) {
	}

	public void removePointerActivityListener(PointerActivityListener pointerActivityListener) {
	}

	// TODO Gdx.input methods

	public interface AsyncRequest<T> extends Callable<T> {
		void onSuccess(T value);

		void onError(Throwable error);

		void cancle();
	}

	public abstract class Action {

	}

	public static abstract class Releasable<T> {
		T value;
		SceneElement owningElement;

		public Releasable(T value) {
			this.value = value;
		}

		public T getValue() {
			return value;
		}

		public SceneElement getOwningElement() {
			return owningElement;
		}

		protected abstract void attach();

		protected abstract void release(T value);
	}

	public static class PointerActivityListenerReleasable extends Releasable<PointerActivityListener> {
		public PointerActivityListenerReleasable(PointerActivityListener value) {
			super(value);
		}

		@Override
		protected void attach() {
			getOwningElement().getScene().inputSystem.pointerActivitySignal.addListener(getValue());
		}

		@Override
		protected void release(PointerActivityListener value) {
			getOwningElement().getScene().inputSystem.pointerActivitySignal.removeListener(getValue());
		}
	}
}
