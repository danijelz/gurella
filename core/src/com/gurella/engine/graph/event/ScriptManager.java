package com.gurella.engine.graph.event;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntSet;
import com.badlogic.gdx.utils.IntSet.IntSetIterator;
import com.badlogic.gdx.utils.OrderedSet;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;
import com.gurella.engine.application.CommonUpdateOrder;
import com.gurella.engine.application.UpdateEvent;
import com.gurella.engine.application.UpdateListener;
import com.gurella.engine.event.EventService;
import com.gurella.engine.graph.GraphListenerSystem;
import com.gurella.engine.graph.SceneGraphListener;
import com.gurella.engine.graph.SceneNode;
import com.gurella.engine.graph.SceneNodeComponent;
import com.gurella.engine.graph.behaviour.BehaviourEvents;
import com.gurella.engine.graph.behaviour.BehaviourComponent;
import com.gurella.engine.pools.SynchronizedPools;
import com.gurella.engine.signal.Listener0;
import com.gurella.engine.signal.Listener1;

//TODO return null values
public class ScriptManager extends GraphListenerSystem {
	private static IntMap<OverridenScriptMethods> scriptMethods = new IntMap<OverridenScriptMethods>();

	private IntMap<OrderedSet<BehaviourComponent>> scriptsByMethod = new IntMap<OrderedSet<BehaviourComponent>>();
	private IntMap<IntMap<OrderedSet<BehaviourComponent>>> nodeScriptsByMethod = new IntMap<IntMap<OrderedSet<BehaviourComponent>>>();

	private OnInputUpdateListener onInputUpdateListener = new OnInputUpdateListener();
	private OnThinkUpdateListener onThinkUpdateListener = new OnThinkUpdateListener();
	private OnPreRenderUpdateListener onPreRenderUpdateListener = new OnPreRenderUpdateListener();
	private OnRenderUpdateListener onRenderUpdateListener = new OnRenderUpdateListener();
	private OnDebugRenderUpdateListener onDebugRenderUpdateListener = new OnDebugRenderUpdateListener();
	private OnAfterRenderUpdateListener onAfterRenderUpdateListener = new OnAfterRenderUpdateListener();
	private OnCleanupUpdateListener onCleanupUpdateListener = new OnCleanupUpdateListener();
	private ScriptSceneGraphListener scriptSceneGraphListener = new ScriptSceneGraphListener();
	private SceneStartListener sceneStartListener = new SceneStartListener();
	private SceneStopListener sceneStopListener = new SceneStopListener();
	private PauseListener pauseListener = new PauseListener();
	private ResumeListener resumeListener = new ResumeListener();

	@Override
	protected void attached() {
		EventService.addListener(UpdateEvent.class, onInputUpdateListener);
		EventService.addListener(UpdateEvent.class, onThinkUpdateListener);
		EventService.addListener(UpdateEvent.class, onPreRenderUpdateListener);
		EventService.addListener(UpdateEvent.class, onRenderUpdateListener);
		EventService.addListener(UpdateEvent.class, onDebugRenderUpdateListener);
		EventService.addListener(UpdateEvent.class, onAfterRenderUpdateListener);
		EventService.addListener(UpdateEvent.class, onCleanupUpdateListener);
		getGraph().addListener(scriptSceneGraphListener);
		getScene().startSignal.addListener(sceneStartListener);
		getScene().stopSignal.addListener(sceneStopListener);
		getScene().pauseSignal.addListener(pauseListener);
		getScene().resumeSignal.addListener(resumeListener);
	}

	@Override
	protected void detached() {
		EventService.removeListener(UpdateEvent.class, onInputUpdateListener);
		EventService.removeListener(UpdateEvent.class, onThinkUpdateListener);
		EventService.removeListener(UpdateEvent.class, onPreRenderUpdateListener);
		EventService.removeListener(UpdateEvent.class, onRenderUpdateListener);
		EventService.removeListener(UpdateEvent.class, onDebugRenderUpdateListener);
		EventService.removeListener(UpdateEvent.class, onAfterRenderUpdateListener);
		EventService.removeListener(UpdateEvent.class, onCleanupUpdateListener);
		getGraph().removeListener(scriptSceneGraphListener);
		getScene().startSignal.removeListener(sceneStartListener);
		getScene().stopSignal.removeListener(sceneStopListener);
		getScene().pauseSignal.removeListener(pauseListener);
		getScene().resumeSignal.removeListener(resumeListener);

		scriptsByMethod.clear();
		nodeScriptsByMethod.clear();
	}

