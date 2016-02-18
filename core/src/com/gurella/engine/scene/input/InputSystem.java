package com.gurella.engine.scene.input;

import static com.gurella.engine.scene.behaviour.BehaviourEvents.keyDown;
import static com.gurella.engine.scene.behaviour.BehaviourEvents.keyTyped;
import static com.gurella.engine.scene.behaviour.BehaviourEvents.keyUp;
import static com.gurella.engine.scene.behaviour.BehaviourEvents.onScrolledGlobal;
import static com.gurella.engine.scene.behaviour.BehaviourEvents.onTouchDown;
import static com.gurella.engine.scene.behaviour.BehaviourEvents.onTouchDownGlobal;
import static com.gurella.engine.scene.behaviour.BehaviourEvents.onTouchUp;
import static com.gurella.engine.scene.behaviour.BehaviourEvents.onTouchUpGlobal;
import static com.gurella.engine.scene.behaviour.BehaviourEvents.scrolled;
import static com.gurella.engine.scene.behaviour.BehaviourEvents.touchDown;
import static com.gurella.engine.scene.behaviour.BehaviourEvents.touchDragged;
import static com.gurella.engine.scene.behaviour.BehaviourEvents.touchUp;
import static com.gurella.engine.scene.input.PointerTrack.PointerTrackerPhase.begin;
import static com.gurella.engine.scene.input.PointerTrack.PointerTrackerPhase.end;
import static com.gurella.engine.scene.input.PointerTrack.PointerTrackerPhase.move;

import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputEventQueue;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntMap.Entries;
import com.badlogic.gdx.utils.IntMap.Entry;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.application.events.ApplicationUpdateSignal.ApplicationUpdateListener;
import com.gurella.engine.application.events.CommonUpdatePriority;
import com.gurella.engine.event.AbstractSignal;
import com.gurella.engine.input.InputService;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneListener;
import com.gurella.engine.scene.SceneNode;
import com.gurella.engine.scene.SceneNodeComponent;
import com.gurella.engine.scene.SceneSystem;
import com.gurella.engine.scene.behaviour.BehaviourComponent;
import com.gurella.engine.scene.camera.CameraComponent;
import com.gurella.engine.scene.event.EventCallbackIdentifier;
import com.gurella.engine.scene.event.EventManager;
import com.gurella.engine.scene.layer.Layer;
import com.gurella.engine.scene.layer.Layer.DescendingLayerOrdinalComparator;
import com.gurella.engine.scene.layer.LayerMask;
import com.gurella.engine.scene.renderable.RenderableComponent;
import com.gurella.engine.scene.spatial.Spatial;
import com.gurella.engine.scene.spatial.SpatialPartitioningSystem;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.SynchronizedPools;

//TODO attach listeners
public class InputSystem extends SceneSystem implements SceneListener, ApplicationUpdateListener {
	private Array<Layer> orderedLayers = new Array<Layer>();
	private ObjectMap<Layer, Array<CameraComponent<?>>> camerasByLayer = new ObjectMap<Layer, Array<CameraComponent<?>>>();

	private EventManager eventManager;
	private SpatialPartitioningSystem<?> spatialPartitioningSystem;

	private InputProcessorDelegate delegate = new InputProcessorDelegate();
	private InputAdapter dummyDelegate = new InputAdapter();
	private InputEventQueue inputQueue = new InputEventQueue(delegate);

	private IntMap<PointerTrack> trackers = new IntMap<PointerTrack>();
	public final PointerActivitySignal pointerActivitySignal = new PointerActivitySignal();

	private MouseMoveProcessor mouseMoveProcessor = new MouseMoveProcessor(this);
	private DragAndDropProcessor dragAndDropProcessor = new DragAndDropProcessor(this);
	private TouchInputProcessor touchInputProcessor = new TouchInputProcessor(this, dragAndDropProcessor);
	private DoubleTouchInputProcessor doubleTouchInputProcessor = new DoubleTouchInputProcessor(this,
			dragAndDropProcessor);
	private DragInputProcessor dragInputProcessor = new DragInputProcessor(this);

	public InputSystem() {
		pointerActivitySignal.addListener(dragAndDropProcessor);
		pointerActivitySignal.addListener(touchInputProcessor);
		pointerActivitySignal.addListener(doubleTouchInputProcessor);
		pointerActivitySignal.addListener(dragInputProcessor);
	}

	@Override
	protected void activated() {
		Scene scene = getScene();
		eventManager = scene.eventManager;
		spatialPartitioningSystem = scene.spatialPartitioningSystem;

		scene.addListener(this);

		// TODO use componentManager
		ImmutableArray<SceneNodeComponent> components = scene.activeComponents;
		for (int i = 0; i < components.size(); i++) {
			componentActivated(components.get(i));
		}

		InputService.addInputProcessor(inputQueue);
	}

