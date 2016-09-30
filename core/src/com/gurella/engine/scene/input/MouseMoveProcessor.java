package com.gurella.engine.scene.input;

import com.badlogic.gdx.math.Vector3;
import com.gurella.engine.event.Event;
import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.scene.renderable.RenderableComponent;
import com.gurella.engine.subscriptions.scene.input.IntersectionMouseOverListener;
import com.gurella.engine.subscriptions.scene.input.NodeMouseOverListener;
import com.gurella.engine.subscriptions.scene.input.SceneMouseListener;

class MouseMoveProcessor {
	private final Scene scene;

	private final SceneMouseEvent sceneMouseEvent = new SceneMouseEvent();
	private final IntersectionMouseOverStartEvent intersectionMouseOverStartEvent = new IntersectionMouseOverStartEvent();
	private final MouseOverStartEvent mouseOverStartEvent = new MouseOverStartEvent();
	private final IntersectionMouseOverMoveEvent intersectionMouseOverMoveEvent = new IntersectionMouseOverMoveEvent();
	private final MouseOverMoveEvent mouseOverMoveEvent = new MouseOverMoveEvent();
	private final IntersectionMouseOverEndEvent intersectionMouseOverEndEvent = new IntersectionMouseOverEndEvent();
	private final MouseOverEndEvent mouseOverEndEvent = new MouseOverEndEvent();

	private SceneNode2 mouseOverNode;

	MouseMoveProcessor(Scene scene) {
		this.scene = scene;
	}

	void mouseMoved(int screenX, int screenY, SceneNode2 pointerNode, Vector3 intersection) {
		sceneMouseEvent.screenX = screenX;
		sceneMouseEvent.screenY = screenY;
		EventService.post(scene.getInstanceId(), sceneMouseEvent);

		if (mouseOverNode != null) {
			RenderableComponent intersected = mouseOverNode.getComponent(RenderableComponent.class);
			if (pointerNode == mouseOverNode) {
				intersectionMouseOverMoveEvent.set(intersected, screenX, screenY, intersection);
				EventService.post(scene.getInstanceId(), intersectionMouseOverMoveEvent);
				intersectionMouseOverMoveEvent.reset();

				mouseOverMoveEvent.set(screenX, screenY, intersection);
				EventService.post(intersected.getNodeId(), mouseOverMoveEvent);
				mouseOverMoveEvent.reset();
			} else {
				intersectionMouseOverEndEvent.set(intersected, screenX, screenY);
				EventService.post(scene.getInstanceId(), intersectionMouseOverEndEvent);
				intersectionMouseOverEndEvent.reset();

				mouseOverEndEvent.set(screenX, screenY);
				EventService.post(intersected.getNodeId(), mouseOverEndEvent);

				mouseOverNode = null;
			}
		}

		if (pointerNode != null && pointerNode != mouseOverNode) {
			mouseOverNode = pointerNode;
			RenderableComponent intersected = pointerNode.getComponent(RenderableComponent.class);

			intersectionMouseOverStartEvent.set(intersected, screenX, screenY, intersection);
			EventService.post(scene.getInstanceId(), intersectionMouseOverStartEvent);
			intersectionMouseOverStartEvent.reset();

			mouseOverStartEvent.set(screenX, screenY, intersection);
			EventService.post(intersected.getNodeId(), mouseOverStartEvent);
			mouseOverStartEvent.reset();
		}
	}

	void reset() {
		mouseOverNode = null;
	}

	private static class SceneMouseEvent implements Event<SceneMouseListener> {
		int screenX;
		int screenY;

		@Override
		public Class<SceneMouseListener> getSubscriptionType() {
			return SceneMouseListener.class;
		}

		@Override
		public void dispatch(SceneMouseListener subscriber) {
			subscriber.mouseMoved(screenX, screenY);
		}
	}

	private static class IntersectionMouseOverStartEvent implements Event<IntersectionMouseOverListener> {
		RenderableComponent intersected;
		int screenX;
		int screenY;
		Vector3 intersection;

