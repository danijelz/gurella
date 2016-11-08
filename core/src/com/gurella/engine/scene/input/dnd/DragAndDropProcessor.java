package com.gurella.engine.scene.input.dnd;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.event.Event;
import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.input.PointerProcessor;
import com.gurella.engine.scene.input.PointerTrack;
import com.gurella.engine.scene.renderable.RenderableComponent;
import com.gurella.engine.subscriptions.scene.input.NodeDragSourceListener;
import com.gurella.engine.subscriptions.scene.input.NodeDropTargetListener;

public class DragAndDropProcessor extends PointerProcessor {
	private RenderableComponent sourceRenderable;
	private RenderableComponent targetRenderable;

	private DragStartCondition condition;
	private final Array<DragSource> dragSources = new Array<DragSource>(10);
	private final Array<Object> transferData = new Array<Object>(10);
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
		RenderableComponent renderable = pointerTrack.getRenderable(0);
		if (renderable == null) {
			return;
		}

		this.condition = condition;
		EventService.post(renderable.getInstanceId(), dragSourceEvent);
		if (dragSources.size < 1) {
			return;
		}

		sourceRenderable = renderable;
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

		RenderableComponent renderable = pointerTrack.getRenderable(last);
		if (targetRenderable != null) {
			if (targetRenderable == renderable) {
				for (int i = 0; i < dropTargets.size; i++) {
					dropTargets.get(i).dragMove(screenX, screenY, transferData);
				}
			} else {
				for (int i = 0; i < dropTargets.size; i++) {
					dropTargets.get(i).dragOut(screenX, screenY, transferData);
				}
				targetRenderable = null;
				dropTargets.clear();
			}
		}

		if (renderable != null && renderable != targetRenderable && renderable != sourceRenderable) {
			EventService.post(renderable.getInstanceId(), dropTargetEvent);
			if (dropTargets.size < 1) {
				return;
			}

			targetRenderable = renderable;
			for (int i = 0; i < dropTargets.size; i++) {
				dropTargets.get(i).dragIn(screenX, screenY, transferData);
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
		RenderableComponent renderable = pointerTrack.getRenderable(last);

		if (targetRenderable != null && renderable == targetRenderable) {
			if (dropTargets.size > 0) {
				for (int i = 0; i < dropTargets.size; i++) {
					dropTargets.get(i).drop(screenX, screenY, transferData);
				}
				targetRenderable = null;
				dropTargets.clear();
			}
		}

		for (int i = 0; i < dragSources.size; i++) {
			dragSources.get(i).dragEnd(screenX, screenY);
		}

		sourceRenderable = null;
		dragSources.clear();
	}

	public void longPress(PointerTrack pointerTrack) {
		if (sourceRenderable == null) {
			begin(pointerTrack, DragStartCondition.longPress);
		}
	}

	public void doubleTouch(PointerTrack pointerTrack) {
		if (sourceRenderable == null) {
			begin(pointerTrack, DragStartCondition.doubleTouch);
		}
	}

	@Override
	public void sceneDeactivated() {
		super.sceneDeactivated();
		sourceRenderable = null;
		targetRenderable = null;
		dragSources.clear();
		transferData.clear();
		dropTargets.clear();
	}

	private class NodeDragSourceEvent implements Event<NodeDragSourceListener> {
		@Override
		public void dispatch(NodeDragSourceListener listener) {
			DragSource dragSource = listener.getDragSource(condition);
			Object sourceTransferData = dragSource == null ? null : dragSource.getTransferData();
			if (sourceTransferData != null) {
				dragSources.add(dragSource);
				transferData.add(sourceTransferData);
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
