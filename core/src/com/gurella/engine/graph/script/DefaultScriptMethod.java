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
	public static final ScriptMethodDescriptor<ScriptComponent> onInput = get("onInput"); 
	public static final ScriptMethodDescriptor<ScriptComponent> onThink = get("onThink"); 
	public static final ScriptMethodDescriptor<ScriptComponent> onPreRender = get("onPreRender");
	public static final ScriptMethodDescriptor<ScriptComponent> onRender = get("onRender"); 
	public static final ScriptMethodDescriptor<ScriptComponent> onDebugRender = get("onDebugRender"); 
	public static final ScriptMethodDescriptor<ScriptComponent> onAfterRender = get("onAfterRender"); 
	public static final ScriptMethodDescriptor<ScriptComponent> onCleanup = get("onCleanup"); 
	
	//NODE INPUT
	public static final ScriptMethodDescriptor<ScriptComponent> onTouchDown = get("onTouchDown", IntersectionTouchEvent.class);
	public static final ScriptMethodDescriptor<ScriptComponent> onTouchUp = get("onTouchUp", IntersectionTouchEvent.class);
	public static final ScriptMethodDescriptor<ScriptComponent> onTap = get("onTap", IntersectionTouchEvent.class, Integer.TYPE);
	public static final ScriptMethodDescriptor<ScriptComponent> onDragOverStart = get("onDragOverStart", IntersectionTouchEvent.class);
	public static final ScriptMethodDescriptor<ScriptComponent> onDragOverMove = get("onDragOverMove", IntersectionTouchEvent.class);
	public static final ScriptMethodDescriptor<ScriptComponent> onDragOverEnd = get("onDragOverEnd", TouchEvent.class);
	public static final ScriptMethodDescriptor<ScriptComponent> onDragStart = get("onDragStart", IntersectionTouchEvent.class);
	public static final ScriptMethodDescriptor<ScriptComponent> onDragMove = get("onDragMove", TouchEvent.class);
	public static final ScriptMethodDescriptor<ScriptComponent> onDragEnd = get("onDragEnd", TouchEvent.class);
	public static final ScriptMethodDescriptor<ScriptComponent> onLongPress = get("onLongPress", IntersectionTouchEvent.class);
	public static final ScriptMethodDescriptor<ScriptComponent> onDoubleTouch = get("onDoubleTouch", IntersectionTouchEvent.class);
	public static final ScriptMethodDescriptor<ScriptComponent> onScrolled = get("onScrolled", Integer.TYPE, Integer.TYPE, Integer.TYPE, Vector3.class);
	public static final ScriptMethodDescriptor<ScriptComponent> onMouseOverStart = get("onMouseOverStart", Integer.TYPE, Integer.TYPE, Vector3.class);
	public static final ScriptMethodDescriptor<ScriptComponent> onMouseOverMove = get("onMouseOverMove", Integer.TYPE, Integer.TYPE, Vector3.class);
	public static final ScriptMethodDescriptor<ScriptComponent> onMouseOverEnd = get("onMouseOverEnd", Integer.TYPE, Integer.TYPE);
	
	//NODE DRAG AND DROP
	public static final ScriptMethodDescriptor<ScriptComponent> getDragSource = get("getDragSource", DragStartCondition.class);
	public static final ScriptMethodDescriptor<ScriptComponent> getDropTarget = get("getDropTarget", Array.class);
	
	//RESOLVED GLOBAL INPUT
	public static final ScriptMethodDescriptor<ScriptComponent> onTouchDownResolved = get("onTouchDown", RenderableComponent.class, IntersectionTouchEvent.class);
	public static final ScriptMethodDescriptor<ScriptComponent> onTouchUpResolved = get("onTouchUp", RenderableComponent.class, IntersectionTouchEvent.class);
	public static final ScriptMethodDescriptor<ScriptComponent> onTapResolved = get("onTap", RenderableComponent.class, IntersectionTouchEvent.class, Integer.TYPE);
	public static final ScriptMethodDescriptor<ScriptComponent> onDragOverStartResolved = get("onDragOverStart", RenderableComponent.class, IntersectionTouchEvent.class);
	public static final ScriptMethodDescriptor<ScriptComponent> onDragOverMoveResolved = get("onDragOverMove", RenderableComponent.class, IntersectionTouchEvent.class);
	public static final ScriptMethodDescriptor<ScriptComponent> onDragOverEndResolved = get("onDragOverEnd", RenderableComponent.class, TouchEvent.class);
	public static final ScriptMethodDescriptor<ScriptComponent> onDragStartResolved = get("onDragStart", RenderableComponent.class, IntersectionTouchEvent.class);
	public static final ScriptMethodDescriptor<ScriptComponent> onDragMoveResolved = get("onDragMove", RenderableComponent.class, TouchEvent.class);
	public static final ScriptMethodDescriptor<ScriptComponent> onDragEndResolved = get("onDragEnd", RenderableComponent.class, TouchEvent.class);
	public static final ScriptMethodDescriptor<ScriptComponent> onLongPressResolved = get("onLongPress", RenderableComponent.class, IntersectionTouchEvent.class);
	public static final ScriptMethodDescriptor<ScriptComponent> onDoubleTouchResolved = get("onDoubleTouch", RenderableComponent.class, IntersectionTouchEvent.class);
	public static final ScriptMethodDescriptor<ScriptComponent> onScrolledResolved = get("onScrolled", RenderableComponent.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Vector3.class);
	public static final ScriptMethodDescriptor<ScriptComponent> onMouseOverStartResolved = get("onMouseOverStart", RenderableComponent.class, Integer.TYPE, Integer.TYPE, Vector3.class);
	public static final ScriptMethodDescriptor<ScriptComponent> onMouseOverMoveResolved = get("onMouseOverMove", RenderableComponent.class, Integer.TYPE, Integer.TYPE, Vector3.class);
	public static final ScriptMethodDescriptor<ScriptComponent> onMouseOverEndResolved = get("onMouseOverEnd", RenderableComponent.class, Integer.TYPE, Integer.TYPE);
	
	//GLOBAL INPUT
	public static final ScriptMethodDescriptor<ScriptComponent> keyDown = get("keyDown", Integer.TYPE);
	public static final ScriptMethodDescriptor<ScriptComponent> keyUp = get("keyUp", Integer.TYPE);
	public static final ScriptMethodDescriptor<ScriptComponent> keyTyped = get("keyTyped", Character.TYPE);
	public static final ScriptMethodDescriptor<ScriptComponent> touchDown = get("touchDown", TouchEvent.class);
	public static final ScriptMethodDescriptor<ScriptComponent> doubleTouchDown = get("doubleTouchDown", TouchEvent.class);
	public static final ScriptMethodDescriptor<ScriptComponent> touchUp = get("touchUp", TouchEvent.class);
	public static final ScriptMethodDescriptor<ScriptComponent> touchDragged = get("touchDragged", TouchEvent.class);
	public static final ScriptMethodDescriptor<ScriptComponent> mouseMoved = get("mouseMoved", Integer.TYPE, Integer.TYPE);
	public static final ScriptMethodDescriptor<ScriptComponent> scrolled = get("scrolled", Integer.TYPE, Integer.TYPE, Integer.TYPE);
	public static final ScriptMethodDescriptor<ScriptComponent> tap = get("tap", TouchEvent.class, Integer.TYPE);
	public static final ScriptMethodDescriptor<ScriptComponent> longPress = get("longPress", TouchEvent.class);
	
	//BULLET PHYSICS
	public static final ScriptMethodDescriptor<ScriptComponent> onCollisionEnter = get("onCollisionEnter", Collision.class); 
	public static final ScriptMethodDescriptor<ScriptComponent> onCollisionStay = get("onCollisionStay", Collision.class); 
	public static final ScriptMethodDescriptor<ScriptComponent> onCollisionExit = get("onCollisionExit", BulletPhysicsRigidBodyComponent.class);
	public static final ScriptMethodDescriptor<ScriptComponent> onPhysicsSimulationStart = get("onPhysicsSimulationStart", btDynamicsWorld.class);
	public static final ScriptMethodDescriptor<ScriptComponent> onPhysicsSimulationStep = get("onPhysicsSimulationStep", btDynamicsWorld.class, Float.TYPE);
	public static final ScriptMethodDescriptor<ScriptComponent> onPhysicsSimulationEnd = get("onPhysicsSimulationEnd", btDynamicsWorld.class);
	
	//GLOBAL BULLET COLLISIONS
	public static final ScriptMethodDescriptor<ScriptComponent> onCollisionEnterGlobal = get("onCollisionEnter", CollisionPair.class);
	public static final ScriptMethodDescriptor<ScriptComponent> onCollisionStayGlobal = get("onCollisionStay", CollisionPair.class);
	public static final ScriptMethodDescriptor<ScriptComponent> onCollisionExitGlobal = get("onCollisionExit", BulletPhysicsRigidBodyComponent.class, BulletPhysicsRigidBodyComponent.class);
	
	//GRAPH
	public static final ScriptMethodDescriptor<ScriptComponent> componentAdded = get("componentAdded", SceneNodeComponent.class);
	public static final ScriptMethodDescriptor<ScriptComponent> componentRemoved = get("componentRemoved", SceneNodeComponent.class);
	public static final ScriptMethodDescriptor<ScriptComponent> componentActivated = get("componentActivated", SceneNodeComponent.class);
	public static final ScriptMethodDescriptor<ScriptComponent> componentDeactivated = get("componentDeactivated", SceneNodeComponent.class);
	
	public static final ScriptMethodDescriptor<ScriptComponent> nodeComponentAdded = get("nodeComponentAdded", SceneNodeComponent.class);
	public static final ScriptMethodDescriptor<ScriptComponent> nodeComponentRemoved = get("nodeComponentRemoved", SceneNodeComponent.class);
	public static final ScriptMethodDescriptor<ScriptComponent> nodeComponentActivated = get("nodeComponentActivated", SceneNodeComponent.class);
	public static final ScriptMethodDescriptor<ScriptComponent> nodeComponentDeactivated = get("nodeComponentDeactivated", SceneNodeComponent.class);
	public static final ScriptMethodDescriptor<ScriptComponent> nodeParentChanged = get("nodeParentChanged", SceneNode.class);
	public static final ScriptMethodDescriptor<ScriptComponent> nodeChildAdded = get("nodeChildAdded", SceneNode.class);
	public static final ScriptMethodDescriptor<ScriptComponent> nodeChildRemoved = get("nodeChildRemoved", SceneNode.class);
	
	//SCENE
	public static final ScriptMethodDescriptor<ScriptComponent> onSceneStart = get("onSceneStart");
	public static final ScriptMethodDescriptor<ScriptComponent> onSceneStop = get("onSceneStop");
	
	//APPLICATION
	public static final ScriptMethodDescriptor<ScriptComponent> onPause = get("onPause");
	public static final ScriptMethodDescriptor<ScriptComponent> onResume = get("onResume");
	
	//APPLICATION
	public static final ScriptMethodDescriptor<ScriptComponent> onMessage = get("onMessage", Object.class, Object.class, Object.class);
	
	//@formatter:on
	
	private static ScriptMethodDescriptor<ScriptComponent> get(String name, Class<?>... parameterTypes ) {
		return ScriptMethodDescriptor.get(ScriptComponent.class, name, parameterTypes);
	}
}