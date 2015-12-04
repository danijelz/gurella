package com.gurella.engine.graph.behaviour;

import com.gurella.engine.graph.event.EventCallbackInstance;

public final class BehaviourEventCallbacks {
	private BehaviourEventCallbacks() {
	}

	// UPDATE
	public static final EventCallbackInstance<BehaviourComponent> onInput = get("onInput");
	public static final EventCallbackInstance<BehaviourComponent> onThink = get("onThink");
	public static final EventCallbackInstance<BehaviourComponent> onPreRender = get("onPreRender");
	public static final EventCallbackInstance<BehaviourComponent> onRender = get("onRender");
	public static final EventCallbackInstance<BehaviourComponent> onDebugRender = get("onDebugRender");
	public static final EventCallbackInstance<BehaviourComponent> onAfterRender = get("onAfterRender");
	public static final EventCallbackInstance<BehaviourComponent> onCleanup = get("onCleanup");

	// NODE INPUT
	public static final EventCallbackInstance<BehaviourComponent> onTouchDown = get("onTouchDown");
	public static final EventCallbackInstance<BehaviourComponent> onTouchUp = get("onTouchUp");
	public static final EventCallbackInstance<BehaviourComponent> onTap = get("onTap");
	public static final EventCallbackInstance<BehaviourComponent> onDragOverStart = get("onDragOverStart");
	public static final EventCallbackInstance<BehaviourComponent> onDragOverMove = get("onDragOverMove");
	public static final EventCallbackInstance<BehaviourComponent> onDragOverEnd = get("onDragOverEnd");
	public static final EventCallbackInstance<BehaviourComponent> onDragStart = get("onDragStart");
	public static final EventCallbackInstance<BehaviourComponent> onDragMove = get("onDragMove");
	public static final EventCallbackInstance<BehaviourComponent> onDragEnd = get("onDragEnd");
	public static final EventCallbackInstance<BehaviourComponent> onLongPress = get("onLongPress");
	public static final EventCallbackInstance<BehaviourComponent> onDoubleTouch = get("onDoubleTouch");
	public static final EventCallbackInstance<BehaviourComponent> onScrolled = get("onScrolled");
	public static final EventCallbackInstance<BehaviourComponent> onMouseOverStart = get("onMouseOverStart");
	public static final EventCallbackInstance<BehaviourComponent> onMouseOverMove = get("onMouseOverMove");
	public static final EventCallbackInstance<BehaviourComponent> onMouseOverEnd = get("onMouseOverEnd");

	// NODE DRAG AND DROP
	public static final EventCallbackInstance<BehaviourComponent> getDragSource = get("getDragSource");
	public static final EventCallbackInstance<BehaviourComponent> getDropTarget = get("getDropTarget");

	// RESOLVED GLOBAL INPUT
	public static final EventCallbackInstance<BehaviourComponent> onTouchDownGlobal = get("onTouchDownGlobal");
	public static final EventCallbackInstance<BehaviourComponent> onTouchUpGlobal = get("onTouchUpGlobal");
	public static final EventCallbackInstance<BehaviourComponent> onTapGlobal = get("onTapGlobal");
	public static final EventCallbackInstance<BehaviourComponent> onDragOverStartGlobal = get("onDragOverStartGlobal");
	public static final EventCallbackInstance<BehaviourComponent> onDragOverMoveGlobal = get("onDragOverMoveGlobal");
	public static final EventCallbackInstance<BehaviourComponent> onDragOverEndGlobal = get("onDragOverEndGlobal");
	public static final EventCallbackInstance<BehaviourComponent> onDragStartGlobal = get("onDragStartGlobal");
	public static final EventCallbackInstance<BehaviourComponent> onDragMoveGlobal = get("onDragMoveGlobal");
	public static final EventCallbackInstance<BehaviourComponent> onDragEndGlobal = get("onDragEndGlobal");
	public static final EventCallbackInstance<BehaviourComponent> onLongPressGlobal = get("onLongPressGlobal");
	public static final EventCallbackInstance<BehaviourComponent> onDoubleTouchGlobal = get("onDoubleTouchGlobal");
	public static final EventCallbackInstance<BehaviourComponent> onScrolledGlobal = get("onScrolledGlobal");
	public static final EventCallbackInstance<BehaviourComponent> onMouseOverStartGlobal = get("onMouseOverStartGlobal");
	public static final EventCallbackInstance<BehaviourComponent> onMouseOverMoveGlobal = get("onMouseOverMoveGlobal");
	public static final EventCallbackInstance<BehaviourComponent> onMouseOverEndGlobal = get("onMouseOverEndGlobal");