	private static OverridenScriptMethods getOverridenScriptMethods(BehaviourComponent behaviourComponent) {
		int componentType = behaviourComponent.componentType;
		OverridenScriptMethods overridenScriptMethods = scriptMethods.get(componentType);
		if (overridenScriptMethods == null) {
			overridenScriptMethods = new OverridenScriptMethods(behaviourComponent.getClass());
			scriptMethods.put(componentType, overridenScriptMethods);
		}
		return overridenScriptMethods;
	}

	@Override
	public void componentActivated(SceneNodeComponent component) {
		super.componentActivated(component);

		if (component instanceof BehaviourComponent) {
			BehaviourComponent behaviourComponent = (BehaviourComponent) component;
			int nodeId = behaviourComponent.getNode().id;
			OverridenScriptMethods overridenScriptMethods = getOverridenScriptMethods(behaviourComponent);
			for (IntSetIterator iterator = overridenScriptMethods.methods.iterator(); iterator.hasNext;) {
				int methodId = iterator.next();
				getScriptsByMethod(methodId).add(behaviourComponent);
				getNodeScriptsByMethod(nodeId, methodId).add(behaviourComponent);
				addNodeListenerMethod(methodId, behaviourComponent);
			}
		}
	}

	private static void addNodeListenerMethod(int methodId, BehaviourComponent behaviourComponent) {
		if (methodId == BehaviourEvents.nodeComponentAdded.id) {
			NodeComponentAddedListener.obtain(behaviourComponent);
			return;
		} else if (methodId == BehaviourEvents.nodeComponentAdded.id) {
			NodeComponentAddedListener.obtain(behaviourComponent);
			return;
		} else if (methodId == BehaviourEvents.nodeComponentRemoved.id) {
			NodeComponentRemovedListener.obtain(behaviourComponent);
			return;
		} else if (methodId == BehaviourEvents.nodeComponentActivated.id) {
			NodeComponentActivatedListener.obtain(behaviourComponent);
			return;
		} else if (methodId == BehaviourEvents.nodeComponentDeactivated.id) {
			NodeComponentDeactivatedListener.obtain(behaviourComponent);
			return;
		} else if (methodId == BehaviourEvents.nodeParentChanged.id) {
			NodeParentChangedListener.obtain(behaviourComponent);
			return;
		} else if (methodId == BehaviourEvents.nodeChildAdded.id) {
			NodeChildAddedListener.obtain(behaviourComponent);
			return;
		} else if (methodId == BehaviourEvents.nodeChildRemoved.id) {
			NodeChildRemovedListener.obtain(behaviourComponent);
			return;
		}
	}

	@Override
	public void componentDeactivated(SceneNodeComponent component) {
		super.componentDeactivated(component);

		if (component instanceof BehaviourComponent) {
			BehaviourComponent behaviourComponent = (BehaviourComponent) component;
			int nodeId = behaviourComponent.getNode().id;
			OverridenScriptMethods overridenScriptMethods = getOverridenScriptMethods(behaviourComponent);
			for (IntSetIterator iterator = overridenScriptMethods.methods.iterator(); iterator.hasNext;) {
				int methodId = iterator.next();
				getScriptsByMethod(methodId).remove(behaviourComponent);
				getNodeScriptsByMethod(nodeId, methodId).remove(behaviourComponent);
			}
		}
	}

	public OrderedSet<BehaviourComponent> getScriptsByMethod(EventCallbackIdentifier method) {
		return getScriptsByMethod(method.id);
	}

	public OrderedSet<BehaviourComponent> getScriptsByMethod(int methodId) {
		OrderedSet<BehaviourComponent> scripts = scriptsByMethod.get(methodId);
		if (scripts == null) {
			scripts = new OrderedSet<BehaviourComponent>();
			scriptsByMethod.put(methodId, scripts);
		}
		return scripts;
	}

	public OrderedSet<BehaviourComponent> getNodeScriptsByMethod(SceneNode node, EventCallbackIdentifier method) {
		return node == null ? null : getNodeScriptsByMethod(node.id, method.id);
	}

