package com.gurella.engine.scene.input;

import static com.gurella.engine.scene.input.PointerTrack.PointerTrackerPhase.begin;
import static com.gurella.engine.scene.input.PointerTrack.PointerTrackerPhase.end;
import static com.gurella.engine.scene.input.PointerTrack.PointerTrackerPhase.move;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputEventQueue;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntMap.Entries;
import com.badlogic.gdx.utils.IntMap.Entry;
import com.badlogic.gdx.utils.Predicate;
import com.gurella.engine.event.Event;
import com.gurella.engine.event.EventService;
import com.gurella.engine.event.Signal;
import com.gurella.engine.input.InputService;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.scene.SceneService2;
import com.gurella.engine.scene.camera.CameraComponent;
import com.gurella.engine.scene.camera.CameraComponent.ReverseOrdinalComparator;
import com.gurella.engine.scene.input.dnd.DragAndDropProcessor;
import com.gurella.engine.scene.renderable.Layer;
import com.gurella.engine.scene.renderable.LayerMask;
import com.gurella.engine.scene.renderable.RenderableComponent;
import com.gurella.engine.scene.spatial.Spatial;
import com.gurella.engine.scene.spatial.SpatialSystem;
import com.gurella.engine.subscriptions.scene.ComponentActivityListener;
import com.gurella.engine.subscriptions.scene.input.NodeScrollListener;
import com.gurella.engine.subscriptions.scene.input.NodeTouchListener;
import com.gurella.engine.subscriptions.scene.input.SceneKeyListener;
import com.gurella.engine.subscriptions.scene.input.SceneKeyTypedListener;
import com.gurella.engine.subscriptions.scene.input.SceneScrollListener;
import com.gurella.engine.subscriptions.scene.input.SceneTouchDraggedListener;
import com.gurella.engine.subscriptions.scene.input.SceneTouchListener;
import com.gurella.engine.subscriptions.scene.update.InputUpdateListener;
import com.gurella.engine.utils.Values;

public class InputSystem extends SceneService2 implements ComponentActivityListener, InputUpdateListener {
	private final Array<CameraComponent<?>> cameras = new Array<CameraComponent<?>>();

	private SpatialSystem<?> spatialSystem;

	private transient final InputProcessorDelegate delegate = new InputProcessorDelegate();
	private transient final InputAdapter dummyDelegate = new InputAdapter();
	private transient final InputEventQueue inputQueue = new InputEventQueue(delegate);

	private transient final IntMap<PointerTrack> trackers = new IntMap<PointerTrack>();
	private transient final PointerActivitySignal pointerActivitySignal = new PointerActivitySignal();

	private transient final Array<Object> tempListeners = new Array<Object>(64);

	private transient final MouseMoveProcessor mouseMoveProcessor;
	private transient final DragProcessor dragProcessor;
	private transient final DragAndDropProcessor dragAndDropProcessor;
	private transient final TouchProcessor touchProcessor;
	private transient final DoubleTouchProcessor doubleTouchProcessor;

	public byte inputActionsFrequency = 10;// TODO limit mouse moves;
	private transient long lastActionHandled;

	public InputSystem(Scene scene) {
		super(scene);
		mouseMoveProcessor = new MouseMoveProcessor(scene);
		dragProcessor = new DragProcessor(scene);
		dragAndDropProcessor = new DragAndDropProcessor();
		touchProcessor = new TouchProcessor(scene, dragAndDropProcessor);
		doubleTouchProcessor = new DoubleTouchProcessor(scene, dragAndDropProcessor);

		pointerActivitySignal.addListener(dragAndDropProcessor);
		pointerActivitySignal.addListener(touchProcessor);
		pointerActivitySignal.addListener(doubleTouchProcessor);
		pointerActivitySignal.addListener(dragProcessor);
	}

	@Override
	protected void serviceActivated() {
		spatialSystem = scene.spatialSystem;
		InputService.addInputProcessor(inputQueue);
	}

	@Override
	protected void serviceDeactivated() {
		InputService.removeInputProcessor(inputQueue);
		// TODO update listeners and finish actions
		inputQueue.setProcessor(dummyDelegate);
		inputQueue.drain();
		inputQueue.setProcessor(delegate);

		delegate.reset();
		pointerActivitySignal.reset();
		mouseMoveProcessor.reset();

		spatialSystem = null;
	}

