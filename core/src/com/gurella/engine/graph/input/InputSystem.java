package com.gurella.engine.graph.input;

import static com.gurella.engine.graph.input.PointerTrack.PointerTrackerPhase.begin;
import static com.gurella.engine.graph.input.PointerTrack.PointerTrackerPhase.end;
import static com.gurella.engine.graph.input.PointerTrack.PointerTrackerPhase.move;
import static com.gurella.engine.graph.script.DefaultScriptMethod.keyDown;
import static com.gurella.engine.graph.script.DefaultScriptMethod.keyTyped;
import static com.gurella.engine.graph.script.DefaultScriptMethod.keyUp;
import static com.gurella.engine.graph.script.DefaultScriptMethod.onScrolledResolved;
import static com.gurella.engine.graph.script.DefaultScriptMethod.onTouchDown;
import static com.gurella.engine.graph.script.DefaultScriptMethod.onTouchDownResolved;
import static com.gurella.engine.graph.script.DefaultScriptMethod.onTouchUp;
import static com.gurella.engine.graph.script.DefaultScriptMethod.onTouchUpResolved;
import static com.gurella.engine.graph.script.DefaultScriptMethod.scrolled;
import static com.gurella.engine.graph.script.DefaultScriptMethod.touchDown;
import static com.gurella.engine.graph.script.DefaultScriptMethod.touchDragged;
import static com.gurella.engine.graph.script.DefaultScriptMethod.touchUp;

import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessorQueue;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntMap.Entries;
import com.badlogic.gdx.utils.IntMap.Entry;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedSet;
import com.gurella.engine.application.Application;
import com.gurella.engine.application.CommonUpdateOrder;
import com.gurella.engine.graph.SceneGraph;
import com.gurella.engine.graph.SceneGraphListener;
import com.gurella.engine.graph.SceneNode;
import com.gurella.engine.graph.SceneNodeComponent;
import com.gurella.engine.graph.UpdateListenerSystem;
import com.gurella.engine.graph.camera.CameraComponent;
import com.gurella.engine.graph.layer.Layer;
import com.gurella.engine.graph.layer.Layer.CommonLayer;
import com.gurella.engine.graph.layer.Layer.DescendingLayerOrdinalComparator;
import com.gurella.engine.graph.renderable.RenderableComponent;
import com.gurella.engine.graph.script.ScriptComponent;
import com.gurella.engine.graph.script.ScriptManager;
import com.gurella.engine.graph.script.ScriptMethod;
import com.gurella.engine.graph.spatial.Spatial;
import com.gurella.engine.graph.spatial.SpatialPartitioningManager;
import com.gurella.engine.pools.SynchronizedPools;
import com.gurella.engine.signal.AbstractSignal;
import com.gurella.engine.utils.ImmutableArray;

public class InputSystem extends UpdateListenerSystem implements SceneGraphListener {
	private Array<Layer> orderedLayers = new Array<Layer>();
	private ObjectMap<Layer, Array<CameraComponent<?>>> camerasByLayer = new ObjectMap<Layer, Array<CameraComponent<?>>>();

	private ScriptManager scriptManager;
	private SpatialPartitioningManager<?> spatialPartitioningManager;

	private InputProcessorDelegate delegate = new InputProcessorDelegate();
	private InputAdapter dummyDelegate = new InputAdapter();
	private InputProcessorQueue inputProcessorQueue = new InputProcessorQueue(delegate);

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
	protected void attached() {
		SceneGraph graph = getGraph();
		scriptManager = graph.scriptManager;
		spatialPartitioningManager = graph.spatialPartitioningManager;
	}

	@Override
	protected void detached() {
		scriptManager = null;
		spatialPartitioningManager = null;
	}

	@Override
	protected void activated() {
		SceneGraph graph = getGraph();
		graph.addListener(this);

		// TODO use componentManager
		ImmutableArray<SceneNodeComponent> components = graph.activeComponents;
		for (int i = 0; i < components.size(); i++) {
			componentActivated(components.get(i));
		}

		Application.addInputProcessor(inputProcessorQueue);
	}

	@Override
	protected void deactivated() {
		Application.removeInputProcessor(inputProcessorQueue);
		resetData();
	}

	@Override
	public void update() {
		inputProcessorQueue.drain();
		delegate.clean();
	}

	@Override
	protected void resetted() {
		resetData();
	}