	public OrderedSet<BehaviourComponent> getNodeScriptsByMethod(int nodeId, int methodId) {
		IntMap<OrderedSet<BehaviourComponent>> nodeScripts = nodeScriptsByMethod.get(nodeId);
		if (nodeScripts == null) {
			nodeScripts = new IntMap<OrderedSet<BehaviourComponent>>();
			nodeScriptsByMethod.put(nodeId, nodeScripts);
		}

		OrderedSet<BehaviourComponent> scripts = nodeScripts.get(methodId);
		if (scripts == null) {
			scripts = new OrderedSet<BehaviourComponent>();
			nodeScripts.put(methodId, scripts);
		}
		return scripts;
	}

	public OrderedSet<BehaviourComponent> getScriptComponents(EventCallbackIdentifier scriptMethod) {
		return getScriptsByMethod(scriptMethod.id);
	}

	private static class OverridenScriptMethods {
		IntSet methods = new IntSet();

		OverridenScriptMethods(Class<? extends BehaviourComponent> scriptComponentClass) {
			Class<?> tempClass = scriptComponentClass;
			while (tempClass != BehaviourComponent.class) {
				for (Method method : ClassReflection.getDeclaredMethods(tempClass)) {
					EventCallbackIdentifier scriptMethod = BehaviourEvents.valueOf(method);
					if (scriptMethod != null) {
						methods.add(scriptMethod.id);
					}
				}

				tempClass = tempClass.getSuperclass();
			}
		}
	}

	private class OnInputUpdateListener implements UpdateListener {
		@Override
		public int getOrdinal() {
			return CommonUpdateOrder.INPUT;
		}

		@Override
		public void update() {
			for (BehaviourComponent behaviourComponent : getScriptComponents(BehaviourEvents.onInput)) {
				behaviourComponent.onInput();
			}
		}
	}

	private class OnThinkUpdateListener implements UpdateListener {
		@Override
		public int getOrdinal() {
			return CommonUpdateOrder.THINK;
		}

		@Override
		public void update() {
			for (BehaviourComponent behaviourComponent : getScriptComponents(BehaviourEvents.onThink)) {
				behaviourComponent.onThink();
			}
		}
	}

	private class OnPreRenderUpdateListener implements UpdateListener {
		@Override
		public int getOrdinal() {
			return CommonUpdateOrder.PRE_RENDER;
		}

		@Override
		public void update() {
			for (BehaviourComponent behaviourComponent : getScriptComponents(BehaviourEvents.onPreRender)) {
				behaviourComponent.onPreRender();
			}
		}
	}

	private class OnRenderUpdateListener implements UpdateListener {
		@Override
		public int getOrdinal() {
			return CommonUpdateOrder.RENDER;
		}

		@Override
		public void update() {
			for (BehaviourComponent behaviourComponent : getScriptComponents(BehaviourEvents.onRender)) {
				behaviourComponent.onRender();
			}
		}
	}

	private class OnDebugRenderUpdateListener implements UpdateListener {
		@Override
		public int getOrdinal() {
			return CommonUpdateOrder.DEBUG_RENDER;
		}

		@Override
		public void update() {
			for (BehaviourComponent behaviourComponent : getScriptComponents(BehaviourEvents.onDebugRender)) {
				behaviourComponent.onDebugRender();
			}
		}
	}

	private class OnAfterRenderUpdateListener implements UpdateListener {
		@Override
		public int getOrdinal() {
			return CommonUpdateOrder.AFTER_RENDER;
		}

		@Override
		public void update() {
			for (BehaviourComponent behaviourComponent : getScriptComponents(BehaviourEvents.onAfterRender)) {
				behaviourComponent.onAfterRender();
			}
		}
	}

	private class OnCleanupUpdateListener implements UpdateListener {
		@Override
		public int getOrdinal() {
			return CommonUpdateOrder.CLEANUP;
		}

		@Override
		public void update() {
			for (BehaviourComponent behaviourComponent : getScriptComponents(BehaviourEvents.onCleanup)) {
				behaviourComponent.onCleanup();
			}
		}
	}

	private class ScriptSceneGraphListener implements SceneGraphListener {
		@Override
		public void componentActivated(SceneNodeComponent component) {
			SceneNode node = component.getNode();
			for (BehaviourComponent behaviourComponent : getScriptComponents(BehaviourEvents.componentActivated)) {
				behaviourComponent.componentActivated(node, component);
			}

			for (BehaviourComponent behaviourComponent : getScriptComponents(BehaviourEvents.nodeComponentActivated)) {
				behaviourComponent.nodeComponentActivated(component);
			}
		}

		@Override
		public void componentDeactivated(SceneNodeComponent component) {
			SceneNode node = component.getNode();
			for (BehaviourComponent behaviourComponent : getScriptComponents(BehaviourEvents.componentDeactivated)) {
				behaviourComponent.componentDeactivated(node, component);
			}
		}

