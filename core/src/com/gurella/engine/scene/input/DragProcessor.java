package com.gurella.engine.scene.input;

import com.badlogic.gdx.utils.IntMap;
import com.gurella.engine.event.Event;
import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.scene.renderable.RenderableComponent;
import com.gurella.engine.subscriptions.scene.input.NodeDragOverListener;
import com.gurella.engine.subscriptions.scene.input.SceneDragListener;

public class DragProcessor extends PointerProcessor {
	private final IntMap<SceneNode2> dragOverNodes = new IntMap<SceneNode2>(10);

	private final DragInfo dragInfo = new DragInfo();

	private final SceneDragStartEvent sceneDragStartEvent = new SceneDragStartEvent();
	private final SceneDragedEvent sceneDragedEvent = new SceneDragedEvent();
	private final SceneDragEndEvent sceneDragEndEvent = new SceneDragEndEvent();

	private final DragOverStartEvent dragOverStartEvent = new DragOverStartEvent();
	private final DragOverMoveEvent dragOverMoveEvent = new DragOverMoveEvent();
	private final DragOverEndEvent dragOverEndEvent = new DragOverEndEvent();

	public DragProcessor(Scene scene) {
		super(scene);
	}

	@Override
	public void onPointerActivity(int pointer, int button, PointerTrack pointerTrack) {
		int key = pointer + button * 100;
		switch (pointerTrack.getPhase()) {
		case begin:
			begin(key, pointer, button, pointerTrack);
			break;
		case move:
			move(key, pointer, button, pointerTrack);
			break;
		case end:
			end(key, pointer, button, pointerTrack);
			break;
		default:
			break;
		}
	}

	private void begin(int key, int pointer, int button, PointerTrack pointerTrack) {
		dragInfo.set(pointer, button, pointerTrack.getScreenX(0), pointerTrack.getScreenY(0));
		SceneNode2 node = pointerTrack.getNode(0);
		if (node != null) {
			dragInfo.renderable = node.getComponent(RenderableComponent.class);
			pointerTrack.getIntersection(0, dragInfo.intersection);
		}

		EventService.post(sceneId, sceneDragStartEvent);

		if (node != null) {
			dragOverNodes.put(key, node);
			EventService.post(node.getInstanceId(), dragOverStartEvent);
		}

		dragInfo.reset();
	}

	private void move(int key, int pointer, int button, PointerTrack pointerTrack) {
		int last = pointerTrack.getSize() - 1;
		dragInfo.set(pointer, button, pointerTrack.getScreenX(last), pointerTrack.getScreenY(last));
		SceneNode2 currentNode = pointerTrack.getNode(last);
		if (currentNode != null) {
			dragInfo.renderable = currentNode.getComponent(RenderableComponent.class);
			pointerTrack.getIntersection(last, dragInfo.intersection);
		}

		EventService.post(sceneId, sceneDragedEvent);

		SceneNode2 dragOverNode = dragOverNodes.get(key);
		if (dragOverNode != null) {
			if (currentNode == dragOverNode) {
				EventService.post(dragOverNode.getInstanceId(), dragOverMoveEvent);
			} else {
				dragInfo.intersection.setZero();
				dragInfo.renderable = dragOverNode.getComponent(RenderableComponent.class);
				EventService.post(dragOverNode.getInstanceId(), dragOverEndEvent);
				dragOverNodes.remove(key);
			}
		}

		if (currentNode != null && currentNode != dragOverNode) {
			dragOverNodes.put(key, currentNode);
			dragInfo.renderable = currentNode.getComponent(RenderableComponent.class);
			pointerTrack.getIntersection(last, dragInfo.intersection);
			EventService.post(currentNode.getInstanceId(), dragOverStartEvent);
		}

		dragInfo.reset();
	}

	private void end(int key, int pointer, int button, PointerTrack pointerTrack) {
		int last = pointerTrack.getSize() - 1;
		dragInfo.set(pointer, button, pointerTrack.getScreenX(last), pointerTrack.getScreenY(last));
		SceneNode2 node = pointerTrack.getNode(last);
		if (node != null) {
			dragInfo.renderable = node.getComponent(RenderableComponent.class);
			pointerTrack.getIntersection(last, dragInfo.intersection);
		}

		EventService.post(sceneId, sceneDragEndEvent);

		SceneNode2 dragOverNode = dragOverNodes.remove(key);
		if (dragOverNode != null) {
			if (dragOverNode != node) {
				dragInfo.renderable = dragOverNode.getComponent(RenderableComponent.class);
				dragInfo.intersection.setZero();
			}
			EventService.post(dragOverNode.getInstanceId(), dragOverEndEvent);
		}

		dragInfo.reset();
	}

	@Override
	public void sceneDeactivated() {
		super.sceneDeactivated();
		dragOverNodes.clear();
	}

	private class SceneDragStartEvent implements Event<SceneDragListener> {
		@Override
		public Class<SceneDragListener> getSubscriptionType() {
			return SceneDragListener.class;
		}

		@Override
		public void dispatch(SceneDragListener subscriber) {
			subscriber.onDragStart(dragInfo);
		}
	}

	private class SceneDragedEvent implements Event<SceneDragListener> {
		@Override
		public Class<SceneDragListener> getSubscriptionType() {
			return SceneDragListener.class;
		}

		@Override
		public void dispatch(SceneDragListener subscriber) {
			subscriber.onDragged(dragInfo);
		}
	}

	private class SceneDragEndEvent implements Event<SceneDragListener> {
		@Override
		public Class<SceneDragListener> getSubscriptionType() {
			return SceneDragListener.class;
		}

		@Override
		public void dispatch(SceneDragListener subscriber) {
			subscriber.onDragEnd(dragInfo);
		}
	}

	private class DragOverStartEvent implements Event<NodeDragOverListener> {
		@Override
		public Class<NodeDragOverListener> getSubscriptionType() {
			return NodeDragOverListener.class;
		}

		@Override
		public void dispatch(NodeDragOverListener subscriber) {
			subscriber.onDragOverStart(dragInfo);
		}
	}

	private class DragOverMoveEvent implements Event<NodeDragOverListener> {

		@Override
		public Class<NodeDragOverListener> getSubscriptionType() {
			return NodeDragOverListener.class;
		}

		@Override
		public void dispatch(NodeDragOverListener subscriber) {
			subscriber.onDragOverMove(dragInfo);
		}
	}

	private class DragOverEndEvent implements Event<NodeDragOverListener> {
		@Override
		public Class<NodeDragOverListener> getSubscriptionType() {
			return NodeDragOverListener.class;
		}

		@Override
		public void dispatch(NodeDragOverListener subscriber) {
			subscriber.onDragOverEnd(dragInfo);
		}
	}
}
