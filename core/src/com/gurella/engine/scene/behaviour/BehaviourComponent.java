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
import com.badlogic.gdx.utils.IdentityMap;
import com.badlogic.gdx.utils.IntArray;
import com.gurella.engine.application.SceneTransition;
import com.gurella.engine.resource.AsyncResourceCallback;
import com.gurella.engine.resource.DependencyMap;
import com.gurella.engine.scene.SceneElement2;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.scene.bullet.BulletPhysicsRigidBodyComponent;
import com.gurella.engine.scene.bullet.Collision;
import com.gurella.engine.scene.bullet.CollisionPair;
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
public abstract class BehaviourComponent extends SceneNodeComponent2 {
	private final IdentityMap<Object, Releasable<?>> releasables = new IdentityMap<Object, Releasable<?>>();

	private void addReleasable(Releasable<?> releasable) {
		releasables.put(releasable.value, releasable);
		if (isActive()) {
			releasable.attach();
		}
	}

	private void removeReleasable(Object value) {
		Releasable<?> releasable = releasables.remove(value);
		if (releasable != null && isActive()) {
			releasable.release();
		}
	}

	private void clearReleasables() {
		if (isActive()) {
			for (Releasable<?> releasable : releasables.values()) {
				releasable.release();
			}
		}

		releasables.clear();
	}

	// UPDATE EVENTS
	public void onInput() {
	}

	public void onThink() {
	}

	public void onPreRender() {
	}

	public void onRender() {
	}

	public void onDebugRender() {
	}

	public void onAfterRender() {
	}

	public void onCleanup() {
	}

