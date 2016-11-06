package com.gurella.engine.scene.input;

import static com.gurella.engine.scene.input.PointerTrack.PointerTrackerPhase.begin;
import static com.gurella.engine.scene.input.PointerTrack.PointerTrackerPhase.end;
import static com.gurella.engine.scene.input.PointerTrack.PointerTrackerPhase.move;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputEventQueue;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntMap.Entries;
import com.badlogic.gdx.utils.IntMap.Entry;
import com.badlogic.gdx.utils.Predicate;
import com.gurella.engine.event.EventService;
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
import com.gurella.engine.scene.renderable.RenderableIntersector;
import com.gurella.engine.scene.spatial.Spatial;
import com.gurella.engine.scene.spatial.SpatialSystem;
import com.gurella.engine.subscriptions.scene.ComponentActivityListener;
import com.gurella.engine.subscriptions.scene.update.InputUpdateListener;

public class InputSystem extends SceneService2 implements ComponentActivityListener, InputUpdateListener {
	private final Array<CameraComponent<?>> cameras = new Array<CameraComponent<?>>();

	private SpatialSystem<?> spatialSystem;

	private transient final InputProcessorDelegate delegate;
	private transient final InputAdapter dummyDelegate;
	private transient final InputEventQueue inputQueue;

	private transient final DragProcessor dragProcessor;
	private transient final DragAndDropProcessor dragAndDropProcessor;
	private transient final TouchProcessor touchProcessor;
	private transient final DoubleTouchProcessor doubleTouchProcessor;

	private final RenderableIntersector intersector = new RenderableIntersector();
	private final Array<Spatial> spatials = new Array<Spatial>(128);

	public byte inputActionsFrequency = 10;// TODO limit mouse moves;
	private transient long lastActionHandled;

	public InputSystem(Scene scene) {
		super(scene);

		delegate = new InputProcessorDelegate(this);
		dummyDelegate = new InputAdapter();
		inputQueue = new InputEventQueue(delegate);

		dragProcessor = new DragProcessor(scene);
		dragAndDropProcessor = new DragAndDropProcessor(scene);
		touchProcessor = new TouchProcessor(scene, dragAndDropProcessor);
		doubleTouchProcessor = new DoubleTouchProcessor(scene, dragAndDropProcessor);
	}

	@Override
	protected void serviceActivated() {
		spatialSystem = scene.spatialSystem;
		InputService.addInputProcessor(inputQueue);

		dragProcessor.sceneActivated();
		dragAndDropProcessor.sceneActivated();
		touchProcessor.sceneActivated();
		doubleTouchProcessor.sceneActivated();
	}

	@Override
	protected void serviceDeactivated() {
		InputService.removeInputProcessor(inputQueue);
		inputQueue.setProcessor(dummyDelegate);
		inputQueue.drain();
		inputQueue.setProcessor(delegate);

		// TODO update listeners and finish actions
		delegate.sceneDeactivated();
		dragProcessor.sceneDeactivated();
		dragAndDropProcessor.sceneDeactivated();
		touchProcessor.sceneDeactivated();
		doubleTouchProcessor.sceneDeactivated();

		spatialSystem = null;
	}

