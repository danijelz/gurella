package com.gurella.engine.graph.behaviour;

import com.gurella.engine.graph.event.EventCallbackSignature;

public final class BehaviourEvents {
	private BehaviourEvents() {
	}

	// UPDATE
	public static final EventCallbackSignature<BehaviourComponent> onInput = get("onInput");
	public static final EventCallbackSignature<BehaviourComponent> onThink = get("onThink");
	public static final EventCallbackSignature<BehaviourComponent> onPreRender = get("onPreRender");
	public static final EventCallbackSignature<BehaviourComponent> onRender = get("onRender");
	public static final EventCallbackSignature<BehaviourComponent> onDebugRender = get("onDebugRender");
	public static final EventCallbackSignature<BehaviourComponent> onAfterRender = get("onAfterRender");
	public static final EventCallbackSignature<BehaviourComponent> onCleanup = get("onCleanup");

	// NODE INPUT
	public static final EventCallbackSignature<BehaviourComponent> onTouchDown = get("onTouchDown");
	public static final EventCallbackSignature<BehaviourComponent> onTouchUp = get("onTouchUp");
	public static final EventCallbackSignature<BehaviourComponent> onTap = get("onTap");
	public static final EventCallbackSignature<BehaviourComponent> onDragOverStart = get("onDragOverStart");
	public static final EventCallbackSignature<BehaviourComponent> onDragOverMove = get("onDragOverMove");
	public static final EventCallbackSignature<BehaviourComponent> onDragOverEnd = get("onDragOverEnd");
	public static final EventCallbackSignature<BehaviourComponent> onDragStart = get("onDragStart");
	public static final EventCallbackSignature<BehaviourComponent> onDragMove = get("onDragMove");
	public static final EventCallbackSignature<BehaviourComponent> onDragEnd = get("onDragEnd");
	public static final EventCallbackSignature<BehaviourComponent> onLongPress = get("onLongPress");
	public static final EventCallbackSignature<BehaviourComponent> onDoubleTouch = get("onDoubleTouch");
	public static final EventCallbackSignature<BehaviourComponent> onScrolled = get("onScrolled");
	public static final EventCallbackSignature<BehaviourComponent> onMouseOverStart = get("onMouseOverStart");
	public static final EventCallbackSignature<BehaviourComponent> onMouseOverMove = get("onMouseOverMove");
	public static final EventCallbackSignature<BehaviourComponent> onMouseOverEnd = get("onMouseOverEnd");

	// NODE DRAG AND DROP
	public static final EventCallbackSignature<BehaviourComponent> getDragSource = get("getDragSource");
	public static final EventCallbackSignature<BehaviourComponent> getDropTarget = get("getDropTarget");

	// RESOLVED GLOBAL INPUT
	public static final EventCallbackSignature<BehaviourComponent> onTouchDownGlobal = get("onTouchDownGlobal");
	public static final EventCallbackSignature<BehaviourComponent> onTouchUpGlobal = get("onTouchUpGlobal");
	public static final EventCallbackSignature<BehaviourComponent> onTapGlobal = get("onTapGlobal");
	public static final EventCallbackSignature<BehaviourComponent> onDragOverStartGlobal = get("onDragOverStartGlobal");
	public static final EventCallbackSignature<BehaviourComponent> onDragOverMoveGlobal = get("onDragOverMoveGlobal");
	public static final EventCallbackSignature<BehaviourComponent> onDragOverEndGlobal = get("onDragOverEndGlobal");
	public static final EventCallbackSignature<BehaviourComponent> onDragStartGlobal = get("onDragStartGlobal");
	public static final EventCallbackSignature<BehaviourComponent> onDragMoveGlobal = get("onDragMoveGlobal");
	public static final EventCallbackSignature<BehaviourComponent> onDragEndGlobal = get("onDragEndGlobal");
	public static final EventCallbackSignature<BehaviourComponent> onLongPressGlobal = get("onLongPressGlobal");
	public static final EventCallbackSignature<BehaviourComponent> onDoubleTouchGlobal = get("onDoubleTouchGlobal");
	public static final EventCallbackSignature<BehaviourComponent> onScrolledGlobal = get("onScrolledGlobal");
	public static final EventCallbackSignature<BehaviourComponent> onMouseOverStartGlobal = get("onMouseOverStartGlobal");
	public static final EventCallbackSignature<BehaviourComponent> onMouseOverMoveGlobal = get("onMouseOverMoveGlobal");
	public static final EventCallbackSignature<BehaviourComponent> onMouseOverEndGlobal = get("onMouseOverEndGlobal");

