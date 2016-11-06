package com.gurella.engine.scene.input;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.utils.IntIntMap;
import com.gurella.engine.event.Event;
import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.scene.renderable.RenderableComponent;
import com.gurella.engine.subscriptions.scene.input.NodeDoubleTouchListener;
import com.gurella.engine.subscriptions.scene.input.SceneDoubleTouchListener;
import com.gurella.engine.utils.IntLongMap;

public class DoubleTouchProcessor implements PointerActivityListener {
	private final Scene scene;
	private DragAndDropProcessor dragAndDropProcessor;

	private float tapSquareSize = 20;
	private long maxDoubleClickDelay = (long) (0.4f * 1000000000l);

	private final IntLongMap startTimes = new IntLongMap(10);
	private final IntIntMap startScreenX = new IntIntMap(10);
	private final IntIntMap startScreenY = new IntIntMap(10);

	private final DoubleTouchInfo doubleTouchInfo = new DoubleTouchInfo();

	private final SceneDoubleTouchDownEvent sceneDoubleTouchDownEvent = new SceneDoubleTouchDownEvent();
	private final NodeDoubleTouchDownEvent nodeDoubleTouchDownEvent = new NodeDoubleTouchDownEvent();

	public DoubleTouchProcessor(Scene scene, DragAndDropProcessor dragAndDropProcessor) {
		this.scene = scene;
		this.dragAndDropProcessor = dragAndDropProcessor;
	}

	@Override
	public void onPointerActivity(int pointer, int button, PointerTrack pointerTrack) {
		int key = pointer + button * 100;
		switch (pointerTrack.getPhase()) {
		case begin:
			begin(key, pointer, button, pointerTrack);
			break;
		case move:
			changed(key, pointerTrack);
			break;
		case end:
			changed(key, pointerTrack);
			break;
		default:
			break;
		}
	}

	private void begin(int key, int pointer, int button, PointerTrack pointerTrack) {
		long currentTime = pointerTrack.getTime(0);
		int screenX = pointerTrack.getScreenX(0);
		int screenY = pointerTrack.getScreenY(0);

		long startTime = startTimes.remove(key, 0);
		if (startTime > 0) {
			int centerX = startScreenX.remove(key, Integer.MAX_VALUE);
			int centerY = startScreenY.remove(key, Integer.MAX_VALUE);
			if (currentTime - startTime <= maxDoubleClickDelay
					&& isWithinTapSquare(screenX, screenY, centerX, centerY)) {
				dispatchDoubleTap(pointer, button, screenX, screenY, pointerTrack);
				return;
			}
		}

		startTimes.put(key, currentTime);
		startScreenX.put(key, screenX);
		startScreenY.put(key, screenY);
	}

	private void changed(int key, PointerTrack pointerTrack) {
		if (startTimes.get(key, 0) > 0) {
			if (pointerTrack.getTimeSpan() > maxDoubleClickDelay || !isWithinTapSquare(pointerTrack)) {
				startTimes.remove(key, 0);
			}
		}
	}

	private boolean isWithinTapSquare(PointerTrack pointerTrack) {
		int last = pointerTrack.getSize() - 1;
		int screenX = pointerTrack.getScreenX(last);
		int screenY = pointerTrack.getScreenY(last);
		int centerX = pointerTrack.getScreenX(0);
		int centerY = pointerTrack.getScreenY(0);
		return isWithinTapSquare(screenX, screenY, centerX, centerY);
	}

	private boolean isWithinTapSquare(int screenX, int screenY, int centerX, int centerY) {
		return Math.abs(screenX - centerX) < tapSquareSize && Math.abs(screenY - centerY) < tapSquareSize;
	}

	private void dispatchDoubleTap(int pointer, int button, int screenX, int screenY, PointerTrack pointerTrack) {
		doubleTouchInfo.set(pointer, button, screenX, screenY);
		SceneNode2 node = pointerTrack.getCommonNode();
		if (node != null) {
			doubleTouchInfo.renderable = node.getComponent(RenderableComponent.class);
			pointerTrack.getIntersection(0, doubleTouchInfo.intersection);
		}

		EventService.post(scene.getInstanceId(), sceneDoubleTouchDownEvent);

		if (node != null) {
			EventService.post(node.getInstanceId(), nodeDoubleTouchDownEvent);

			if (pointer == 0 && button == Buttons.LEFT) {
				dragAndDropProcessor.doubleTouch(pointerTrack);
			}
		}
	}

	@Override
	public void reset() {
		startTimes.clear();
		startScreenX.clear();
		startScreenY.clear();
	}

	private class SceneDoubleTouchDownEvent implements Event<SceneDoubleTouchListener> {
		@Override
		public Class<SceneDoubleTouchListener> getSubscriptionType() {
			return SceneDoubleTouchListener.class;
		}

		@Override
		public void dispatch(SceneDoubleTouchListener subscriber) {
			subscriber.doubleTouchDown(doubleTouchInfo);
		}
	}

	private class NodeDoubleTouchDownEvent implements Event<NodeDoubleTouchListener> {
		@Override
		public Class<NodeDoubleTouchListener> getSubscriptionType() {
			return NodeDoubleTouchListener.class;
		}

		@Override
		public void dispatch(NodeDoubleTouchListener subscriber) {
			subscriber.onDoubleTouch(doubleTouchInfo);
		}
	}
}
