package com.gurella.engine.scene.input.dnd;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.event.Event;
import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.scene.input.PointerProcessor;
import com.gurella.engine.scene.input.PointerTrack;
import com.gurella.engine.subscriptions.scene.input.NodeDragSourceListener;
import com.gurella.engine.subscriptions.scene.input.NodeDropTargetListener;

public class DragAndDropProcessor extends PointerProcessor {
	private SceneNode2 sourceNode;
	private SceneNode2 targetNode;

	private DragStartCondition condition;
	private final Array<DragSource> dragSources = new Array<DragSource>(10);
	private Array<DropTarget> dropTargets = new Array<DropTarget>(10);

	private final NodeDragSourceEvent dragSourceEvent = new NodeDragSourceEvent();
	private final NodeDropTargetEvent dropTargetEvent = new NodeDropTargetEvent();

	public DragAndDropProcessor(Scene scene) {
		super(scene);
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
		SceneNode2 node = pointerTrack.getNode(0);
		if (node == null) {
			return;
		}

		this.condition = condition;
		EventService.post(node.getInstanceId(), dragSourceEvent);
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

		SceneNode2 node = pointerTrack.getNode(last);
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
			EventService.post(node.getInstanceId(), dropTargetEvent);
			if (dropTargets.size < 1) {
				return;
			}

			targetNode = node;
			for (int i = 0; i < dropTargets.size; i++) {
				dropTargets.get(i).dragIn(screenX, screenY, dragSources);
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
		SceneNode2 node = pointerTrack.getNode(last);

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

	public void longPress(PointerTrack pointerTrack) {
		if (sourceNode == null) {
			begin(pointerTrack, DragStartCondition.longPress);
		}
	}

	public void doubleTouch(PointerTrack pointerTrack) {
		if (sourceNode == null) {
			begin(pointerTrack, DragStartCondition.doubleTouch);
		}
	}

	@Override
	public void sceneDeactivated() {
		super.sceneDeactivated();
		sourceNode = null;
		targetNode = null;
		dragSources.clear();
		dropTargets.clear();
	}

	private class NodeDragSourceEvent implements Event<NodeDragSourceListener> {
		@Override
		public void dispatch(NodeDragSourceListener listener) {
			DragSource dragSource = listener.getDragSource(condition);
			if (dragSource != null) {
				dragSources.add(dragSource);
			}
		}

		@Override
		public Class<NodeDragSourceListener> getSubscriptionType() {
			return NodeDragSourceListener.class;
		}
	}

	private class NodeDropTargetEvent implements Event<NodeDropTargetListener> {
		@Override
		public void dispatch(NodeDropTargetListener listener) {
			DropTarget dropTarget = listener.getDropTarget(dragSources);
			if (dropTarget != null) {
				dropTargets.add(dropTarget);
			}
		}

		@Override
		public Class<NodeDropTargetListener> getSubscriptionType() {
			return NodeDropTargetListener.class;
		}
	}
}
