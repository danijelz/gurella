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
import com.gurella.engine.scene.layer.LayerMask;
import com.gurella.engine.scene.manager.ComponentManager.ComponentFamily;
import com.gurella.engine.scene.manager.NodeManager.SceneNodeFamily;
import com.gurella.engine.scene.movement.TransformComponent;
import com.gurella.engine.scene.renderable.RenderableComponent;
import com.gurella.engine.scene.spatial.Spatial;
import com.gurella.engine.scene.tag.Tag;
import com.gurella.engine.utils.ImmutableArray;

//TODO move methods to parent classes
@SuppressWarnings("unused")
public abstract class BehaviourComponent extends SceneNodeComponent2 {

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

	public void onTouchDown(IntersectionTouchEvent touchEvent) {
	}

	public void onTouchUp(IntersectionTouchEvent touchEvent) {
	}

	public void onTap(IntersectionTouchEvent touchEvent, int count) {
	}

	public void onDragOverStart(IntersectionTouchEvent touchEvent) {
	}

	public void onDragOverMove(IntersectionTouchEvent touchEvent) {
	}

	public void onDragOverEnd(TouchEvent touchEvent) {
	}

	public void onDragStart(IntersectionTouchEvent touchEvent) {
	}

	public void onDragMove(TouchEvent touchEvent) {
	}

	public void onDragEnd(TouchEvent touchEvent) {
	}

	public void onLongPress(IntersectionTouchEvent touchEvent) {
	}

	public void onDoubleTouch(IntersectionTouchEvent touchEvent) {
	}

	public void onScrolled(int screenX, int screenY, int amount, Vector3 intersection) {
	}

	public void onMouseOverStart(int screenX, int screenY, Vector3 intersection) {
	}

	public void onMouseOverMove(int screenX, int screenY, Vector3 intersection) {
	}

	public void onMouseOverEnd(int screenX, int screenY) {
	}

	// NODE DRAG AND DROP

	public DragSource getDragSource(DragStartCondition dragStartCondition) {
		return null;
	}

	public DropTarget getDropTarget(Array<DragSource> dragSources) {
		return null;
	}

	// RESOLVED GLOBAL INPUT

	public void onTouchDown(RenderableComponent renderableComponent, IntersectionTouchEvent touchEvent) {
	}

	public void onTouchUp(RenderableComponent renderableComponent, IntersectionTouchEvent touchEvent) {
	}

	public void onTap(RenderableComponent renderableComponent, IntersectionTouchEvent touchEvent, int count) {
	}

	public void onDragOverStart(RenderableComponent renderableComponent, IntersectionTouchEvent touchEvent) {
	}

	public void onDragOverMove(RenderableComponent renderableComponent, IntersectionTouchEvent touchEvent) {
	}

	public void onDragOverEnd(RenderableComponent renderableComponent, TouchEvent touchEvent) {
	}

	public void onDragStart(RenderableComponent renderableComponent, IntersectionTouchEvent touchEvent) {
	}

	public void onDragMove(RenderableComponent renderableComponent, TouchEvent touchEvent) {
	}

	public void onDragEnd(RenderableComponent renderableComponent, TouchEvent touchEvent) {
	}

	public void onLongPress(RenderableComponent renderableComponent, IntersectionTouchEvent touchEvent) {
	}

	public void onDoubleTouch(RenderableComponent renderableComponent, IntersectionTouchEvent touchEvent) {
	}

	public void onScrolled(RenderableComponent renderableComponent, int screenX, int screenY, int amount,
			Vector3 intersection) {
	}

	public void onMouseOverStart(RenderableComponent renderableComponent, int screenX, int screenY,
			Vector3 intersection) {
	}

	public void onMouseOverMove(RenderableComponent renderableComponent, int screenX, int screenY,
			Vector3 intersection) {
	}

	public void onMouseOverEnd(RenderableComponent renderableComponent, int screenX, int screenY) {
	}

	// //GLOBAL INPUT

	public void keyDown(int keycode) {
	}

	public void keyUp(int keycode) {
	}

	public void keyTyped(char character) {
	}

	public void touchDown(TouchEvent touchEvent) {
	}

