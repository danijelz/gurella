package com.gurella.engine.scene.input;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntIntMap;
import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.SceneNode;
import com.gurella.engine.scene.renderable.RenderableComponent;
import com.gurella.engine.subscriptions.scene.input.GlobalDoubleTouchDownListener;
import com.gurella.engine.subscriptions.scene.input.IntersectionDoubleTouchDownListener;
import com.gurella.engine.subscriptions.scene.input.ObjectDoubleTouchDownListener;
import com.gurella.engine.utils.IntLongMap;
import com.gurella.engine.utils.Values;

public class DoubleTouchInputProcessor implements PointerActivityListener {
	private float tapSquareSize = 20;
	private long maxDoubleClickDelay = (long) (0.4f * 1000000000l);

	private final IntLongMap startTimes = new IntLongMap(10);
	private final IntIntMap startScreenX = new IntIntMap(10);
	private final IntIntMap startScreenY = new IntIntMap(10);

	private final TouchEvent touchEvent = new TouchEvent();
	private final IntersectionTouchEvent intersectionTouchEvent = new IntersectionTouchEvent();

	private Array<Object> tempListeners;
	private DragAndDropProcessor dragAndDropProcessor;

	public DoubleTouchInputProcessor(Array<Object> tempListeners, DragAndDropProcessor dragAndDropProcessor) {
		this.tempListeners = tempListeners;
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
		touchEvent.set(pointer, button, screenX, screenY);
		Array<GlobalDoubleTouchDownListener> globalListeners = Values.cast(tempListeners);
		EventService.getSubscribers(GlobalDoubleTouchDownListener.class, globalListeners);
		for (int i = 0; i < globalListeners.size; i++) {
			globalListeners.get(i).doubleTouchDown(touchEvent);
		}

		SceneNode node = pointerTrack.getCommonNode();
		if (node != null) {
			intersectionTouchEvent.set(pointer, button, screenX, screenY, pointerTrack, 0);
			RenderableComponent renderableComponent = node.getComponent(RenderableComponent.class);
			Array<IntersectionDoubleTouchDownListener> intersectionListeners = Values.cast(tempListeners);
			EventService.getSubscribers(IntersectionDoubleTouchDownListener.class, intersectionListeners);
			for (int i = 0; i < intersectionListeners.size; i++) {
				intersectionListeners.get(i).onDoubleTouch(renderableComponent, intersectionTouchEvent);
			}

			Array<ObjectDoubleTouchDownListener> listeners = Values.cast(tempListeners);
			EventService.getSubscribers(renderableComponent.getNodeId(), ObjectDoubleTouchDownListener.class,
					listeners);
			for (int i = 0; i < listeners.size; i++) {
				listeners.get(i).onDoubleTouch(intersectionTouchEvent);
			}

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
}