	// GLOBAL INPUT
	public static final EventCallbackSignature<BehaviourComponent> keyDown = get("keyDown");
	public static final EventCallbackSignature<BehaviourComponent> keyUp = get("keyUp");
	public static final EventCallbackSignature<BehaviourComponent> keyTyped = get("keyTyped");
	public static final EventCallbackSignature<BehaviourComponent> touchDown = get("touchDown");
	public static final EventCallbackSignature<BehaviourComponent> doubleTouchDown = get("doubleTouchDown");
	public static final EventCallbackSignature<BehaviourComponent> touchUp = get("touchUp");
	public static final EventCallbackSignature<BehaviourComponent> touchDragged = get("touchDragged");
	public static final EventCallbackSignature<BehaviourComponent> mouseMoved = get("mouseMoved");
	public static final EventCallbackSignature<BehaviourComponent> scrolled = get("scrolled");
	public static final EventCallbackSignature<BehaviourComponent> tap = get("tap");
	public static final EventCallbackSignature<BehaviourComponent> longPress = get("longPress");

	// BULLET PHYSICS
	public static final EventCallbackSignature<BehaviourComponent> onCollisionEnter = get("onCollisionEnter");
	public static final EventCallbackSignature<BehaviourComponent> onCollisionStay = get("onCollisionStay");
	public static final EventCallbackSignature<BehaviourComponent> onCollisionExit = get("onCollisionExit");
	public static final EventCallbackSignature<BehaviourComponent> onPhysicsSimulationStart = get(
			"onPhysicsSimulationStart");
	public static final EventCallbackSignature<BehaviourComponent> onPhysicsSimulationStep = get("onPhysicsSimulationStep");
	public static final EventCallbackSignature<BehaviourComponent> onPhysicsSimulationEnd = get("onPhysicsSimulationEnd");

	// GLOBAL BULLET COLLISIONS
	public static final EventCallbackSignature<BehaviourComponent> onCollisionEnterGlobal = get("onCollisionEnterGlobal");
	public static final EventCallbackSignature<BehaviourComponent> onCollisionStayGlobal = get("onCollisionStayGlobal");
	public static final EventCallbackSignature<BehaviourComponent> onCollisionExitGlobal = get("onCollisionExitGlobal");

	// GRAPH
	public static final EventCallbackSignature<BehaviourComponent> componentAdded = get("componentAdded");
	public static final EventCallbackSignature<BehaviourComponent> componentRemoved = get("componentRemoved");
	public static final EventCallbackSignature<BehaviourComponent> componentActivated = get("componentActivated");
	public static final EventCallbackSignature<BehaviourComponent> componentDeactivated = get("componentDeactivated");
	public static final EventCallbackSignature<BehaviourComponent> parentChanged = get("parentChanged");
	public static final EventCallbackSignature<BehaviourComponent> childAdded = get("childAdded");
	public static final EventCallbackSignature<BehaviourComponent> childRemoved = get("childRemoved");

	public static final EventCallbackSignature<BehaviourComponent> nodeComponentAdded = get("nodeComponentAdded");
	public static final EventCallbackSignature<BehaviourComponent> nodeComponentRemoved = get("nodeComponentRemoved");
	public static final EventCallbackSignature<BehaviourComponent> nodeComponentActivated = get("nodeComponentActivated");
	public static final EventCallbackSignature<BehaviourComponent> nodeComponentDeactivated = get(
			"nodeComponentDeactivated");
	public static final EventCallbackSignature<BehaviourComponent> nodeParentChanged = get("nodeParentChanged");
	public static final EventCallbackSignature<BehaviourComponent> nodeChildAdded = get("nodeChildAdded");
	public static final EventCallbackSignature<BehaviourComponent> nodeChildRemoved = get("nodeChildRemoved");

	// SCENE
	public static final EventCallbackSignature<BehaviourComponent> onSceneStart = get("onSceneStart");
	public static final EventCallbackSignature<BehaviourComponent> onSceneStop = get("onSceneStop");

	// APPLICATION
	public static final EventCallbackSignature<BehaviourComponent> onPause = get("onPause");
	public static final EventCallbackSignature<BehaviourComponent> onResume = get("onResume");

	// APPLICATION
	public static final EventCallbackSignature<BehaviourComponent> onMessage = get("onMessage");

	private static EventCallbackSignature<BehaviourComponent> get(String id) {
		return EventCallbackSignature.get(BehaviourComponent.class, id);
	}
}