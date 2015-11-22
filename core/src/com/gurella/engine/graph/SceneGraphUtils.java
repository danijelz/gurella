package com.gurella.engine.graph;

import com.gurella.engine.application.UpdateEvent;
import com.gurella.engine.application.UpdateListener;
import com.gurella.engine.event.EventBus;
import com.gurella.engine.signal.Listener0;

public class SceneGraphUtils {
	private SceneGraphUtils() {
	}

	public static <T extends SceneGraphElement & SceneGraphListener> void asSceneGraphListener(T sceneGraphElement) {
		sceneGraphElement.attachedSignal.addListener(new AttachedListener<T>(sceneGraphElement));
		sceneGraphElement.detachedSignal.addListener(new DetachedListener<T>(sceneGraphElement));
	}

	private static class AttachedListener<T extends SceneGraphElement & SceneGraphListener> implements Listener0 {
		private T sceneGraphElement;

		public AttachedListener(T sceneGraphElement) {
			this.sceneGraphElement = sceneGraphElement;
		}

		@Override
		public void handle() {
			sceneGraphElement.graph.addListener(sceneGraphElement);
		}
	}

	private static class DetachedListener<T extends SceneGraphElement & SceneGraphListener> implements Listener0 {
		private T sceneGraphElement;

		public DetachedListener(T sceneGraphElement) {
			this.sceneGraphElement = sceneGraphElement;
		}

		@Override
		public void handle() {
			sceneGraphElement.graph.removeListener(sceneGraphElement);
		}
	}

	public static <T extends SceneGraphElement & UpdateListener> void asUpdateListener(T sceneGraphElement) {
		SceneStartListener<T> showSceneListener = new SceneStartListener<T>(sceneGraphElement);
		SceneStopListener<T> sceneStopListener = new SceneStopListener<T>(sceneGraphElement);
		sceneGraphElement.activatedSignal.addListener(new ActivatedListener<T>(sceneGraphElement, showSceneListener,
				sceneStopListener));
		sceneGraphElement.deactivatedSignal.addListener(new DeactivatedListener<T>(sceneGraphElement,
				showSceneListener, sceneStopListener));
	}

	private static class ActivatedListener<T extends SceneGraphElement & UpdateListener> implements Listener0 {
		private T sceneGraphElement;
		private SceneStartListener<T> sceneStartListener;
		private SceneStopListener<T> sceneStopListener;

		public ActivatedListener(T sceneGraphElement, SceneStartListener<T> showSceneListener,
				SceneStopListener<T> hideSceneListener) {
			this.sceneGraphElement = sceneGraphElement;
			this.sceneStartListener = showSceneListener;
			this.sceneStopListener = hideSceneListener;
		}

		@Override
		public void handle() {
			sceneGraphElement.scene.startSignal.addListener(sceneStartListener);
			sceneGraphElement.scene.stopSignal.addListener(sceneStopListener);
			EventBus.GLOBAL.addListener(UpdateEvent.class, sceneGraphElement);
		}
	}

	private static class DeactivatedListener<T extends SceneGraphElement & UpdateListener> implements Listener0 {
		private T sceneGraphElement;
		private SceneStartListener<T> showSceneListener;
		private SceneStopListener<T> sceneStopListener;

		public DeactivatedListener(T sceneGraphElement, SceneStartListener<T> showSceneListener,
				SceneStopListener<T> hideSceneListener) {
			this.sceneGraphElement = sceneGraphElement;
			this.showSceneListener = showSceneListener;
			this.sceneStopListener = hideSceneListener;
		}

		@Override
		public void handle() {
			EventBus.GLOBAL.removeListener(UpdateEvent.class, sceneGraphElement);
			sceneGraphElement.scene.startSignal.removeListener(showSceneListener);
			sceneGraphElement.scene.stopSignal.removeListener(sceneStopListener);
		}
	}

	private static class SceneStartListener<T extends SceneGraphElement & UpdateListener> implements Listener0 {
		private T sceneGraphElement;

		public SceneStartListener(T sceneGraphElement) {
			this.sceneGraphElement = sceneGraphElement;
		}

		@Override
		public void handle() {
			EventBus.GLOBAL.addListener(UpdateEvent.class, sceneGraphElement);
		}
	}

	private static class SceneStopListener<T extends SceneGraphElement & UpdateListener> implements Listener0 {
		private T sceneGraphElement;

		public SceneStopListener(T sceneGraphElement) {
			this.sceneGraphElement = sceneGraphElement;
		}

		@Override
		public void handle() {
			EventBus.GLOBAL.removeListener(UpdateEvent.class, sceneGraphElement);
		}
	}
}
