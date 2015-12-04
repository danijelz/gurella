package com.gurella.engine.graph.event;

import static com.gurella.engine.graph.event.EventCallbackRegistry.getCallbacks;

import com.badlogic.gdx.utils.IntMap;
import com.gurella.engine.graph.GraphListenerSystem;
import com.gurella.engine.graph.SceneNode;
import com.gurella.engine.graph.SceneNodeComponent;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.Consumer;
import com.gurella.engine.utils.ImmutableArray;

public class EventSystem extends GraphListenerSystem {
	private final IntMap<ArrayExt<SceneNodeComponent>> componentsByCallback = new IntMap<ArrayExt<SceneNodeComponent>>();
	private final IntMap<IntMap<ArrayExt<SceneNodeComponent>>> nodeComponentsByCallback = new IntMap<IntMap<ArrayExt<SceneNodeComponent>>>();

	@Override
	protected void activated() {
		ImmutableArray<? extends SceneNodeComponent> components = getGraph().activeComponents;
		for (int i = 0; i < components.size(); i++) {
			componentActivated(components.get(i));
		}
	}

	@Override
	protected void deactivated() {
		componentsByCallback.clear();
		nodeComponentsByCallback.clear();
	}

	@Override
	public void componentActivated(SceneNodeComponent component) {
		int nodeId = component.getNode().id;
		for (EventCallbackInstance<?> callback : getCallbacks(component.getClass())) {
			int callbackId = callback.id;
			findListeners(callbackId).add(component);
			findListeners(nodeId, callbackId).add(component);
			callback.decorator.componentActivated(component);
		}
	}

	@Override
	public void componentDeactivated(SceneNodeComponent component) {
		int nodeId = component.getNode().id;
		for (EventCallbackInstance<?> callback : getCallbacks(component.getClass())) {
			int callbackId = callback.id;
			componentsByCallback.get(callbackId).removeValue(component, true);
			findListeners(nodeId, callbackId).removeValue(component, true);
			callback.decorator.componentDeactivated(component);
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

	public <T extends SceneNodeComponent> ImmutableArray<T> getListeners(EventCallbackInstance<T> callback) {
		@SuppressWarnings("unchecked")
		ArrayExt<T> listeners = (ArrayExt<T>) componentsByCallback.get(callback.id);
		return listeners == null ? ImmutableArray.<T> empty() : listeners.immutable();
	}

	public <T extends SceneNodeComponent> ImmutableArray<T> getListeners(SceneNode node,
			EventCallbackInstance<T> callback) {
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
	public <T extends SceneNodeComponent> void execute(SceneNode node, EventCallbackInstance<T> callback,
			Consumer<T> consumer) {
		ImmutableArray<T> listeners = getListeners(node, callback);
		for (int i = 0; i < listeners.size(); i++) {
			T script = listeners.get(i);
			consumer.accept(script);
		}
	}

	public <T extends SceneNodeComponent> void execute(Class<T> declaringClass, String id, SceneNode node,
			Consumer<T> consumer) {
		EventCallbackInstance<T> callback = EventCallbackInstance.get(declaringClass, id);
		ImmutableArray<T> listeners = getListeners(node, callback);
		for (int i = 0; i < listeners.size(); i++) {
			T script = listeners.get(i);
			consumer.accept(script);
		}
	}
}
