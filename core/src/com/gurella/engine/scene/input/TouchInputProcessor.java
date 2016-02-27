package com.gurella.engine.scene.input;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.gurella.engine.event.EventService;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.scene.SceneNode;
import com.gurella.engine.scene.behaviour.BehaviourComponent;
import com.gurella.engine.scene.input.PointerTrack.PointerTrackerPhase;
import com.gurella.engine.scene.renderable.RenderableComponent;
import com.gurella.engine.subscriptions.scene.input.GlobalLongPressListener;
import com.gurella.engine.subscriptions.scene.input.IntersectionLongPressListener;
import com.gurella.engine.subscriptions.scene.input.ObjectLongPressListener;
import com.gurella.engine.utils.IntLongMap;
import com.gurella.engine.utils.Values;

public class TouchInputProcessor implements PointerActivityListener {
	private float tapSquareSize = 20;
	private long tapCountInterval = (long) (0.4f * 1000000000l);
	private float longPressSeconds = 0.8f;

	private final LongPressTaskPool pool = new LongPressTaskPool();
	private final IntMap<LongPressTask> tasks = new IntMap<LongPressTask>(10);
	private final IntIntMap validKeys = new IntIntMap(10);
	private final IntIntMap tapCounters = new IntIntMap(10);
	private final IntLongMap lastTapTimes = new IntLongMap(10);

	private final TouchEvent touchEvent = new TouchEvent();
	private final IntersectionTouchEvent intersectionTouchEvent = new IntersectionTouchEvent();

	private Array<Object> tempListeners;
	private DragAndDropProcessor dragAndDropProcessor;

