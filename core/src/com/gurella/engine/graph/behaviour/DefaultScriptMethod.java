package com.gurella.engine.graph.behaviour;

import com.gurella.engine.graph.event.ScriptMethodDescriptor;

public final class DefaultScriptMethod {
	private DefaultScriptMethod() {
	}

	// UPDATE
	public static final ScriptMethodDescriptor<ScriptComponent> onInput = get("onInput");
	public static final ScriptMethodDescriptor<ScriptComponent> onThink = get("onThink");
	public static final ScriptMethodDescriptor<ScriptComponent> onPreRender = get("onPreRender");
	public static final ScriptMethodDescriptor<ScriptComponent> onRender = get("onRender");
	public static final ScriptMethodDescriptor<ScriptComponent> onDebugRender = get("onDebugRender");
	public static final ScriptMethodDescriptor<ScriptComponent> onAfterRender = get("onAfterRender");
	public static final ScriptMethodDescriptor<ScriptComponent> onCleanup = get("onCleanup");

	// NODE INPUT
	public static final ScriptMethodDescriptor<ScriptComponent> onTouchDown = get("onTouchDown");
	public static final ScriptMethodDescriptor<ScriptComponent> onTouchUp = get("onTouchUp");
	public static final ScriptMethodDescriptor<ScriptComponent> onTap = get("onTap");
	public static final ScriptMethodDescriptor<ScriptComponent> onDragOverStart = get("onDragOverStart");
	public static final ScriptMethodDescriptor<ScriptComponent> onDragOverMove = get("onDragOverMove");
	public static final ScriptMethodDescriptor<ScriptComponent> onDragOverEnd = get("onDragOverEnd");
	public static final ScriptMethodDescriptor<ScriptComponent> onDragStart = get("onDragStart");
	public static final ScriptMethodDescriptor<ScriptComponent> onDragMove = get("onDragMove");
	public static final ScriptMethodDescriptor<ScriptComponent> onDragEnd = get("onDragEnd");
	public static final ScriptMethodDescriptor<ScriptComponent> onLongPress = get("onLongPress");
	public static final ScriptMethodDescriptor<ScriptComponent> onDoubleTouch = get("onDoubleTouch");
	public static final ScriptMethodDescriptor<ScriptComponent> onScrolled = get("onScrolled");
	public static final ScriptMethodDescriptor<ScriptComponent> onMouseOverStart = get("onMouseOverStart");
	public static final ScriptMethodDescriptor<ScriptComponent> onMouseOverMove = get("onMouseOverMove");
	public static final ScriptMethodDescriptor<ScriptComponent> onMouseOverEnd = get("onMouseOverEnd");

	// NODE DRAG AND DROP
	public static final ScriptMethodDescriptor<ScriptComponent> getDragSource = get("getDragSource");
	public static final ScriptMethodDescriptor<ScriptComponent> getDropTarget = get("getDropTarget");

	// RESOLVED GLOBAL INPUT
	public static final ScriptMethodDescriptor<ScriptComponent> onTouchDownGlobal = get("onTouchDownGlobal");
	public static final ScriptMethodDescriptor<ScriptComponent> onTouchUpResolved = get("onTouchUpGlobal");
	public static final ScriptMethodDescriptor<ScriptComponent> onTapResolved = get("onTapGlobal");
	public static final ScriptMethodDescriptor<ScriptComponent> onDragOverStartResolved = get("onDragOverStartGlobal");
	public static final ScriptMethodDescriptor<ScriptComponent> onDragOverMoveResolved = get("onDragOverMoveGlobal");
	public static final ScriptMethodDescriptor<ScriptComponent> onDragOverEndResolved = get("onDragOverEndGlobal");
	public static final ScriptMethodDescriptor<ScriptComponent> onDragStartResolved = get("onDragStartGlobal");
	public static final ScriptMethodDescriptor<ScriptComponent> onDragMoveResolved = get("onDragMoveGlobal");
	public static final ScriptMethodDescriptor<ScriptComponent> onDragEndResolved = get("onDragEndGlobal");
	public static final ScriptMethodDescriptor<ScriptComponent> onLongPressResolved = get("onLongPressGlobal");
	public static final ScriptMethodDescriptor<ScriptComponent> onDoubleTouchResolved = get("onDoubleTouchGlobal");
	public static final ScriptMethodDescriptor<ScriptComponent> onScrolledResolved = get("onScrolledGlobal");
	public static final ScriptMethodDescriptor<ScriptComponent> onMouseOverStartResolved = get(
			"onMouseOverStartGlobal");
	public static final ScriptMethodDescriptor<ScriptComponent> onMouseOverMoveResolved = get("onMouseOverMoveGlobal");
	public static final ScriptMethodDescriptor<ScriptComponent> onMouseOverEndResolved = get("onMouseOverEndGlobal");

