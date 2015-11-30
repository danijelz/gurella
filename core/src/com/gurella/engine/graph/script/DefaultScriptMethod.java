package com.gurella.engine.graph.script;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.graph.SceneNode;
import com.gurella.engine.graph.SceneNodeComponent;
import com.gurella.engine.graph.bullet.BulletPhysicsRigidBodyComponent;
import com.gurella.engine.graph.bullet.Collision;
import com.gurella.engine.graph.bullet.CollisionPair;
import com.gurella.engine.graph.input.DragStartCondition;
import com.gurella.engine.graph.input.IntersectionTouchEvent;
import com.gurella.engine.graph.input.TouchEvent;
import com.gurella.engine.graph.renderable.RenderableComponent;

public final class DefaultScriptMethod {
	private DefaultScriptMethod() {
	}
	
	//@formatter:off
	
	//UPDATE
	public static final ScriptMethodDescriptor onInput = get("onInput"); 
	public static final ScriptMethodDescriptor onThink = get("onThink"); 
	public static final ScriptMethodDescriptor onPreRender = get("onPreRender");
	public static final ScriptMethodDescriptor onRender = get("onRender"); 
	public static final ScriptMethodDescriptor onDebugRender = get("onDebugRender"); 
	public static final ScriptMethodDescriptor onAfterRender = get("onAfterRender"); 
	public static final ScriptMethodDescriptor onCleanup = get("onCleanup"); 
	
	//NODE INPUT
	public static final ScriptMethodDescriptor onTouchDown = get("onTouchDown", IntersectionTouchEvent.class);
	public static final ScriptMethodDescriptor onTouchUp = get("onTouchUp", IntersectionTouchEvent.class);
	public static final ScriptMethodDescriptor onTap = get("onTap", IntersectionTouchEvent.class, Integer.TYPE);
	public static final ScriptMethodDescriptor onDragOverStart = get("onDragOverStart", IntersectionTouchEvent.class);
	public static final ScriptMethodDescriptor onDragOverMove = get("onDragOverMove", IntersectionTouchEvent.class);
	public static final ScriptMethodDescriptor onDragOverEnd = get("onDragOverEnd", TouchEvent.class);
	public static final ScriptMethodDescriptor onDragStart = get("onDragStart", IntersectionTouchEvent.class);
	public static final ScriptMethodDescriptor onDragMove = get("onDragMove", TouchEvent.class);
	public static final ScriptMethodDescriptor onDragEnd = get("onDragEnd", TouchEvent.class);
	public static final ScriptMethodDescriptor onLongPress = get("onLongPress", IntersectionTouchEvent.class);
	public static final ScriptMethodDescriptor onDoubleTouch = get("onDoubleTouch", IntersectionTouchEvent.class);
	public static final ScriptMethodDescriptor onScrolled = get("onScrolled", Integer.TYPE, Integer.TYPE, Integer.TYPE, Vector3.class);
	public static final ScriptMethodDescriptor onMouseOverStart = get("onMouseOverStart", Integer.TYPE, Integer.TYPE, Vector3.class);
	public static final ScriptMethodDescriptor onMouseOverMove = get("onMouseOverMove", Integer.TYPE, Integer.TYPE, Vector3.class);
	public static final ScriptMethodDescriptor onMouseOverEnd = get("onMouseOverEnd", Integer.TYPE, Integer.TYPE);
	
	//NODE DRAG AND DROP
	public static final ScriptMethodDescriptor getDragSource = get("getDragSource", DragStartCondition.class);
	public static final ScriptMethodDescriptor getDropTarget = get("getDropTarget", Array.class);
	