	public void doubleTouchDown(TouchEvent touchEvent) {
	}

	public void touchUp(TouchEvent touchEvent) {
	}

	public void touchDragged(TouchEvent touchEvent) {
	}

	public void mouseMoved(int screenX, int screenY) {
	}

	public void scrolled(int screenX, int screenY, int amount) {
	}

	public void tap(TouchEvent touchEvent, int count) {
	}

	public void longPress(TouchEvent touchEvent) {
	}

	// BULLET PHYSICS EVENTS

	public void onCollisionEnter(Collision collision) {
	}

	public void onCollisionStay(Collision collision) {
	}

	public void onCollisionExit(BulletPhysicsRigidBodyComponent rigidBodyComponent) {
	}

	// GLOBAL BULLET PHYSICS EVENTS

	public void onCollisionEnter(CollisionPair collision) {
	}

	public void onCollisionStay(CollisionPair collision) {
	}

	public void onCollisionExit(BulletPhysicsRigidBodyComponent rigidBodyComponent1,
			BulletPhysicsRigidBodyComponent rigidBodyComponent2) {
	}

	public void onPhysicsSimulationStart(btDynamicsWorld dynamicsWorld) {
	}

	public void onPhysicsSimulationStep(btDynamicsWorld dynamicsWorld, float timeStep) {
	}

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

	// TODO OWNING NODE EVENTS 

	public void nodeComponentAdded(SceneNodeComponent2 component) {
	}

	public void nodeComponentRemoved(SceneNodeComponent2 component) {
	}

	public void nodeComponentActivated(SceneNodeComponent2 component) {
	}

	public void nodeComponentDeactivated(SceneNodeComponent2 component) {
	}

	// TODO triggers

	public void nodeParentChanged(SceneNode2 newParent) {
	}

	public void nodeChildAdded(SceneNode2 child) {
	}

	public void nodeChildRemoved(SceneNode2 child) {
	}

	public void componentAdded(SceneNode2 node, SceneNodeComponent2 component) {
	}

	public void componentRemoved(SceneNode2 node, SceneNodeComponent2 component) {
	}

	public void componentActivated(SceneNode2 node, SceneNodeComponent2 component) {
	}

	public void componentDeactivated(SceneNode2 node, SceneNodeComponent2 component) {
	}

	// TODO triggers

	public void parentChanged(SceneNode2 node, SceneNode2 newParent) {
	}

	public void childAdded(SceneNode2 node, SceneNode2 child) {
	}

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

	public void onResize(int width, int height) {
	}

	// TODO transform events
	public void onTransformChanged(TransformComponent transformComponent) {
	}

	// TODO box2d physics events

	// TODO animation events

	// TODO tag events

	public void onNodeTagged(SceneNode2 node, Tag tag) {
	}

	public void onNodeUntagged(SceneNode2 node, Tag tag) {
	}

	// TODO layer events

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

	public <T extends SceneNodeComponent2> void registerComponentFamily(ComponentFamily componentFamily) {
	}

	public <T extends SceneNodeComponent2> void unregisterComponentFamily(ComponentFamily componentFamily) {
	}

	public <T extends SceneNodeComponent2> ImmutableArray<T> getComponents(ComponentFamily componentFamily) {
		return null;
	}

	public void addTag(Tag tag) {
	}

	public void removeTag(Tag tag) {
	}

	public void setLayer(Layer layer) {
	}

	public void getSpatials(BoundingBox bounds, Array<Spatial> out, LayerMask layers) {
	}

	public void getSpatials(Frustum frustum, Array<Spatial> out, LayerMask layers) {
	}

	public void getSpatials(Ray ray, Array<Spatial> out, LayerMask layers) {
	}
	
	public void getSpatials(Ray ray, float maxDistance, Array<Spatial> out, LayerMask layers) {
	}

	public void registerListener(Object listener) {
	}

	public void unregisterListener(Object listener) {
	}

	// TODO Gdx.input methods

	public interface AsyncRequest<T> extends Callable<T> {
		void onSuccess(T value);

		void onError(Throwable error);

		void cancle();
	}

	public abstract class Action {

	}
}