	// INPUT EVENTS
	@SuppressWarnings("unused")
	public void onTouchDown(IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	public void onTouchUp(IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	public void onTap(IntersectionTouchEvent touchEvent, int count) {
	}

	@SuppressWarnings("unused")
	public void onDragOverStart(IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	public void onDragOverMove(IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	public void onDragOverEnd(TouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	public void onDragStart(IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	public void onDragMove(TouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	public void onDragEnd(TouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	public void onLongPress(IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	public void onDoubleTouch(IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	public void onScrolled(int screenX, int screenY, int amount, Vector3 intersection) {
	}

	@SuppressWarnings("unused")
	public void onMouseOverStart(int screenX, int screenY, Vector3 intersection) {
	}

	@SuppressWarnings("unused")
	public void onMouseOverMove(int screenX, int screenY, Vector3 intersection) {
	}

	@SuppressWarnings("unused")
	public void onMouseOverEnd(int screenX, int screenY) {
	}

	// NODE DRAG AND DROP
	@SuppressWarnings("unused")
	public DragSource getDragSource(DragStartCondition dragStartCondition) {
		return null;
	}

	@SuppressWarnings("unused")
	public DropTarget getDropTarget(Array<DragSource> dragSources) {
		return null;
	}

	// RESOLVED GLOBAL INPUT
	@SuppressWarnings("unused")
	public void onTouchDown(RenderableComponent renderableComponent, IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	public void onTouchUp(RenderableComponent renderableComponent, IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	public void onTap(RenderableComponent renderableComponent, IntersectionTouchEvent touchEvent, int count) {
	}

	@SuppressWarnings("unused")
	public void onDragOverStart(RenderableComponent renderableComponent, IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	public void onDragOverMove(RenderableComponent renderableComponent, IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	public void onDragOverEnd(RenderableComponent renderableComponent, TouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	public void onDragStart(RenderableComponent renderableComponent, IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	public void onDragMove(RenderableComponent renderableComponent, TouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	public void onDragEnd(RenderableComponent renderableComponent, TouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	public void onLongPress(RenderableComponent renderableComponent, IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	public void onDoubleTouch(RenderableComponent renderableComponent, IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	public void onScrolled(RenderableComponent renderableComponent, int screenX, int screenY, int amount,
			Vector3 intersection) {
	}

	@SuppressWarnings("unused")
	public void onMouseOverStart(RenderableComponent renderableComponent, int screenX, int screenY,
			Vector3 intersection) {
	}

	@SuppressWarnings("unused")
	public void onMouseOverMove(RenderableComponent renderableComponent, int screenX, int screenY,
			Vector3 intersection) {
	}

	@SuppressWarnings("unused")
	public void onMouseOverEnd(RenderableComponent renderableComponent, int screenX, int screenY) {
	}

	// //GLOBAL INPUT
	@SuppressWarnings("unused")
	public void keyDown(int keycode) {
	}

	@SuppressWarnings("unused")
	public void keyUp(int keycode) {
	}

	@SuppressWarnings("unused")
	public void keyTyped(char character) {
	}

	@SuppressWarnings("unused")
	public void touchDown(TouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	public void doubleTouchDown(TouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	public void touchUp(TouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	public void touchDragged(TouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	public void mouseMoved(int screenX, int screenY) {
	}

	@SuppressWarnings("unused")
	public void scrolled(int screenX, int screenY, int amount) {
	}

	@SuppressWarnings("unused")
	public void tap(TouchEvent touchEvent, int count) {
	}

	@SuppressWarnings("unused")
	public void longPress(TouchEvent touchEvent) {
	}

	// BULLET PHYSICS EVENTS
	@SuppressWarnings("unused")
	public void onCollisionEnter(Collision collision) {
	}

	@SuppressWarnings("unused")
	public void onCollisionStay(Collision collision) {
	}

	@SuppressWarnings("unused")
	public void onCollisionExit(BulletPhysicsRigidBodyComponent rigidBodyComponent) {
	}

	// GLOBAL BULLET PHYSICS EVENTS
	@SuppressWarnings("unused")
	public void onCollisionEnter(CollisionPair collision) {
	}

	@SuppressWarnings("unused")
	public void onCollisionStay(CollisionPair collision) {
	}

	@SuppressWarnings("unused")
	public void onCollisionExit(BulletPhysicsRigidBodyComponent rigidBodyComponent1,
			BulletPhysicsRigidBodyComponent rigidBodyComponent2) {
	}

	@SuppressWarnings("unused")
	public void onPhysicsSimulationStart(btDynamicsWorld dynamicsWorld) {
	}

	@SuppressWarnings("unused")
	public void onPhysicsSimulationStep(btDynamicsWorld dynamicsWorld, float timeStep) {
	}

	@SuppressWarnings("unused")
	public void onPhysicsSimulationEnd(btDynamicsWorld dynamicsWorld) {
	}

	// TODO onJointBreak

	// COMPONENT LIFECYCLE EVENTS
	@Override
	protected void init() {
	}

	//	@Override
	//	protected void attached() {
	//	}
	//
	//	@Override
	//	protected void activated() {
	//	}
	//
	//	@Override
	//	protected void deactivated() {
	//	}
	//
	//	@Override
	//	protected void resetted() {
	//	}
	//
	//	@Override
	//	protected void detached() {
	//	}
	//
	//	@Override
	//	protected void disposed() {
	//	}

	// OWNING NODE EVENTS TODO
	@SuppressWarnings("unused")
	public void nodeComponentAdded(SceneNodeComponent2 component) {
	}

	@SuppressWarnings("unused")
	public void nodeComponentRemoved(SceneNodeComponent2 component) {
	}

	@SuppressWarnings("unused")
	public void nodeComponentActivated(SceneNodeComponent2 component) {
	}

	@SuppressWarnings("unused")
	public void nodeComponentDeactivated(SceneNodeComponent2 component) {
	}

	// TODO triggers
	@SuppressWarnings("unused")
	public void nodeParentChanged(SceneNode2 newParent) {
	}

	@SuppressWarnings("unused")
	public void nodeChildAdded(SceneNode2 child) {
	}

	@SuppressWarnings("unused")
	public void nodeChildRemoved(SceneNode2 child) {
	}

	@SuppressWarnings("unused")
	public void componentAdded(SceneNode2 node, SceneNodeComponent2 component) {
	}

	@SuppressWarnings("unused")
	public void componentRemoved(SceneNode2 node, SceneNodeComponent2 component) {
	}

	@SuppressWarnings("unused")
	public void componentActivated(SceneNode2 node, SceneNodeComponent2 component) {
	}

	@SuppressWarnings("unused")
	public void componentDeactivated(SceneNode2 node, SceneNodeComponent2 component) {
	}

	// TODO triggers
	@SuppressWarnings("unused")
	public void parentChanged(SceneNode2 node, SceneNode2 newParent) {
	}

	@SuppressWarnings("unused")
	public void childAdded(SceneNode2 node, SceneNode2 child) {
	}

	@SuppressWarnings("unused")
	public void childRemoved(SceneNode2 node, SceneNode2 child) {
	}

	// SCENE EVENTS
	public void onSceneStart() {
	}

	public void onSceneStop() {
	}

	// APPLICATION EVENTS
	public void onPause() {
	}

	public void onResume() {
	}

	public boolean isPaused() {
		return false;
	}

	// TODO
	@SuppressWarnings("unused")
	public void onResize(int width, int height) {
	}

	// TODO transform events
	public void onTransformChanged(TransformComponent transformComponent) {
	}

	// TODO box2d physics events

	// TODO animation events

	// TODO tag events
	@SuppressWarnings("unused")
	public void onNodeTagged(SceneNode2 node, Tag tag) {
	}

	@SuppressWarnings("unused")
	public void onNodeUntagged(SceneNode2 node, Tag tag) {
	}

	// TODO layer events
	@SuppressWarnings("unused")
	public void onNodeLayerChanged(SceneNode2 node, Layer oldLayer, Layer newLayer) {
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
	public <T extends SceneNodeComponent2> T getComponent(Class<T> componnetType) {
		return getNode().getComponent(componnetType);
	}

	public Array<? super SceneNodeComponent2> getComponents(Array<? super SceneNodeComponent2> out) {
		//		Values<SceneNodeComponent2> components = getNode().getComponents();
		//		while (components.hasNext) {
		//			out.add(components.next());
		//		}
		return out;
	}

	public <T extends SceneNodeComponent2> T getComponentInChildren(Class<T> componetType) {
		//		ImmutableArray<SceneNode2> children = getNode().children;
		//		for (int i = 0; i < children.size(); i++) {
		//			T component = children.get(i).getComponent(componetType);
		//			if (component != null) {
		//				return component;
		//			}
		//		}
		return null;
	}

	public <T extends SceneNodeComponent2> Array<T> getComponentsInChildren(Class<T> componnetType, Array<T> out) {
		return null;
	}

	public <T extends SceneNodeComponent2> T getComponentInParent(Class<T> componnetType) {
		return null;
	}

	public <T extends SceneNodeComponent2> Array<T> getComponentsInParent(Class<T> componnetType, Array<T> out) {
		return null;
	}

	public Array<SceneNodeComponent2> getSceneComponents(Class<? extends SceneNodeComponent2> componentClass,
			Comparator<SceneNodeComponent2> comparator) {
		return null;
	}

	public <T> void runAssync(final AsyncRequest<T> request) {
	}

	public void runAction(final Action action) {
	}

	public SceneNode2 getParentNode() {
		SceneNode2 node = getNode();
		return null;// node == null ? null : node.getParent();
	}

	public <T> T obtainResource(int resourceId) {
		return null;
	}

	public DependencyMap obtainResources(IntArray resourceIds) {
		return null;
	}

	public <T> void obtainResourceAsync(int resourceId, AsyncResourceCallback<T> callback) {
	}

	public void obtainResourcesAsync(IntArray resourceIds, AsyncResourceCallback<DependencyMap> callback) {
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

	public Array<SceneNode2> getNodes(SceneNodeFamily nodeFamily) {
		return null;
	}

	public <T extends SceneNodeComponent2> void registerComponentFamily(ComponentFamily<T> componentFamily) {
	}

	public <T extends SceneNodeComponent2> void unregisterComponentFamily(ComponentFamily<?> componentFamily) {
	}

	public <T extends SceneNodeComponent2> ImmutableArray<T> getComponents(ComponentFamily<T> componentFamily) {
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

	public void registerListener(Object listener) {
	}

	public void unregisterListener(Object listener) {
	}

	public void addPointerActivityListener(PointerActivityListener pointerActivityListener) {
		PointerActivityListenerReleasable releasable = new PointerActivityListenerReleasable();
		releasable.init(this, pointerActivityListener);
		addReleasable(releasable);
	}

	public void removePointerActivityListener(PointerActivityListener pointerActivityListener) {
		removeReleasable(pointerActivityListener);
	}

	// TODO Gdx.input methods

	public interface AsyncRequest<T> extends Callable<T> {
		void onSuccess(T value);

		void onError(Throwable error);

		void cancle();
	}

	public abstract class Action {

	}

	// TODO poolable
	public static abstract class Releasable<T> {
		SceneElement2 owningElement;
		T value;

		void init(SceneElement2 owningElement, T value) {
			this.owningElement = owningElement;
			this.value = value;
		}

		public T getValue() {
			return value;
		}

		public SceneElement2 getOwningElement() {
			return owningElement;
		}

		protected abstract void attach();

		protected abstract void release();
	}

	public static class PointerActivityListenerReleasable extends Releasable<PointerActivityListener> {
		@Override
		protected void attach() {
			getOwningElement().getScene().inputSystem.pointerActivitySignal.addListener(getValue());
		}

		@Override
		protected void release() {
			getOwningElement().getScene().inputSystem.pointerActivitySignal.removeListener(getValue());
		}
	}
}