	@Override
	protected void deactivated() {
		InputService.removeInputProcessor(inputQueue);
		resetData();
		eventManager = null;
		spatialPartitioningSystem = null;
	}

	@Override
	public void update() {
		inputQueue.drain();
		delegate.clean();
	}

	@Override
	protected void resetted() {
		resetData();
	}

	private void resetData() {
		// TODO update listeners and finish actions
		inputQueue.setProcessor(dummyDelegate);
		inputQueue.drain();
		inputQueue.setProcessor(delegate);
		delegate.reset();
		pointerActivitySignal.reset();
		mouseMoveProcessor.reset();
	}

	@Override
	public void componentAdded(SceneNodeComponent component) {
	}

	@Override
	public void componentRemoved(SceneNodeComponent component) {
	}

	@Override
	public void componentActivated(SceneNodeComponent component) {
		if (component instanceof CameraComponent) {
			addCameraComponent((CameraComponent<?>) component);
		}
	}

	private void addCameraComponent(CameraComponent<?> cameraComponent) {
		boolean layersUpdated = false;
		Array<Layer> renderingLayers = cameraComponent.renderingLayers;
		if (renderingLayers.size == 0) {
			layersUpdated |= addCameraComponent(Layer.DEFAULT, cameraComponent);
		} else {
			for (int i = 0; i < renderingLayers.size; i++) {
				layersUpdated |= addCameraComponent(renderingLayers.get(i), cameraComponent);
			}
		}

		if (layersUpdated) {
			orderedLayers.sort(DescendingLayerOrdinalComparator.instance);
		}
	}

	private boolean addCameraComponent(Layer layer, CameraComponent<?> cameraComponent) {
		Array<CameraComponent<?>> layerCameras = camerasByLayer.get(layer);
		if (layerCameras == null) {
			layerCameras = new Array<CameraComponent<?>>();
			camerasByLayer.put(layer, layerCameras);
		}

		layerCameras.add(cameraComponent);
		layerCameras.sort();
		return addLayer(layer);
	}

