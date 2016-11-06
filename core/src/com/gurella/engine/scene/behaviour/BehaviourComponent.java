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
import com.gurella.engine.async.AsyncCallback;
import com.gurella.engine.scene.SceneElement2;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.scene.bullet.Collision;
import com.gurella.engine.scene.bullet.CollisionPair;
import com.gurella.engine.scene.bullet.rigidbody.BulletRigidBodyComponent;
import com.gurella.engine.scene.input.TouchInfo;
import com.gurella.engine.scene.input.dnd.DragSource;
import com.gurella.engine.scene.input.dnd.DragStartCondition;
import com.gurella.engine.scene.input.dnd.DropTarget;
import com.gurella.engine.scene.manager.ComponentManager.ComponentFamily;
import com.gurella.engine.scene.manager.NodeManager.SceneNodeFamily;
import com.gurella.engine.scene.renderable.Layer;
import com.gurella.engine.scene.renderable.LayerMask;
import com.gurella.engine.scene.renderable.RenderableComponent;
import com.gurella.engine.scene.spatial.Spatial;
import com.gurella.engine.scene.tag.Tag;
import com.gurella.engine.scene.transform.TransformComponent;
import com.gurella.engine.subscriptions.scene.input.PointerActivityListener;
import com.gurella.engine.utils.ImmutableArray;

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


	// NODE DRAG AND DROP

	public DragSource getDragSource(DragStartCondition dragStartCondition) {
		return null;
	}

	public DropTarget getDropTarget(Array<DragSource> dragSources) {
		return null;
	}

	// BULLET PHYSICS EVENTS

	public void onCollisionEnter(Collision collision) {
	}

	public void onCollisionStay(Collision collision) {
	}

	public void onCollisionExit(BulletRigidBodyComponent rigidBodyComponent) {
	}

	// GLOBAL BULLET PHYSICS EVENTS

	public void onCollisionEnter(CollisionPair collision) {
	}

	public void onCollisionStay(CollisionPair collision) {
	}

	public void onCollisionExit(BulletRigidBodyComponent rigidBodyComponent1,
			BulletRigidBodyComponent rigidBodyComponent2) {
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

	public void nodeComponentAdded(SceneNodeComponent2 component) {
	}

	public void nodeComponentRemoved(SceneNodeComponent2 component) {
	}

	public void nodeComponentActivated(SceneNodeComponent2 component) {
	}

	public void nodeComponentDeactivated(SceneNodeComponent2 component) {
	}

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

	public void onResize(int width, int height) {
	}

	public void onTransformChanged(TransformComponent transformComponent) {
	}

	// TODO box2d physics events

	// TODO animation events

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

	public <T> void obtainResourceAsync(int resourceId, AsyncCallback<T> callback) {
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

	public interface AsyncRequest<T> extends Callable<T> {
		void onSuccess(T value);

		void onError(Throwable error);

		void cancle();
	}

	public abstract class Action {

	}
}
