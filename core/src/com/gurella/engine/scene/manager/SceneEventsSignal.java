package com.gurella.engine.scene.manager;

import com.gurella.engine.scene.SceneNodeComponent;
import com.gurella.engine.scene.event.EventManager;
import com.gurella.engine.scene.event.EventSubscription;
import com.gurella.engine.utils.ImmutableArray;

public final class SceneEventsSignal {
	private EventManager eventManager;

	public SceneEventsSignal(EventManager eventManager) {
		this.eventManager = eventManager;
	}

	public void sceneStarted() {
		ImmutableArray<SceneActivityListener> listeners = eventManager.getListeners(SceneActivityListener.class);
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).sceneStarted();
		}
	}

	public void sceneStopped() {
		ImmutableArray<SceneActivityListener> listeners = eventManager.getListeners(SceneActivityListener.class);
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).sceneStopped();
		}
	}

	public void componentActivated(SceneNodeComponent component) {
		ImmutableArray<ComponentActivityListener> listeners = eventManager
				.getListeners(ComponentActivityListener.class);
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).componentActivated(component);
		}

		ImmutableArray<NodeComponentActivityListener> nodeListeners = eventManager.getListeners(component.getNode(),
				NodeComponentActivityListener.class);
		for (int i = 0; i < nodeListeners.size(); i++) {
			nodeListeners.get(i).nodeComponentActivated(component);
		}
	}

	public void componentDeactivated(SceneNodeComponent component) {
		ImmutableArray<ComponentActivityListener> listeners = eventManager
				.getListeners(ComponentActivityListener.class);
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).componentDeactivated(component);
		}

		ImmutableArray<NodeComponentActivityListener> nodeListeners = eventManager.getListeners(component.getNode(),
				NodeComponentActivityListener.class);
		for (int i = 0; i < nodeListeners.size(); i++) {
			nodeListeners.get(i).nodeComponentDeactivated(component);
		}
	}

	public void componentAdded(SceneNodeComponent component) {
		ImmutableArray<ComponentAccumulationListener> listeners = eventManager
				.getListeners(ComponentAccumulationListener.class);
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).componentAdded(component);
		}

		ImmutableArray<NodeComponentAccumulationListener> nodeListeners = eventManager.getListeners(component.getNode(),
				NodeComponentAccumulationListener.class);
		for (int i = 0; i < nodeListeners.size(); i++) {
			nodeListeners.get(i).nodeComponentAdded(component);
		}
	}

	public void componentRemoved(SceneNodeComponent component) {
		ImmutableArray<ComponentAccumulationListener> listeners = eventManager
				.getListeners(ComponentAccumulationListener.class);
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).componentRemoved(component);
		}

		ImmutableArray<NodeComponentAccumulationListener> nodeListeners = eventManager.getListeners(component.getNode(),
				NodeComponentAccumulationListener.class);
		for (int i = 0; i < nodeListeners.size(); i++) {
			nodeListeners.get(i).nodeComponentRemoved(component);
		}
	}

	public interface SceneActivityListener extends EventSubscription {
		void sceneStarted();

		void sceneStopped();
	}

	public interface ComponentActivityListener extends EventSubscription {
		void componentActivated(SceneNodeComponent component);

		void componentDeactivated(SceneNodeComponent component);
	}

	public interface ComponentAccumulationListener extends EventSubscription {
		void componentAdded(SceneNodeComponent component);

		void componentRemoved(SceneNodeComponent component);
	}

	public interface NodeComponentActivityListener extends EventSubscription {
		void nodeComponentActivated(SceneNodeComponent component);

		void nodeComponentDeactivated(SceneNodeComponent component);
	}

	public interface NodeComponentAccumulationListener extends EventSubscription {
		void nodeComponentAdded(SceneNodeComponent component);

		void nodeComponentRemoved(SceneNodeComponent component);
	}
}
