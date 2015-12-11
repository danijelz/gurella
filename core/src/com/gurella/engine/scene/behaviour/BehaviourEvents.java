package com.gurella.engine.scene.behaviour;

import com.gurella.engine.scene.event.EventCallbackIdentifier;

public final class BehaviourEvents {
	private BehaviourEvents() {
	}

	// UPDATE
	public static final EventCallbackIdentifier<BehaviourComponent> onInput = get("onInput");
	public static final EventCallbackIdentifier<BehaviourComponent> onThink = get("onThink");
	public static final EventCallbackIdentifier<BehaviourComponent> onPreRender = get("onPreRender");
	public static final EventCallbackIdentifier<BehaviourComponent> onRender = get("onRender");
	public static final EventCallbackIdentifier<BehaviourComponent> onDebugRender = get("onDebugRender");
	public static final EventCallbackIdentifier<BehaviourComponent> onAfterRender = get("onAfterRender");
	public static final EventCallbackIdentifier<BehaviourComponent> onCleanup = get("onCleanup");

	// NODE INPUT
	public static final EventCallbackIdentifier<BehaviourComponent> onTouchDown = get("onTouchDown");
	public static final EventCallbackIdentifier<BehaviourComponent> onTouchUp = get("onTouchUp");
	public static final EventCallbackIdentifier<BehaviourComponent> onTap = get("onTap");
	public static final EventCallbackIdentifier<BehaviourComponent> onDragOverStart = get("onDragOverStart");
	public static final EventCallbackIdentifier<BehaviourComponent> onDragOverMove = get("onDragOverMove");
	public static final EventCallbackIdentifier<BehaviourComponent> onDragOverEnd = get("onDragOverEnd");
	public static final EventCallbackIdentifier<BehaviourComponent> onDragStart = get("onDragStart");
	public static final EventCallbackIdentifier<BehaviourComponent> onDragMove = get("onDragMove");
	public static final EventCallbackIdentifier<BehaviourComponent> onDragEnd = get("onDragEnd");
	public static final EventCallbackIdentifier<BehaviourComponent> onLongPress = get("onLongPress");
	public static final EventCallbackIdentifier<BehaviourComponent> onDoubleTouch = get("onDoubleTouch");
	public static final EventCallbackIdentifier<BehaviourComponent> onScrolled = get("onScrolled");
	public static final EventCallbackIdentifier<BehaviourComponent> onMouseOverStart = get("onMouseOverStart");
	public static final EventCallbackIdentifier<BehaviourComponent> onMouseOverMove = get("onMouseOverMove");
	public static final EventCallbackIdentifier<BehaviourComponent> onMouseOverEnd = get("onMouseOverEnd");

	// NODE DRAG AND DROP
	public static final EventCallbackIdentifier<BehaviourComponent> getDragSource = get("getDragSource");
	public static final EventCallbackIdentifier<BehaviourComponent> getDropTarget = get("getDropTarget");

	// RESOLVED GLOBAL INPUT
	public static final EventCallbackIdentifier<BehaviourComponent> onTouchDownGlobal = get("onTouchDownGlobal");
	public static final EventCallbackIdentifier<BehaviourComponent> onTouchUpGlobal = get("onTouchUpGlobal");
	public static final EventCallbackIdentifier<BehaviourComponent> onTapGlobal = get("onTapGlobal");
	public static final EventCallbackIdentifier<BehaviourComponent> onDragOverStartGlobal = get("onDragOverStartGlobal");
	public static final EventCallbackIdentifier<BehaviourComponent> onDragOverMoveGlobal = get("onDragOverMoveGlobal");
	public static final EventCallbackIdentifier<BehaviourComponent> onDragOverEndGlobal = get("onDragOverEndGlobal");
	public static final EventCallbackIdentifier<BehaviourComponent> onDragStartGlobal = get("onDragStartGlobal");
	public static final EventCallbackIdentifier<BehaviourComponent> onDragMoveGlobal = get("onDragMoveGlobal");
	public static final EventCallbackIdentifier<BehaviourComponent> onDragEndGlobal = get("onDragEndGlobal");
	public static final EventCallbackIdentifier<BehaviourComponent> onLongPressGlobal = get("onLongPressGlobal");
	public static final EventCallbackIdentifier<BehaviourComponent> onDoubleTouchGlobal = get("onDoubleTouchGlobal");
	public static final EventCallbackIdentifier<BehaviourComponent> onScrolledGlobal = get("onScrolledGlobal");
	public static final EventCallbackIdentifier<BehaviourComponent> onMouseOverStartGlobal = get("onMouseOverStartGlobal");
	public static final EventCallbackIdentifier<BehaviourComponent> onMouseOverMoveGlobal = get("onMouseOverMoveGlobal");
	public static final EventCallbackIdentifier<BehaviourComponent> onMouseOverEndGlobal = get("onMouseOverEndGlobal");

