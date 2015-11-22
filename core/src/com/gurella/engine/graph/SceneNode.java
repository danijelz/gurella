package com.gurella.engine.graph;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntMap.Values;
import com.gurella.engine.resource.model.ResourceProperty;
import com.gurella.engine.resource.model.TransientProperty;
import com.gurella.engine.resource.model.common.SceneNodeChildrenModelProperty;
import com.gurella.engine.resource.model.common.SceneNodeComponentsModelProperty;
import com.gurella.engine.signal.AbstractSignal;
import com.gurella.engine.signal.Signal1.Signal1Impl;

//TODO add local event bus
//TODO make SceneNodeSignal usable
public class SceneNode extends SceneGraphElement {
	@TransientProperty
	SceneNode parent;

	// TODO make private
	@ResourceProperty(model = SceneNodeChildrenModelProperty.class)
	public final Array<SceneNode> children = new Array<SceneNode>();

	@ResourceProperty(model = SceneNodeComponentsModelProperty.class)
	final IntMap<SceneNodeComponent> components = new IntMap<SceneNodeComponent>();
	final Bits componentBits = new Bits();

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

	// TODO public final EventBus eventBus = new EventBus();

	public SceneNode getParent() {
		return parent;
	}

	public Bits getComponentBits() {
		return componentBits;
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
		for (SceneNode child : new Array<SceneNode>(children)) {
			child.dispose();
		}

		for (SceneNodeComponent component : components.values().toArray()) {
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

			int componentType = component.getImplementationComponentType();
			if (components.containsKey(componentType)) {
				throw new IllegalStateException("Node already contains component of type: "
						+ component.getClass().getSimpleName());
			}

			components.put(componentType, component);
			componentBits.set(component.componentType);
			nodeChangedSignal.componentAdded(component);
		} else {
			graph.addComponent(this, component);
		}
	}

	public void removeComponent(Class<? extends SceneNodeComponent> componentType) {
		removeComponent(SceneNodeComponent.getImplementationComponentType(componentType));
	}

	public void removeComponent(int componentType) {
		if (graph == null) {
			SceneNodeComponent removed = components.remove(componentType);
			if (removed != null) {
				nodeChangedSignal.componentRemoved(removed);
				componentBits.clear(removed.componentType);
			}
		} else {
			SceneNodeComponent component = components.get(componentType);
			if (component != null) {
				graph.removeComponent(component);
			}
		}
	}

	public <T extends SceneNodeComponent> T getComponent(Class<T> componentClass) {
		return getComponent(SceneNodeComponent.getImplementationComponentType(componentClass));
	}

	public <T extends SceneNodeComponent> T getActiveComponent(Class<T> componentClass) {
		T component = getComponent(componentClass);
		return component == null || !component.isActive()
				? null
				: component;
	}

	@SuppressWarnings("unchecked")
	public <T extends SceneNodeComponent> T getComponent(int componentType) {
		return (T) components.get(componentType);
	}

	public <T extends SceneNodeComponent> T getActiveComponent(int componentType) {
		T component = getComponent(componentType);
		return component == null || !component.isActive()
				? null
				: component;
	}

	public Values<SceneNodeComponent> getComponents() {
		return components.values();
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
			children.add(child);
			nodeChangedSignal.childAdded(child);
			child.nodeChangedSignal.parentChanged(child);
		} else {
			child.parent = this;
			children.add(child);

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
			children.removeValue(child, true);
			child.nodeChangedSignal.parentChanged(null);
		} else {
			throw new IllegalStateException("Child is not owned by node.");
		}
	}

	@Override
	public final void reset() {
		for (SceneNodeComponent component : components.values()) {
			component.reset();
		}

		for (SceneNode child : children) {
			child.reset();
		}

		resettedSignal.dispatch();
		clearSignals();
		initialized = false;
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
