package com.gurella.engine.scene.input;

import com.badlogic.gdx.utils.IntMap;
import com.gurella.engine.event.Event;
import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.renderable.RenderableComponent;
import com.gurella.engine.subscriptions.scene.input.NodeDragOverListener;
import com.gurella.engine.subscriptions.scene.input.SceneDragListener;

public class DragProcessor extends PointerProcessor {
	private final IntMap<RenderableComponent> dragOverRenderables = new IntMap<RenderableComponent>(10);

	private final PointerInfo pointerInfo = new PointerInfo();

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
		RenderableComponent renderable = pointerTrack.getRenderable(0);
		pointerInfo.set(pointer, button, pointerTrack.getScreenX(0), pointerTrack.getScreenY(0), renderable,
				pointerTrack.getIntersection(0));
		EventService.post(sceneId, sceneDragStartEvent);

		if (renderable != null) {
			dragOverRenderables.put(key, renderable);
			EventService.post(renderable.getNodeId(), dragOverStartEvent);
		}

		pointerInfo.reset();
	}

	private void move(int key, int pointer, int button, PointerTrack pointerTrack) {
		int last = pointerTrack.getSize() - 1;
		RenderableComponent currentRenderable = pointerTrack.getRenderable(last);
		pointerInfo.set(pointer, button, pointerTrack.getScreenX(last), pointerTrack.getScreenY(last), currentRenderable,
				pointerTrack.getIntersection(last));
		EventService.post(sceneId, sceneDragedEvent);

		RenderableComponent dragOverRenderable = dragOverRenderables.get(key);
		if (dragOverRenderable != null) {
			if (currentRenderable == dragOverRenderable) {
				EventService.post(dragOverRenderable.getInstanceId(), dragOverMoveEvent);
			} else {
				pointerInfo.intersection.set(Float.NaN, Float.NaN, Float.NaN);
				pointerInfo.renderable = dragOverRenderable;
				EventService.post(dragOverRenderable.getNodeId(), dragOverEndEvent);
				dragOverRenderables.remove(key);
			}
		}

		if (currentRenderable != null && currentRenderable != dragOverRenderable) {
			dragOverRenderables.put(key, currentRenderable);
			pointerInfo.renderable = currentRenderable;
			pointerInfo.intersection.set(pointerTrack.getIntersection(last));
			EventService.post(currentRenderable.getNodeId(), dragOverStartEvent);
		}

		pointerInfo.reset();
	}

	private void end(int key, int pointer, int button, PointerTrack pointerTrack) {
		int last = pointerTrack.getSize() - 1;
		RenderableComponent renderable = pointerTrack.getRenderable(last);
		pointerInfo.set(pointer, button, pointerTrack.getScreenX(last), pointerTrack.getScreenY(last), renderable,
				pointerTrack.getIntersection(last));
		EventService.post(sceneId, sceneDragEndEvent);

		RenderableComponent dragOverRenderable = dragOverRenderables.remove(key);
		if (dragOverRenderable != null) {
			if (dragOverRenderable != renderable) {
				pointerInfo.renderable = dragOverRenderable;
				pointerInfo.intersection.set(Float.NaN, Float.NaN, Float.NaN);
			}
			EventService.post(dragOverRenderable.getNodeId(), dragOverEndEvent);
		}

		pointerInfo.reset();
	}

	@Override
	public void sceneDeactivated() {
		super.sceneDeactivated();
		dragOverRenderables.clear();
	}

	private class SceneDragStartEvent implements Event<SceneDragListener> {
		@Override
		public Class<SceneDragListener> getSubscriptionType() {
			return SceneDragListener.class;
		}

		@Override
		public void dispatch(SceneDragListener subscriber) {
			subscriber.onDragStart(pointerInfo);
		}
	}

	private class SceneDragedEvent implements Event<SceneDragListener> {
		@Override
		public Class<SceneDragListener> getSubscriptionType() {
			return SceneDragListener.class;
		}

		@Override
		public void dispatch(SceneDragListener subscriber) {
			subscriber.onDragged(pointerInfo);
		}
	}

	private class SceneDragEndEvent implements Event<SceneDragListener> {
		@Override
		public Class<SceneDragListener> getSubscriptionType() {
			return SceneDragListener.class;
		}

		@Override
		public void dispatch(SceneDragListener subscriber) {
			subscriber.onDragEnd(pointerInfo);
		}
	}

	private class DragOverStartEvent implements Event<NodeDragOverListener> {
		@Override
		public Class<NodeDragOverListener> getSubscriptionType() {
			return NodeDragOverListener.class;
		}

		@Override
		public void dispatch(NodeDragOverListener subscriber) {
			subscriber.onDragOverStart(pointerInfo);
		}
	}

	private class DragOverMoveEvent implements Event<NodeDragOverListener> {

		@Override
		public Class<NodeDragOverListener> getSubscriptionType() {
			return NodeDragOverListener.class;
		}

		@Override
		public void dispatch(NodeDragOverListener subscriber) {
			subscriber.onDragOverMove(pointerInfo);
		}
	}

	private class DragOverEndEvent implements Event<NodeDragOverListener> {
		@Override
		public Class<NodeDragOverListener> getSubscriptionType() {
			return NodeDragOverListener.class;
		}

		@Override
		public void dispatch(NodeDragOverListener subscriber) {
			subscriber.onDragOverEnd(pointerInfo);
		}
	}
}
