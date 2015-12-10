package com.gurella.engine.graph.event;

import static com.gurella.engine.graph.event.EventRegistry.getCallbacks;
import static com.gurella.engine.graph.event.EventRegistry.getPriority;
import static com.gurella.engine.graph.event.EventRegistry.getSubscriptions;

import java.util.Comparator;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
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

public class EventManager extends GraphListenerSystem {
	private final IntMap<ArrayExt<Object>> listenersByCallback = new IntMap<ArrayExt<Object>>();
	private final ObjectMap<Class<?>, ArrayExt<Object>> listenersBySubscription = new ObjectMap<Class<?>, ArrayExt<Object>>();
	private final IntMap<IntMap<ArrayExt<Object>>> elementListenersByCallback = new IntMap<IntMap<ArrayExt<Object>>>();
	private final IntMap<ObjectMap<Class<?>, ArrayExt<Object>>> elementListenersBySubscription = new IntMap<ObjectMap<Class<?>, ArrayExt<Object>>>();
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
		elementListenersByCallback.clear();
	}

	@Override
	public void componentActivated(SceneNodeComponent component) {
		register(component.getNode().id, component);
	}

	public void register(Object listener) {
		registerCallbacks(listener);
		registerSubscriptions(listener);
	}

	private void registerCallbacks(Object listener) {
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

	private void registerSubscriptions(Object listener) {
		ObjectSet<Class<?>> subscriptions = getSubscriptions(listener.getClass());
		if (subscriptions.size == 0) {
			return;
		}

		for (Class<?> subscription : subscriptions) {
			ArrayExt<Object> subscribers = findSubscribers(subscription);
			subscribers.add(listener);
			// TODO subscribers.sort(comparator);
			// TODO trigger
		}
	}

	public void register(SceneGraphElement element, Object listener) {
		register(element.id, listener);
	}

	private void register(int elementId, Object listener) {
		registerCallbacks(elementId, listener);
		registerSubscriptions(elementId, listener);
	}

	private void registerCallbacks(int elementId, Object listener) {
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

			ArrayExt<Object> listenersByElement = findListeners(elementId, callbackId);
			listenersByElement.add(listener);
			listenersByElement.sort(comparator);

			ensureTriggerExists(callback);
		}

		Pools.free(comparator);
	}

	private void registerSubscriptions(int elementId, Object listener) {
		ObjectSet<Class<?>> subscriptions = getSubscriptions(listener.getClass());
		if (subscriptions.size == 0) {
			return;
		}

		for (Class<?> subscription : subscriptions) {
			ArrayExt<Object> subscribers = findSubscribers(subscription);
			subscribers.add(listener);
			// TODO subscribers.sort(comparator);

			ArrayExt<Object> listenersByElement = findSubscribers(elementId, subscription);
			listenersByElement.add(listener);
			// TODO listenersByElement.sort(comparator);
			// TODO trigger
		}
	}

	private void ensureTriggerExists(EventCallbackIdentifier<?> callback) {
		int callbackId = callback.id;
		if (!triggers.containsKey(callbackId)) {
			Class<? extends EventTrigger> triggerClass = callback.triggerClass;
			if (NopEventTrigger.class.equals(triggerClass)) {
				triggers.put(callbackId, NopEventTrigger.instance);
			} else {
				EventTrigger trigger = ReflectionUtils.newInstance(triggerClass);
				trigger.eventManager = this;
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
		unregisterCallbacks(listener);
		unregisterSubscriptions(listener);
	}

	private void unregisterCallbacks(Object listener) {
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

	private void unregisterSubscriptions(Object listener) {
		ObjectSet<Class<?>> subscriptions = getSubscriptions(listener.getClass());
		if (subscriptions.size == 0) {
			return;
		}

		for (Class<?> subscription : subscriptions) {
			ArrayExt<Object> listeners = listenersBySubscription.get(subscription);
			if (listeners != null) {
				listeners.removeValue(listener, true);
			}
		}
	}

	public void unregister(SceneGraphElement element, Object listener) {
		unregister(element.id, listener);
	}

	private void unregister(int elementId, Object listener) {
		unregisterCallbacks(elementId, listener);
		unregisterSubscriptions(elementId, listener);
	}

	private void unregisterCallbacks(int elementId, Object listener) {
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

			IntMap<ArrayExt<Object>> listenersByElement = elementListenersByCallback.get(elementId);
			if (listenersByElement != null) {
				listeners = listenersByElement.get(callbackId);
				if (listeners != null) {
					listeners.removeValue(listener, true);
				}
			}
		}
	}

	private void unregisterSubscriptions(int elementId, Object listener) {
		ObjectSet<Class<?>> subscriptions = getSubscriptions(listener.getClass());
		if (subscriptions.size == 0) {
			return;
		}

		for (Class<?> subscription : subscriptions) {
			ArrayExt<Object> listeners = listenersBySubscription.get(subscription);
			if (listeners != null) {
				listeners.removeValue(listener, true);
			}

			ObjectMap<Class<?>, ArrayExt<Object>> listenersByElement = elementListenersBySubscription.get(elementId);
			if (listenersByElement != null) {
				listeners = listenersByElement.get(subscription);
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

	private ArrayExt<Object> findSubscribers(Class<?> subscription) {
		ArrayExt<Object> listeners = listenersBySubscription.get(subscription);
		if (listeners == null) {
			listeners = new ArrayExt<Object>();
			listenersBySubscription.put(subscription, listeners);
		}
		return listeners;
	}

	private ArrayExt<Object> findListeners(int elementId, int callbackId) {
		IntMap<ArrayExt<Object>> listenersByElement = elementListenersByCallback.get(elementId);
		if (listenersByElement == null) {
			listenersByElement = new IntMap<ArrayExt<Object>>();
			elementListenersByCallback.put(elementId, listenersByElement);
		}

		ArrayExt<Object> listeners = listenersByElement.get(callbackId);
		if (listeners == null) {
			listeners = new ArrayExt<Object>();
			listenersByElement.put(callbackId, listeners);
		}

		return listeners;
	}

	private ArrayExt<Object> findSubscribers(int elementId, Class<?> subscription) {
		ObjectMap<Class<?>, ArrayExt<Object>> listenersByElement = elementListenersBySubscription.get(elementId);
		if (listenersByElement == null) {
			listenersByElement = new ObjectMap<Class<?>, ArrayExt<Object>>();
			elementListenersBySubscription.put(elementId, listenersByElement);
		}

		ArrayExt<Object> listeners = listenersByElement.get(subscription);
		if (listeners == null) {
			listeners = new ArrayExt<Object>();
			listenersByElement.put(subscription, listeners);
		}

		return listeners;
	}

	public <T> ImmutableArray<T> getListeners(EventCallbackIdentifier<T> callback) {
		@SuppressWarnings("unchecked")
		ArrayExt<T> listeners = (ArrayExt<T>) listenersByCallback.get(callback.id);
		return listeners == null ? ImmutableArray.<T> empty() : listeners.immutable();
	}

	public <T> ImmutableArray<T> getListeners(SceneGraphElement element, EventCallbackIdentifier<T> callback) {
		IntMap<ArrayExt<Object>> listenersByElement = elementListenersByCallback.get(element.id);
		if (listenersByElement == null) {
			return ImmutableArray.<T> empty();
		}
		int callbackId = callback.id;
		@SuppressWarnings("unchecked")
		ArrayExt<T> listeners = (ArrayExt<T>) listenersByElement.get(callbackId);
		return listeners == null ? ImmutableArray.<T> empty() : listeners.immutable();
	}

	public <T extends EventSubscription> ImmutableArray<T> getListeners(Class<T> subscription) {
		@SuppressWarnings("unchecked")
		ArrayExt<T> listeners = (ArrayExt<T>) listenersBySubscription.get(subscription);
		return listeners == null ? ImmutableArray.<T> empty() : listeners.immutable();
	}

	public <T extends EventSubscription> ImmutableArray<T> getListeners(SceneGraphElement element,
			Class<T> subscription) {
		ObjectMap<Class<?>, ArrayExt<Object>> listenersByElement = elementListenersBySubscription.get(element.id);
		if (listenersByElement == null) {
			return ImmutableArray.<T> empty();
		}
		@SuppressWarnings("unchecked")
		ArrayExt<T> listeners = (ArrayExt<T>) listenersByElement.get(subscription);
		return listeners == null ? ImmutableArray.<T> empty() : listeners.immutable();
	}

	// TODO remove
	public <T> void notify(SceneGraphElement element, CallbackEvent<T> event) {
		ImmutableArray<T> listeners = getListeners(element, event.eventCallbackIdentifier);
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

	public <T extends EventSubscription> void notify(SceneGraphElement element, SubscriptionEvent<T> event) {
		ImmutableArray<T> listeners = getListeners(element, event.subscriptionType);
		for (int i = 0; i < listeners.size(); i++) {
			T script = listeners.get(i);
			event.notify(script);
		}
	}

	public <T extends EventSubscription> void notifyParentHierarchy(SceneNode node, SubscriptionEvent<T> event) {
		notify(node, event);
		SceneNode parent = node.getParent();
		if (parent != null) {
			notifyParentHierarchy(parent, event);
		}
	}

	public <T extends EventSubscription> void notifyChildHierarchy(SceneNode node, SubscriptionEvent<T> event) {
		notify(node, event);
		ImmutableArray<SceneNode> children = node.children;
		for (int i = 0; i < children.size(); i++) {
			SceneNode child = children.get(i);
			if (child.isActive()) {
				notifyChildHierarchy(child, event);
			}
		}
	}

	public <T extends EventSubscription> void notify(SubscriptionEvent<T> event) {
		ImmutableArray<T> listeners = getListeners(event.subscriptionType);
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
