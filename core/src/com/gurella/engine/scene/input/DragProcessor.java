package com.gurella.engine.scene.input;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.SceneNode;
import com.gurella.engine.scene.renderable.RenderableComponent;
import com.gurella.engine.subscriptions.scene.input.GlobalDragListener;
import com.gurella.engine.subscriptions.scene.input.IntersectionDragListener;
import com.gurella.engine.subscriptions.scene.input.IntersectionDragOverListener;
import com.gurella.engine.subscriptions.scene.input.ObjectDragListener;
import com.gurella.engine.subscriptions.scene.input.ObjectOverDragListener;
import com.gurella.engine.utils.Values;

public class DragProcessor implements PointerActivityListener {
	private final IntMap<SceneNode> dragStartNodes = new IntMap<SceneNode>(10);
	private final IntMap<SceneNode> dragOverNodes = new IntMap<SceneNode>(10);

	private final TouchEvent touchEvent = new TouchEvent();
	private final IntersectionTouchEvent intersectionTouchEvent = new IntersectionTouchEvent();

	private Array<Object> tempListeners;

	public DragProcessor(Array<Object> tempListeners) {
		this.tempListeners = tempListeners;
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
		int screenX = pointerTrack.getScreenX(0);
		int screenY = pointerTrack.getScreenY(0);
		touchEvent.set(pointer, button, screenX, screenY);
		Array<GlobalDragListener> globalListeners = Values.cast(tempListeners);
		EventService.getSubscribers(GlobalDragListener.class, globalListeners);
		for (int i = 0; i < globalListeners.size; i++) {
			globalListeners.get(i).touchDragged(touchEvent);
		}

		SceneNode node = pointerTrack.getNode(0);
		if (node != null) {
			intersectionTouchEvent.set(pointer, button, screenX, screenY, pointerTrack, 0);
			dragStartNodes.put(key, node);
			RenderableComponent renderableComponent = node.getComponent(RenderableComponent.class);
			Array<IntersectionDragListener> intersectionListeners = Values.cast(tempListeners);
			EventService.getSubscribers(IntersectionDragListener.class, intersectionListeners);
			for (int i = 0; i < intersectionListeners.size; i++) {
				intersectionListeners.get(i).onDragStart(renderableComponent, intersectionTouchEvent);
			}

			Array<ObjectDragListener> objectListeners = Values.cast(tempListeners);
			EventService.getSubscribers(renderableComponent.getNodeId(), ObjectDragListener.class, objectListeners);
			for (int i = 0; i < objectListeners.size; i++) {
				objectListeners.get(i).onDragStart(intersectionTouchEvent);
			}

			dragOverNodes.put(key, node);
			renderableComponent = node.getComponent(RenderableComponent.class);
			Array<IntersectionDragOverListener> intersectionOverListeners = Values.cast(tempListeners);
			EventService.getSubscribers(IntersectionDragOverListener.class, intersectionOverListeners);
			for (int i = 0; i < intersectionOverListeners.size; i++) {
				intersectionOverListeners.get(i).onDragOverStart(renderableComponent, intersectionTouchEvent);
			}

			Array<ObjectOverDragListener> objectOverListeners = Values.cast(tempListeners);
			EventService.getSubscribers(renderableComponent.getNodeId(), ObjectOverDragListener.class,
					objectOverListeners);
			for (int i = 0; i < objectOverListeners.size; i++) {
				objectOverListeners.get(i).onDragOverStart(intersectionTouchEvent);
			}
		}
	}

