package com.gurella.engine.graph.event;

import static com.gurella.engine.graph.event.EventCallbackRegistry.getCallbacks;
import static com.gurella.engine.graph.event.EventCallbackRegistry.getPriority;

import java.util.Comparator;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Pools;
import com.gurella.engine.graph.GraphListenerSystem;
import com.gurella.engine.graph.SceneGraphElement;
import com.gurella.engine.graph.SceneNode;
import com.gurella.engine.graph.SceneNodeComponent;
import com.gurella.engine.graph.event.EventTrigger.NopEventTrigger;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.ReflectionUtils;

public class EventSystem extends GraphListenerSystem {
	private final IntMap<ArrayExt<Object>> listenersByCallback = new IntMap<ArrayExt<Object>>();
	private final IntMap<IntMap<ArrayExt<Object>>> nodeListenersByCallback = new IntMap<IntMap<ArrayExt<Object>>>();
	private final IntMap<EventTrigger> triggers = new IntMap<EventTrigger>();

	@Override
	protected void activated() {
		for (EventTrigger trigger : triggers.values()) {
			trigger.activated();
		}

		ImmutableArray<? extends SceneNodeComponent> components = getGraph().activeComponents;
		for (int i = 0; i < components.size(); i++) {
			componentActivated(components.get(i));
		}
	}

	@Override
	protected void deactivated() {
		for (EventTrigger trigger : triggers.values()) {
			trigger.deactivated();
		}

		listenersByCallback.clear();
		nodeListenersByCallback.clear();
	}

	@Override
	public void componentActivated(SceneNodeComponent component) {
		register(component.getNode().id, component);
	}

	public void register(Object listener) {
		ObjectSet<EventCallbackIdentifier<?>> callbacks = getCallbacks(listener.getClass());
		if (callbacks.size == 0) {
			return;
		}

		ListenersComparator comparator = Pools.obtain(ListenersComparator.class);

		for (EventCallbackIdentifier<?> callback : callbacks) {
			int callbackId = callback.id;
			comparator.callbackId = callbackId;
			ArrayExt<Object> listeners = findListeners(callbackId);
			listeners.add(listener);
			listeners.sort(comparator);
			ensureTriggerExists(callback);
		}

		Pools.free(comparator);
	}

	public void register(SceneGraphElement element, Object listener) {
		register(element.id, listener);
	}

	private void register(int elementId, Object listener) {
		ObjectSet<EventCallbackIdentifier<?>> callbacks = getCallbacks(listener.getClass());
		if (callbacks.size == 0) {
			return;
		}

		ListenersComparator comparator = Pools.obtain(ListenersComparator.class);

		for (EventCallbackIdentifier<?> callback : callbacks) {
			int callbackId = callback.id;
			comparator.callbackId = callbackId;
			ArrayExt<Object> listeners = findListeners(callbackId);
			listeners.add(listener);
			listeners.sort(comparator);
			ArrayExt<Object> listenersByElement = findListenersByElement(elementId, callbackId);
			listenersByElement.add(listener);
			listenersByElement.sort(comparator);
			ensureTriggerExists(callback);
		}

		Pools.free(comparator);
	}

	private void ensureTriggerExists(EventCallbackIdentifier<?> callback) {
		int callbackId = callback.id;
		if (!triggers.containsKey(callbackId)) {
			Class<? extends EventTrigger> triggerClass = callback.triggerClass;
			if (NopEventTrigger.class.equals(triggerClass)) {
				triggers.put(callbackId, NopEventTrigger.instance);
			} else {
				EventTrigger trigger = ReflectionUtils.newInstance(triggerClass);
				trigger.eventSystem = this;
				trigger.activated();
				triggers.put(callbackId, trigger);
			}
		}
	}

	@Override
	public void componentDeactivated(SceneNodeComponent component) {
		unregister(component.getNode().id, component);
	}

	public void unregister(Object listener) {
		ObjectSet<EventCallbackIdentifier<?>> callbacks = getCallbacks(listener.getClass());
		if (callbacks.size == 0) {
			return;
		}

		for (EventCallbackIdentifier<?> callback : callbacks) {
			int callbackId = callback.id;
			ArrayExt<Object> listeners = listenersByCallback.get(callbackId);
			if (listeners != null) {
				listeners.removeValue(listener, true);
			}
		}
	}

