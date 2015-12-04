package com.gurella.engine.graph;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntMap.Values;
import com.gurella.engine.graph.behaviour.BehaviourEventCallbacks;
import com.gurella.engine.graph.behaviour.BehaviourComponent;
import com.gurella.engine.resource.model.ResourceProperty;
import com.gurella.engine.resource.model.TransientProperty;
import com.gurella.engine.resource.model.common.SceneNodeChildrenModelProperty;
import com.gurella.engine.resource.model.common.SceneNodeComponentsModelProperty;
import com.gurella.engine.signal.AbstractSignal;
import com.gurella.engine.signal.Signal1.Signal1Impl;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.ImmutableBits;
import com.gurella.engine.utils.ImmutableIntMapValues;

//TODO make SceneNodeSignal usable
public final class SceneNode extends SceneGraphElement {
	@TransientProperty
	SceneNode parent;

	@ResourceProperty(model = SceneNodeChildrenModelProperty.class)
	final Array<SceneNode> childrenInternal = new Array<SceneNode>();
	@TransientProperty
	public final ImmutableArray<SceneNode> children = ImmutableArray.with(childrenInternal);

	@ResourceProperty(model = SceneNodeComponentsModelProperty.class)
	final IntMap<SceneNodeComponent> componentsInternal = new IntMap<SceneNodeComponent>();
	@TransientProperty
	public final ImmutableIntMapValues<SceneNodeComponent> components = ImmutableIntMapValues.with(componentsInternal);
	@TransientProperty
	final Bits componentBitsInternal = new Bits();
	@TransientProperty
	public final ImmutableBits componentBits = new ImmutableBits(componentBitsInternal);
	@TransientProperty
	final Bits activeComponentBitsInternal = new Bits();
	@TransientProperty
	public final ImmutableBits activeComponentBits = new ImmutableBits(activeComponentBitsInternal);

	@TransientProperty
	public final Signal1Impl<SceneNode> parentChangedSignal = new Signal1Impl<SceneNode>();
	@TransientProperty
	public final Signal1Impl<SceneNode> childAddedSignal = new Signal1Impl<SceneNode>();
	@TransientProperty
	public final Signal1Impl<SceneNode> childRemovedSignal = new Signal1Impl<SceneNode>();
	@TransientProperty
	public final Signal1Impl<SceneNodeComponent> componentAddedSignal = new Signal1Impl<SceneNodeComponent>();
	@TransientProperty
	public final Signal1Impl<SceneNodeComponent> componentRemovedSignal = new Signal1Impl<SceneNodeComponent>();
	@TransientProperty
	public final Signal1Impl<SceneNodeComponent> componentActivatedSignal = new Signal1Impl<SceneNodeComponent>();
	@TransientProperty
	public final Signal1Impl<SceneNodeComponent> componentDeactivatedSignal = new Signal1Impl<SceneNodeComponent>();
	@TransientProperty
	public final NodeChangedSignal nodeChangedSignal = new NodeChangedSignal();

	public SceneNode getParent() {
		return parent;
	}

	@Override
	final void activate() {
		if (graph != null) {
			graph.activateNode(this);
		}
	}

	@Override
	final void deactivate() {
		if (graph != null) {
			graph.deactivateNode(this);
		}
	}

	@Override
	public final void detach() {
		if (graph != null) {
			graph.removeNode(this);
		}
	}

	@Override
	public final void dispose() {
		for (int i = 0; i < childrenInternal.size; i++) {
			SceneNode child = childrenInternal.get(i);
			child.dispose();
		}

		for (SceneNodeComponent component : componentsInternal.values()) {
			component.dispose();
		}

		detach();
		disposedSignal.dispatch();
		INDEXER.removeIndexed(this);
	}

	public final boolean isHierarchyEnabled() {
		return this.enabled && (parent == null || parent.isHierarchyEnabled());
	}

	@Override
	public final void setEnabled(boolean enabled) {
		if (this.enabled == enabled) {
			return;
		} else {
			this.enabled = enabled;
			boolean hierarchyEnabled = isHierarchyEnabled();
			if (!hierarchyEnabled && active) {
				deactivate();
			} else if (hierarchyEnabled && !active) {
				activate();
			}
		}
	}

	public void addComponent(SceneNodeComponent component) {
		if (graph == null) {
			if (component.graph != null || component.node != null) {
				throw new IllegalStateException("Component already belongs to node.");
			}

			int baseComponentType = component.baseComponentType;
			if (componentsInternal.containsKey(baseComponentType)) {
				throw new IllegalStateException(
						"Node already contains component of type: " + component.getClass().getSimpleName());
			}

			componentsInternal.put(baseComponentType, component);
			componentBitsInternal.set(component.componentType);
			nodeChangedSignal.componentAdded(component);
		} else {
			graph.addComponent(this, component);
		}
	}

	public void removeComponent(Class<? extends SceneNodeComponent> componentType) {
		int baseComponentType = SceneNodeComponent.getBaseComponentType(componentType);
		if (graph == null) {
			SceneNodeComponent removed = componentsInternal.remove(baseComponentType);
			if (removed != null) {
				nodeChangedSignal.componentRemoved(removed);
				componentBitsInternal.clear(removed.componentType);
			}
		} else {
			SceneNodeComponent component = componentsInternal.get(baseComponentType);
			if (component != null) {
				graph.removeComponent(component);
			}
		}
	}

	public <T extends SceneNodeComponent> T getComponent(int componentType) {
		@SuppressWarnings("unchecked")
		T value = (T) componentsInternal.get(componentType);
		return value;
	}

	public <T extends SceneNodeComponent> T getComponent(Class<T> componentClass) {
		@SuppressWarnings("unchecked")
		T value = (T) componentsInternal.get(SceneNodeComponent.findBaseComponentType(componentClass));
		return value;
	}