	private boolean addLayer(Layer layer) {
		if (camerasByLayer.containsKey(layer) && !orderedLayers.contains(layer, true)) {
			orderedLayers.add(layer);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void componentDeactivated(SceneNodeComponent component) {
		if (component instanceof CameraComponent) {
			removeCameraComponent((CameraComponent<?>) component);
		}
	}

	private void removeCameraComponent(CameraComponent<?> cameraComponent) {
		boolean layersUpdated = false;
		for (Layer layer : cameraComponent.renderingLayers) {
			layersUpdated |= removeCameraComponent(layer, cameraComponent);
		}

		if (layersUpdated) {
			orderedLayers.sort(DescendingLayerOrdinalComparator.instance);
		}
	}

	private boolean removeCameraComponent(Layer layer, CameraComponent<?> cameraComponent) {
		Array<CameraComponent<?>> layerCameras = camerasByLayer.get(layer);
		layerCameras.removeValue(cameraComponent, true);

		if (layerCameras.size < 1) {
			camerasByLayer.remove(layer);
			orderedLayers.removeValue(layer, true);
			return true;
		} else {
			layerCameras.sort();
			return false;
		}
	}

	public PickResult pickNode(float screenX, float screenY) {
		return pickNode(new PickResult(), screenX, screenY);
	}

	public PickResult pickNode(PickResult out, float screenX, float screenY) {
		out.reset();
		for (Layer layer : orderedLayers) {
			pickNode(out, screenX, screenY, layer);
			if (out.node != null) {
				return out;
			}
		}

		out.reset();
		return out;
	}

	public PickResult pickNodeExcludeLayers(float screenX, float screenY, Layer... excludedLayers) {
		return pickNodeExcludeLayers(new PickResult(), screenX, screenY, excludedLayers);
	}

	public PickResult pickNodeExcludeLayers(PickResult out, float screenX, float screenY, Layer... excludedLayers) {
		out.reset();
		Arrays.sort(excludedLayers);
		for (Layer layer : orderedLayers) {
			if (Arrays.binarySearch(excludedLayers, layer) < 0) {
				pickNode(out, screenX, screenY, layer);
				if (out.node != null) {
					return out;
				}
			}
		}

		out.reset();
		return out;
	}

	public PickResult pickNodeIncludeLayers(float screenX, float screenY, Layer... includedLayers) {
		return pickNodeIncludeLayers(new PickResult(), screenX, screenY, includedLayers);
	}

	public PickResult pickNodeIncludeLayers(PickResult out, float screenX, float screenY, Layer... includedLayers) {
		Arrays.sort(includedLayers);
		for (Layer layer : orderedLayers) {
			if (Arrays.binarySearch(includedLayers, layer) >= 0) {
				pickNode(out, screenX, screenY, layer);
				if (out.node != null) {
					return out;
				}
			}
		}

		out.reset();
		return out;
	}

	public PickResult pickNode(PickResult out, float screenX, float screenY, Layer layer) {
		for (CameraComponent<?> cameraComponent : camerasByLayer.get(layer)) {
			pickNode(out, screenX, screenY, layer, cameraComponent.camera);
			if (out.node != null) {
				return out;
			}
		}

		out.reset();
		return out;
	}

	public PickResult pickNode(PickResult out, float screenX, float screenY, Layer layer,
			CameraComponent<?> cameraComponent) {
		pickNode(out, screenX, screenY, layer, cameraComponent.camera);
		if (out.node == null) {
			out.reset();
		}
		return out;
	}

	private final Array<Spatial> spatials = new Array<Spatial>();
	private final Vector3 intersection = new Vector3();
	private final Vector3 closestIntersection = new Vector3();
	private final Vector3 screenCoord = new Vector3();
	private final Vector3 projectedCoord = new Vector3();
	private final PickResult pickResult = new PickResult();
	private final LayerMask layerMask = new LayerMask();

	private PickResult pickNode(PickResult out, float screenX, float screenY, Layer layer, Camera camera) {
		Vector3 cameraPosition = camera.position;
		Spatial closestSpatial = null;
		closestIntersection.set(Float.NaN, Float.NaN, Float.NaN);
		float closestDistance = Float.MAX_VALUE;

		Ray pickRay = camera.getPickRay(screenX, screenY);
		layerMask.reset();
		spatialPartitioningSystem.getSpatials(pickRay, spatials, layerMask.allowed(layer));
		for (int i = 0; i < spatials.size; i++) {
			Spatial spatial = spatials.get(i);
			RenderableComponent renderableComponent = spatial.renderableComponent;
			renderableComponent.getIntersection(pickRay, intersection);
			float distance = intersection.dst2(cameraPosition);
			if (closestDistance > distance) {
				closestDistance = distance;
				closestSpatial = spatial;
				closestIntersection.set(intersection);
				// TODO Z order of sprites
			}
		}

		spatials.clear();
		projectScreenCoordinates(screenX, screenY, camera);
		if (closestSpatial != null) {
			out.node = closestSpatial.renderableComponent.getNode();
			out.intersection.set(closestIntersection);
		}
		return out;
	}

	private void projectScreenCoordinates(float screenX, float screenY, Camera camera) {
		screenCoord.set(screenX, screenY, 0);
		projectedCoord.set(screenCoord);
		camera.unproject(projectedCoord);
	}

	private PointerTrack createTracker(int pointer, int button) {
		int key = pointer + button * 100;
		PointerTrack pointerTrack = trackers.get(key);
		if (pointerTrack == null) {
			pointerTrack = SynchronizedPools.obtain(PointerTrack.class);
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

	@Override
	public int getPriority() {
		return CommonUpdatePriority.INPUT;
	}

	ImmutableArray<BehaviourComponent> getListeners(EventCallbackIdentifier<BehaviourComponent> method) {
		return eventManager.getListeners(method);
	}

	ImmutableArray<BehaviourComponent> getListeners(SceneNode node,
			EventCallbackIdentifier<BehaviourComponent> method) {
		return eventManager.getListeners(node, method);
	}

	private class InputProcessorDelegate implements com.badlogic.gdx.InputProcessor {
		private final TouchEvent touchEvent = new TouchEvent();
		private final IntersectionTouchEvent intersectionTouchEvent = new IntersectionTouchEvent();

		@Override
		public boolean keyDown(int keycode) {
			for (BehaviourComponent behaviourComponent : eventManager.getListeners(keyDown)) {
				behaviourComponent.keyDown(keycode);
			}
			return false;
		}

		@Override
		public boolean keyUp(int keycode) {
			for (BehaviourComponent behaviourComponent : eventManager.getListeners(keyUp)) {
				behaviourComponent.keyUp(keycode);
			}
			return false;
		}

		@Override
		public boolean keyTyped(char character) {
			for (BehaviourComponent behaviourComponent : eventManager.getListeners(keyTyped)) {
				behaviourComponent.keyTyped(character);
			}
			return false;
		}

		@Override
		public boolean mouseMoved(int screenX, int screenY) {
			SceneNode node = pickNodeExcludeLayers(pickResult, screenX, screenY, Layer.DnD).node;
			mouseMoveProcessor.mouseMoved(screenX, screenY, node, closestIntersection);
			return false;
		}

		@Override
		public boolean scrolled(int amount) {
			int screenX = Gdx.input.getX();
			int screenY = Gdx.input.getY();

			for (BehaviourComponent behaviourComponent : eventManager.getListeners(scrolled)) {
				behaviourComponent.scrolled(screenX, screenY, amount);
			}

			SceneNode node = pickNodeExcludeLayers(pickResult, screenX, screenY, Layer.DnD).node;
			if (node != null) {
				RenderableComponent renderableComponent = node.getComponent(RenderableComponent.class);
				for (BehaviourComponent behaviourComponent : eventManager.getListeners(onScrolledGlobal)) {
					behaviourComponent.onScrolled(renderableComponent, screenX, screenY, amount, closestIntersection);
				}

				for (BehaviourComponent behaviourComponent : eventManager.getListeners(node, onTouchDown)) {
					behaviourComponent.onScrolled(screenX, screenY, amount, closestIntersection);
				}
			}

			return false;
		}

		@Override
		public boolean touchDown(int screenX, int screenY, int pointer, int button) {
			SceneNode node = pickNodeExcludeLayers(pickResult, screenX, screenY, Layer.DnD).node;
			PointerTrack tracker = createTracker(pointer, button);
			long eventTime = inputQueue.getCurrentEventTime();
			tracker.add(eventTime, screenX, screenY, closestIntersection, node, begin);

			touchEvent.set(pointer, button, screenX, screenY);
			for (BehaviourComponent behaviourComponent : eventManager.getListeners(touchDown)) {
				behaviourComponent.touchDown(touchEvent);
			}

			if (node != null) {
				intersectionTouchEvent.set(pointer, button, screenX, screenY, closestIntersection);
				RenderableComponent renderableComponent = node.getComponent(RenderableComponent.class);
				for (BehaviourComponent behaviourComponent : eventManager.getListeners(onTouchDownGlobal)) {
					behaviourComponent.onTouchDown(renderableComponent, intersectionTouchEvent);
				}

				for (BehaviourComponent behaviourComponent : eventManager.getListeners(node, onTouchDown)) {
					behaviourComponent.onTouchDown(intersectionTouchEvent);
				}
			}

			pointerActivitySignal.onPointerActivity(pointer, button, tracker);

			return false;
		}

		@Override
		public boolean touchUp(int screenX, int screenY, int pointer, int button) {
			touchEvent.set(pointer, button, screenX, screenY);
			for (BehaviourComponent behaviourComponent : eventManager.getListeners(touchUp)) {
				behaviourComponent.touchUp(touchEvent);
			}

			SceneNode node = pickNodeExcludeLayers(pickResult, screenX, screenY, Layer.DnD).node;
			if (node != null) {
				intersectionTouchEvent.set(pointer, button, screenX, screenY, closestIntersection);
				RenderableComponent renderableComponent = node.getComponent(RenderableComponent.class);
				for (BehaviourComponent behaviourComponent : eventManager.getListeners(onTouchUpGlobal)) {
					behaviourComponent.onTouchUp(renderableComponent, intersectionTouchEvent);
				}

				for (BehaviourComponent behaviourComponent : eventManager.getListeners(node, onTouchUp)) {
					behaviourComponent.onTouchUp(intersectionTouchEvent);
				}
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
			SceneNode node = pickNodeExcludeLayers(pickResult, screenX, screenY, Layer.DnD).node;
			touchEvent.set(pointer, -1, screenX, screenY);

			for (int button = 0; button < 3; button++) {
				if (Gdx.input.isButtonPressed(button)) {
					touchEvent.set(pointer, button, screenX, screenY);
					for (BehaviourComponent behaviourComponent : eventManager.getListeners(touchDragged)) {
						behaviourComponent.touchDragged(touchEvent);
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
				SynchronizedPools.free(pointerTrack);
			}
			trackers.clear();
		}

		private void clean() {
			Entries<PointerTrack> entries = trackers.entries();
			while (entries.hasNext()) {
				Entry<PointerTrack> entry = entries.next();
				PointerTrack pointerTrack = entry.value;
				if (end == pointerTrack.getPhase()) {
					SynchronizedPools.free(pointerTrack);
					entries.remove();
				}
			}
		}
	}

	public static class PointerActivitySignal extends AbstractSignal<PointerActivityListener> {
		public void onPointerActivity(int pointer, int button, PointerTrack pointerTrack) {
			for (PointerActivityListener listener : listeners) {
				listener.onPointerActivity(pointer, button, pointerTrack);
			}
		}

		private void reset() {
			for (PointerActivityListener listener : listeners) {
				listener.reset();
			}
		}
	}
}
