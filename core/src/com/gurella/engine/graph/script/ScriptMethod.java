package com.gurella.engine.graph.script;

import java.util.Arrays;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.Method;
import com.gurella.engine.graph.SceneNode;
import com.gurella.engine.graph.SceneNodeComponent;
import com.gurella.engine.graph.bullet.BulletPhysicsRigidBodyComponent;
import com.gurella.engine.graph.bullet.Collision;
import com.gurella.engine.graph.bullet.CollisionPair;
import com.gurella.engine.graph.input.DragStartCondition;
import com.gurella.engine.graph.input.IntersectionTouchEvent;
import com.gurella.engine.graph.input.TouchEvent;
import com.gurella.engine.graph.renderable.RenderableComponent;
import com.gurella.engine.pools.SynchronizedPools;

public enum ScriptMethod {
	//@formatter:off
	
	//UPDATE
	onInput(), 
	onThink(), 
	onPreRender(),
	onRender(), 
	onDebugRender(), 
	onAfterRender(), 
	onCleanup(), 
	
	//NODE INPUT
	onTouchDown(IntersectionTouchEvent.class),
	onTouchUp(IntersectionTouchEvent.class),
	onTap(IntersectionTouchEvent.class, Integer.TYPE),
	onDragOverStart(IntersectionTouchEvent.class),
	onDragOverMove(IntersectionTouchEvent.class),
	onDragOverEnd(TouchEvent.class),
	onDragStart(IntersectionTouchEvent.class),
	onDragMove(TouchEvent.class),
	onDragEnd(TouchEvent.class),
	onLongPress(IntersectionTouchEvent.class),
	onDoubleTouch(IntersectionTouchEvent.class),
	onScrolled(Integer.TYPE, Integer.TYPE, Integer.TYPE, Vector3.class),
	onMouseOverStart(Integer.TYPE, Integer.TYPE, Vector3.class),
	onMouseOverMove(Integer.TYPE, Integer.TYPE, Vector3.class),
	onMouseOverEnd(Integer.TYPE, Integer.TYPE),
	
	//NODE DRAG AND DROP
	getDragSource(DragStartCondition.class),
	getDropTarget(Array.class),
	
	//RESOLVED GLOBAL INPUT
	onTouchDownResolved("onTouchDown", RenderableComponent.class, IntersectionTouchEvent.class),
	onTouchUpResolved("onTouchUp", RenderableComponent.class, IntersectionTouchEvent.class),
	onTapResolved("onTap", RenderableComponent.class, IntersectionTouchEvent.class, Integer.TYPE),
	onDragOverStartResolved("onDragOverStart", RenderableComponent.class, IntersectionTouchEvent.class),
	onDragOverMoveResolved("onDragOverMove", RenderableComponent.class, IntersectionTouchEvent.class),
	onDragOverEndResolved("onDragOverEnd", RenderableComponent.class, TouchEvent.class),
	onDragStartResolved("onDragStart", RenderableComponent.class, IntersectionTouchEvent.class),
	onDragMoveResolved("onDragMove", RenderableComponent.class, TouchEvent.class),
	onDragEndResolved("onDragEnd", RenderableComponent.class, TouchEvent.class),
	onLongPressResolved("onLongPress", RenderableComponent.class, IntersectionTouchEvent.class),
	onDoubleTouchResolved("onDoubleTouch", RenderableComponent.class, IntersectionTouchEvent.class),
	onScrolledResolved("onScrolled", RenderableComponent.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Vector3.class),
	onMouseOverStartResolved("onMouseOverStart", RenderableComponent.class, Integer.TYPE, Integer.TYPE, Vector3.class),
	onMouseOverMoveResolved("onMouseOverMove", RenderableComponent.class, Integer.TYPE, Integer.TYPE, Vector3.class),
	onMouseOverEndResolved("onMouseOverEnd", RenderableComponent.class, Integer.TYPE, Integer.TYPE),
	