	public <T extends SceneNodeComponent> T getActiveComponent(Class<T> componentClass) {
		T component = getComponent(componentClass);
		return component == null || !component.isActive() ? null : component;
	}

	public <T extends SceneNodeComponent> T getComponentSafely(Class<T> componentClass) {
		@SuppressWarnings("unchecked")
		T value = (T) componentsInternal.get(SceneNodeComponent.findBaseComponentType(componentClass));
		if (value == null || value.baseComponentType == value.componentType) {
			return value;
		} else {
			return SceneNodeComponent.isSubtype(componentClass, value.getClass()) ? value : null;
		}
	}

	public <T extends SceneNodeComponent> T getActiveComponentSafely(Class<T> componentClass) {
		T component = getComponentSafely(componentClass);
		return component == null || !component.isActive() ? null : component;
	}

	public Values<SceneNodeComponent> getComponents() {
		return components;
	}

	public void addChild(SceneNode child) {
		if (child.parent != null) {
			throw new IllegalStateException("Child already belongs to node.");
		}

		if (graph == null) {
			if (child.graph != null) {
				throw new IllegalStateException("Child is owned by other graph.");
			}

			child.parent = this;
			childrenInternal.add(child);
			nodeChangedSignal.childAdded(child);
			child.nodeChangedSignal.parentChanged(child);
		} else {
			child.parent = this;
			childrenInternal.add(child);

			if (child.graph == null) {
				graph.addNode(child);
			}
		}
	}

	public void removeChild(SceneNode child) {
		if (child.parent == this) {
			if (child.graph != null) {
				graph.removeNode(child);
			}

			// TODO graph must handle child
			childrenInternal.removeValue(child, true);
			child.nodeChangedSignal.parentChanged(null);
		} else {
			throw new IllegalStateException("Child is not owned by node.");
		}
	}

	public void broadcastMessage(Object sender, Object messageType, Object messageData) {
		if (graph != null) {
			ImmutableArray<BehaviourComponent> listeners = graph.eventSystem.getListeners(this,
					BehaviourEventCallbacks.onMessage);
			for (int i = 0; i < listeners.size(); i++) {
				BehaviourComponent listener = listeners.get(i);
				listener.onMessage(sender, messageType, messageData);
			}
		}
	}

	public void broadcastMessageToChildren(Object sender, Object messageType, Object messageData) {
		broadcastMessage(sender, messageType, messageData);
		for (int i = 0; i < childrenInternal.size; i++) {
			SceneNode child = childrenInternal.get(i);
			child.broadcastMessageToChildren(sender, messageType, messageData);
		}
	}

	public void broadcastMessageToParents(Object sender, Object messageType, Object messageData) {
		broadcastMessage(sender, messageType, messageData);
		if (parent != null) {
			parent.broadcastMessageToParents(sender, messageType, messageData);
		}
	}

	@Override
	public final void reset() {
		for (SceneNodeComponent component : componentsInternal.values()) {
			component.reset();
		}

		for (SceneNode child : childrenInternal) {
			child.reset();
		}

		resettedSignal.dispatch();
		clearSignals();
		initialized = false;
		componentBitsInternal.clear();
		activeComponentBitsInternal.clear();
	}

	public interface NodeChangedListener {
		void parentChanged(SceneNode newParent);

		void childAdded(SceneNode child);

		void childRemoved(SceneNode child);

		void componentAdded(SceneNodeComponent component);

		void componentRemoved(SceneNodeComponent component);

		void componentActivated(SceneNodeComponent component);

		void componentDeactivated(SceneNodeComponent component);
	}

	public static class NodeChangedListenerAdapter implements NodeChangedListener {
		@Override
		public void parentChanged(SceneNode newParent) {
		}

		@Override
		public void childAdded(SceneNode child) {
		}

		@Override
		public void childRemoved(SceneNode child) {
		}

		@Override
		public void componentAdded(SceneNodeComponent component) {
		}

		@Override
		public void componentRemoved(SceneNodeComponent component) {
		}

		@Override
		public void componentActivated(SceneNodeComponent component) {
		}

		@Override
		public void componentDeactivated(SceneNodeComponent component) {
		}
	}

	public class NodeChangedSignal extends AbstractSignal<NodeChangedListener> {
		private NodeChangedSignal() {
		}

		void parentChanged(SceneNode newParent) {
			for (NodeChangedListener listener : listeners) {
				listener.parentChanged(newParent);
			}
			parentChangedSignal.dispatch(newParent);
		}

		void childAdded(SceneNode child) {
			for (NodeChangedListener listener : listeners) {
				listener.childAdded(child);
			}
			childAddedSignal.dispatch(child);
		}

		void childRemoved(SceneNode child) {
			for (NodeChangedListener listener : listeners) {
				listener.childRemoved(child);
			}
			childRemovedSignal.dispatch(child);
		}

		void componentAdded(SceneNodeComponent component) {
			for (NodeChangedListener listener : listeners) {
				listener.componentAdded(component);
			}
			componentAddedSignal.dispatch(component);
		}

		void componentRemoved(SceneNodeComponent component) {
			for (NodeChangedListener listener : listeners) {
				listener.componentRemoved(component);
			}
			componentRemovedSignal.dispatch(component);
		}

		void componentActivated(SceneNodeComponent component) {
			for (NodeChangedListener listener : listeners) {
				listener.componentActivated(component);
			}
			componentActivatedSignal.dispatch(component);
		}

		void componentDeactivated(SceneNodeComponent component) {
			for (NodeChangedListener listener : listeners) {
				listener.componentDeactivated(component);
			}
			componentDeactivatedSignal.dispatch(component);
		}
	}
}