	private void move(int key, int pointer, int button, PointerTrack pointerTrack) {
		int last = pointerTrack.getSize() - 1;
		int screenX = pointerTrack.getScreenX(last);
		int screenY = pointerTrack.getScreenY(last);
		touchEvent.set(pointer, button, screenX, screenY);
		Array<GlobalDragListener> globalListeners = Values.cast(tempListeners);
		EventService.getSubscribers(GlobalDragListener.class, globalListeners);
		for (int i = 0; i < globalListeners.size; i++) {
			globalListeners.get(i).touchDragged(touchEvent);
		}

		SceneNode dragStartNode = dragStartNodes.get(key);
		if (dragStartNode != null) {
			RenderableComponent renderableComponent = dragStartNode.getComponent(RenderableComponent.class);
			Array<IntersectionDragListener> intersectionListeners = Values.cast(tempListeners);
			EventService.getSubscribers(IntersectionDragListener.class, intersectionListeners);
			for (int i = 0; i < intersectionListeners.size; i++) {
				intersectionListeners.get(i).onDragMove(renderableComponent, touchEvent);
			}

			Array<ObjectDragListener> listeners = Values.cast(tempListeners);
			EventService.getSubscribers(renderableComponent.getNodeId(), ObjectDragListener.class, listeners);
			for (int i = 0; i < listeners.size; i++) {
				listeners.get(i).onDragMove(touchEvent);
			}
		}

		SceneNode pointerNode = pointerTrack.getNode(last);
		SceneNode dragOverNode = dragOverNodes.get(key);
		intersectionTouchEvent.set(pointer, button, screenX, screenY, pointerTrack, last);
		if (dragOverNode != null) {
			RenderableComponent renderableComponent = dragOverNode.getComponent(RenderableComponent.class);
			if (pointerNode == dragOverNode) {
				Array<IntersectionDragOverListener> intersectionListeners = Values.cast(tempListeners);
				EventService.getSubscribers(IntersectionDragOverListener.class, intersectionListeners);
				for (int i = 0; i < intersectionListeners.size; i++) {
					intersectionListeners.get(i).onDragOverMove(renderableComponent, intersectionTouchEvent);
				}

				Array<ObjectOverDragListener> objectListeners = Values.cast(tempListeners);
				EventService.getSubscribers(renderableComponent.getNodeId(), ObjectOverDragListener.class,
						objectListeners);
				for (int i = 0; i < objectListeners.size; i++) {
					objectListeners.get(i).onDragOverMove(intersectionTouchEvent);
				}
			} else {
				Array<IntersectionDragOverListener> intersectionListeners = Values.cast(tempListeners);
				EventService.getSubscribers(IntersectionDragOverListener.class, intersectionListeners);
				for (int i = 0; i < intersectionListeners.size; i++) {
					intersectionListeners.get(i).onDragOverEnd(renderableComponent, touchEvent);
				}

				Array<ObjectOverDragListener> objectListeners = Values.cast(tempListeners);
				EventService.getSubscribers(renderableComponent.getNodeId(), ObjectOverDragListener.class,
						objectListeners);
				for (int i = 0; i < objectListeners.size; i++) {
					objectListeners.get(i).onDragOverEnd(touchEvent);
				}

				dragOverNodes.remove(key);
			}
		}

		if (pointerNode != null && pointerNode != dragOverNode) {
			dragOverNodes.put(key, pointerNode);
			RenderableComponent renderableComponent = pointerNode.getComponent(RenderableComponent.class);
			Array<IntersectionDragOverListener> intersectionListeners = Values.cast(tempListeners);
			EventService.getSubscribers(IntersectionDragOverListener.class, intersectionListeners);
			for (int i = 0; i < intersectionListeners.size; i++) {
				intersectionListeners.get(i).onDragOverStart(renderableComponent, intersectionTouchEvent);
			}

			Array<ObjectOverDragListener> objectListeners = Values.cast(tempListeners);
			EventService.getSubscribers(renderableComponent.getNodeId(), ObjectOverDragListener.class, objectListeners);
			for (int i = 0; i < objectListeners.size; i++) {
				objectListeners.get(i).onDragOverStart(intersectionTouchEvent);
			}
		}
	}

	private void end(int key, int pointer, int button, PointerTrack pointerTrack) {
		int last = pointerTrack.getSize() - 1;
		int screenX = pointerTrack.getScreenX(last);
		int screenY = pointerTrack.getScreenY(last);
		touchEvent.set(pointer, button, screenX, screenY);

		SceneNode dragStartNode = dragStartNodes.remove(key);
		if (dragStartNode != null) {
			RenderableComponent renderableComponent = dragStartNode.getComponent(RenderableComponent.class);
			Array<IntersectionDragListener> intersectionListeners = Values.cast(tempListeners);
			EventService.getSubscribers(IntersectionDragListener.class, intersectionListeners);
			for (int i = 0; i < intersectionListeners.size; i++) {
				intersectionListeners.get(i).onDragEnd(renderableComponent, touchEvent);
			}

			Array<ObjectDragListener> listeners = Values.cast(tempListeners);
			EventService.getSubscribers(renderableComponent.getNodeId(), ObjectDragListener.class, listeners);
			for (int i = 0; i < listeners.size; i++) {
				listeners.get(i).onDragEnd(touchEvent);
			}
		}

		SceneNode dragOverNode = dragOverNodes.remove(key);
		if (dragOverNode != null) {
			RenderableComponent renderableComponent = dragOverNode.getComponent(RenderableComponent.class);
			Array<IntersectionDragOverListener> intersectionListeners = Values.cast(tempListeners);
			EventService.getSubscribers(IntersectionDragOverListener.class, intersectionListeners);
			for (int i = 0; i < intersectionListeners.size; i++) {
				intersectionListeners.get(i).onDragOverEnd(renderableComponent, touchEvent);
			}

			Array<ObjectOverDragListener> objectListeners = Values.cast(tempListeners);
			EventService.getSubscribers(renderableComponent.getNodeId(), ObjectOverDragListener.class, objectListeners);
			for (int i = 0; i < objectListeners.size; i++) {
				objectListeners.get(i).onDragOverEnd(touchEvent);
			}
		}
	}

	@Override
	public void reset() {
		dragStartNodes.clear();
		dragOverNodes.clear();
	}
}
