package com.gurella.engine.scene.input;

import static com.gurella.engine.scene.behaviour.BehaviourEvents.getDragSource;
import static com.gurella.engine.scene.behaviour.BehaviourEvents.getDropTarget;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.scene.SceneNode;
import com.gurella.engine.scene.behaviour.BehaviourComponent;
import com.gurella.engine.utils.ImmutableArray;

public class DragAndDropProcessor implements PointerActivityListener {
	private SceneNode sourceNode;
	private SceneNode targetNode;
	private Array<DragSource> dragSources = new Array<DragSource>(10);
	private Array<DropTarget> dropTargets = new Array<DropTarget>(10);

	private InputSystem inputSystem;

	public DragAndDropProcessor(InputSystem inputSystem) {
		this.inputSystem = inputSystem;
	}

	@Override
	public void onPointerActivity(int pointer, int button, PointerTrack pointerTrack) {
		if (pointer != 0 || button != Buttons.LEFT) {
			return;
		}
		switch (pointerTrack.getPhase()) {
		case begin:
			begin(pointerTrack, DragStartCondition.none);
			break;
		case move:
			move(pointerTrack);
			break;
		case end:
			end(pointerTrack);
			break;
		default:
			break;
		}
	}

	private void begin(PointerTrack pointerTrack, DragStartCondition condition) {
		SceneNode node = pointerTrack.getNode(0);
		if (node == null) {
			return;
		}

		initDragSources(node, condition);
		if (dragSources.size < 1) {
			return;
		}

		sourceNode = node;
		int screenX = pointerTrack.getScreenX(0);
		int screenY = pointerTrack.getScreenY(0);

		for (int i = 0; i < dragSources.size; i++) {
			dragSources.get(i).dragStarted(screenX, screenY);
		}
	}

	private void initDragSources(SceneNode node, DragStartCondition condition) {
		dragSources.clear();
		ImmutableArray<BehaviourComponent> scripts = inputSystem.getListeners(node, getDragSource);
		if (scripts == null || scripts.size() < 1) {
			return;
		}

		for (BehaviourComponent behaviourComponent : scripts) {
			DragSource dragSource = behaviourComponent.getDragSource(condition);
			if (dragSource != null) {
				dragSources.add(dragSource);
			}
		}
	}

	private void move(PointerTrack pointerTrack) {
		if (dragSources.size < 1) {
			return;
		}

		int last = pointerTrack.getSize() - 1;
		int screenX = pointerTrack.getScreenX(last);
		int screenY = pointerTrack.getScreenY(last);
		for (int i = 0; i < dragSources.size; i++) {
			dragSources.get(i).dragMove(screenX, screenY);
		}

		SceneNode node = pointerTrack.getNode(last);
		if (targetNode != null) {
			if (targetNode == node) {
				for (int i = 0; i < dropTargets.size; i++) {
					dropTargets.get(i).dragMove(screenX, screenY, dragSources);
				}
			} else {
				for (int i = 0; i < dropTargets.size; i++) {
					dropTargets.get(i).dragOut(screenX, screenY, dragSources);
				}
				targetNode = null;
				dropTargets.clear();
			}
		}

		if (node != null && node != targetNode && node != sourceNode) {
			initDropTargets(node);
			if (dropTargets.size < 1) {
				return;
			}

			targetNode = node;
			for (int i = 0; i < dropTargets.size; i++) {
				dropTargets.get(i).dragIn(screenX, screenY, dragSources);
			}
		}
	}

	private void initDropTargets(SceneNode node) {
		ImmutableArray<BehaviourComponent> scripts = inputSystem.getListeners(node, getDropTarget);
		if (scripts == null || scripts.size() < 1) {
			return;
		}

		for (BehaviourComponent behaviourComponent : scripts) {
			DropTarget dropTarget = behaviourComponent.getDropTarget(dragSources);
			if (dropTarget != null) {
				dropTargets.add(dropTarget);
			}
		}
	}

	private void end(PointerTrack pointerTrack) {
		if (dragSources.size < 1) {
			return;
		}

		int last = pointerTrack.getSize() - 1;
		int screenX = pointerTrack.getScreenX(last);
		int screenY = pointerTrack.getScreenY(last);
		SceneNode node = pointerTrack.getNode(last);

		if (targetNode != null && node == targetNode) {
			if (dropTargets.size > 0) {
				for (int i = 0; i < dropTargets.size; i++) {
					dropTargets.get(i).drop(screenX, screenY, dragSources);
				}
				targetNode = null;
				dropTargets.clear();
			}
		}

		for (int i = 0; i < dragSources.size; i++) {
			dragSources.get(i).dragEnd(screenX, screenY);
		}

		sourceNode = null;
		dragSources.clear();
	}

	void longPress(PointerTrack pointerTrack) {
		if (sourceNode == null) {
			begin(pointerTrack, DragStartCondition.longPress);
		}
	}

	void doubleTouch(PointerTrack pointerTrack) {
		if (sourceNode == null) {
			begin(pointerTrack, DragStartCondition.doubleTouch);
		}
	}

	@Override
	public void reset() {
		sourceNode = null;
		targetNode = null;
		dragSources.clear();
		dropTargets.clear();
	}
}
