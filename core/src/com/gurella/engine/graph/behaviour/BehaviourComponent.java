package com.gurella.engine.graph.behaviour;

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
import com.gurella.engine.graph.SceneNode;
import com.gurella.engine.graph.SceneNodeComponent;
import com.gurella.engine.graph.bullet.BulletPhysicsRigidBodyComponent;
import com.gurella.engine.graph.bullet.Collision;
import com.gurella.engine.graph.bullet.CollisionPair;
import com.gurella.engine.graph.event.EventCallback;
import com.gurella.engine.graph.input.DragSource;
import com.gurella.engine.graph.input.DragStartCondition;
import com.gurella.engine.graph.input.DropTarget;
import com.gurella.engine.graph.input.IntersectionTouchEvent;
import com.gurella.engine.graph.input.TouchEvent;
import com.gurella.engine.graph.layer.Layer;
import com.gurella.engine.graph.manager.ComponentManager.ComponentFamily;
import com.gurella.engine.graph.manager.NodeManager.SceneNodeFamily;
import com.gurella.engine.graph.renderable.RenderableComponent;
import com.gurella.engine.graph.spatial.Spatial;
import com.gurella.engine.graph.tag.Tag;
import com.gurella.engine.resource.AsyncResourceCallback;
import com.gurella.engine.resource.ResourceMap;
import com.gurella.engine.scene.SceneTransition;
import com.gurella.engine.utils.ImmutableArray;

//TODO move methods to parent classes
public abstract class BehaviourComponent extends SceneNodeComponent {
	// UPDATE EVENTS
	@EventCallback(trigger = OnInputUpdateTrigger.class, marker = true)
	public void onInput() {
	}

	@EventCallback(trigger = OnThinkUpdateTrigger.class, marker = true)
	public void onThink() {
	}

	@EventCallback(trigger = OnPreRenderUpdateTrigger.class, marker = true)
	public void onPreRender() {
	}

	@EventCallback(trigger = OnRenderUpdateTrigger.class, marker = true)
	public void onRender() {
	}

	@EventCallback(trigger = OnDebugRenderUpdateTrigger.class, marker = true)
	public void onDebugRender() {
	}

	@EventCallback(trigger = OnAfterRenderUpdateTrigger.class, marker = true)
	public void onAfterRender() {
	}

	@EventCallback(trigger = OnCleanupUpdateTrigger.class, marker = true)
	public void onCleanup() {
	}

