package com.gurella.engine.scene.input;

import com.badlogic.gdx.math.Vector3;
import com.gurella.engine.event.Event;
import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.scene.renderable.RenderableComponent;
import com.gurella.engine.subscriptions.scene.input.NodeMouseOverListener;
import com.gurella.engine.subscriptions.scene.input.SceneMouseListener;

class MouseMoveProcessor {
	private final Scene scene;

	private final MouseMoveInfo mouseMoveInfo = new MouseMoveInfo();
	private final SceneMouseEvent sceneMouseEvent = new SceneMouseEvent();
	private final MouseOverStartEvent mouseOverStartEvent = new MouseOverStartEvent();
	private final MouseOverMoveEvent mouseOverMoveEvent = new MouseOverMoveEvent();
	private final MouseOverEndEvent mouseOverEndEvent = new MouseOverEndEvent();

	private SceneNode2 mouseOverNode;

	MouseMoveProcessor(Scene scene) {
		this.scene = scene;
	}

	void mouseMoved(int screenX, int screenY, SceneNode2 currentNode, Vector3 intersection) {
		mouseMoveInfo.screenX = screenX;
		mouseMoveInfo.screenY = screenY;
		if (currentNode != null) {
			mouseMoveInfo.renderable = currentNode.getComponent(RenderableComponent.class);
			mouseMoveInfo.intersection.set(intersection);
		}

		EventService.post(scene.getInstanceId(), sceneMouseEvent);

		if (mouseOverNode != null) {
			if (currentNode == mouseOverNode) {
				EventService.post(mouseOverNode.getInstanceId(), mouseOverMoveEvent);
			} else {
				mouseMoveInfo.intersection.setZero();
				mouseMoveInfo.renderable = mouseOverNode.getComponent(RenderableComponent.class);
				EventService.post(mouseOverNode.getInstanceId(), mouseOverEndEvent);
				mouseOverNode = null;
			}
		}

		if (currentNode != null && currentNode != mouseOverNode) {
			mouseOverNode = currentNode;
			mouseMoveInfo.renderable = currentNode.getComponent(RenderableComponent.class);
			mouseMoveInfo.intersection.set(intersection);
			EventService.post(currentNode.getInstanceId(), mouseOverStartEvent);
		}

		mouseMoveInfo.reset();
	}

	void sceneDeactivated() {
		mouseOverNode = null;
	}

	private class SceneMouseEvent implements Event<SceneMouseListener> {
		@Override
		public Class<SceneMouseListener> getSubscriptionType() {
			return SceneMouseListener.class;
		}

		@Override
		public void dispatch(SceneMouseListener subscriber) {
			subscriber.mouseMoved(mouseMoveInfo);
		}
	}

	private class MouseOverStartEvent implements Event<NodeMouseOverListener> {
		@Override
		public Class<NodeMouseOverListener> getSubscriptionType() {
			return NodeMouseOverListener.class;
		}

		@Override
		public void dispatch(NodeMouseOverListener subscriber) {
			subscriber.onMouseOverStart(mouseMoveInfo);
		}
	}

	private class MouseOverMoveEvent implements Event<NodeMouseOverListener> {

		@Override
		public Class<NodeMouseOverListener> getSubscriptionType() {
			return NodeMouseOverListener.class;
		}

		@Override
		public void dispatch(NodeMouseOverListener subscriber) {
			subscriber.onMouseOverMove(mouseMoveInfo);
		}
	}

	private class MouseOverEndEvent implements Event<NodeMouseOverListener> {
		@Override
		public Class<NodeMouseOverListener> getSubscriptionType() {
			return NodeMouseOverListener.class;
		}

		@Override
		public void dispatch(NodeMouseOverListener subscriber) {
			subscriber.onMouseOverEnd(mouseMoveInfo);
		}
	}
}