		void set(RenderableComponent intersected, int screenX, int screenY, Vector3 intersection) {
			this.intersected = intersected;
			this.screenX = screenX;
			this.screenY = screenY;
			this.intersection = intersection;
		}

		void reset() {
			this.intersected = null;
			this.intersection = null;
		}

		@Override
		public Class<IntersectionMouseOverListener> getSubscriptionType() {
			return IntersectionMouseOverListener.class;
		}

		@Override
		public void dispatch(IntersectionMouseOverListener subscriber) {
			subscriber.onMouseOverStart(intersected, screenX, screenY, intersection);
		}
	}

	private static class MouseOverStartEvent implements Event<NodeMouseOverListener> {
		int screenX;
		int screenY;
		Vector3 intersection;

		void set(int screenX, int screenY, Vector3 intersection) {
			this.screenX = screenX;
			this.screenY = screenY;
			this.intersection = intersection;
		}

		void reset() {
			this.intersection = null;
		}

		@Override
		public Class<NodeMouseOverListener> getSubscriptionType() {
			return NodeMouseOverListener.class;
		}

		@Override
		public void dispatch(NodeMouseOverListener subscriber) {
			subscriber.onMouseOverStart(screenX, screenY, intersection);
		}
	}

	private static class IntersectionMouseOverMoveEvent implements Event<IntersectionMouseOverListener> {
		RenderableComponent intersected;
		int screenX;
		int screenY;
		Vector3 intersection;

		void set(RenderableComponent intersected, int screenX, int screenY, Vector3 intersection) {
			this.intersected = intersected;
			this.screenX = screenX;
			this.screenY = screenY;
			this.intersection = intersection;
		}

		void reset() {
			this.intersected = null;
			this.intersection = null;
		}

		@Override
		public Class<IntersectionMouseOverListener> getSubscriptionType() {
			return IntersectionMouseOverListener.class;
		}

		@Override
		public void dispatch(IntersectionMouseOverListener subscriber) {
			subscriber.onMouseOverMove(intersected, screenX, screenY, intersection);
		}
	}

	private static class MouseOverMoveEvent implements Event<NodeMouseOverListener> {
		int screenX;
		int screenY;
		Vector3 intersection;

		void set(int screenX, int screenY, Vector3 intersection) {
			this.screenX = screenX;
			this.screenY = screenY;
			this.intersection = intersection;
		}

		void reset() {
			this.intersection = null;
		}

		@Override
		public Class<NodeMouseOverListener> getSubscriptionType() {
			return NodeMouseOverListener.class;
		}

		@Override
		public void dispatch(NodeMouseOverListener subscriber) {
			subscriber.onMouseOverMove(screenX, screenY, intersection);
		}
	}

	private static class IntersectionMouseOverEndEvent implements Event<IntersectionMouseOverListener> {
		RenderableComponent intersected;
		int screenX;
		int screenY;

		void set(RenderableComponent intersected, int screenX, int screenY) {
			this.intersected = intersected;
			this.screenX = screenX;
			this.screenY = screenY;
		}

		void reset() {
			this.intersected = null;
		}

		@Override
		public Class<IntersectionMouseOverListener> getSubscriptionType() {
			return IntersectionMouseOverListener.class;
		}

		@Override
		public void dispatch(IntersectionMouseOverListener subscriber) {
			subscriber.onMouseOverEnd(intersected, screenX, screenY);
		}
	}

	private static class MouseOverEndEvent implements Event<NodeMouseOverListener> {
		int screenX;
		int screenY;

		void set(int screenX, int screenY) {
			this.screenX = screenX;
			this.screenY = screenY;
		}

		@Override
		public Class<NodeMouseOverListener> getSubscriptionType() {
			return NodeMouseOverListener.class;
		}

		@Override
		public void dispatch(NodeMouseOverListener subscriber) {
			subscriber.onMouseOverEnd(screenX, screenY);
		}
	}
}
