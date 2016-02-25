package com.gurella.engine.scene.event;

import static com.gurella.engine.event.EventSubscriptions.getPriority;
import static com.gurella.engine.event.EventSubscriptions.getSubscriptions;

import java.util.Comparator;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.gurella.engine.event.EventSubscription;
import com.gurella.engine.scene.SceneElement;
import com.gurella.engine.scene.SceneNode;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.Values;

//TODO activate and deactivate triggers
public class EventManager {
	private final ObjectMap<Class<?>, ArrayExt<Object>> listenersBySubscription = new ObjectMap<Class<?>, ArrayExt<Object>>();
	private final IntMap<ObjectMap<Class<?>, ArrayExt<Object>>> elementListenersBySubscription = new IntMap<ObjectMap<Class<?>, ArrayExt<Object>>>();

	public void register(Object listener) {
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

	public void register(SceneElement element, Object listener) {
		register(element.id, listener);
	}

	private void register(int elementId, Object listener) {
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

	public void unregister(Object listener) {
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

	public void unregister(SceneElement element, Object listener) {
		unregister(element.id, listener);
	}

	private void unregister(int elementId, Object listener) {
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

	private ArrayExt<Object> findSubscribers(Class<?> subscription) {
		ArrayExt<Object> listeners = listenersBySubscription.get(subscription);
		if (listeners == null) {
			listeners = new ArrayExt<Object>();
			listenersBySubscription.put(subscription, listeners);
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

	public <T extends EventSubscription> ImmutableArray<T> getListeners(Class<T> subscription) {
		@SuppressWarnings("unchecked")
		ArrayExt<T> listeners = (ArrayExt<T>) listenersBySubscription.get(subscription);
		return listeners == null ? ImmutableArray.<T> empty() : listeners.immutable();
	}

	public <T extends EventSubscription> ImmutableArray<T> getListeners(SceneElement element, Class<T> subscription) {
		ObjectMap<Class<?>, ArrayExt<Object>> listenersByElement = elementListenersBySubscription.get(element.id);
		if (listenersByElement == null) {
			return ImmutableArray.<T> empty();
		}
		@SuppressWarnings("unchecked")
		ArrayExt<T> listeners = (ArrayExt<T>) listenersByElement.get(subscription);
		return listeners == null ? ImmutableArray.<T> empty() : listeners.immutable();
	}

	public <T extends EventSubscription> void notify(SceneElement element, SubscriptionEvent<T> event) {
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

	public void clear() {
		listenersBySubscription.clear();
		elementListenersBySubscription.clear();
	}

	private static class ListenersComparator implements Comparator<Object> {
		private int callbackId;

		@Override
		public int compare(Object o1, Object o2) {
			return Values.compare(getPriority(o1.getClass(), callbackId), getPriority(o2.getClass(), callbackId));
		}
	}
}
