package com.gurella.engine.graph.event;

import static com.gurella.engine.graph.event.EventCallbackRegistry.getCallbacks;

import com.badlogic.gdx.utils.IntMap;
import com.gurella.engine.graph.GraphListenerSystem;
import com.gurella.engine.graph.SceneNode;
import com.gurella.engine.graph.SceneNodeComponent;
import com.gurella.engine.graph.event.EventTrigger.NopEventTrigger;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.ReflectionUtils;

public class EventSystem extends GraphListenerSystem {
	private final IntMap<ArrayExt<SceneNodeComponent>> componentsByCallback = new IntMap<ArrayExt<SceneNodeComponent>>();
	private final IntMap<IntMap<ArrayExt<SceneNodeComponent>>> nodeComponentsByCallback = new IntMap<IntMap<ArrayExt<SceneNodeComponent>>>();
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

		componentsByCallback.clear();
		nodeComponentsByCallback.clear();
	}

	@Override
	public void componentActivated(SceneNodeComponent component) {
		int nodeId = component.getNode().id;
		for (EventCallbackIdentifier<?> callback : getCallbacks(component.getClass())) {
			int callbackId = callback.id;
			findListeners(callbackId).add(component);
			findListeners(nodeId, callbackId).add(component);
			ensureTrigger(callback);
		}
	}

	private void ensureTrigger(EventCallbackIdentifier<?> callback) {
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
		int nodeId = component.getNode().id;
		for (EventCallbackIdentifier<?> callback : getCallbacks(component.getClass())) {
			int callbackId = callback.id;
			componentsByCallback.get(callbackId).removeValue(component, true);
			findListeners(nodeId, callbackId).removeValue(component, true);
		}
	}

	private ArrayExt<SceneNodeComponent> findListeners(int callbackId) {
		ArrayExt<SceneNodeComponent> listeners = componentsByCallback.get(callbackId);
		if (listeners == null) {
			listeners = new ArrayExt<SceneNodeComponent>();
			componentsByCallback.put(callbackId, listeners);
		}
		return listeners;
	}

	private ArrayExt<SceneNodeComponent> findListeners(int nodeId, int callbackId) {
		IntMap<ArrayExt<SceneNodeComponent>> listenersByNode = nodeComponentsByCallback.get(nodeId);
		if (listenersByNode == null) {
			listenersByNode = new IntMap<ArrayExt<SceneNodeComponent>>();
			nodeComponentsByCallback.put(nodeId, listenersByNode);
		}

		ArrayExt<SceneNodeComponent> listeners = listenersByNode.get(callbackId);
		if (listeners == null) {
			listeners = new ArrayExt<SceneNodeComponent>();
			listenersByNode.put(callbackId, listeners);
		}

		return listeners;
	}

	public <T> ImmutableArray<T> getListeners(EventCallbackIdentifier<T> callback) {
		@SuppressWarnings("unchecked")
		ArrayExt<T> listeners = (ArrayExt<T>) componentsByCallback.get(callback.id);
		return listeners == null ? ImmutableArray.<T> empty() : listeners.immutable();
	}

	public <T> ImmutableArray<T> getListeners(SceneNode node, EventCallbackIdentifier<T> callback) {
		int nodeId = node.id;
		IntMap<ArrayExt<SceneNodeComponent>> listenersByNode = nodeComponentsByCallback.get(nodeId);
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

	public <T> void notify(CallbackEvent<T> event) {
		ImmutableArray<T> listeners = getListeners(event.eventCallbackIdentifier);
		for (int i = 0; i < listeners.size(); i++) {
			T script = listeners.get(i);
			event.notify(script);
		}
	}
}