	//RESOLVED GLOBAL INPUT
	public static final ScriptMethodDescriptor onTouchDownResolved = get("onTouchDown", RenderableComponent.class, IntersectionTouchEvent.class);
	public static final ScriptMethodDescriptor onTouchUpResolved = get("onTouchUp", RenderableComponent.class, IntersectionTouchEvent.class);
	public static final ScriptMethodDescriptor onTapResolved = get("onTap", RenderableComponent.class, IntersectionTouchEvent.class, Integer.TYPE);
	public static final ScriptMethodDescriptor onDragOverStartResolved = get("onDragOverStart", RenderableComponent.class, IntersectionTouchEvent.class);
	public static final ScriptMethodDescriptor onDragOverMoveResolved = get("onDragOverMove", RenderableComponent.class, IntersectionTouchEvent.class);
	public static final ScriptMethodDescriptor onDragOverEndResolved = get("onDragOverEnd", RenderableComponent.class, TouchEvent.class);
	public static final ScriptMethodDescriptor onDragStartResolved = get("onDragStart", RenderableComponent.class, IntersectionTouchEvent.class);
	public static final ScriptMethodDescriptor onDragMoveResolved = get("onDragMove", RenderableComponent.class, TouchEvent.class);
	public static final ScriptMethodDescriptor onDragEndResolved = get("onDragEnd", RenderableComponent.class, TouchEvent.class);
	public static final ScriptMethodDescriptor onLongPressResolved = get("onLongPress", RenderableComponent.class, IntersectionTouchEvent.class);
	public static final ScriptMethodDescriptor onDoubleTouchResolved = get("onDoubleTouch", RenderableComponent.class, IntersectionTouchEvent.class);
	public static final ScriptMethodDescriptor onScrolledResolved = get("onScrolled", RenderableComponent.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Vector3.class);
	public static final ScriptMethodDescriptor onMouseOverStartResolved = get("onMouseOverStart", RenderableComponent.class, Integer.TYPE, Integer.TYPE, Vector3.class);
	public static final ScriptMethodDescriptor onMouseOverMoveResolved = get("onMouseOverMove", RenderableComponent.class, Integer.TYPE, Integer.TYPE, Vector3.class);
	public static final ScriptMethodDescriptor onMouseOverEndResolved = get("onMouseOverEnd", RenderableComponent.class, Integer.TYPE, Integer.TYPE);
	
	//GLOBAL INPUT
	public static final ScriptMethodDescriptor keyDown = get("keyDown", Integer.TYPE);
	public static final ScriptMethodDescriptor keyUp = get("keyUp", Integer.TYPE);
	public static final ScriptMethodDescriptor keyTyped = get("keyTyped", Character.TYPE);
	public static final ScriptMethodDescriptor touchDown = get("touchDown", TouchEvent.class);
	public static final ScriptMethodDescriptor doubleTouchDown = get("doubleTouchDown", TouchEvent.class);
	public static final ScriptMethodDescriptor touchUp = get("touchUp", TouchEvent.class);
	public static final ScriptMethodDescriptor touchDragged = get("touchDragged", TouchEvent.class);
	public static final ScriptMethodDescriptor mouseMoved = get("mouseMoved", Integer.TYPE, Integer.TYPE);
	public static final ScriptMethodDescriptor scrolled = get("scrolled", Integer.TYPE, Integer.TYPE, Integer.TYPE);
	public static final ScriptMethodDescriptor tap = get("tap", TouchEvent.class, Integer.TYPE);
	public static final ScriptMethodDescriptor longPress = get("longPress", TouchEvent.class);
	
	//BULLET PHYSICS
	public static final ScriptMethodDescriptor onCollisionEnter = get("onCollisionEnter", Collision.class); 
	public static final ScriptMethodDescriptor onCollisionStay = get("onCollisionStay", Collision.class); 
	public static final ScriptMethodDescriptor onCollisionExit = get("onCollisionExit", BulletPhysicsRigidBodyComponent.class);
	public static final ScriptMethodDescriptor onPhysicsSimulationStart = get("onPhysicsSimulationStart", btDynamicsWorld.class);
	public static final ScriptMethodDescriptor onPhysicsSimulationStep = get("onPhysicsSimulationStep", btDynamicsWorld.class, Float.TYPE);
	public static final ScriptMethodDescriptor onPhysicsSimulationEnd = get("onPhysicsSimulationEnd", btDynamicsWorld.class);
	
	//GLOBAL BULLET COLLISIONS
	public static final ScriptMethodDescriptor onCollisionEnterGlobal = get("onCollisionEnterGlobal", CollisionPair.class);
	public static final ScriptMethodDescriptor onCollisionStayGlobal = get("onCollisionStayGlobal", CollisionPair.class);
	public static final ScriptMethodDescriptor onCollisionExitGlobal = get("onCollisionExitGlobal", BulletPhysicsRigidBodyComponent.class, BulletPhysicsRigidBodyComponent.class);
	
	//GRAPH
	public static final ScriptMethodDescriptor componentAdded = get("componentAdded", SceneNodeComponent.class);
	public static final ScriptMethodDescriptor componentRemoved = get("componentRemoved", SceneNodeComponent.class);
	public static final ScriptMethodDescriptor componentActivated = get("componentActivated", SceneNodeComponent.class);
	public static final ScriptMethodDescriptor componentDeactivated = get("componentDeactivated", SceneNodeComponent.class);
	
