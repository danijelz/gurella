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
	public static final ScriptMethod onInput = get("onInput"); 
	public static final ScriptMethod onThink = get("onThink"); 
	public static final ScriptMethod onPreRender = get("onPreRender");
	public static final ScriptMethod onRender = get("onRender"); 
	public static final ScriptMethod onDebugRender = get("onDebugRender"); 
	public static final ScriptMethod onAfterRender = get("onAfterRender"); 
	public static final ScriptMethod onCleanup = get("onCleanup"); 
	
	//NODE INPUT
	public static final ScriptMethod onTouchDown = get("onTouchDown", IntersectionTouchEvent.class);
	public static final ScriptMethod onTouchUp = get("onTouchUp", IntersectionTouchEvent.class);
	public static final ScriptMethod onTap = get("onTap", IntersectionTouchEvent.class, Integer.TYPE);
	public static final ScriptMethod onDragOverStart = get("onDragOverStart", IntersectionTouchEvent.class);
	public static final ScriptMethod onDragOverMove = get("onDragOverMove", IntersectionTouchEvent.class);
	public static final ScriptMethod onDragOverEnd = get("onDragOverEnd", TouchEvent.class);
	public static final ScriptMethod onDragStart = get("onDragStart", IntersectionTouchEvent.class);
	public static final ScriptMethod onDragMove = get("onDragMove", TouchEvent.class);
	public static final ScriptMethod onDragEnd = get("onDragEnd", TouchEvent.class);
	public static final ScriptMethod onLongPress = get("onLongPress", IntersectionTouchEvent.class);
	public static final ScriptMethod onDoubleTouch = get("onDoubleTouch", IntersectionTouchEvent.class);
	public static final ScriptMethod onScrolled = get("onScrolled", Integer.TYPE, Integer.TYPE, Integer.TYPE, Vector3.class);
	public static final ScriptMethod onMouseOverStart = get("onMouseOverStart", Integer.TYPE, Integer.TYPE, Vector3.class);
	public static final ScriptMethod onMouseOverMove = get("onMouseOverMove", Integer.TYPE, Integer.TYPE, Vector3.class);
	public static final ScriptMethod onMouseOverEnd = get("onMouseOverEnd", Integer.TYPE, Integer.TYPE);
	
	//NODE DRAG AND DROP
	public static final ScriptMethod getDragSource = get("getDragSource", DragStartCondition.class);
	public static final ScriptMethod getDropTarget = get("getDropTarget", Array.class);
	
	//RESOLVED GLOBAL INPUT
	public static final ScriptMethod onTouchDownResolved = get("onTouchDown", RenderableComponent.class, IntersectionTouchEvent.class);
	public static final ScriptMethod onTouchUpResolved = get("onTouchUp", RenderableComponent.class, IntersectionTouchEvent.class);
	public static final ScriptMethod onTapResolved = get("onTap", RenderableComponent.class, IntersectionTouchEvent.class, Integer.TYPE);
	public static final ScriptMethod onDragOverStartResolved = get("onDragOverStart", RenderableComponent.class, IntersectionTouchEvent.class);
	public static final ScriptMethod onDragOverMoveResolved = get("onDragOverMove", RenderableComponent.class, IntersectionTouchEvent.class);
	public static final ScriptMethod onDragOverEndResolved = get("onDragOverEnd", RenderableComponent.class, TouchEvent.class);
	public static final ScriptMethod onDragStartResolved = get("onDragStart", RenderableComponent.class, IntersectionTouchEvent.class);
	public static final ScriptMethod onDragMoveResolved = get("onDragMove", RenderableComponent.class, TouchEvent.class);
	public static final ScriptMethod onDragEndResolved = get("onDragEnd", RenderableComponent.class, TouchEvent.class);
	public static final ScriptMethod onLongPressResolved = get("onLongPress", RenderableComponent.class, IntersectionTouchEvent.class);
	public static final ScriptMethod onDoubleTouchResolved = get("onDoubleTouch", RenderableComponent.class, IntersectionTouchEvent.class);
	public static final ScriptMethod onScrolledResolved = get("onScrolled", RenderableComponent.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Vector3.class);
	public static final ScriptMethod onMouseOverStartResolved = get("onMouseOverStart", RenderableComponent.class, Integer.TYPE, Integer.TYPE, Vector3.class);
	public static final ScriptMethod onMouseOverMoveResolved = get("onMouseOverMove", RenderableComponent.class, Integer.TYPE, Integer.TYPE, Vector3.class);
	public static final ScriptMethod onMouseOverEndResolved = get("onMouseOverEnd", RenderableComponent.class, Integer.TYPE, Integer.TYPE);
	
	//GLOBAL INPUT
	public static final ScriptMethod keyDown = get("keyDown", Integer.TYPE);
	public static final ScriptMethod keyUp = get("keyUp", Integer.TYPE);
	public static final ScriptMethod keyTyped = get("keyTyped", Character.TYPE);
	public static final ScriptMethod touchDown = get("touchDown", TouchEvent.class);
	public static final ScriptMethod doubleTouchDown = get("doubleTouchDown", TouchEvent.class);
	public static final ScriptMethod touchUp = get("touchUp", TouchEvent.class);
	public static final ScriptMethod touchDragged = get("touchDragged", TouchEvent.class);
	public static final ScriptMethod mouseMoved = get("mouseMoved", Integer.TYPE, Integer.TYPE);
	public static final ScriptMethod scrolled = get("scrolled", Integer.TYPE, Integer.TYPE, Integer.TYPE);
	public static final ScriptMethod tap = get("tap", TouchEvent.class, Integer.TYPE);
	public static final ScriptMethod longPress = get("longPress", TouchEvent.class);
	
	//BULLET PHYSICS
	public static final ScriptMethod onCollisionEnter = get("onCollisionEnter", Collision.class); 
	public static final ScriptMethod onCollisionStay = get("onCollisionStay", Collision.class); 
	public static final ScriptMethod onCollisionExit = get("onCollisionExit", BulletPhysicsRigidBodyComponent.class);
	public static final ScriptMethod onPhysicsSimulationStart = get("onPhysicsSimulationStart", btDynamicsWorld.class);
	public static final ScriptMethod onPhysicsSimulationStep = get("onPhysicsSimulationStep", btDynamicsWorld.class, Float.TYPE);
	public static final ScriptMethod onPhysicsSimulationEnd = get("onPhysicsSimulationEnd", btDynamicsWorld.class);
	
	//GLOBAL BULLET COLLISIONS
	public static final ScriptMethod onCollisionEnterGlobal = get("onCollisionEnterGlobal", CollisionPair.class);
	public static final ScriptMethod onCollisionStayGlobal = get("onCollisionStayGlobal", CollisionPair.class);
	public static final ScriptMethod onCollisionExitGlobal = get("onCollisionExitGlobal", BulletPhysicsRigidBodyComponent.class, BulletPhysicsRigidBodyComponent.class);
	
	//GRAPH
	public static final ScriptMethod componentAdded = get("componentAdded", SceneNodeComponent.class);
	public static final ScriptMethod componentRemoved = get("componentRemoved", SceneNodeComponent.class);
	public static final ScriptMethod componentActivated = get("componentActivated", SceneNodeComponent.class);
	public static final ScriptMethod componentDeactivated = get("componentDeactivated", SceneNodeComponent.class);
	
	public static final ScriptMethod nodeComponentAdded = get("nodeComponentAdded", SceneNodeComponent.class);
	public static final ScriptMethod nodeComponentRemoved = get("nodeComponentRemoved", SceneNodeComponent.class);
	public static final ScriptMethod nodeComponentActivated = get("nodeComponentActivated", SceneNodeComponent.class);
	public static final ScriptMethod nodeComponentDeactivated = get("nodeComponentDeactivated", SceneNodeComponent.class);
	public static final ScriptMethod nodeParentChanged = get("nodeParentChanged", SceneNode.class);
	public static final ScriptMethod nodeChildAdded = get("nodeChildAdded", SceneNode.class);
	public static final ScriptMethod nodeChildRemoved = get("nodeChildRemoved", SceneNode.class);
	
	//SCENE
	public static final ScriptMethod onSceneStart = get("onSceneStart");
	public static final ScriptMethod onSceneStop = get("onSceneStop");
	
	//APPLICATION
	public static final ScriptMethod onPause = get("onPause");
	public static final ScriptMethod onResume = get("onResume");
	
	//APPLICATION
	public static final ScriptMethod onMessage = get("onMessage", Object.class, Object.class, Object.class);
	
	//@formatter:on
	
	private static ScriptMethod get(String name, Class<?>... parameterTypes ) {
		return new ScriptMethod(ScriptComponent.class, name, parameterTypes);
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