		@Override
		public void componentAdded(SceneNodeComponent component) {
			SceneNode node = component.getNode();
			for (BehaviourComponent behaviourComponent : getScriptComponents(BehaviourEvents.componentAdded)) {
				behaviourComponent.componentAdded(node, component);
			}
		}

		@Override
		public void componentRemoved(SceneNodeComponent component) {
			SceneNode node = component.getNode();
			for (BehaviourComponent behaviourComponent : getScriptComponents(BehaviourEvents.componentRemoved)) {
				behaviourComponent.componentRemoved(node, component);
			}
		}
	}

	private class SceneStartListener implements Listener0 {
		@Override
		public void handle() {
			for (BehaviourComponent behaviourComponent : getScriptComponents(BehaviourEvents.onSceneStart)) {
				behaviourComponent.onSceneStart();
			}
		}
	}

	private class SceneStopListener implements Listener0 {
		@Override
		public void handle() {
			for (BehaviourComponent behaviourComponent : getScriptComponents(BehaviourEvents.onSceneStop)) {
				behaviourComponent.onSceneStop();
			}
		}
	}

	private class PauseListener implements Listener0 {
		@Override
		public void handle() {
			for (BehaviourComponent behaviourComponent : getScriptComponents(BehaviourEvents.onPause)) {
				behaviourComponent.onPause();
			}
		}
	}

	private class ResumeListener implements Listener0 {
		@Override
		public void handle() {
			for (BehaviourComponent behaviourComponent : getScriptComponents(BehaviourEvents.onResume)) {
				behaviourComponent.onPause();
			}
		}
	}

	private static class NodeComponentAddedListener implements Listener1<SceneNodeComponent>, Listener0, Poolable {
		BehaviourComponent behaviourComponent;

		static NodeComponentAddedListener obtain(BehaviourComponent behaviourComponent) {
			NodeComponentAddedListener listener = SynchronizedPools.obtain(NodeComponentAddedListener.class);
			listener.behaviourComponent = behaviourComponent;
			behaviourComponent.getNode().componentAddedSignal.addListener(listener);
			behaviourComponent.deactivatedSignal.addListener(listener);
			return listener;
		}

		@Override
		public void handle(SceneNodeComponent component) {
			behaviourComponent.nodeComponentAdded(component);
		}

		@Override
		public void handle() {
			behaviourComponent.getNode().componentAddedSignal.removeListener(this);
			behaviourComponent.deactivatedSignal.removeListener(this);
			free();
		}

		@Override
		public void reset() {
			behaviourComponent = null;
		}

		void free() {
			SynchronizedPools.free(this);
		}
	}

	private static class NodeComponentRemovedListener implements Listener1<SceneNodeComponent>, Listener0, Poolable {
		BehaviourComponent behaviourComponent;

		static NodeComponentRemovedListener obtain(BehaviourComponent behaviourComponent) {
			NodeComponentRemovedListener listener = SynchronizedPools.obtain(NodeComponentRemovedListener.class);
			listener.behaviourComponent = behaviourComponent;
			behaviourComponent.getNode().componentRemovedSignal.addListener(listener);
			behaviourComponent.deactivatedSignal.addListener(listener);
			return listener;
		}

		@Override
		public void handle(SceneNodeComponent component) {
			behaviourComponent.nodeComponentRemoved(component);
		}

		@Override
		public void handle() {
			behaviourComponent.getNode().componentRemovedSignal.removeListener(this);
			behaviourComponent.deactivatedSignal.removeListener(this);
			free();
		}

		@Override
		public void reset() {
			behaviourComponent = null;
		}

		void free() {
			SynchronizedPools.free(this);
		}
	}

	private static class NodeComponentActivatedListener implements Listener1<SceneNodeComponent>, Listener0, Poolable {
		BehaviourComponent behaviourComponent;

		static NodeComponentActivatedListener obtain(BehaviourComponent behaviourComponent) {
			NodeComponentActivatedListener listener = SynchronizedPools.obtain(NodeComponentActivatedListener.class);
			listener.behaviourComponent = behaviourComponent;
			behaviourComponent.getNode().componentActivatedSignal.addListener(listener);
			behaviourComponent.deactivatedSignal.addListener(listener);
			return listener;
		}

		@Override
		public void handle(SceneNodeComponent component) {
			behaviourComponent.nodeComponentActivated(component);
		}