	@Override
	public void onInputUpdate() {
		inputQueue.drain();
		delegate.clean();
	}

	@Override
	public void componentActivated(SceneNodeComponent2 component) {
		if (component instanceof CameraComponent) {
			cameras.add((CameraComponent<?>) component);
			cameras.sort(ReverseOrdinalComparator.instance);
		}
	}

	@Override
	public void componentDeactivated(SceneNodeComponent2 component) {
		if (component instanceof CameraComponent) {
			cameras.removeValue((CameraComponent<?>) component, true);
		}
	}

	public PickResult pickNode(PickResult out, float screenX, float screenY) {
		out.reset();
		for (int i = 0, n = cameras.size; i < n; i++) {
			pickNode(out, screenX, screenY, cameras.get(i).camera, null);
			if (out.node != null) {
				return out;
			}
		}

		out.reset();
		return out;
	}

	public PickResult pickNodeExcludeLayers(PickResult out, float screenX, float screenY, Layer... excludedLayers) {
		out.reset();
		layerMask.reset();

		for (int i = 0, n = excludedLayers.length; i < n; i++) {
			layerMask.ignored(excludedLayers[i]);
		}

		for (int i = 0, n = cameras.size; i < n; i++) {
			pickNode(out, screenX, screenY, cameras.get(i).camera, layerMask);
			if (out.node != null) {
				return out;
			}
		}

		out.reset();
		return out;
	}

	public PickResult pickNodeIncludeLayers(float screenX, float screenY, Layer... includedLayers) {
		return pickNodeIncludeLayers(new PickResult(), screenX, screenY, includedLayers);
	}

	public PickResult pickNodeIncludeLayers(PickResult out, float screenX, float screenY, Layer... includedLayers) {
		out.reset();
		layerMask.reset();

		for (int i = 0, n = includedLayers.length; i < n; i++) {
			layerMask.allowed(includedLayers[i]);
		}

		for (int i = 0, n = cameras.size; i < n; i++) {
			pickNode(out, screenX, screenY, cameras.get(i).camera, layerMask);
			if (out.node != null) {
				return out;
			}
		}

		out.reset();
		return out;
	}

	public PickResult pickNode(PickResult out, float screenX, float screenY, CameraComponent<?> cameraComponent) {
		pickNode(out, screenX, screenY, cameraComponent.camera, null);
		if (out.node == null) {
			out.reset();
		}
		return out;
	}

	private final Array<Spatial> spatials = new Array<Spatial>();
	private final Vector3 intersection = new Vector3();
	private final Vector3 closestIntersection = new Vector3();
	private final PickResult pickResult = new PickResult();
	private final LayerMask layerMask = new LayerMask();

	public PickResult pickNode(PickResult out, float screenX, float screenY, Camera camera,
			Predicate<RenderableComponent> predicate) {
		Vector3 cameraPosition = camera.position;
		Spatial closestSpatial = null;
		closestIntersection.set(Float.NaN, Float.NaN, Float.NaN);
		float closestDistance = Float.MAX_VALUE;

		Ray pickRay = camera.getPickRay(screenX, screenY);
		layerMask.reset();
		spatialSystem.getSpatials(pickRay, spatials, predicate);

		for (int i = 0; i < spatials.size; i++) {
			Spatial spatial = spatials.get(i);
			RenderableComponent renderableComponent = spatial.renderableComponent;
			if (renderableComponent.getIntersection(pickRay, intersection)) {
				float distance = intersection.dst2(cameraPosition);
				if (closestDistance > distance) {
					closestDistance = distance;
					closestSpatial = spatial;
					closestIntersection.set(intersection);
					// TODO Z order of sprites
				}
			}
		}

		spatials.clear();
		if (closestSpatial != null) {
			out.node = closestSpatial.renderableComponent.getNode();
			out.intersection.set(closestIntersection);
		}

		return out;
	}

	private PointerTrack createTracker(int pointer, int button) {
		int key = pointer + button * 100;
		PointerTrack pointerTrack = trackers.get(key);
		if (pointerTrack == null) {
			pointerTrack = PoolService.obtain(PointerTrack.class);
			trackers.put(key, pointerTrack);
		} else {
			pointerTrack.reset();
		}

		return pointerTrack;
	}

	public PointerTrack getTracker(int pointer, int button) {
		int key = pointer + button * 100;
		return trackers.get(key);
	}