	@Override
	public void onInputUpdate() {
		inputQueue.drain();
		delegate.finshUpdate();
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

	public boolean pickNode(PickResult out, float screenX, float screenY, Predicate<RenderableComponent> predicate) {
		intersector.reset();
		for (int i = 0, n = cameras.size; i < n; i++) {
			if (doPickNode(out, screenX, screenY, cameras.get(i).camera, predicate)) {
				return true;
			}
		}

		return false;
	}

	public boolean pickNode(PickResult out, float screenX, float screenY, CameraComponent<?> camera,
			Predicate<RenderableComponent> predicate) {
		intersector.reset();
		return doPickNode(out, screenX, screenY, camera.camera, predicate);
	}

	public boolean pickNode(PickResult out, float screenX, float screenY, Camera camera,
			Predicate<RenderableComponent> predicate) {
		intersector.reset();
		return doPickNode(out, screenX, screenY, camera, predicate);
	}

	private boolean doPickNode(PickResult out, float screenX, float screenY, Camera camera,
			Predicate<RenderableComponent> predicate) {
		Ray pickRay = camera.getPickRay(screenX, screenY);
		intersector.set(camera, pickRay);

		spatialSystem.getSpatials(pickRay, spatials, predicate);
		Spatial closestSpatial = null;
		for (int i = 0, n = spatials.size; i < n; i++) {
			Spatial spatial = spatials.get(i);
			RenderableComponent renderable = spatial.renderable;
			//TODO renderable.looseInput, renderable.inputSensitivity
			if (renderable.inputSensitivity != 0 && intersector.append(renderable)) {
				closestSpatial = spatial;
			}
		}
		spatials.clear();

		if (closestSpatial != null) {
			out.node = closestSpatial.renderable.getNode();
			out.location.set(intersector.getClosestIntersection());
			out.distance = intersector.getClosestDistance();
			return true;
		} else {
			return false;
		}
	}

	private static class InputProcessorDelegate implements InputProcessor {
		private final InputSystem inputSystem;
		private final Scene scene;

		private final PointerActivityEvent pointerActivityEvent;

		private final KeyDownEvent keyDownEvent;
		private final KeyUpEvent keyUpEvent;
		private final KeyTypedEvent keyTypedEvent;

		private final TouchInfo touchInfo = new TouchInfo();
		private final SceneTouchDownEvent sceneTouchDownEvent = new SceneTouchDownEvent(touchInfo);
		private final SceneTouchUpEvent sceneTouchUpEvent = new SceneTouchUpEvent(touchInfo);
		private final NodeTouchDownEvent nodeTouchDownEvent = new NodeTouchDownEvent(touchInfo);
		private final NodeTouchUpEvent nodeTouchUpEvent = new NodeTouchUpEvent(touchInfo);

		private final ScrollInfo scrollInfo = new ScrollInfo();
		private final SceneScrollEvent sceneScrollEvent = new SceneScrollEvent(scrollInfo);
		private final NodeScrollEvent nodeScrollEvent = new NodeScrollEvent(scrollInfo);

		private transient final MouseMoveProcessor mouseMoveProcessor;

		private transient final IntMap<PointerTrack> trackers = new IntMap<PointerTrack>();

		private final PickResult pickResult = new PickResult();
		private final LayerMask layerMask = new LayerMask().ignored(Layer.DnD);

		public InputProcessorDelegate(InputSystem inputSystem) {
			this.inputSystem = inputSystem;
			this.scene = inputSystem.scene;

			keyDownEvent = new KeyDownEvent(scene);
			keyUpEvent = new KeyUpEvent(scene);
			keyTypedEvent = new KeyTypedEvent(scene);
			pointerActivityEvent = new PointerActivityEvent(scene);

			mouseMoveProcessor = new MouseMoveProcessor(scene);
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

		private PointerTrack getTracker(int pointer, int button) {
			int key = pointer + button * 100;
			return trackers.get(key);
		}

		@Override
		public boolean keyDown(int keycode) {
			keyDownEvent.post(keycode);
			return false;
		}

		@Override
		public boolean keyUp(int keycode) {
			keyUpEvent.post(keycode);
			return false;
		}

		@Override
		public boolean keyTyped(char character) {
			keyTypedEvent.post(character);
			return false;
		}

		@Override
		public boolean mouseMoved(int screenX, int screenY) {
			inputSystem.pickNode(pickResult, screenX, screenY, layerMask);
			mouseMoveProcessor.mouseMoved(screenX, screenY, pickResult.node, pickResult.location);
			return false;
		}

		@Override
		public boolean scrolled(int amount) {
			int screenX = Gdx.input.getX();
			int screenY = Gdx.input.getY();
			scrollInfo.set(screenX, screenY);
			scrollInfo.amount = amount;
			inputSystem.pickNode(pickResult, screenX, screenY, layerMask);

			SceneNode2 node = pickResult.node;
			if (node != null) {
				scrollInfo.renderable = node.getComponent(RenderableComponent.class);
				scrollInfo.intersection.set(pickResult.location);
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
			long eventTime = inputSystem.inputQueue.getCurrentEventTime();
			inputSystem.pickNode(pickResult, screenX, screenY, layerMask);
			SceneNode2 node = pickResult.node;
			tracker.add(eventTime, screenX, screenY, pickResult.location, node, begin);

			touchInfo.set(pointer, button, screenX, screenY);
			if (node != null) {
				touchInfo.renderable = node.getComponent(RenderableComponent.class);
				touchInfo.intersection.set(pickResult.location);
			}

			EventService.post(scene.getInstanceId(), sceneTouchDownEvent);

			if (node != null) {
				EventService.post(node.getInstanceId(), nodeTouchDownEvent);
			}

			pointerActivityEvent.post(pointer, button, tracker);
			touchInfo.reset();

			return false;
		}

		@Override
		public boolean touchUp(int screenX, int screenY, int pointer, int button) {
			touchInfo.set(pointer, button, screenX, screenY);
			inputSystem.pickNode(pickResult, screenX, screenY, layerMask);
			SceneNode2 node = pickResult.node;
			if (node != null) {
				touchInfo.renderable = node.getComponent(RenderableComponent.class);
				touchInfo.intersection.set(pickResult.location);
			}

			EventService.post(scene.getInstanceId(), sceneTouchUpEvent);

			if (node != null) {
				EventService.post(node.getInstanceId(), nodeTouchUpEvent);
			}

			PointerTrack tracker = getTracker(pointer, button);
			if (tracker != null) {
				long eventTime = inputSystem.inputQueue.getCurrentEventTime();
				tracker.add(eventTime, screenX, screenY, pickResult.location, node, end);
				pointerActivityEvent.post(pointer, button, tracker);
			}

			return false;
		}

		@Override
		public boolean touchDragged(int screenX, int screenY, int pointer) {
			inputSystem.pickNode(pickResult, screenX, screenY, layerMask);
			SceneNode2 node = pickResult.node;
			touchInfo.set(pointer, -1, screenX, screenY);

			for (int button = 0; button < 3; button++) {
				if (Gdx.input.isButtonPressed(button)) {
					PointerTrack tracker = getTracker(pointer, button);
					if (tracker != null) {
						long eventTime = inputSystem.inputQueue.getCurrentEventTime();
						tracker.add(eventTime, screenX, screenY, pickResult.location, node, move);
						pointerActivityEvent.post(pointer, button, tracker);
					}
				}
			}

			return false;
		}

		private void sceneDeactivated() {
			mouseMoveProcessor.sceneDeactivated();
			for (PointerTrack pointerTrack : trackers.values()) {
				PoolService.free(pointerTrack);
			}
			trackers.clear();
		}

		private void finshUpdate() {
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
	}
}
