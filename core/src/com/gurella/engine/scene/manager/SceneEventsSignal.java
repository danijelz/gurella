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

	public void componentActivated(SceneNodeComponent component) {
		ImmutableArray<ComponentActivityListener> listeners = eventManager
				.getListeners(ComponentActivityListener.class);
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).componentActivated(component);
		}

		ImmutableArray<NodeComponentActivityListener> nodeListeners = eventManager.getListeners(component.getNode(),
				NodeComponentActivityListener.class);
		for (int i = 0; i < nodeListeners.size(); i++) {
			nodeListeners.get(i).componentActivated(component);
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
			nodeListeners.get(i).componentDeactivated(component);
		}
	}

	public void componentAdded(SceneNodeComponent component) {
		ImmutableArray<ComponentAddedListener> listeners = eventManager.getListeners(ComponentAddedListener.class);
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).componentAdded(component);
		}

		ImmutableArray<NodeComponentAddedListener> nodeListeners = eventManager.getListeners(component.getNode(),
				NodeComponentAddedListener.class);
		for (int i = 0; i < nodeListeners.size(); i++) {
			nodeListeners.get(i).componentAdded(component);
		}
	}

	public void componentRemoved(SceneNodeComponent component) {
		ImmutableArray<ComponentRemovedListener> listeners = eventManager.getListeners(ComponentRemovedListener.class);
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).componentRemoved(component);
		}

		ImmutableArray<NodeComponentRemovedListener> nodeListeners = eventManager.getListeners(component.getNode(),
				NodeComponentRemovedListener.class);
		for (int i = 0; i < nodeListeners.size(); i++) {
			nodeListeners.get(i).componentRemoved(component);
		}
	}

	public interface ComponentActivityListener extends EventSubscription {
		void componentActivated(SceneNodeComponent component);

		void componentDeactivated(SceneNodeComponent component);
	}

	public interface ComponentAddedListener extends EventSubscription {
		void componentAdded(SceneNodeComponent component);
	}

	public interface ComponentRemovedListener extends EventSubscription {
		void componentRemoved(SceneNodeComponent component);
	}

	public interface NodeComponentActivityListener extends EventSubscription {
		void componentActivated(SceneNodeComponent component);

		void componentDeactivated(SceneNodeComponent component);
	}

	public interface NodeComponentAddedListener extends EventSubscription {
		void componentAdded(SceneNodeComponent component);
	}

	public interface NodeComponentRemovedListener extends EventSubscription {
		void componentRemoved(SceneNodeComponent component);
	}
}
