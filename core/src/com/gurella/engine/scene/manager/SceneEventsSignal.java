package com.gurella.engine.scene.manager;

import com.gurella.engine.scene.SceneNode;
import com.gurella.engine.scene.SceneNodeComponent;
import com.gurella.engine.scene.SceneSystem;
import com.gurella.engine.scene.event.EventManager;
import com.gurella.engine.scene.event.EventSubscription;
import com.gurella.engine.utils.ImmutableArray;

//TODO unused
public final class SceneEventsSignal {
	private EventManager eventManager;

	public SceneEventsSignal(EventManager eventManager) {
		this.eventManager = eventManager;
	}

	public void systemActivated(SceneSystem system) {
		ImmutableArray<SystemActivatedListener> listeners = eventManager.getListeners(SystemActivatedListener.class);
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).systemActivated(system);
		}
	}

	public void systemDeactivated(SceneSystem system) {
		ImmutableArray<SystemDeactivatedListener> listeners = eventManager
				.getListeners(SystemDeactivatedListener.class);
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).systemDeactivated(system);
		}
	}

	public void systemAdded(SceneSystem system) {
		ImmutableArray<SystemAddedListener> listeners = eventManager.getListeners(SystemAddedListener.class);
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).systemAdded(system);
		}
	}

	public void systemRemoved(SceneSystem system) {
		ImmutableArray<SystemRemovedListener> listeners = eventManager.getListeners(SystemRemovedListener.class);
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).systemRemoved(system);
		}
	}

	public void nodeActivated(SceneNode node) {
		ImmutableArray<NodeActivatedListener> listeners = eventManager.getListeners(NodeActivatedListener.class);
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).nodeActivated(node);
		}
	}

	public void nodeDeactivated(SceneNode node) {
		ImmutableArray<NodeDeactivatedListener> listeners = eventManager.getListeners(NodeDeactivatedListener.class);
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).nodeDeactivated(node);
		}
	}

	public void nodeAdded(SceneNode node) {
		ImmutableArray<NodeAddedListener> listeners = eventManager.getListeners(NodeAddedListener.class);
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).nodeAdded(node);
		}
	}

	public void nodeRemoved(SceneNode node) {
		ImmutableArray<NodeRemovedListener> listeners = eventManager.getListeners(NodeRemovedListener.class);
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).nodeRemoved(node);
		}
	}

	public void componentActivated(SceneNodeComponent component) {
		ImmutableArray<ComponentActivatedListener> listeners = eventManager
				.getListeners(ComponentActivatedListener.class);
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).componentActivated(component);
		}

		ImmutableArray<NodeComponentActivatedListener> nodeListeners = eventManager.getListeners(component.getNode(),
				NodeComponentActivatedListener.class);
		for (int i = 0; i < nodeListeners.size(); i++) {
			nodeListeners.get(i).componentActivated(component);
		}
	}

	public void componentDeactivated(SceneNodeComponent component) {
		ImmutableArray<ComponentDeactivatedListener> listeners = eventManager
				.getListeners(ComponentDeactivatedListener.class);
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).componentDeactivated(component);
		}

		ImmutableArray<NodeComponentDeactivatedListener> nodeListeners = eventManager.getListeners(component.getNode(),
				NodeComponentDeactivatedListener.class);
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

	public interface SystemActivatedListener extends EventSubscription {
		void systemActivated(SceneSystem system);
	}

	public interface SystemDeactivatedListener extends EventSubscription {
		void systemDeactivated(SceneSystem system);
	}

	public interface SystemAddedListener extends EventSubscription {
		void systemAdded(SceneSystem system);
	}

	public interface SystemRemovedListener extends EventSubscription {
		void systemRemoved(SceneSystem system);
	}

	public interface NodeActivatedListener extends EventSubscription {
		void nodeActivated(SceneNode node);
	}

	public interface NodeDeactivatedListener extends EventSubscription {
		void nodeDeactivated(SceneNode node);
	}

	public interface NodeAddedListener extends EventSubscription {
		void nodeAdded(SceneNode node);
	}

	public interface NodeRemovedListener extends EventSubscription {
		void nodeRemoved(SceneNode node);
	}

	public interface ComponentActivatedListener extends EventSubscription {
		void componentActivated(SceneNodeComponent component);
	}

	public interface ComponentDeactivatedListener extends EventSubscription {
		void componentDeactivated(SceneNodeComponent component);
	}

	public interface ComponentAddedListener extends EventSubscription {
		void componentAdded(SceneNodeComponent component);
	}

	public interface ComponentRemovedListener extends EventSubscription {
		void componentRemoved(SceneNodeComponent component);
	}

	public interface NodeComponentActivatedListener extends EventSubscription {
		void componentActivated(SceneNodeComponent component);
	}

	public interface NodeComponentDeactivatedListener extends EventSubscription {
		void componentDeactivated(SceneNodeComponent component);
	}

	public interface NodeComponentAddedListener extends EventSubscription {
		void componentAdded(SceneNodeComponent component);
	}

	public interface NodeComponentRemovedListener extends EventSubscription {
		void componentRemoved(SceneNodeComponent component);
	}
}