	//GLOBAL INPUT
	keyDown(Integer.TYPE),
    keyUp(Integer.TYPE),
    keyTyped(Character.TYPE),
	touchDown(TouchEvent.class),
	doubleTouchDown(TouchEvent.class),
	touchUp(TouchEvent.class),
	touchDragged(TouchEvent.class),
	mouseMoved(Integer.TYPE, Integer.TYPE),
	scrolled(Integer.TYPE, Integer.TYPE, Integer.TYPE),
	tap(TouchEvent.class, Integer.TYPE),
	longPress(TouchEvent.class),
	
	//BULLET PHYSICS
	onCollisionEnter(Collision.class), 
	onCollisionStay(Collision.class), 
	onCollisionExit(BulletPhysicsRigidBodyComponent.class),
	onPhysicsSimulationStart(btDynamicsWorld.class),
	onPhysicsSimulationStep(btDynamicsWorld.class, Float.TYPE),
	onPhysicsSimulationEnd(btDynamicsWorld.class),
	
	//GLOBAL BULLET COLLISIONS
	onCollisionEnterGlobal(CollisionPair.class),
	onCollisionStayGlobal(CollisionPair.class),
	onCollisionExitGlobal(BulletPhysicsRigidBodyComponent.class, BulletPhysicsRigidBodyComponent.class),
	
	//GRAPH
	componentAdded(SceneNodeComponent.class),
	componentRemoved(SceneNodeComponent.class),
	componentActivated(SceneNodeComponent.class),
	componentDeactivated(SceneNodeComponent.class),
	
	nodeComponentAdded(SceneNodeComponent.class),
	nodeComponentRemoved(SceneNodeComponent.class),
	nodeComponentActivated(SceneNodeComponent.class),
	nodeComponentDeactivated(SceneNodeComponent.class),
	nodeParentChanged(SceneNode.class),
	nodeChildAdded(SceneNode.class),
	nodeChildRemoved(SceneNode.class),
	
	//SCENE
	onSceneStart(),
	onSceneStop(),
	
	//APPLICATION
	onPause(),
	onResume(),
	;
	
	//@formatter:on

	private static final ObjectMap<ScriptMethodKey, ScriptMethod> methods = new ObjectMap<ScriptMethod.ScriptMethodKey, ScriptMethod>();
	static {
		ScriptMethod[] scriptMethods = ScriptMethod.values();
		for (int i = 0; i < scriptMethods.length; i++) {
			ScriptMethod scriptMethod = scriptMethods[i];
			methods.put(new ScriptMethodKey(scriptMethod), scriptMethod);
		}
	}

	final String name;
	final Class<?>[] parameterTypes;

	ScriptMethod(String name, Class<?>... parameterTypes) {
		this.name = name;
		this.parameterTypes = parameterTypes;
	}

	ScriptMethod(Class<?>... parameterTypes) {
		this.name = name();
		this.parameterTypes = parameterTypes;
	}

	static ScriptMethod valueOf(Method method) {
		ScriptMethodKey key = SynchronizedPools.obtain(ScriptMethodKey.class);
		key.name = method.getName();
		key.parameterTypes = method.getParameterTypes();
		ScriptMethod scriptMethod = methods.get(key);
		SynchronizedPools.free(key);
		return scriptMethod;
	}

	public static class ScriptMethodKey {
		String name;
		Class<?>[] parameterTypes;

		public ScriptMethodKey() {
		}

		public ScriptMethodKey(ScriptMethod scriptMethod) {
			this.name = scriptMethod.name;
			this.parameterTypes = scriptMethod.parameterTypes;
		}

		@Override
		public int hashCode() {
			return 31 + name.hashCode() + Arrays.hashCode(parameterTypes);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (ScriptMethodKey.class != obj.getClass()) {
				return false;
			}
			ScriptMethodKey other = (ScriptMethodKey) obj;
			return name.equals(other.name) && Arrays.equals(parameterTypes, other.parameterTypes);
		}
	}
}