	// GLOBAL INPUT
	public static final EventCallbackInstance<BehaviourComponent> keyDown = get("keyDown");
	public static final EventCallbackInstance<BehaviourComponent> keyUp = get("keyUp");
	public static final EventCallbackInstance<BehaviourComponent> keyTyped = get("keyTyped");
	public static final EventCallbackInstance<BehaviourComponent> touchDown = get("touchDown");
	public static final EventCallbackInstance<BehaviourComponent> doubleTouchDown = get("doubleTouchDown");
	public static final EventCallbackInstance<BehaviourComponent> touchUp = get("touchUp");
	public static final EventCallbackInstance<BehaviourComponent> touchDragged = get("touchDragged");
	public static final EventCallbackInstance<BehaviourComponent> mouseMoved = get("mouseMoved");
	public static final EventCallbackInstance<BehaviourComponent> scrolled = get("scrolled");
	public static final EventCallbackInstance<BehaviourComponent> tap = get("tap");
	public static final EventCallbackInstance<BehaviourComponent> longPress = get("longPress");

	// BULLET PHYSICS
	public static final EventCallbackInstance<BehaviourComponent> onCollisionEnter = get("onCollisionEnter");
	public static final EventCallbackInstance<BehaviourComponent> onCollisionStay = get("onCollisionStay");
	public static final EventCallbackInstance<BehaviourComponent> onCollisionExit = get("onCollisionExit");
	public static final EventCallbackInstance<BehaviourComponent> onPhysicsSimulationStart = get(
			"onPhysicsSimulationStart");
	public static final EventCallbackInstance<BehaviourComponent> onPhysicsSimulationStep = get("onPhysicsSimulationStep");
	public static final EventCallbackInstance<BehaviourComponent> onPhysicsSimulationEnd = get("onPhysicsSimulationEnd");

	// GLOBAL BULLET COLLISIONS
	public static final EventCallbackInstance<BehaviourComponent> onCollisionEnterGlobal = get("onCollisionEnterGlobal");
	public static final EventCallbackInstance<BehaviourComponent> onCollisionStayGlobal = get("onCollisionStayGlobal");
	public static final EventCallbackInstance<BehaviourComponent> onCollisionExitGlobal = get("onCollisionExitGlobal");

	// GRAPH
	public static final EventCallbackInstance<BehaviourComponent> componentAdded = get("componentAdded");
	public static final EventCallbackInstance<BehaviourComponent> componentRemoved = get("componentRemoved");
	public static final EventCallbackInstance<BehaviourComponent> componentActivated = get("componentActivated");
	public static final EventCallbackInstance<BehaviourComponent> componentDeactivated = get("componentDeactivated");
	public static final EventCallbackInstance<BehaviourComponent> parentChanged = get("parentChanged");
	public static final EventCallbackInstance<BehaviourComponent> childAdded = get("childAdded");
	public static final EventCallbackInstance<BehaviourComponent> childRemoved = get("childRemoved");

	public static final EventCallbackInstance<BehaviourComponent> nodeComponentAdded = get("nodeComponentAdded");
	public static final EventCallbackInstance<BehaviourComponent> nodeComponentRemoved = get("nodeComponentRemoved");
	public static final EventCallbackInstance<BehaviourComponent> nodeComponentActivated = get("nodeComponentActivated");
	public static final EventCallbackInstance<BehaviourComponent> nodeComponentDeactivated = get(
			"nodeComponentDeactivated");
	public static final EventCallbackInstance<BehaviourComponent> nodeParentChanged = get("nodeParentChanged");
	public static final EventCallbackInstance<BehaviourComponent> nodeChildAdded = get("nodeChildAdded");
	public static final EventCallbackInstance<BehaviourComponent> nodeChildRemoved = get("nodeChildRemoved");

	// SCENE
	public static final EventCallbackInstance<BehaviourComponent> onSceneStart = get("onSceneStart");
	public static final EventCallbackInstance<BehaviourComponent> onSceneStop = get("onSceneStop");

	// APPLICATION
	public static final EventCallbackInstance<BehaviourComponent> onPause = get("onPause");
	public static final EventCallbackInstance<BehaviourComponent> onResume = get("onResume");

	// APPLICATION
	public static final EventCallbackInstance<BehaviourComponent> onMessage = get("onMessage");

	private static EventCallbackInstance<BehaviourComponent> get(String id) {
		return EventCallbackInstance.get(BehaviourComponent.class, id);
	}
}