	// GLOBAL INPUT
	public static final EventCallbackIdentifier<BehaviourComponent> keyDown = get("keyDown");
	public static final EventCallbackIdentifier<BehaviourComponent> keyUp = get("keyUp");
	public static final EventCallbackIdentifier<BehaviourComponent> keyTyped = get("keyTyped");
	public static final EventCallbackIdentifier<BehaviourComponent> touchDown = get("touchDown");
	public static final EventCallbackIdentifier<BehaviourComponent> doubleTouchDown = get("doubleTouchDown");
	public static final EventCallbackIdentifier<BehaviourComponent> touchUp = get("touchUp");
	public static final EventCallbackIdentifier<BehaviourComponent> touchDragged = get("touchDragged");
	public static final EventCallbackIdentifier<BehaviourComponent> mouseMoved = get("mouseMoved");
	public static final EventCallbackIdentifier<BehaviourComponent> scrolled = get("scrolled");
	public static final EventCallbackIdentifier<BehaviourComponent> tap = get("tap");
	public static final EventCallbackIdentifier<BehaviourComponent> longPress = get("longPress");

	// BULLET PHYSICS
	public static final EventCallbackIdentifier<BehaviourComponent> onCollisionEnter = get("onCollisionEnter");
	public static final EventCallbackIdentifier<BehaviourComponent> onCollisionStay = get("onCollisionStay");
	public static final EventCallbackIdentifier<BehaviourComponent> onCollisionExit = get("onCollisionExit");
	public static final EventCallbackIdentifier<BehaviourComponent> onPhysicsSimulationStart = get(
			"onPhysicsSimulationStart");
	public static final EventCallbackIdentifier<BehaviourComponent> onPhysicsSimulationStep = get("onPhysicsSimulationStep");
	public static final EventCallbackIdentifier<BehaviourComponent> onPhysicsSimulationEnd = get("onPhysicsSimulationEnd");

	// GLOBAL BULLET COLLISIONS
	public static final EventCallbackIdentifier<BehaviourComponent> onCollisionEnterGlobal = get("onCollisionEnterGlobal");
	public static final EventCallbackIdentifier<BehaviourComponent> onCollisionStayGlobal = get("onCollisionStayGlobal");
	public static final EventCallbackIdentifier<BehaviourComponent> onCollisionExitGlobal = get("onCollisionExitGlobal");

	// GRAPH
	public static final EventCallbackIdentifier<BehaviourComponent> componentAdded = get("componentAdded");
	public static final EventCallbackIdentifier<BehaviourComponent> componentRemoved = get("componentRemoved");
	public static final EventCallbackIdentifier<BehaviourComponent> componentActivated = get("componentActivated");
	public static final EventCallbackIdentifier<BehaviourComponent> componentDeactivated = get("componentDeactivated");
	public static final EventCallbackIdentifier<BehaviourComponent> parentChanged = get("parentChanged");
	public static final EventCallbackIdentifier<BehaviourComponent> childAdded = get("childAdded");
	public static final EventCallbackIdentifier<BehaviourComponent> childRemoved = get("childRemoved");

	public static final EventCallbackIdentifier<BehaviourComponent> nodeComponentAdded = get("nodeComponentAdded");
	public static final EventCallbackIdentifier<BehaviourComponent> nodeComponentRemoved = get("nodeComponentRemoved");
	public static final EventCallbackIdentifier<BehaviourComponent> nodeComponentActivated = get("nodeComponentActivated");
	public static final EventCallbackIdentifier<BehaviourComponent> nodeComponentDeactivated = get(
			"nodeComponentDeactivated");
	public static final EventCallbackIdentifier<BehaviourComponent> nodeParentChanged = get("nodeParentChanged");
	public static final EventCallbackIdentifier<BehaviourComponent> nodeChildAdded = get("nodeChildAdded");
	public static final EventCallbackIdentifier<BehaviourComponent> nodeChildRemoved = get("nodeChildRemoved");

	// SCENE
	public static final EventCallbackIdentifier<BehaviourComponent> onSceneStart = get("onSceneStart");
	public static final EventCallbackIdentifier<BehaviourComponent> onSceneStop = get("onSceneStop");

	// APPLICATION
	public static final EventCallbackIdentifier<BehaviourComponent> onPause = get("onPause");
	public static final EventCallbackIdentifier<BehaviourComponent> onResume = get("onResume");

	private static EventCallbackIdentifier<BehaviourComponent> get(String id) {
		return EventCallbackIdentifier.get(BehaviourComponent.class, id);
	}
}