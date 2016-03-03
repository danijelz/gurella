package com.gurella.engine.scene.input;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.SceneNode;
import com.gurella.engine.scene.renderable.RenderableComponent;
import com.gurella.engine.subscriptions.scene.input.SceneMouseListener;
import com.gurella.engine.subscriptions.scene.input.IntersectionMouseListener;
import com.gurella.engine.subscriptions.scene.input.NodeMouseOverListener;
import com.gurella.engine.utils.Values;

public class MouseMoveProcessor {
	private SceneNode mouseOverNode;

	private Array<Object> tempListeners;

	MouseMoveProcessor(Array<Object> tempListeners) {
		this.tempListeners = tempListeners;
	}

	void mouseMoved(int screenX, int screenY, SceneNode pointerNode, Vector3 intersection) {
		Array<SceneMouseListener> globalListeners = Values.cast(tempListeners);
		EventService.getSubscribers(SceneMouseListener.class, globalListeners);
		for (int i = 0; i < globalListeners.size; i++) {
			globalListeners.get(i).mouseMoved(screenX, screenY);
		}

		if (mouseOverNode != null) {
			RenderableComponent renderableComponent = mouseOverNode.getComponent(RenderableComponent.class);
			if (pointerNode == mouseOverNode) {
				Array<IntersectionMouseListener> intersectionListeners = Values.cast(tempListeners);
				EventService.getSubscribers(IntersectionMouseListener.class, intersectionListeners);
				for (int i = 0; i < intersectionListeners.size; i++) {
					intersectionListeners.get(i).onMouseOverMove(renderableComponent, screenX, screenY, intersection);
				}

				Array<NodeMouseOverListener> listeners = Values.cast(tempListeners);
				EventService.getSubscribers(renderableComponent.getNodeId(), NodeMouseOverListener.class, listeners);
				for (int i = 0; i < listeners.size; i++) {
					listeners.get(i).onMouseOverMove(screenX, screenY, intersection);
				}
			} else {
				Array<IntersectionMouseListener> intersectionListeners = Values.cast(tempListeners);
				EventService.getSubscribers(IntersectionMouseListener.class, intersectionListeners);
				for (int i = 0; i < intersectionListeners.size; i++) {
					intersectionListeners.get(i).onMouseOverEnd(renderableComponent, screenX, screenY);
				}

				Array<NodeMouseOverListener> listeners = Values.cast(tempListeners);
				EventService.getSubscribers(renderableComponent.getNodeId(), NodeMouseOverListener.class, listeners);
				for (int i = 0; i < listeners.size; i++) {
					listeners.get(i).onMouseOverEnd(screenX, screenY);
				}

				mouseOverNode = null;
			}
		}

		if (pointerNode != null && pointerNode != mouseOverNode) {
			mouseOverNode = pointerNode;
			RenderableComponent renderableComponent = pointerNode.getComponent(RenderableComponent.class);
			Array<IntersectionMouseListener> intersectionListeners = Values.cast(tempListeners);
			EventService.getSubscribers(IntersectionMouseListener.class, intersectionListeners);
			for (int i = 0; i < intersectionListeners.size; i++) {
				intersectionListeners.get(i).onMouseOverStart(renderableComponent, screenX, screenY, intersection);
			}

			Array<NodeMouseOverListener> listeners = Values.cast(tempListeners);
			EventService.getSubscribers(renderableComponent.getNodeId(), NodeMouseOverListener.class, listeners);
			for (int i = 0; i < listeners.size; i++) {
				listeners.get(i).onMouseOverStart(screenX, screenY, intersection);
			}
		}
	}

	void reset() {
		mouseOverNode = null;
	}
}