	public void unregister(SceneGraphElement element, Object listener) {
		unregister(element.id, listener);
	}

	private void unregister(int elementId, Object listener) {
		ObjectSet<EventCallbackIdentifier<?>> callbacks = getCallbacks(listener.getClass());
		if (callbacks.size == 0) {
			return;
		}

		for (EventCallbackIdentifier<?> callback : callbacks) {
			int callbackId = callback.id;
			ArrayExt<Object> listeners = listenersByCallback.get(callbackId);
			if (listeners != null) {
				listeners.removeValue(listener, true);
			}

			IntMap<ArrayExt<Object>> listenersByElement = nodeListenersByCallback.get(elementId);
			if (listenersByElement != null) {
				listeners = listenersByElement.get(callbackId);
				if (listeners != null) {
					listeners.removeValue(listener, true);
				}
			}
		}
	}

	private ArrayExt<Object> findListeners(int callbackId) {
		ArrayExt<Object> listeners = listenersByCallback.get(callbackId);
		if (listeners == null) {
			listeners = new ArrayExt<Object>();
			listenersByCallback.put(callbackId, listeners);
		}
		return listeners;
	}

	private ArrayExt<Object> findListenersByElement(int elementId, int callbackId) {
		IntMap<ArrayExt<Object>> listenersByElement = nodeListenersByCallback.get(elementId);
		if (listenersByElement == null) {
			listenersByElement = new IntMap<ArrayExt<Object>>();
			nodeListenersByCallback.put(elementId, listenersByElement);
		}

		ArrayExt<Object> listeners = listenersByElement.get(callbackId);
		if (listeners == null) {
			listeners = new ArrayExt<Object>();
			listenersByElement.put(callbackId, listeners);
		}

		return listeners;
	}

	public <T> ImmutableArray<T> getListeners(EventCallbackIdentifier<T> callback) {
		@SuppressWarnings("unchecked")
		ArrayExt<T> listeners = (ArrayExt<T>) listenersByCallback.get(callback.id);
		return listeners == null ? ImmutableArray.<T> empty() : listeners.immutable();
	}

	public <T> ImmutableArray<T> getListeners(SceneGraphElement element, EventCallbackIdentifier<T> callback) {
		IntMap<ArrayExt<Object>> listenersByNode = nodeListenersByCallback.get(element.id);
		if (listenersByNode == null) {
			return ImmutableArray.<T> empty();
		}
		int callbackId = callback.id;
		@SuppressWarnings("unchecked")
		ArrayExt<T> listeners = (ArrayExt<T>) listenersByNode.get(callbackId);
		return listeners == null ? ImmutableArray.<T> empty() : listeners.immutable();
	}

	// TODO remove
	public <T> void notify(SceneNode node, CallbackEvent<T> event) {
		ImmutableArray<T> listeners = getListeners(node, event.eventCallbackIdentifier);
		for (int i = 0; i < listeners.size(); i++) {
			T script = listeners.get(i);
			event.notify(script);
		}
	}

	public <T> void notifyParentHierarchy(SceneNode node, CallbackEvent<T> event) {
		notify(node, event);
		SceneNode parent = node.getParent();
		if (parent != null) {
			notifyParentHierarchy(parent, event);
		}
	}

	public <T> void notifyChildHierarchy(SceneNode node, CallbackEvent<T> event) {
		notify(node, event);
		ImmutableArray<SceneNode> children = node.children;
		for (int i = 0; i < children.size(); i++) {
			SceneNode child = children.get(i);
			if (child.isActive()) {
				notifyChildHierarchy(child, event);
			}
		}
	}

	public <T> void notify(CallbackEvent<T> event) {
		ImmutableArray<T> listeners = getListeners(event.eventCallbackIdentifier);
		for (int i = 0; i < listeners.size(); i++) {
			T script = listeners.get(i);
			event.notify(script);
		}
	}

	private static class ListenersComparator implements Comparator<Object> {
		private int callbackId;

		@Override
		public int compare(Object o1, Object o2) {
			return Integer.compare(getPriority(o1.getClass(), callbackId), getPriority(o2.getClass(), callbackId));
		}
	}
}
