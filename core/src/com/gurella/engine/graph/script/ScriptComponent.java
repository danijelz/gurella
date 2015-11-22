package com.gurella.engine.graph.script;

import java.util.Comparator;
import java.util.concurrent.Callable;

import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntMap.Values;
import com.gurella.engine.graph.SceneGraphListener;
import com.gurella.engine.graph.SceneNode;
import com.gurella.engine.graph.SceneNodeComponent;
import com.gurella.engine.graph.bullet.BulletPhysicsRigidBodyComponent;
import com.gurella.engine.graph.bullet.Collision;
import com.gurella.engine.graph.bullet.CollisionPair;
import com.gurella.engine.graph.input.DragSource;
import com.gurella.engine.graph.input.DragStartCondition;
import com.gurella.engine.graph.input.DropTarget;
import com.gurella.engine.graph.input.IntersectionTouchEvent;
import com.gurella.engine.graph.input.TouchEvent;
import com.gurella.engine.graph.layer.Layer;
import com.gurella.engine.graph.manager.ComponentManager.ComponentGroup;
import com.gurella.engine.graph.manager.NodeManager.NodeGroup;
import com.gurella.engine.graph.renderable.RenderableComponent;
import com.gurella.engine.graph.spatial.Spatial;
import com.gurella.engine.resource.AsyncResourceCallback;
import com.gurella.engine.resource.ResourceMap;

public abstract class ScriptComponent extends SceneNodeComponent implements SceneGraphListener {
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
	public void onMouseOverStart(RenderableComponent renderableComponent, int screenX, int screenY, Vector3 intersection) {
	}

	@SuppressWarnings("unused")
	public void onMouseOverMove(RenderableComponent renderableComponent, int screenX, int screenY, Vector3 intersection) {
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
	public void onCollisionExit(BulletPhysicsRigidBodyComponent rigidBodyComponent,
			BulletPhysicsRigidBodyComponent rigidBodyComponent1) {
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

	// GRAPH EVENTS
	@Override
	public void componentAdded(SceneNodeComponent component) {
	}

	@Override
	public void componentRemoved(SceneNodeComponent component) {
	}

	@Override
	public void componentActivated(SceneNodeComponent component) {
	}

	@Override
	public void componentDeactivated(SceneNodeComponent component) {
	}

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
	public void nodeComponentAdded(SceneNodeComponent component) {
	}

	@SuppressWarnings("unused")
	public void nodeComponentRemoved(SceneNodeComponent component) {
	}

	@SuppressWarnings("unused")
	public void nodeComponentActivated(SceneNodeComponent component) {
	}

	@SuppressWarnings("unused")
	public void nodeComponentDeactivated(SceneNodeComponent component) {
	}

	@SuppressWarnings("unused")
	public void nodeParentChanged(SceneNode newParent) {
	}

	@SuppressWarnings("unused")
	public void nodeChildAdded(SceneNode child) {
	}

	@SuppressWarnings("unused")
	public void nodeChildRemoved(SceneNode child) {
	}

	// TODO global node events
	@SuppressWarnings("unused")
	public void componentAdded(SceneNode node, SceneNodeComponent component) {
	}

	@SuppressWarnings("unused")
	public void componentRemoved(SceneNode node, SceneNodeComponent component) {
	}

	@SuppressWarnings("unused")
	public void componentActivated(SceneNode node, SceneNodeComponent component) {
	}

	@SuppressWarnings("unused")
	public void componentDeactivated(SceneNode node, SceneNodeComponent component) {
	}

	@SuppressWarnings("unused")
	public void nodeParentChanged(SceneNode node, SceneNode newParent) {
	}

	@SuppressWarnings("unused")
	public void nodeChildAdded(SceneNode node, SceneNode child) {
	}

	@SuppressWarnings("unused")
	public void nodeChildRemoved(SceneNode node, SceneNode child) {
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

	// TODO
	@SuppressWarnings("unused")
	public void onResize(int width, int height) {
	}

	// TODO box2d physics

	// TODO animation

	// //////////TODO METHODS
	public void sendMessage(Object message) {

	}

	public void sendMessageToChildren(Object message) {

	}

	public void sendMessageToParents(Object message) {

	}

	// TODO getActiveComponent
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
		Array<SceneNode> children = getNode().children;
		for (int i = 0; i < children.size; i++) {
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

	public void broadcastMessage(String methodName, Object... parameters) {

	}

	// Calls the method named methodName on every Component in this node or any
	// of its children.
	public void broadcastMessageToChildren(String methodName, Object... parameters) {

	}

	public void broadcastMessageToParents(String methodName, Object... parameters) {

	}

	public <T> void runAssync(final AsyncRequest<T> request) {
	}

	public void runAction(final Action action) {
	}

	public SceneNode getNodeParent() {
		SceneNode node = getNode();
		return node == null
				? null
				: node.getParent();
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

	public NodeGroup[] registerNodeGroupsOnInit() {
		return null;
	}

	public void registerNodeGroup(NodeGroup nodeGroup) {

	}

	public void unregisterNodeGroup(NodeGroup nodeGroup) {

	}

	public Array<SceneNode> getNodes(NodeGroup nodeGroup) {
		return null;
	}

	public ComponentGroup[] registerComponentGroupsOnInit() {
		return null;
	}

	public void registerComponentGroup(ComponentGroup componentGroup) {

	}

	public void unregisterComponentGroup(ComponentGroup componentGroup) {

	}

	public Array<SceneNodeComponent> getComponents(ComponentGroup componentGroup) {
		return null;
	}

	public void getSpatials(BoundingBox bounds, Array<Spatial> out, Layer... layers) {
	}

	public void getSpatials(Frustum frustum, Array<Spatial> out, Layer... layers) {
	}

	public void getSpatials(Ray ray, Array<Spatial> out, Layer... layers) {
	}

	// TODO Gdx.input methods

	public interface AsyncRequest<T> extends Callable<T> {
		void onSuccess(T value);

		void onError(Throwable error);
	}

	public abstract class Action {

	}
}
