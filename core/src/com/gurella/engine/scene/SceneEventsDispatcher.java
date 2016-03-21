package com.gurella.engine.scene;

import com.badlogic.gdx.utils.Array;
import com.gurella.engine.event.EventService;
import com.gurella.engine.event.TypePriority;
import com.gurella.engine.subscriptions.application.ApplicationUpdateListener;
import com.gurella.engine.subscriptions.application.CommonUpdatePriority;
import com.gurella.engine.subscriptions.scene.ComponentActivityListener;
import com.gurella.engine.subscriptions.scene.NodeComponentActivityListener;
import com.gurella.engine.subscriptions.scene.NodeRenamedListener;
import com.gurella.engine.subscriptions.scene.SceneActivityListener;
import com.gurella.engine.subscriptions.scene.update.CleanupUpdateListener;
import com.gurella.engine.subscriptions.scene.update.DebugRenderUpdateListener;
import com.gurella.engine.subscriptions.scene.update.InputUpdateListener;
import com.gurella.engine.subscriptions.scene.update.IoUpdateListener;
import com.gurella.engine.subscriptions.scene.update.LogicUpdateListener;
import com.gurella.engine.subscriptions.scene.update.PhysicsUpdateListener;
import com.gurella.engine.subscriptions.scene.update.PreRenderUpdateListener;
import com.gurella.engine.subscriptions.scene.update.RenderUpdateListener;
import com.gurella.engine.utils.Values;

@TypePriority(priority = CommonUpdatePriority.updatePriority, type = ApplicationUpdateListener.class)
class SceneEventsDispatcher implements ApplicationUpdateListener {
	private final Scene scene;
	private final Array<Object> tempListeners = new Array<Object>(64);

	private int sceneId;

	SceneEventsDispatcher(Scene scene) {
		this.scene = scene;
	}

	void activate() {
		sceneId = scene.getInstanceId();
		EventService.subscribe(this);
		Array<SceneActivityListener> listeners = Values.cast(tempListeners);
		EventService.getSubscribers(sceneId, SceneActivityListener.class, listeners);
		for (int i = 0; i < listeners.size; i++) {
			listeners.get(i).sceneStarted();
		}
		tempListeners.clear();
	}

	void deactivate() {
		Array<SceneActivityListener> listeners = Values.cast(tempListeners);
		EventService.getSubscribers(sceneId, SceneActivityListener.class, listeners);
		for (int i = 0; i < listeners.size; i++) {
			listeners.get(i).sceneStopped();
		}
		tempListeners.clear();
		EventService.unsubscribe(this);
	}

	void componentActivated(SceneNodeComponent2 component) {
		Array<ComponentActivityListener> sceneListeners = Values.cast(tempListeners);
		EventService.getSubscribers(sceneId, ComponentActivityListener.class, sceneListeners);
		for (int i = 0; i < sceneListeners.size; i++) {
			sceneListeners.get(i).componentActivated(component);
		}
		tempListeners.clear();

		Array<NodeComponentActivityListener> listeners = Values.cast(tempListeners);
		EventService.getSubscribers(component.getNodeId(), NodeComponentActivityListener.class, listeners);
		for (int i = 0; i < listeners.size; i++) {
			listeners.get(i).nodeComponentActivated(component);
		}
		tempListeners.clear();
	}

	void componentDeactivated(SceneNodeComponent2 component) {
		Array<ComponentActivityListener> sceneListeners = Values.cast(tempListeners);
		EventService.getSubscribers(sceneId, ComponentActivityListener.class, sceneListeners);
		for (int i = 0; i < sceneListeners.size; i++) {
			sceneListeners.get(i).componentDeactivated(component);
		}
		tempListeners.clear();

		Array<NodeComponentActivityListener> listeners = Values.cast(tempListeners);
		EventService.getSubscribers(component.getNodeId(), NodeComponentActivityListener.class, listeners);
		for (int i = 0; i < listeners.size; i++) {
			listeners.get(i).nodeComponentDeactivated(component);
		}
		tempListeners.clear();
	}

	void nodeRenamed(SceneNode2 node, String oldName, String newName) {
		Array<NodeRenamedListener> sceneListeners = Values.cast(tempListeners);
		EventService.getSubscribers(sceneId, NodeRenamedListener.class, sceneListeners);
		for (int i = 0; i < sceneListeners.size; i++) {
			sceneListeners.get(i).nodeRenamed(node, oldName, newName);
		}
		tempListeners.clear();
	}

	@Override
	public void update() {
		Array<IoUpdateListener> ioUpdateListeners = Values.cast(tempListeners);
		EventService.getSubscribers(sceneId, IoUpdateListener.class, ioUpdateListeners);
		for (int i = 0; i < ioUpdateListeners.size; i++) {
			ioUpdateListeners.get(i).onIoUpdate();
		}
		tempListeners.clear();

		Array<InputUpdateListener> inputUpdateListeners = Values.cast(tempListeners);
		EventService.getSubscribers(sceneId, InputUpdateListener.class, inputUpdateListeners);
		for (int i = 0; i < inputUpdateListeners.size; i++) {
			inputUpdateListeners.get(i).onInputUpdate();
		}
		tempListeners.clear();

		Array<LogicUpdateListener> logicUpdateListeners = Values.cast(tempListeners);
		EventService.getSubscribers(sceneId, LogicUpdateListener.class, logicUpdateListeners);
		for (int i = 0; i < logicUpdateListeners.size; i++) {
			logicUpdateListeners.get(i).onLogicUpdate();
		}
		tempListeners.clear();

		Array<PhysicsUpdateListener> physicsUpdateListeners = Values.cast(tempListeners);
		EventService.getSubscribers(sceneId, PhysicsUpdateListener.class, physicsUpdateListeners);
		for (int i = 0; i < physicsUpdateListeners.size; i++) {
			physicsUpdateListeners.get(i).onPhisycsUpdate();
		}
		tempListeners.clear();

		Array<PreRenderUpdateListener> preRenderUpdateListeners = Values.cast(tempListeners);
		EventService.getSubscribers(sceneId, PreRenderUpdateListener.class, preRenderUpdateListeners);
		for (int i = 0; i < preRenderUpdateListeners.size; i++) {
			preRenderUpdateListeners.get(i).onPreRenderUpdate();
		}
		tempListeners.clear();

		Array<RenderUpdateListener> renderUpdateListeners = Values.cast(tempListeners);
		EventService.getSubscribers(sceneId, RenderUpdateListener.class, renderUpdateListeners);
		for (int i = 0; i < renderUpdateListeners.size; i++) {
			renderUpdateListeners.get(i).onRenderUpdate();
		}
		tempListeners.clear();

		// TODO only fire if in debug or editor mode
		Array<DebugRenderUpdateListener> debugRenderUpdateListeners = Values.cast(tempListeners);
		EventService.getSubscribers(sceneId, DebugRenderUpdateListener.class, debugRenderUpdateListeners);
		for (int i = 0; i < debugRenderUpdateListeners.size; i++) {
			debugRenderUpdateListeners.get(i).onDebugRenderUpdate();
		}
		tempListeners.clear();

		Array<CleanupUpdateListener> cleanupUpdateListeners = Values.cast(tempListeners);
		EventService.getSubscribers(sceneId, CleanupUpdateListener.class, cleanupUpdateListeners);
		for (int i = 0; i < cleanupUpdateListeners.size; i++) {
			cleanupUpdateListeners.get(i).onCleanupUpdate();
		}
		tempListeners.clear();
	}
}
