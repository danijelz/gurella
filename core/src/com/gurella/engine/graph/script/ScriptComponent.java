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
import com.gurella.engine.graph.manager.ComponentsManager.ComponentFamily;
import com.gurella.engine.graph.manager.NodesManager.SceneNodeFamily;
import com.gurella.engine.graph.renderable.RenderableComponent;
import com.gurella.engine.graph.spatial.Spatial;
import com.gurella.engine.graph.tag.Tag;
import com.gurella.engine.resource.AsyncResourceCallback;
import com.gurella.engine.resource.ResourceMap;
import com.gurella.engine.utils.ImmutableArray;

//TODO move methods to parent classes
public abstract class ScriptComponent extends SceneNodeComponent implements SceneGraphListener {
	// UPDATE EVENTS
	@ScriptMethod(marker = true)
	public void onInput() {
	}

	@ScriptMethod(marker = true)
	public void onThink() {
	}

	@ScriptMethod(marker = true)
	public void onPreRender() {
	}

	@ScriptMethod(marker = true)
	public void onRender() {
	}

	@ScriptMethod(marker = true)
	public void onDebugRender() {
	}

	@ScriptMethod(marker = true)
	public void onAfterRender() {
	}

	@ScriptMethod(marker = true)
	public void onCleanup() {
	}