	private class InputProcessorDelegate implements InputProcessor {
		private final TouchInfo touchInfo = new TouchInfo();
		private final SceneTouchDownEvent sceneTouchDownEvent = new SceneTouchDownEvent();
		private final SceneTouchUpEvent sceneTouchUpEvent = new SceneTouchUpEvent();
		private final NodeTouchDownEvent nodeTouchDownEvent = new NodeTouchDownEvent();
		private final NodeTouchUpEvent nodeTouchUpEvent = new NodeTouchUpEvent();

		private final ScrollInfo scrollInfo = new ScrollInfo();
		private final SceneScrollEvent sceneScrollEvent = new SceneScrollEvent();
		private final NodeScrollEvent nodeScrollEvent = new NodeScrollEvent();

		@Override
		public boolean keyDown(int keycode) {
			Array<SceneKeyListener> listeners = Values.cast(tempListeners);
			EventService.getSubscribers(SceneKeyListener.class, listeners);
			for (int i = 0; i < listeners.size; i++) {
				listeners.get(i).keyDown(keycode);
			}
			return false;
		}

		@Override
		public boolean keyUp(int keycode) {
			Array<SceneKeyListener> listeners = Values.cast(tempListeners);
			EventService.getSubscribers(SceneKeyListener.class, listeners);
			for (int i = 0; i < listeners.size; i++) {
				listeners.get(i).keyUp(keycode);
			}
			return false;
		}

		@Override
		public boolean keyTyped(char character) {
			Array<SceneKeyTypedListener> listeners = Values.cast(tempListeners);
			EventService.getSubscribers(SceneKeyTypedListener.class, listeners);
			for (int i = 0; i < listeners.size; i++) {
				listeners.get(i).keyTyped(character);
			}
			return false;
		}

		@Override
		public boolean mouseMoved(int screenX, int screenY) {
			SceneNode2 node = pickNodeExcludeLayers(pickResult, screenX, screenY, Layer.DnD).node;
			mouseMoveProcessor.mouseMoved(screenX, screenY, node, closestIntersection);
			return false;
		}

		@Override
		public boolean scrolled(int amount) {
			int screenX = Gdx.input.getX();
			int screenY = Gdx.input.getY();
			scrollInfo.set(screenX, screenY);
			scrollInfo.amount = amount;
			SceneNode2 node = pickNodeExcludeLayers(pickResult, screenX, screenY, Layer.DnD).node;
			if (node != null) {
				scrollInfo.renderable = node.getComponent(RenderableComponent.class);
				scrollInfo.intersection.set(closestIntersection);
			}

			EventService.post(scene.getInstanceId(), sceneScrollEvent);

			if (node != null) {
				EventService.post(node.getInstanceId(), nodeScrollEvent);
			}

			scrollInfo.reset();
			return false;
		}

		@Override
		public boolean touchDown(int screenX, int screenY, int pointer, int button) {
			PointerTrack tracker = createTracker(pointer, button);
			long eventTime = inputQueue.getCurrentEventTime();
			SceneNode2 node = pickNodeExcludeLayers(pickResult, screenX, screenY, Layer.DnD).node;
			tracker.add(eventTime, screenX, screenY, closestIntersection, node, begin);

			touchInfo.set(pointer, button, screenX, screenY);
			if (node != null) {
				touchInfo.renderable = node.getComponent(RenderableComponent.class);
				touchInfo.intersection.set(closestIntersection);
			}

			EventService.post(scene.getInstanceId(), sceneTouchDownEvent);

			if (node != null) {
				EventService.post(node.getInstanceId(), nodeTouchDownEvent);
			}

			pointerActivitySignal.onPointerActivity(pointer, button, tracker);
			touchInfo.reset();

			return false;
		}

		@Override
		public boolean touchUp(int screenX, int screenY, int pointer, int button) {
			touchInfo.set(pointer, button, screenX, screenY);
			SceneNode2 node = pickNodeExcludeLayers(pickResult, screenX, screenY, Layer.DnD).node;
			if (node != null) {
				touchInfo.renderable = node.getComponent(RenderableComponent.class);
				touchInfo.intersection.set(closestIntersection);
			}

			EventService.post(scene.getInstanceId(), sceneTouchUpEvent);

			if (node != null) {
				EventService.post(node.getInstanceId(), nodeTouchUpEvent);
			}

			PointerTrack tracker = getTracker(pointer, button);
			if (tracker != null) {
				long eventTime = inputQueue.getCurrentEventTime();
				tracker.add(eventTime, screenX, screenY, closestIntersection, node, end);
				pointerActivitySignal.onPointerActivity(pointer, button, tracker);
			}

			return false;
		}

