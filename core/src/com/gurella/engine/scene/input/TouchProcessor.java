package com.gurella.engine.scene.input;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.gurella.engine.event.Event;
import com.gurella.engine.event.EventService;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.scene.input.PointerTrack.PointerTrackerPhase;
import com.gurella.engine.scene.input.dnd.DragAndDropProcessor;
import com.gurella.engine.scene.renderable.RenderableComponent;
import com.gurella.engine.subscriptions.scene.input.NodeLongPressListener;
import com.gurella.engine.subscriptions.scene.input.NodeTapListener;
import com.gurella.engine.subscriptions.scene.input.SceneLongPressListener;
import com.gurella.engine.subscriptions.scene.input.SceneTapListener;
import com.gurella.engine.utils.IntLongMap;

public class TouchProcessor extends PointerProcessor {
	private final DragAndDropProcessor dragAndDropProcessor;

	private float tapSquareSize = 20;
	private long tapCountInterval = (long) (0.4f * 1000000000l);
	private float longPressSeconds = 0.8f;

	private final LongPressTaskPool pool = new LongPressTaskPool();
	private final IntMap<LongPressTask> tasks = new IntMap<LongPressTask>(10);
	private final IntIntMap validKeys = new IntIntMap(10);
	private final IntIntMap tapCounters = new IntIntMap(10);
	private final IntLongMap lastTapTimes = new IntLongMap(10);

	private final TapInfo tapInfo = new TapInfo();
	private final TapEvent tapEvent = new TapEvent();
	private final NodeTapEvent nodeTapEvent = new NodeTapEvent();

	private final LongPressInfo longPressInfo = new LongPressInfo();
	private final LongPressEvent longPressEvent = new LongPressEvent();
	private final NodeLongPressEvent nodeLongPressEvent = new NodeLongPressEvent();

	public TouchProcessor(Scene scene, DragAndDropProcessor dragAndDropProcessor) {
		super(scene);
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
		synchronized (pointerTrack) { // TODO remove sync
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
		tapInfo.set(pointer, button, pointerTrack.getScreenX(0), pointerTrack.getScreenY(0));
		tapInfo.count = tapCounters.get(key, 1);
		SceneNode2 node = pointerTrack.getCommonNode();
		if (node != null) {
			tapInfo.renderable = node.getComponent(RenderableComponent.class);
			pointerTrack.getIntersection(0, tapInfo.intersection);
		}

		EventService.post(scene.getInstanceId(), tapEvent);

		if (node != null) {
			EventService.post(node.getInstanceId(), nodeTapEvent);
		}

		tapInfo.reset();
	}

	private void dispatchLongPress(int pointer, int button, PointerTrack pointerTrack) {
		longPressInfo.set(pointer, button, pointerTrack.getScreenX(0), pointerTrack.getScreenY(0));
		SceneNode2 node = pointerTrack.getCommonNode();
		if (node != null) {
			longPressInfo.renderable = node.getComponent(RenderableComponent.class);
			pointerTrack.getIntersection(0, longPressInfo.intersection);
		}

		EventService.post(scene.getInstanceId(), longPressEvent);

		if (node != null) {
			EventService.post(node.getInstanceId(), nodeLongPressEvent);

			if (pointer == 0 && button == Buttons.LEFT && pointerTrack.getPhase() != PointerTrackerPhase.end) {
				dragAndDropProcessor.longPress(pointerTrack);
			}
		}

		longPressInfo.reset();
	}

	@Override
	public void sceneDeactivated() {
		super.sceneDeactivated();
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
		public synchronized void run() {
			if (isScheduled()) {
				endLongPress(pointer + button * 100, pointer, button, pointerTrack);
			}
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

	private class TapEvent implements Event<SceneTapListener> {
		@Override
		public void dispatch(SceneTapListener listener) {
			listener.onTap(tapInfo);
		}

		@Override
		public Class<SceneTapListener> getSubscriptionType() {
			return SceneTapListener.class;
		}
	}

	private class NodeTapEvent implements Event<NodeTapListener> {
		@Override
		public void dispatch(NodeTapListener listener) {
			listener.onTap(tapInfo);
		}

		@Override
		public Class<NodeTapListener> getSubscriptionType() {
			return NodeTapListener.class;
		}
	}

	private class LongPressEvent implements Event<SceneLongPressListener> {
		@Override
		public void dispatch(SceneLongPressListener listener) {
			listener.onLongPress(longPressInfo);
		}

		@Override
		public Class<SceneLongPressListener> getSubscriptionType() {
			return SceneLongPressListener.class;
		}
	}

	private class NodeLongPressEvent implements Event<NodeLongPressListener> {
		@Override
		public void dispatch(NodeLongPressListener listener) {
			listener.onLongPress(longPressInfo);
		}

		@Override
		public Class<NodeLongPressListener> getSubscriptionType() {
			return NodeLongPressListener.class;
		}
	}
}
