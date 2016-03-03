package com.gurella.engine.scene;

import com.badlogic.gdx.utils.Array;
import com.gurella.engine.event.EventService;
import com.gurella.engine.subscriptions.application.ApplicationUpdateListener;
import com.gurella.engine.subscriptions.scene.CleanupUpdateListener;
import com.gurella.engine.subscriptions.scene.DebugRenderUpdateListener;
import com.gurella.engine.subscriptions.scene.InputUpdateListener;
import com.gurella.engine.subscriptions.scene.IoUpdateListener;
import com.gurella.engine.subscriptions.scene.LogicUpdateListener;
import com.gurella.engine.subscriptions.scene.PhysicsUpdateListener;
import com.gurella.engine.subscriptions.scene.PreRenderUpdateListener;
import com.gurella.engine.subscriptions.scene.RenderUpdateListener;
import com.gurella.engine.subscriptions.scene.SceneActivityListener;
import com.gurella.engine.utils.Values;

class SceneEventsDispatcher implements ApplicationUpdateListener {
	private Scene scene;
	private int instanceId;
	private final Array<Object> tempListeners = new Array<Object>(64);

	SceneEventsDispatcher(Scene scene) {
		this.scene = scene;
	}

	void activate() {
		instanceId = scene.getInstanceId();
		EventService.subscribe(this);
		Array<SceneActivityListener> listeners = Values.cast(tempListeners);
		EventService.getSubscribers(instanceId, SceneActivityListener.class, listeners);
		for (int i = 0; i < listeners.size; i++) {
			listeners.get(i).sceneStarted();
		}
	}

	void deactivate() {
		Array<SceneActivityListener> listeners = Values.cast(tempListeners);
		EventService.getSubscribers(instanceId, SceneActivityListener.class, listeners);
		for (int i = 0; i < listeners.size; i++) {
			listeners.get(i).sceneStopped();
		}
		EventService.unsubscribe(this);
	}

	@Override
	public void update() {
		Array<IoUpdateListener> ioUpdateListeners = Values.cast(tempListeners);
		EventService.getSubscribers(instanceId, IoUpdateListener.class, ioUpdateListeners);
		for (int i = 0; i < ioUpdateListeners.size; i++) {
			ioUpdateListeners.get(i).onIoUpdate();
		}

		Array<InputUpdateListener> inputUpdateListeners = Values.cast(tempListeners);
		EventService.getSubscribers(instanceId, InputUpdateListener.class, inputUpdateListeners);
		for (int i = 0; i < inputUpdateListeners.size; i++) {
			inputUpdateListeners.get(i).onInputUpdate();
		}

		Array<LogicUpdateListener> logicUpdateListeners = Values.cast(tempListeners);
		EventService.getSubscribers(instanceId, LogicUpdateListener.class, logicUpdateListeners);
		for (int i = 0; i < logicUpdateListeners.size; i++) {
			logicUpdateListeners.get(i).onLogicUpdate();
		}

		Array<PhysicsUpdateListener> physicsUpdateListeners = Values.cast(tempListeners);
		EventService.getSubscribers(instanceId, PhysicsUpdateListener.class, physicsUpdateListeners);
		for (int i = 0; i < physicsUpdateListeners.size; i++) {
			physicsUpdateListeners.get(i).onPhisycsUpdate();
		}

		Array<PreRenderUpdateListener> preRenderUpdateListeners = Values.cast(tempListeners);
		EventService.getSubscribers(instanceId, PreRenderUpdateListener.class, preRenderUpdateListeners);
		for (int i = 0; i < preRenderUpdateListeners.size; i++) {
			preRenderUpdateListeners.get(i).onPreRenderUpdate();
		}

		Array<RenderUpdateListener> renderUpdateListeners = Values.cast(tempListeners);
		EventService.getSubscribers(instanceId, RenderUpdateListener.class, renderUpdateListeners);
		for (int i = 0; i < renderUpdateListeners.size; i++) {
			renderUpdateListeners.get(i).onRenderUpdate();
		}

		Array<DebugRenderUpdateListener> debugRenderUpdateListeners = Values.cast(tempListeners);
		EventService.getSubscribers(instanceId, DebugRenderUpdateListener.class, debugRenderUpdateListeners);
		for (int i = 0; i < debugRenderUpdateListeners.size; i++) {
			debugRenderUpdateListeners.get(i).onDebugRenderUpdate();
		}

		Array<CleanupUpdateListener> cleanupUpdateListeners = Values.cast(tempListeners);
		EventService.getSubscribers(instanceId, CleanupUpdateListener.class, cleanupUpdateListeners);
		for (int i = 0; i < cleanupUpdateListeners.size; i++) {
			cleanupUpdateListeners.get(i).onCleanupUpdate();
		}
	}
}