		@Override
		public boolean touchDragged(int screenX, int screenY, int pointer) {
			SceneNode2 node = pickNodeExcludeLayers(pickResult, screenX, screenY, Layer.DnD).node;
			touchInfo.set(pointer, -1, screenX, screenY);

			for (int button = 0; button < 3; button++) {
				if (Gdx.input.isButtonPressed(button)) {
					touchInfo.set(pointer, button, screenX, screenY);
					Array<SceneTouchDraggedListener> listeners = Values.cast(tempListeners);
					EventService.getSubscribers(SceneTouchDraggedListener.class, listeners);
					for (int i = 0; i < listeners.size; i++) {
						listeners.get(i).touchDragged(touchInfo);
					}

					PointerTrack tracker = getTracker(pointer, button);
					if (tracker != null) {
						long eventTime = inputQueue.getCurrentEventTime();
						tracker.add(eventTime, screenX, screenY, closestIntersection, node, move);
						pointerActivitySignal.onPointerActivity(pointer, button, tracker);
					}
				}
			}

			return false;
		}

		private void reset() {
			for (PointerTrack pointerTrack : trackers.values()) {
				PoolService.free(pointerTrack);
			}
			trackers.clear();
		}

		private void clean() {
			Entries<PointerTrack> entries = trackers.entries();
			while (entries.hasNext()) {
				Entry<PointerTrack> entry = entries.next();
				PointerTrack pointerTrack = entry.value;
				if (end == pointerTrack.getPhase()) {
					PoolService.free(pointerTrack);
					entries.remove();
				}
			}
		}

		private class SceneTouchDownEvent implements Event<SceneTouchListener> {
			@Override
			public Class<SceneTouchListener> getSubscriptionType() {
				return SceneTouchListener.class;
			}

			@Override
			public void dispatch(SceneTouchListener subscriber) {
				subscriber.onTouchDown(touchInfo);
			}
		}

		private class SceneTouchUpEvent implements Event<SceneTouchListener> {
			@Override
			public Class<SceneTouchListener> getSubscriptionType() {
				return SceneTouchListener.class;
			}

			@Override
			public void dispatch(SceneTouchListener subscriber) {
				subscriber.onTouchUp(touchInfo);
			}
		}

		private class NodeTouchDownEvent implements Event<NodeTouchListener> {
			@Override
			public Class<NodeTouchListener> getSubscriptionType() {
				return NodeTouchListener.class;
			}

			@Override
			public void dispatch(NodeTouchListener subscriber) {
				subscriber.onTouchDown(touchInfo);
			}
		}

		private class NodeTouchUpEvent implements Event<NodeTouchListener> {
			@Override
			public Class<NodeTouchListener> getSubscriptionType() {
				return NodeTouchListener.class;
			}

			@Override
			public void dispatch(NodeTouchListener subscriber) {
				subscriber.onTouchUp(touchInfo);
			}
		}

		private class SceneScrollEvent implements Event<SceneScrollListener> {
			@Override
			public Class<SceneScrollListener> getSubscriptionType() {
				return SceneScrollListener.class;
			}

			@Override
			public void dispatch(SceneScrollListener subscriber) {
				subscriber.onScrolled(scrollInfo);
			}
		}

		private class NodeScrollEvent implements Event<NodeScrollListener> {
			@Override
			public Class<NodeScrollListener> getSubscriptionType() {
				return NodeScrollListener.class;
			}

			@Override
			public void dispatch(NodeScrollListener subscriber) {
				subscriber.onScrolled(scrollInfo);
			}
		}
	}

	public static class PointerActivitySignal extends Signal<PointerActivityListener> {
		public void onPointerActivity(int pointer, int button, PointerTrack pointerTrack) {
			Object[] items = listeners.begin();
			for (int i = 0, n = listeners.size; i < n; i++) {
				((PointerActivityListener) items[i]).onPointerActivity(pointer, button, pointerTrack);
			}
			listeners.end();
		}

		private void reset() {
			Object[] items = listeners.begin();
			for (int i = 0, n = listeners.size; i < n; i++) {
				((PointerActivityListener) items[i]).reset();
			}
			listeners.end();
		}
	}
}