	public static final ScriptMethodDescriptor nodeComponentAdded = get("nodeComponentAdded", SceneNodeComponent.class);
	public static final ScriptMethodDescriptor nodeComponentRemoved = get("nodeComponentRemoved", SceneNodeComponent.class);
	public static final ScriptMethodDescriptor nodeComponentActivated = get("nodeComponentActivated", SceneNodeComponent.class);
	public static final ScriptMethodDescriptor nodeComponentDeactivated = get("nodeComponentDeactivated", SceneNodeComponent.class);
	public static final ScriptMethodDescriptor nodeParentChanged = get("nodeParentChanged", SceneNode.class);
	public static final ScriptMethodDescriptor nodeChildAdded = get("nodeChildAdded", SceneNode.class);
	public static final ScriptMethodDescriptor nodeChildRemoved = get("nodeChildRemoved", SceneNode.class);
	
	//SCENE
	public static final ScriptMethodDescriptor onSceneStart = get("onSceneStart");
	public static final ScriptMethodDescriptor onSceneStop = get("onSceneStop");
	
	//APPLICATION
	public static final ScriptMethodDescriptor onPause = get("onPause");
	public static final ScriptMethodDescriptor onResume = get("onResume");
	
	//APPLICATION
	public static final ScriptMethodDescriptor onMessage = get("onMessage", Object.class, Object.class, Object.class);
	
	//@formatter:on
	
	private static ScriptMethodDescriptor get(String name, Class<?>... parameterTypes ) {
		return null;//TODO new ScriptMethod(ScriptComponent.class, name, parameterTypes);
	}

//	private static final ObjectMap<ScriptMethodKey, DefaultScriptMethod> methods = new ObjectMap<DefaultScriptMethod.ScriptMethodKey, DefaultScriptMethod>();
//	static {
//		DefaultScriptMethod[] scriptMethods = DefaultScriptMethod.values();
//		for (int i = 0; i < scriptMethods.length; i++) {
//			DefaultScriptMethod scriptMethod = scriptMethods[i];
//			methods.put(new ScriptMethodKey(scriptMethod), scriptMethod);
//		}
//	}
//
//	final String methodName;
//	final Class<?>[] methodParameterTypes;
//
//	DefaultScriptMethod(String methodName, Class<?>... parameterTypes) {
//		this.methodName = methodName;
//		this.methodParameterTypes = parameterTypes;
//	}
//
//	DefaultScriptMethod(Class<?>... parameterTypes) {
//		this.methodName = name();
//		this.methodParameterTypes = parameterTypes;
//	}
//
//	static DefaultScriptMethod valueOf(Method method) {
//		ScriptMethodKey key = SynchronizedPools.obtain(ScriptMethodKey.class);
//		key.name = method.getName();
//		key.parameterTypes = method.getParameterTypes();
//		DefaultScriptMethod scriptMethod = methods.get(key);
//		SynchronizedPools.free(key);
//		return scriptMethod;
//	}
//
//	@Override
//	public Class<?> getMethodDeclaringClass() {
//		return ScriptComponent.class;
//	}
//
//	@Override
//	public String getMethodName() {
//		return methodName;
//	}
//
//	@Override
//	public Class<?>[] getMethodParameterTypes() {
//		return methodParameterTypes;
//	}
//
//	public static class ScriptMethodKey {
//		String name;
//		Class<?>[] parameterTypes;
//
//		public ScriptMethodKey() {
//		}
//
//		public ScriptMethodKey(DefaultScriptMethod scriptMethod) {
//			this.name = scriptMethod.methodName;
//			this.parameterTypes = scriptMethod.methodParameterTypes;
//		}
//
//		@Override
//		public int hashCode() {
//			return 31 + name.hashCode() + Arrays.hashCode(parameterTypes);
//		}
//
//		@Override
//		public boolean equals(Object obj) {
//			if (this == obj) {
//				return true;
//			}
//			if (obj == null) {
//				return false;
//			}
//			if (ScriptMethodKey.class != obj.getClass()) {
//				return false;
//			}
//			ScriptMethodKey other = (ScriptMethodKey) obj;
//			return name.equals(other.name) && Arrays.equals(parameterTypes, other.parameterTypes);
//		}
//	}
}