	// INPUT EVENTS
	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void onTouchDown(IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void onTouchUp(IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void onTap(IntersectionTouchEvent touchEvent, int count) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void onDragOverStart(IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void onDragOverMove(IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void onDragOverEnd(TouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void onDragStart(IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void onDragMove(TouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void onDragEnd(TouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void onLongPress(IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void onDoubleTouch(IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void onScrolled(int screenX, int screenY, int amount, Vector3 intersection) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void onMouseOverStart(int screenX, int screenY, Vector3 intersection) {
	}

	@SuppressWarnings("unused")
	public void onMouseOverMove(int screenX, int screenY, Vector3 intersection) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void onMouseOverEnd(int screenX, int screenY) {
	}

	// NODE DRAG AND DROP
	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public DragSource getDragSource(DragStartCondition dragStartCondition) {
		return null;
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public DropTarget getDropTarget(Array<DragSource> dragSources) {
		return null;
	}

	// RESOLVED GLOBAL INPUT
	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void onTouchDown(RenderableComponent renderableComponent, IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void onTouchUp(RenderableComponent renderableComponent, IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void onTap(RenderableComponent renderableComponent, IntersectionTouchEvent touchEvent, int count) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void onDragOverStart(RenderableComponent renderableComponent, IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void onDragOverMove(RenderableComponent renderableComponent, IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void onDragOverEnd(RenderableComponent renderableComponent, TouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void onDragStart(RenderableComponent renderableComponent, IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void onDragMove(RenderableComponent renderableComponent, TouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void onDragEnd(RenderableComponent renderableComponent, TouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void onLongPress(RenderableComponent renderableComponent, IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void onDoubleTouch(RenderableComponent renderableComponent, IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void onScrolled(RenderableComponent renderableComponent, int screenX, int screenY, int amount,
			Vector3 intersection) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void onMouseOverStart(RenderableComponent renderableComponent, int screenX, int screenY,
			Vector3 intersection) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void onMouseOverMove(RenderableComponent renderableComponent, int screenX, int screenY,
			Vector3 intersection) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void onMouseOverEnd(RenderableComponent renderableComponent, int screenX, int screenY) {
	}

	// //GLOBAL INPUT
	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void keyDown(int keycode) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void keyUp(int keycode) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void keyTyped(char character) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void touchDown(TouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void doubleTouchDown(TouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void touchUp(TouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void touchDragged(TouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void mouseMoved(int screenX, int screenY) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void scrolled(int screenX, int screenY, int amount) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void tap(TouchEvent touchEvent, int count) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void longPress(TouchEvent touchEvent) {
	}

	// BULLET PHYSICS EVENTS
	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void onCollisionEnter(Collision collision) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void onCollisionStay(Collision collision) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void onCollisionExit(BulletPhysicsRigidBodyComponent rigidBodyComponent) {
	}

	// GLOBAL BULLET PHYSICS EVENTS
	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void onCollisionEnter(CollisionPair collision) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void onCollisionStay(CollisionPair collision) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void onCollisionExit(BulletPhysicsRigidBodyComponent rigidBodyComponent,
			BulletPhysicsRigidBodyComponent rigidBodyComponent1) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void onPhysicsSimulationStart(btDynamicsWorld dynamicsWorld) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void onPhysicsSimulationStep(btDynamicsWorld dynamicsWorld, float timeStep) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void onPhysicsSimulationEnd(btDynamicsWorld dynamicsWorld) {
	}

	// TODO onJointBreak

	// GRAPH EVENTS
	@Override
	@ScriptMethod(marker = true)
	public void componentAdded(SceneNodeComponent component) {
	}

	@Override
	@ScriptMethod(marker = true)
	public void componentRemoved(SceneNodeComponent component) {
	}

	@Override
	@ScriptMethod(marker = true)
	public void componentActivated(SceneNodeComponent component) {
	}

	@Override
	@ScriptMethod(marker = true)
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
	@ScriptMethod(marker = true)
	public void nodeComponentAdded(SceneNodeComponent component) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void nodeComponentRemoved(SceneNodeComponent component) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void nodeComponentActivated(SceneNodeComponent component) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void nodeComponentDeactivated(SceneNodeComponent component) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void nodeParentChanged(SceneNode newParent) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void nodeChildAdded(SceneNode child) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void nodeChildRemoved(SceneNode child) {
	}

	// TODO global node events
	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void componentAdded(SceneNode node, SceneNodeComponent component) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void componentRemoved(SceneNode node, SceneNodeComponent component) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void componentActivated(SceneNode node, SceneNodeComponent component) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void componentDeactivated(SceneNode node, SceneNodeComponent component) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void nodeParentChanged(SceneNode node, SceneNode newParent) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void nodeChildAdded(SceneNode node, SceneNode child) {
	}

	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void nodeChildRemoved(SceneNode node, SceneNode child) {
	}

	// SCENE EVENTS
	@ScriptMethod(marker = true)
	public void onSceneStart() {
	}

	@ScriptMethod(marker = true)
	public void onSceneStop() {
	}

	// APPLICATION EVENTS
	@ScriptMethod(marker = true)
	public void onPause() {
	}

	@ScriptMethod(marker = true)
	public void onResume() {
	}

	// TODO
	@SuppressWarnings("unused")
	@ScriptMethod(marker = true)
	public void onResize(int width, int height) {
	}

	// TODO box2d physics events

	// TODO animation events

	// TODO tag events
	@ScriptMethod(marker = true)
	public void onNodeTagged(SceneNode node, Tag tag) {
	}

	@ScriptMethod(marker = true)
	public void onNodeUntagged(SceneNode node, Tag tag) {
	}
	
	// TODO layer events
	@ScriptMethod(marker = true)
	public void onNodeLayerChanged(SceneNode node, Layer oldLayer, Layer newLayer) {
	}

	@ScriptMethod(marker = true)
	public boolean onMessage(Object sender, Object messageType, Object messageData) {
		return false;
	}

	// //////////TODO METHODS
	public void sendMessage(Object message) {

	}

	public void sendMessageToChildren(Object message) {

	}

	public void sendMessageToParents(Object message) {

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

	public void broadcastMessage(Object messageType, Object messageData) {
		getNode().broadcastMessage(this, messageType, messageData);
	}

	// Calls the method named methodName on every Component in this node or any
	// of its children.
	public void broadcastMessageToChildren(Object messageType, Object messageData) {
		getNode().broadcastMessageToChildren(this, messageType, messageData);
	}

	public void broadcastMessageToParents(Object messageType, Object messageData) {
		getNode().broadcastMessageToParents(this, messageType, messageData);
	}

	public <T> void runAssync(final AsyncRequest<T> request) {
	}

	public void runAction(final Action action) {
	}

	public SceneNode getNodeParent() {
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

	public void registerNodeGroup(SceneNodeFamily nodeGroup) {

	}

	public void unregisterNodeGroup(SceneNodeFamily nodeGroup) {

	}

	public Array<SceneNode> getNodes(SceneNodeFamily nodeGroup) {
		return null;
	}

	public ComponentFamily<?>[] registerComponentGroupsOnInit() {
		return null;
	}

	public void registerComponentGroup(ComponentFamily<?> componentGroup) {

	}

	public void unregisterComponentGroup(ComponentFamily<?> componentGroup) {

	}

	public Array<SceneNodeComponent> getComponents(ComponentFamily<?> componentGroup) {
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

	// TODO Gdx.input methods

	public interface AsyncRequest<T> extends Callable<T> {
		void onSuccess(T value);

		void onError(Throwable error);

		void cancle();
	}

	public abstract class Action {

	}
}