	// GLOBAL INPUT
	public static final ScriptMethodDescriptor<ScriptComponent> keyDown = get("keyDown");
	public static final ScriptMethodDescriptor<ScriptComponent> keyUp = get("keyUp");
	public static final ScriptMethodDescriptor<ScriptComponent> keyTyped = get("keyTyped");
	public static final ScriptMethodDescriptor<ScriptComponent> touchDown = get("touchDown");
	public static final ScriptMethodDescriptor<ScriptComponent> doubleTouchDown = get("doubleTouchDown");
	public static final ScriptMethodDescriptor<ScriptComponent> touchUp = get("touchUp");
	public static final ScriptMethodDescriptor<ScriptComponent> touchDragged = get("touchDragged");
	public static final ScriptMethodDescriptor<ScriptComponent> mouseMoved = get("mouseMoved");
	public static final ScriptMethodDescriptor<ScriptComponent> scrolled = get("scrolled");
	public static final ScriptMethodDescriptor<ScriptComponent> tap = get("tap");
	public static final ScriptMethodDescriptor<ScriptComponent> longPress = get("longPress");

	// BULLET PHYSICS
	public static final ScriptMethodDescriptor<ScriptComponent> onCollisionEnter = get("onCollisionEnter");
	public static final ScriptMethodDescriptor<ScriptComponent> onCollisionStay = get("onCollisionStay");
	public static final ScriptMethodDescriptor<ScriptComponent> onCollisionExit = get("onCollisionExit");
	public static final ScriptMethodDescriptor<ScriptComponent> onPhysicsSimulationStart = get(
			"onPhysicsSimulationStart");
	public static final ScriptMethodDescriptor<ScriptComponent> onPhysicsSimulationStep = get(
			"onPhysicsSimulationStep");
	public static final ScriptMethodDescriptor<ScriptComponent> onPhysicsSimulationEnd = get("onPhysicsSimulationEnd");

	// GLOBAL BULLET COLLISIONS
	public static final ScriptMethodDescriptor<ScriptComponent> onCollisionEnterGlobal = get("onCollisionEnterGlobal");
	public static final ScriptMethodDescriptor<ScriptComponent> onCollisionStayGlobal = get("onCollisionStayGlobal");
	public static final ScriptMethodDescriptor<ScriptComponent> onCollisionExitGlobal = get("onCollisionExitGlobal");

	// GRAPH
	public static final ScriptMethodDescriptor<ScriptComponent> componentAdded = get("componentAdded");
	public static final ScriptMethodDescriptor<ScriptComponent> componentRemoved = get("componentRemoved");
	public static final ScriptMethodDescriptor<ScriptComponent> componentActivated = get("componentActivated");
	public static final ScriptMethodDescriptor<ScriptComponent> componentDeactivated = get("componentDeactivated");
	public static final ScriptMethodDescriptor<ScriptComponent> parentChanged = get("parentChanged");
	public static final ScriptMethodDescriptor<ScriptComponent> childAdded = get("childAdded");
	public static final ScriptMethodDescriptor<ScriptComponent> childRemoved = get("childRemoved");

	public static final ScriptMethodDescriptor<ScriptComponent> nodeComponentAdded = get("nodeComponentAdded");
	public static final ScriptMethodDescriptor<ScriptComponent> nodeComponentRemoved = get("nodeComponentRemoved");
	public static final ScriptMethodDescriptor<ScriptComponent> nodeComponentActivated = get("nodeComponentActivated");
	public static final ScriptMethodDescriptor<ScriptComponent> nodeComponentDeactivated = get(
			"nodeComponentDeactivated");
	public static final ScriptMethodDescriptor<ScriptComponent> nodeParentChanged = get("nodeParentChanged");
	public static final ScriptMethodDescriptor<ScriptComponent> nodeChildAdded = get("nodeChildAdded");
	public static final ScriptMethodDescriptor<ScriptComponent> nodeChildRemoved = get("nodeChildRemoved");

	// SCENE
	public static final ScriptMethodDescriptor<ScriptComponent> onSceneStart = get("onSceneStart");
	public static final ScriptMethodDescriptor<ScriptComponent> onSceneStop = get("onSceneStop");

	// APPLICATION
	public static final ScriptMethodDescriptor<ScriptComponent> onPause = get("onPause");
	public static final ScriptMethodDescriptor<ScriptComponent> onResume = get("onResume");

	// APPLICATION
	public static final ScriptMethodDescriptor<ScriptComponent> onMessage = get("onMessage");

	private static ScriptMethodDescriptor<ScriptComponent> get(String id) {
		return ScriptMethodDescriptor.get(ScriptComponent.class, id);
	}
}