		@Override
		public void handle() {
			behaviourComponent.getNode().componentActivatedSignal.removeListener(this);
			behaviourComponent.deactivatedSignal.removeListener(this);
			free();
		}

		@Override
		public void reset() {
			behaviourComponent = null;
		}

		void free() {
			SynchronizedPools.free(this);
		}
	}

	private static class NodeComponentDeactivatedListener
			implements Listener1<SceneNodeComponent>, Listener0, Poolable {
		BehaviourComponent behaviourComponent;

		static NodeComponentDeactivatedListener obtain(BehaviourComponent behaviourComponent) {
			NodeComponentDeactivatedListener listener = SynchronizedPools
					.obtain(NodeComponentDeactivatedListener.class);
			listener.behaviourComponent = behaviourComponent;
			behaviourComponent.getNode().componentDeactivatedSignal.addListener(listener);
			behaviourComponent.deactivatedSignal.addListener(listener);
			return listener;
		}

		@Override
		public void handle(SceneNodeComponent component) {
			behaviourComponent.nodeComponentDeactivated(component);
		}

		@Override
		public void handle() {
			behaviourComponent.getNode().componentDeactivatedSignal.removeListener(this);
			behaviourComponent.deactivatedSignal.removeListener(this);
			free();
		}

		@Override
		public void reset() {
			behaviourComponent = null;
		}

		void free() {
			SynchronizedPools.free(this);
		}
	}

	private static class NodeParentChangedListener implements Listener1<SceneNode>, Listener0, Poolable {
		BehaviourComponent behaviourComponent;

		static NodeParentChangedListener obtain(BehaviourComponent behaviourComponent) {
			NodeParentChangedListener listener = SynchronizedPools.obtain(NodeParentChangedListener.class);
			listener.behaviourComponent = behaviourComponent;
			behaviourComponent.getNode().parentChangedSignal.addListener(listener);
			behaviourComponent.deactivatedSignal.addListener(listener);
			return listener;
		}

		@Override
		public void handle(SceneNode newParent) {
			behaviourComponent.nodeParentChanged(newParent);
		}

		@Override
		public void handle() {
			behaviourComponent.getNode().parentChangedSignal.removeListener(this);
			behaviourComponent.deactivatedSignal.removeListener(this);
			free();
		}

		@Override
		public void reset() {
			behaviourComponent = null;
		}

		void free() {
			SynchronizedPools.free(this);
		}
	}

	private static class NodeChildAddedListener implements Listener1<SceneNode>, Listener0, Poolable {
		BehaviourComponent behaviourComponent;

		static NodeChildAddedListener obtain(BehaviourComponent behaviourComponent) {
			NodeChildAddedListener listener = SynchronizedPools.obtain(NodeChildAddedListener.class);
			listener.behaviourComponent = behaviourComponent;
			behaviourComponent.getNode().childAddedSignal.addListener(listener);
			behaviourComponent.deactivatedSignal.addListener(listener);
			return listener;
		}

		@Override
		public void handle(SceneNode child) {
			behaviourComponent.nodeChildAdded(child);
		}

		@Override
		public void handle() {
			behaviourComponent.getNode().childAddedSignal.removeListener(this);
			behaviourComponent.deactivatedSignal.removeListener(this);
			free();
		}

		@Override
		public void reset() {
			behaviourComponent = null;
		}

		void free() {
			SynchronizedPools.free(this);
		}
	}

	private static class NodeChildRemovedListener implements Listener1<SceneNode>, Listener0, Poolable {
		BehaviourComponent behaviourComponent;

		static NodeChildRemovedListener obtain(BehaviourComponent behaviourComponent) {
			NodeChildRemovedListener listener = SynchronizedPools.obtain(NodeChildRemovedListener.class);
			listener.behaviourComponent = behaviourComponent;
			behaviourComponent.getNode().childRemovedSignal.addListener(listener);
			behaviourComponent.deactivatedSignal.addListener(listener);
			return listener;
		}

		@Override
		public void handle(SceneNode child) {
			behaviourComponent.nodeChildRemoved(child);
		}

		@Override
		public void handle() {
			behaviourComponent.getNode().childRemovedSignal.removeListener(this);
			behaviourComponent.deactivatedSignal.removeListener(this);
			free();
		}

		@Override
		public void reset() {
			behaviourComponent = null;
		}

		void free() {
			SynchronizedPools.free(this);
		}
	}
}
