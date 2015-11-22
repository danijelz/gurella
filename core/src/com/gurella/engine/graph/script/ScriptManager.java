package com.gurella.engine.graph.script;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntSet;
import com.badlogic.gdx.utils.IntSet.IntSetIterator;
import com.badlogic.gdx.utils.OrderedSet;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;
import com.gurella.engine.application.UpdateEvent;
import com.gurella.engine.application.UpdateListener;
import com.gurella.engine.application.UpdateOrder;
import com.gurella.engine.event.EventBus;
import com.gurella.engine.graph.GraphListenerSystem;
import com.gurella.engine.graph.SceneGraphListener;
import com.gurella.engine.graph.SceneNode;
import com.gurella.engine.graph.SceneNodeComponent;
import com.gurella.engine.pools.SynchronizedPools;
import com.gurella.engine.signal.Listener0;
import com.gurella.engine.signal.Listener1;

public class ScriptManager extends GraphListenerSystem {
	private static IntMap<OverridenScriptMethods> scriptMethods = new IntMap<OverridenScriptMethods>();

	private IntMap<OrderedSet<ScriptComponent>> scriptsByMethod = new IntMap<OrderedSet<ScriptComponent>>();
	private IntMap<IntMap<OrderedSet<ScriptComponent>>> nodeScriptsByMethod = new IntMap<IntMap<OrderedSet<ScriptComponent>>>();

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
		EventBus.GLOBAL.addListener(UpdateEvent.class, onInputUpdateListener);
		EventBus.GLOBAL.addListener(UpdateEvent.class, onThinkUpdateListener);
		EventBus.GLOBAL.addListener(UpdateEvent.class, onPreRenderUpdateListener);
		EventBus.GLOBAL.addListener(UpdateEvent.class, onRenderUpdateListener);
		EventBus.GLOBAL.addListener(UpdateEvent.class, onDebugRenderUpdateListener);
		EventBus.GLOBAL.addListener(UpdateEvent.class, onAfterRenderUpdateListener);
		EventBus.GLOBAL.addListener(UpdateEvent.class, onCleanupUpdateListener);
		getGraph().addListener(scriptSceneGraphListener);
		getScene().startSignal.addListener(sceneStartListener);
		getScene().stopSignal.addListener(sceneStopListener);
		getScene().pauseSignal.addListener(pauseListener);
		getScene().resumeSignal.addListener(resumeListener);
	}

	@Override
	protected void detached() {
		EventBus.GLOBAL.removeListener(UpdateEvent.class, onInputUpdateListener);
		EventBus.GLOBAL.removeListener(UpdateEvent.class, onThinkUpdateListener);
		EventBus.GLOBAL.removeListener(UpdateEvent.class, onPreRenderUpdateListener);
		EventBus.GLOBAL.removeListener(UpdateEvent.class, onRenderUpdateListener);
		EventBus.GLOBAL.removeListener(UpdateEvent.class, onDebugRenderUpdateListener);
		EventBus.GLOBAL.removeListener(UpdateEvent.class, onAfterRenderUpdateListener);
		EventBus.GLOBAL.removeListener(UpdateEvent.class, onCleanupUpdateListener);
		getGraph().removeListener(scriptSceneGraphListener);
		getScene().startSignal.removeListener(sceneStartListener);
		getScene().stopSignal.removeListener(sceneStopListener);
		getScene().pauseSignal.removeListener(pauseListener);
		getScene().resumeSignal.removeListener(resumeListener);

		scriptsByMethod.clear();
		nodeScriptsByMethod.clear();
	}

	private static OverridenScriptMethods getOverridenScriptMethods(ScriptComponent scriptComponent) {
		int componentType = scriptComponent.getImplementationComponentType();
		OverridenScriptMethods overridenScriptMethods = scriptMethods.get(componentType);
		if (overridenScriptMethods == null) {
			overridenScriptMethods = new OverridenScriptMethods(scriptComponent.getClass());
			scriptMethods.put(componentType, overridenScriptMethods);
		}
		return overridenScriptMethods;
	}

	@Override
	public void componentActivated(SceneNodeComponent component) {
		super.componentActivated(component);

		if (component instanceof ScriptComponent) {
			ScriptComponent scriptComponent = (ScriptComponent) component;
			int nodeId = scriptComponent.getNode().id;
			OverridenScriptMethods overridenScriptMethods = getOverridenScriptMethods(scriptComponent);
			for (IntSetIterator iterator = overridenScriptMethods.methods.iterator(); iterator.hasNext;) {
				int methodId = iterator.next();
				getScriptsByMethod(methodId).add(scriptComponent);
				getNodeScriptsByMethod(nodeId, methodId).add(scriptComponent);
				addNodeListenerMethod(methodId, scriptComponent);
			}
		}
	}

	private static void addNodeListenerMethod(int methodId, ScriptComponent scriptComponent) {
		ScriptMethod method = ScriptMethod.values()[methodId];
		switch (method) {
		case nodeComponentAdded:
			NodeComponentAddedListener.obtain(scriptComponent);
			return;
		case nodeComponentRemoved:
			NodeComponentRemovedListener.obtain(scriptComponent);
			return;
		case nodeComponentActivated:
			NodeComponentActivatedListener.obtain(scriptComponent);
			return;
		case nodeComponentDeactivated:
			NodeComponentDeactivatedListener.obtain(scriptComponent);
			return;
		case nodeParentChanged:
			NodeParentChangedListener.obtain(scriptComponent);
			return;
		case nodeChildAdded:
			NodeChildAddedListener.obtain(scriptComponent);
			return;
		case nodeChildRemoved:
			NodeChildRemovedListener.obtain(scriptComponent);
			return;
		default:
			return;
		}
	}

	@Override
	public void componentDeactivated(SceneNodeComponent component) {
		super.componentDeactivated(component);

		if (component instanceof ScriptComponent) {
			ScriptComponent scriptComponent = (ScriptComponent) component;
			int nodeId = scriptComponent.getNode().id;
			OverridenScriptMethods overridenScriptMethods = getOverridenScriptMethods(scriptComponent);
			for (IntSetIterator iterator = overridenScriptMethods.methods.iterator(); iterator.hasNext;) {
				int methodId = iterator.next();
				getScriptsByMethod(methodId).remove(scriptComponent);
				getNodeScriptsByMethod(nodeId, methodId).remove(scriptComponent);
			}
		}
	}

	public OrderedSet<ScriptComponent> getScriptsByMethod(ScriptMethod method) {
		return getScriptsByMethod(method.ordinal());
	}

	public OrderedSet<ScriptComponent> getScriptsByMethod(int methodId) {
		OrderedSet<ScriptComponent> scripts = scriptsByMethod.get(methodId);
		if (scripts == null) {
			scripts = new OrderedSet<ScriptComponent>();
			scriptsByMethod.put(methodId, scripts);
		}
		return scripts;
	}

	public OrderedSet<ScriptComponent> getNodeScriptsByMethod(SceneNode node, ScriptMethod method) {
		return node == null
				? null
				: getNodeScriptsByMethod(node.id, method.ordinal());
	}

	public OrderedSet<ScriptComponent> getNodeScriptsByMethod(int nodeId, int methodId) {
		IntMap<OrderedSet<ScriptComponent>> nodeScripts = nodeScriptsByMethod.get(nodeId);
		if (nodeScripts == null) {
			nodeScripts = new IntMap<OrderedSet<ScriptComponent>>();
			nodeScriptsByMethod.put(nodeId, nodeScripts);
		}

		OrderedSet<ScriptComponent> scripts = nodeScripts.get(methodId);
		if (scripts == null) {
			scripts = new OrderedSet<ScriptComponent>();
			nodeScripts.put(methodId, scripts);
		}
		return scripts;
	}

	public OrderedSet<ScriptComponent> getScriptComponents(ScriptMethod scriptMethod) {
		return getScriptsByMethod(scriptMethod.ordinal());
	}

	private static class OverridenScriptMethods {
		IntSet methods = new IntSet();

		OverridenScriptMethods(Class<? extends ScriptComponent> scriptComponentClass) {
			Class<?> tempClass = scriptComponentClass;
			while (tempClass != ScriptComponent.class) {
				for (Method method : ClassReflection.getDeclaredMethods(tempClass)) {
					ScriptMethod scriptMethod = ScriptMethod.valueOf(method);
					if (scriptMethod != null) {
						methods.add(scriptMethod.ordinal());
					}
				}

				tempClass = tempClass.getSuperclass();
			}
		}
	}

	private class OnInputUpdateListener implements UpdateListener {
		@Override
		public int getOrdinal() {
			return UpdateOrder.INPUT;
		}

		@Override
		public void update() {
			for (ScriptComponent scriptComponent : getScriptComponents(ScriptMethod.onInput)) {
				scriptComponent.onInput();
			}
		}
	}

	private class OnThinkUpdateListener implements UpdateListener {
		@Override
		public int getOrdinal() {
			return UpdateOrder.THINK;
		}

		@Override
		public void update() {
			for (ScriptComponent scriptComponent : getScriptComponents(ScriptMethod.onThink)) {
				scriptComponent.onThink();
			}
		}
	}

	private class OnPreRenderUpdateListener implements UpdateListener {
		@Override
		public int getOrdinal() {
			return UpdateOrder.PRE_RENDER;
		}

		@Override
		public void update() {
			for (ScriptComponent scriptComponent : getScriptComponents(ScriptMethod.onPreRender)) {
				scriptComponent.onPreRender();
			}
		}
	}

	private class OnRenderUpdateListener implements UpdateListener {
		@Override
		public int getOrdinal() {
			return UpdateOrder.RENDER;
		}

		@Override
		public void update() {
			for (ScriptComponent scriptComponent : getScriptComponents(ScriptMethod.onRender)) {
				scriptComponent.onRender();
			}
		}
	}

	private class OnDebugRenderUpdateListener implements UpdateListener {
		@Override
		public int getOrdinal() {
			return UpdateOrder.DEBUG_RENDER;
		}

		@Override
		public void update() {
			for (ScriptComponent scriptComponent : getScriptComponents(ScriptMethod.onDebugRender)) {
				scriptComponent.onDebugRender();
			}
		}
	}

	private class OnAfterRenderUpdateListener implements UpdateListener {
		@Override
		public int getOrdinal() {
			return UpdateOrder.AFTER_RENDER;
		}

		@Override
		public void update() {
			for (ScriptComponent scriptComponent : getScriptComponents(ScriptMethod.onAfterRender)) {
				scriptComponent.onAfterRender();
			}
		}
	}

	private class OnCleanupUpdateListener implements UpdateListener {
		@Override
		public int getOrdinal() {
			return UpdateOrder.CLEANUP;
		}

		@Override
		public void update() {
			for (ScriptComponent scriptComponent : getScriptComponents(ScriptMethod.onCleanup)) {
				scriptComponent.onCleanup();
			}
		}
	}

	private class ScriptSceneGraphListener implements SceneGraphListener {
		@Override
		public void componentActivated(SceneNodeComponent component) {
			for (ScriptComponent scriptComponent : getScriptComponents(ScriptMethod.componentActivated)) {
				scriptComponent.componentActivated(component);
			}
		}

		@Override
		public void componentDeactivated(SceneNodeComponent component) {
			for (ScriptComponent scriptComponent : getScriptComponents(ScriptMethod.componentDeactivated)) {
				scriptComponent.componentDeactivated(component);
			}
		}

		@Override
		public void componentAdded(SceneNodeComponent component) {
			for (ScriptComponent scriptComponent : getScriptComponents(ScriptMethod.componentAdded)) {
				scriptComponent.componentAdded(component);
			}
		}

		@Override
		public void componentRemoved(SceneNodeComponent component) {
			for (ScriptComponent scriptComponent : getScriptComponents(ScriptMethod.componentRemoved)) {
				scriptComponent.componentRemoved(component);
			}
		}
	}

	private class SceneStartListener implements Listener0 {
		@Override
		public void handle() {
			for (ScriptComponent scriptComponent : getScriptComponents(ScriptMethod.onSceneStart)) {
				scriptComponent.onSceneStart();
			}
		}
	}

	private class SceneStopListener implements Listener0 {
		@Override
		public void handle() {
			for (ScriptComponent scriptComponent : getScriptComponents(ScriptMethod.onSceneStop)) {
				scriptComponent.onSceneStop();
			}
		}
	}

	private class PauseListener implements Listener0 {
		@Override
		public void handle() {
			for (ScriptComponent scriptComponent : getScriptComponents(ScriptMethod.onPause)) {
				scriptComponent.onPause();
			}
		}
	}

	private class ResumeListener implements Listener0 {
		@Override
		public void handle() {
			for (ScriptComponent scriptComponent : getScriptComponents(ScriptMethod.onResume)) {
				scriptComponent.onPause();
			}
		}
	}

	private static class NodeComponentAddedListener implements Listener1<SceneNodeComponent>, Listener0, Poolable {
		ScriptComponent scriptComponent;

		static NodeComponentAddedListener obtain(ScriptComponent scriptComponent) {
			NodeComponentAddedListener listener = SynchronizedPools.obtain(NodeComponentAddedListener.class);
			listener.scriptComponent = scriptComponent;
			scriptComponent.getNode().componentAddedSignal.addListener(listener);
			scriptComponent.deactivatedSignal.addListener(listener);
			return listener;
		}

		@Override
		public void handle(SceneNodeComponent component) {
			scriptComponent.nodeComponentAdded(component);
		}

		@Override
		public void handle() {
			scriptComponent.getNode().componentAddedSignal.removeListener(this);
			scriptComponent.deactivatedSignal.removeListener(this);
			free();
		}

		@Override
		public void reset() {
			scriptComponent = null;
		}

		void free() {
			SynchronizedPools.free(this);
		}
	}

	private static class NodeComponentRemovedListener implements Listener1<SceneNodeComponent>, Listener0, Poolable {
		ScriptComponent scriptComponent;

		static NodeComponentRemovedListener obtain(ScriptComponent scriptComponent) {
			NodeComponentRemovedListener listener = SynchronizedPools.obtain(NodeComponentRemovedListener.class);
			listener.scriptComponent = scriptComponent;
			scriptComponent.getNode().componentRemovedSignal.addListener(listener);
			scriptComponent.deactivatedSignal.addListener(listener);
			return listener;
		}

		@Override
		public void handle(SceneNodeComponent component) {
			scriptComponent.nodeComponentRemoved(component);
		}

		@Override
		public void handle() {
			scriptComponent.getNode().componentRemovedSignal.removeListener(this);
			scriptComponent.deactivatedSignal.removeListener(this);
			free();
		}

		@Override
		public void reset() {
			scriptComponent = null;
		}

		void free() {
			SynchronizedPools.free(this);
		}
	}

	private static class NodeComponentActivatedListener implements Listener1<SceneNodeComponent>, Listener0, Poolable {
		ScriptComponent scriptComponent;

		static NodeComponentActivatedListener obtain(ScriptComponent scriptComponent) {
			NodeComponentActivatedListener listener = SynchronizedPools.obtain(NodeComponentActivatedListener.class);
			listener.scriptComponent = scriptComponent;
			scriptComponent.getNode().componentActivatedSignal.addListener(listener);
			scriptComponent.deactivatedSignal.addListener(listener);
			return listener;
		}

		@Override
		public void handle(SceneNodeComponent component) {
			scriptComponent.nodeComponentActivated(component);
		}

		@Override
		public void handle() {
			scriptComponent.getNode().componentActivatedSignal.removeListener(this);
			scriptComponent.deactivatedSignal.removeListener(this);
			free();
		}

		@Override
		public void reset() {
			scriptComponent = null;
		}

		void free() {
			SynchronizedPools.free(this);
		}
	}

	private static class NodeComponentDeactivatedListener implements Listener1<SceneNodeComponent>, Listener0, Poolable {
		ScriptComponent scriptComponent;

		static NodeComponentDeactivatedListener obtain(ScriptComponent scriptComponent) {
			NodeComponentDeactivatedListener listener = SynchronizedPools
					.obtain(NodeComponentDeactivatedListener.class);
			listener.scriptComponent = scriptComponent;
			scriptComponent.getNode().componentDeactivatedSignal.addListener(listener);
			scriptComponent.deactivatedSignal.addListener(listener);
			return listener;
		}

		@Override
		public void handle(SceneNodeComponent component) {
			scriptComponent.nodeComponentDeactivated(component);
		}

		@Override
		public void handle() {
			scriptComponent.getNode().componentDeactivatedSignal.removeListener(this);
			scriptComponent.deactivatedSignal.removeListener(this);
			free();
		}

		@Override
		public void reset() {
			scriptComponent = null;
		}

		void free() {
			SynchronizedPools.free(this);
		}
	}

	private static class NodeParentChangedListener implements Listener1<SceneNode>, Listener0, Poolable {
		ScriptComponent scriptComponent;

		static NodeParentChangedListener obtain(ScriptComponent scriptComponent) {
			NodeParentChangedListener listener = SynchronizedPools.obtain(NodeParentChangedListener.class);
			listener.scriptComponent = scriptComponent;
			scriptComponent.getNode().parentChangedSignal.addListener(listener);
			scriptComponent.deactivatedSignal.addListener(listener);
			return listener;
		}

		@Override
		public void handle(SceneNode newParent) {
			scriptComponent.nodeParentChanged(newParent);
		}

		@Override
		public void handle() {
			scriptComponent.getNode().parentChangedSignal.removeListener(this);
			scriptComponent.deactivatedSignal.removeListener(this);
			free();
		}

		@Override
		public void reset() {
			scriptComponent = null;
		}

		void free() {
			SynchronizedPools.free(this);
		}
	}

	private static class NodeChildAddedListener implements Listener1<SceneNode>, Listener0, Poolable {
		ScriptComponent scriptComponent;

		static NodeChildAddedListener obtain(ScriptComponent scriptComponent) {
			NodeChildAddedListener listener = SynchronizedPools.obtain(NodeChildAddedListener.class);
			listener.scriptComponent = scriptComponent;
			scriptComponent.getNode().childAddedSignal.addListener(listener);
			scriptComponent.deactivatedSignal.addListener(listener);
			return listener;
		}

		@Override
		public void handle(SceneNode child) {
			scriptComponent.nodeChildAdded(child);
		}

		@Override
		public void handle() {
			scriptComponent.getNode().childAddedSignal.removeListener(this);
			scriptComponent.deactivatedSignal.removeListener(this);
			free();
		}

		@Override
		public void reset() {
			scriptComponent = null;
		}

		void free() {
			SynchronizedPools.free(this);
		}
	}

	private static class NodeChildRemovedListener implements Listener1<SceneNode>, Listener0, Poolable {
		ScriptComponent scriptComponent;

		static NodeChildRemovedListener obtain(ScriptComponent scriptComponent) {
			NodeChildRemovedListener listener = SynchronizedPools.obtain(NodeChildRemovedListener.class);
			listener.scriptComponent = scriptComponent;
			scriptComponent.getNode().childRemovedSignal.addListener(listener);
			scriptComponent.deactivatedSignal.addListener(listener);
			return listener;
		}

		@Override
		public void handle(SceneNode child) {
			scriptComponent.nodeChildRemoved(child);
		}

		@Override
		public void handle() {
			scriptComponent.getNode().childRemovedSignal.removeListener(this);
			scriptComponent.deactivatedSignal.removeListener(this);
			free();
		}

		@Override
		public void reset() {
			scriptComponent = null;
		}

		void free() {
			SynchronizedPools.free(this);
		}
	}
}