	public TouchInputProcessor(Array<Object> tempListeners, DragAndDropProcessor dragAndDropProcessor) {
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
			move(key, pointerTrack);
			break;
		case end:
			end(key, pointer, button, pointerTrack);
			break;
		default:
			break;
		}
	}

	private void begin(int key, int pointer, int button, PointerTrack pointerTrack) {
		validKeys.put(key, 1);
		LongPressTask task = tasks.get(key);
		if (task == null) {
			task = pool.obtain();
			tasks.put(key, task);
		} else {
			task.cancel();
		}

		task.pointer = pointer;
		task.button = button;
		task.pointerTrack = pointerTrack;
		Timer.schedule(task, longPressSeconds);
	}

	private void move(int key, PointerTrack pointerTrack) {
		synchronized (pointerTrack) {
			int validKey = validKeys.get(key, -1);
			if (validKey == 1) {
				if (!isWithinTapSquare(pointerTrack)) {
					removeEntry(key);
				}
			}
		}
	}

	private void removeEntry(int key) {
		validKeys.remove(key, -1);
		LongPressTask task = tasks.remove(key);
		if (task != null) {
			task.cancel();
			PoolService.free(task);
		}
	}

	private boolean isWithinTapSquare(PointerTrack pointerTrack) {
		int last = pointerTrack.getSize() - 1;
		int x = pointerTrack.getScreenX(last);
		int y = pointerTrack.getScreenY(last);
		int centerX = pointerTrack.getScreenX(0);
		int centerY = pointerTrack.getScreenY(0);
		return Math.abs(x - centerX) < tapSquareSize && Math.abs(y - centerY) < tapSquareSize;
	}

	private void end(int key, int pointer, int button, PointerTrack pointerTrack) {
		synchronized (pointerTrack) {
			int validKey = validKeys.remove(key, -1);
			if (validKey == 1) {
				removeEntry(key);
				if (!isWithinTapSquare(pointerTrack)) {
					return;
				}

				long activityEndTime = pointerTrack.getTime(pointerTrack.getSize() - 1) - lastTapTimes.get(key, -1);
				int tapCount = activityEndTime > tapCountInterval ? 0 : tapCounters.get(key, 0);
				tapCounters.put(key, tapCount + 1);
				lastTapTimes.put(key, pointerTrack.getTime(pointerTrack.getSize() - 1));

				float timeSpanSeconds = pointerTrack.getTimeSpan() / 1000000000f;
				if (timeSpanSeconds < longPressSeconds) {
					dispatchTap(key, pointer, button, pointerTrack);
				} else {
					dispatchLongPress(pointer, button, pointerTrack);
				}
			}
		}
	}

	private void endLongPress(int key, int pointer, int button, PointerTrack pointerTrack) {
		synchronized (pointerTrack) {
			int validKey = validKeys.remove(key, -1);
			if (validKey == 1) {
				removeEntry(key);
				if (isWithinTapSquare(pointerTrack)) {
					dispatchLongPress(pointer, button, pointerTrack);
				}
			}
		}
	}

	private void dispatchTap(int key, int pointer, int button, PointerTrack pointerTrack) {
		int screenX = pointerTrack.getScreenX(0), screenY = pointerTrack.getScreenY(0);
		int tapCount = tapCounters.get(key, 1);
		touchEvent.set(pointer, button, screenX, screenY);
		for (BehaviourComponent behaviourComponent : inputSystem.getListeners(tap)) {
			behaviourComponent.tap(touchEvent, tapCount);
		}

		SceneNode node = pointerTrack.getCommonNode();
		if (node != null) {
			intersectionTouchEvent.set(pointer, button, screenX, screenY, pointerTrack, 0);
			RenderableComponent renderableComponent = node.getComponent(RenderableComponent.class);
			for (BehaviourComponent behaviourComponent : inputSystem.getListeners(onTapGlobal)) {
				behaviourComponent.onTap(renderableComponent, intersectionTouchEvent, tapCount);
			}
			for (BehaviourComponent behaviourComponent : inputSystem.getListeners(node, onTap)) {
				behaviourComponent.onTap(intersectionTouchEvent, tapCount);
			}
		}
	}

	private void dispatchLongPress(int pointer, int button, PointerTrack pointerTrack) {
		int screenX = pointerTrack.getScreenX(0), screenY = pointerTrack.getScreenY(0);
		touchEvent.set(pointer, button, screenX, screenY);
		Array<GlobalLongPressListener> globalListeners = Values.cast(tempListeners);
		EventService.getSubscribers(GlobalLongPressListener.class, globalListeners);
		for (int i = 0; i < globalListeners.size; i++) {
			globalListeners.get(i).longPress(touchEvent);
		}

		SceneNode node = pointerTrack.getCommonNode();
		if (node != null) {
			intersectionTouchEvent.set(pointer, button, screenX, screenY, pointerTrack, 0);
			RenderableComponent renderableComponent = node.getComponent(RenderableComponent.class);
			Array<IntersectionLongPressListener> intersectionListeners = Values.cast(tempListeners);
			EventService.getSubscribers(IntersectionLongPressListener.class, intersectionListeners);
			for (int i = 0; i < intersectionListeners.size; i++) {
				intersectionListeners.get(i).onLongPress(renderableComponent, intersectionTouchEvent);
			}

			Array<ObjectLongPressListener> listeners = Values.cast(tempListeners);
			EventService.getSubscribers(ObjectLongPressListener.class, listeners);
			for (int i = 0; i < listeners.size; i++) {
				listeners.get(i).onLongPress(intersectionTouchEvent);
			}

			if (pointer == 0 && button == Buttons.LEFT && pointerTrack.getPhase() != PointerTrackerPhase.end) {
				dragAndDropProcessor.longPress(pointerTrack);
			}
		}
	}

	@Override
	public void reset() {
		for (LongPressTask longPressTask : tasks.values()) {
			pool.free(longPressTask);
		}
		tasks.clear();
		validKeys.clear();
		tapCounters.clear();
		lastTapTimes.clear();
	}

	private class LongPressTask extends Task implements Poolable {
		private int pointer;
		private int button;
		private PointerTrack pointerTrack;

		@Override
		public void run() {
			endLongPress(pointer + button * 100, pointer, button, pointerTrack);
			reset();
		}

		@Override
		public synchronized void cancel() {
			super.cancel();
			reset();
		}

		@Override
		public void reset() {
			pointer = -1;
			button = -1;
			pointerTrack = null;
		}
	}

	private class LongPressTaskPool extends Pool<LongPressTask> {
		@Override
		protected LongPressTask newObject() {
			return new LongPressTask();
		}
	}
}