	// INPUT EVENTS
	@SuppressWarnings("unused")
	@EventCallback(marker = true)
	public void onTouchDown(IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@EventCallback(marker = true)
	public void onTouchUp(IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@EventCallback(marker = true)
	public void onTap(IntersectionTouchEvent touchEvent, int count) {
	}

	@SuppressWarnings("unused")
	@EventCallback(marker = true)
	public void onDragOverStart(IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@EventCallback(marker = true)
	public void onDragOverMove(IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@EventCallback(marker = true)
	public void onDragOverEnd(TouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@EventCallback(marker = true)
	public void onDragStart(IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@EventCallback(marker = true)
	public void onDragMove(TouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@EventCallback(marker = true)
	public void onDragEnd(TouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@EventCallback(marker = true)
	public void onLongPress(IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@EventCallback(marker = true)
	public void onDoubleTouch(IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@EventCallback(marker = true)
	public void onScrolled(int screenX, int screenY, int amount, Vector3 intersection) {
	}

	@SuppressWarnings("unused")
	@EventCallback(marker = true)
	public void onMouseOverStart(int screenX, int screenY, Vector3 intersection) {
	}

	@SuppressWarnings("unused")
	@EventCallback(marker = true)
	public void onMouseOverMove(int screenX, int screenY, Vector3 intersection) {
	}

	@SuppressWarnings("unused")
	@EventCallback(marker = true)
	public void onMouseOverEnd(int screenX, int screenY) {
	}

	// NODE DRAG AND DROP
	@SuppressWarnings("unused")
	@EventCallback(marker = true)
	public DragSource getDragSource(DragStartCondition dragStartCondition) {
		return null;
	}

	@SuppressWarnings("unused")
	@EventCallback(marker = true)
	public DropTarget getDropTarget(Array<DragSource> dragSources) {
		return null;
	}

	// RESOLVED GLOBAL INPUT
	@SuppressWarnings("unused")
	@EventCallback(id = "onTouchDownGlobal", marker = true)
	public void onTouchDown(RenderableComponent renderableComponent, IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@EventCallback(id = "onTouchUpGlobal", marker = true)
	public void onTouchUp(RenderableComponent renderableComponent, IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@EventCallback(id = "onTapGlobal", marker = true)
	public void onTap(RenderableComponent renderableComponent, IntersectionTouchEvent touchEvent, int count) {
	}

	@SuppressWarnings("unused")
	@EventCallback(id = "onDragOverStartGlobal", marker = true)
	public void onDragOverStart(RenderableComponent renderableComponent, IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@EventCallback(id = "onDragOverMoveGlobal", marker = true)
	public void onDragOverMove(RenderableComponent renderableComponent, IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@EventCallback(id = "onDragOverEndGlobal", marker = true)
	public void onDragOverEnd(RenderableComponent renderableComponent, TouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@EventCallback(id = "onDragStartGlobal", marker = true)
	public void onDragStart(RenderableComponent renderableComponent, IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@EventCallback(id = "onDragMoveGlobal", marker = true)
	public void onDragMove(RenderableComponent renderableComponent, TouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@EventCallback(id = "onDragEndGlobal", marker = true)
	public void onDragEnd(RenderableComponent renderableComponent, TouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@EventCallback(id = "onLongPressGlobal", marker = true)
	public void onLongPress(RenderableComponent renderableComponent, IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@EventCallback(id = "onDoubleTouchGlobal", marker = true)
	public void onDoubleTouch(RenderableComponent renderableComponent, IntersectionTouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@EventCallback(id = "onScrolledGlobal", marker = true)
	public void onScrolled(RenderableComponent renderableComponent, int screenX, int screenY, int amount,
			Vector3 intersection) {
	}

	@SuppressWarnings("unused")
	@EventCallback(id = "onMouseOverStartGlobal", marker = true)
	public void onMouseOverStart(RenderableComponent renderableComponent, int screenX, int screenY,
			Vector3 intersection) {
	}

	@SuppressWarnings("unused")
	@EventCallback(id = "onMouseOverMoveGlobal", marker = true)
	public void onMouseOverMove(RenderableComponent renderableComponent, int screenX, int screenY,
			Vector3 intersection) {
	}

	@SuppressWarnings("unused")
	@EventCallback(id = "onMouseOverEndGlobal", marker = true)
	public void onMouseOverEnd(RenderableComponent renderableComponent, int screenX, int screenY) {
	}

	// //GLOBAL INPUT
	@SuppressWarnings("unused")
	@EventCallback(marker = true)
	public void keyDown(int keycode) {
	}

	@SuppressWarnings("unused")
	@EventCallback(marker = true)
	public void keyUp(int keycode) {
	}

	@SuppressWarnings("unused")
	@EventCallback(marker = true)
	public void keyTyped(char character) {
	}

	@SuppressWarnings("unused")
	@EventCallback(marker = true)
	public void touchDown(TouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@EventCallback(marker = true)
	public void doubleTouchDown(TouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@EventCallback(marker = true)
	public void touchUp(TouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@EventCallback(marker = true)
	public void touchDragged(TouchEvent touchEvent) {
	}

	@SuppressWarnings("unused")
	@EventCallback(marker = true)
	public void mouseMoved(int screenX, int screenY) {
	}

	@SuppressWarnings("unused")
	@EventCallback(marker = true)
	public void scrolled(int screenX, int screenY, int amount) {
	}

	@SuppressWarnings("unused")
	@EventCallback(marker = true)
	public void tap(TouchEvent touchEvent, int count) {
	}

	@SuppressWarnings("unused")
	@EventCallback(marker = true)
	public void longPress(TouchEvent touchEvent) {
	}

	// BULLET PHYSICS EVENTS
	@SuppressWarnings("unused")
	@EventCallback(marker = true)
	public void onCollisionEnter(Collision collision) {
	}

	@SuppressWarnings("unused")
	@EventCallback(marker = true)
	public void onCollisionStay(Collision collision) {
	}

	@SuppressWarnings("unused")
	@EventCallback(marker = true)
	public void onCollisionExit(BulletPhysicsRigidBodyComponent rigidBodyComponent) {
	}

	// GLOBAL BULLET PHYSICS EVENTS
	@SuppressWarnings("unused")
	@EventCallback(id = "onCollisionEnterGlobal", marker = true)
	public void onCollisionEnter(CollisionPair collision) {
	}

	@SuppressWarnings("unused")
	@EventCallback(id = "onCollisionStayGlobal", marker = true)
	public void onCollisionStay(CollisionPair collision) {
	}

	@SuppressWarnings("unused")
	@EventCallback(id = "onCollisionExitGlobal", marker = true)
	public void onCollisionExit(BulletPhysicsRigidBodyComponent rigidBodyComponent1,
			BulletPhysicsRigidBodyComponent rigidBodyComponent2) {
	}

	@SuppressWarnings("unused")
	@EventCallback(marker = true)
	public void onPhysicsSimulationStart(btDynamicsWorld dynamicsWorld) {
	}

	@SuppressWarnings("unused")
	@EventCallback(marker = true)
	public void onPhysicsSimulationStep(btDynamicsWorld dynamicsWorld, float timeStep) {
	}

	@SuppressWarnings("unused")
	@EventCallback(marker = true)
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
	@EventCallback(trigger = NodeComponentAddedTrigger.class, marker = true)
	public void nodeComponentAdded(SceneNodeComponent component) {
	}

	@SuppressWarnings("unused")
	@EventCallback(trigger = NodeComponentRemovedTrigger.class, marker = true)
	public void nodeComponentRemoved(SceneNodeComponent component) {
	}

	@SuppressWarnings("unused")
	@EventCallback(trigger = NodeComponentActivatedTrigger.class, marker = true)
	public void nodeComponentActivated(SceneNodeComponent component) {
	}

	@SuppressWarnings("unused")
	@EventCallback(trigger = NodeComponentDeactivatedTrigger.class, marker = true)
	public void nodeComponentDeactivated(SceneNodeComponent component) {
	}

	// TODO triggers
	@SuppressWarnings("unused")
	@EventCallback(marker = true)
	public void nodeParentChanged(SceneNode newParent) {
	}

	@SuppressWarnings("unused")
	@EventCallback(marker = true)
	public void nodeChildAdded(SceneNode child) {
	}

	@SuppressWarnings("unused")
	@EventCallback(marker = true)
	public void nodeChildRemoved(SceneNode child) {
	}

	@SuppressWarnings("unused")
	@EventCallback(trigger = ComponentAddedTrigger.class, marker = true)
	public void componentAdded(SceneNode node, SceneNodeComponent component) {
	}

	@SuppressWarnings("unused")
	@EventCallback(trigger = ComponentRemovedTrigger.class, marker = true)
	public void componentRemoved(SceneNode node, SceneNodeComponent component) {
	}

	@SuppressWarnings("unused")
	@EventCallback(trigger = ComponentActivatedTrigger.class, marker = true)
	public void componentActivated(SceneNode node, SceneNodeComponent component) {
	}

	@SuppressWarnings("unused")
	@EventCallback(trigger = ComponentDeactivatedTrigger.class, marker = true)
	public void componentDeactivated(SceneNode node, SceneNodeComponent component) {
	}

	// TODO triggers
	@SuppressWarnings("unused")
	@EventCallback(marker = true)
	public void parentChanged(SceneNode node, SceneNode newParent) {
	}

	@SuppressWarnings("unused")
	@EventCallback(marker = true)
	public void childAdded(SceneNode node, SceneNode child) {
	}

	@SuppressWarnings("unused")
	@EventCallback(marker = true)
	public void childRemoved(SceneNode node, SceneNode child) {
	}

	// SCENE EVENTS
	@EventCallback(trigger = SceneStartTrigger.class, marker = true)
	public void onSceneStart() {
	}

	@EventCallback(trigger = SceneStopTrigger.class, marker = true)
	public void onSceneStop() {
	}

	// APPLICATION EVENTS
	@EventCallback(trigger = PauseTrigger.class, marker = true)
	public void onPause() {
	}

	@EventCallback(trigger = ResumeTrigger.class, marker = true)
	public void onResume() {
	}

	// TODO
	@SuppressWarnings("unused")
	@EventCallback(marker = true)
	public void onResize(int width, int height) {
	}

	// TODO box2d physics events

	// TODO animation events

	// TODO tag events
	@EventCallback(marker = true)
	public void onNodeTagged(SceneNode node, Tag tag) {
	}

	@EventCallback(marker = true)
	public void onNodeUntagged(SceneNode node, Tag tag) {
	}

	// TODO layer events
	@EventCallback(marker = true)
	public void onNodeLayerChanged(SceneNode node, Layer oldLayer, Layer newLayer) {
	}

	@EventCallback(marker = true)
	public boolean onMessage(Object sender, Object messageType, Object messageData) {
		return false;
	}

	// //////////TODO METHODS
	
	public void loadScene(String sceneId, SceneTransition transition) {
	}
	
	public void nextScene(SceneTransition transition) {
	}
	
	//TODO InputMapper InputContext
	
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

	// TODO Gdx.input methods

	public interface AsyncRequest<T> extends Callable<T> {
		void onSuccess(T value);

		void onError(Throwable error);

		void cancle();
	}

	public abstract class Action {

	}
}