	private void resetData() {
		//TODO update listeners and finish actions
		inputProcessorQueue.setProcessor(dummyDelegate);
		inputProcessorQueue.drain();
		inputProcessorQueue.setProcessor(delegate);
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
		for (Layer layer : orderedLayers) {
			if (Arrays.binarySearch(excludedLayers, layer) < 0) {
				continue;
			}

			pickNode(out, screenX, screenY, layer);
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

	private PickResult pickNode(PickResult out, float screenX, float screenY, Layer layer, Camera camera) {
		Vector3 cameraPosition = camera.position;
		Spatial closestSpatial = null;
		closestIntersection.set(Float.NaN, Float.NaN, Float.NaN);
		float closestDistance = Float.MAX_VALUE;

		Ray pickRay = camera.getPickRay(screenX, screenY);
		spatialPartitioningManager.getSpatials(pickRay, spatials, layer);
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

	OrderedSet<ScriptComponent> getScriptsByMethod(ScriptMethod method) {
		return scriptManager.getScriptsByMethod(method);
	}

	OrderedSet<ScriptComponent> getNodeScriptsByMethod(SceneNode node, ScriptMethod method) {
		return scriptManager.getNodeScriptsByMethod(node, method);
	}

	@Override
	public int getOrdinal() {
		return CommonUpdateOrder.INPUT;
	}

	private class InputProcessorDelegate implements com.badlogic.gdx.InputProcessor {
		private final TouchEvent touchEvent = new TouchEvent();
		private final IntersectionTouchEvent intersectionTouchEvent = new IntersectionTouchEvent();

		@Override
		public boolean keyDown(int keycode) {
			for (ScriptComponent scriptComponent : getScriptsByMethod(keyDown)) {
				scriptComponent.keyDown(keycode);
			}
			return false;
		}

		@Override
		public boolean keyUp(int keycode) {
			for (ScriptComponent scriptComponent : getScriptsByMethod(keyUp)) {
				scriptComponent.keyUp(keycode);
			}
			return false;
		}

		@Override
		public boolean keyTyped(char character) {
			for (ScriptComponent scriptComponent : getScriptsByMethod(keyTyped)) {
				scriptComponent.keyTyped(character);
			}
			return false;
		}

		@Override
		public boolean mouseMoved(int screenX, int screenY) {
			SceneNode node = pickNodeExcludeLayers(pickResult, screenX, screenY, CommonLayer.Dnd.layer).node;
			mouseMoveProcessor.mouseMoved(screenX, screenY, node, closestIntersection);
			return false;
		}

		@Override
		public boolean scrolled(int amount) {
			int screenX = Gdx.input.getX();
			int screenY = Gdx.input.getY();

			for (ScriptComponent scriptComponent : getScriptsByMethod(scrolled)) {
				scriptComponent.scrolled(screenX, screenY, amount);
			}

			SceneNode node = pickNodeExcludeLayers(pickResult, screenX, screenY, CommonLayer.Dnd.layer).node;
			if (node != null) {
				RenderableComponent renderableComponent = node.getComponent(RenderableComponent.class);
				for (ScriptComponent scriptComponent : getScriptsByMethod(onScrolledResolved)) {
					scriptComponent.onScrolled(renderableComponent, screenX, screenY, amount, closestIntersection);
				}

				for (ScriptComponent scriptComponent : getNodeScriptsByMethod(node, onTouchDown)) {
					scriptComponent.onScrolled(screenX, screenY, amount, closestIntersection);
				}
			}

			return false;
		}

		@Override
		public boolean touchDown(int screenX, int screenY, int pointer, int button) {
			SceneNode node = pickNodeExcludeLayers(pickResult, screenX, screenY, CommonLayer.Dnd.layer).node;
			PointerTrack tracker = createTracker(pointer, button);
			long eventTime = inputProcessorQueue.getCurrentEventTime();
			tracker.add(eventTime, screenX, screenY, closestIntersection, node, begin);

			touchEvent.set(pointer, button, screenX, screenY);
			for (ScriptComponent scriptComponent : getScriptsByMethod(touchDown)) {
				scriptComponent.touchDown(touchEvent);
			}

			if (node != null) {
				intersectionTouchEvent.set(pointer, button, screenX, screenY, closestIntersection);
				RenderableComponent renderableComponent = node.getComponent(RenderableComponent.class);
				for (ScriptComponent scriptComponent : getScriptsByMethod(onTouchDownResolved)) {
					scriptComponent.onTouchDown(renderableComponent, intersectionTouchEvent);
				}

				for (ScriptComponent scriptComponent : getNodeScriptsByMethod(node, onTouchDown)) {
					scriptComponent.onTouchDown(intersectionTouchEvent);
				}
			}

			pointerActivitySignal.onPointerActivity(pointer, button, tracker);

			return false;
		}

		@Override
		public boolean touchUp(int screenX, int screenY, int pointer, int button) {
			touchEvent.set(pointer, button, screenX, screenY);
			for (ScriptComponent scriptComponent : getScriptsByMethod(touchUp)) {
				scriptComponent.touchUp(touchEvent);
			}

			SceneNode node = pickNodeExcludeLayers(pickResult, screenX, screenY, CommonLayer.Dnd.layer).node;
			if (node != null) {
				intersectionTouchEvent.set(pointer, button, screenX, screenY, closestIntersection);
				RenderableComponent renderableComponent = node.getComponent(RenderableComponent.class);
				for (ScriptComponent scriptComponent : getScriptsByMethod(onTouchUpResolved)) {
					scriptComponent.onTouchUp(renderableComponent, intersectionTouchEvent);
				}

				for (ScriptComponent scriptComponent : getNodeScriptsByMethod(node, onTouchUp)) {
					scriptComponent.onTouchUp(intersectionTouchEvent);
				}
			}

			PointerTrack tracker = getTracker(pointer, button);
			if (tracker != null) {
				long eventTime = inputProcessorQueue.getCurrentEventTime();
				tracker.add(eventTime, screenX, screenY, closestIntersection, node, end);
				pointerActivitySignal.onPointerActivity(pointer, button, tracker);
			}

			return false;
		}

		@Override
		public boolean touchDragged(int screenX, int screenY, int pointer) {
			SceneNode node = pickNodeExcludeLayers(pickResult, screenX, screenY, CommonLayer.Dnd.layer).node;
			touchEvent.set(pointer, -1, screenX, screenY);

			for (int button = 0; button < 3; button++) {
				if (Gdx.input.isButtonPressed(button)) {
					touchEvent.set(pointer, button, screenX, screenY);
					for (ScriptComponent scriptComponent : getScriptsByMethod(touchDragged)) {
						scriptComponent.touchDragged(touchEvent);
					}

					PointerTrack tracker = getTracker(pointer, button);
					if (tracker != null) {
						long eventTime = inputProcessorQueue.getCurrentEventTime();
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
