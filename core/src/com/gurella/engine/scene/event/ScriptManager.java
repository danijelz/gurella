package com.gurella.engine.scene.event;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntSet;
import com.badlogic.gdx.utils.IntSet.IntSetIterator;
import com.badlogic.gdx.utils.OrderedSet;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;
import com.gurella.engine.application.events.ApplicationUpdateEvent;
import com.gurella.engine.application.events.ApplicationUpdateSignal.ApplicationUpdateListener;
import com.gurella.engine.application.events.CommonUpdatePriority;
import com.gurella.engine.event.EventService;
import com.gurella.engine.event.Listener0;
import com.gurella.engine.event.Listener1;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.scene.SceneListener;
import com.gurella.engine.scene.SceneNode;
import com.gurella.engine.scene.SceneNodeComponent;
import com.gurella.engine.scene.SceneSystem;
import com.gurella.engine.scene.behaviour.BehaviourComponent;

public class ScriptManager extends SceneSystem implements SceneListener {
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
	private ScriptSceneListener scriptSceneListener = new ScriptSceneListener();
	private SceneStartListener sceneStartListener = new SceneStartListener();
	private SceneStopListener sceneStopListener = new SceneStopListener();
	private PauseListener pauseListener = new PauseListener();
	private ResumeListener resumeListener = new ResumeListener();

	@Override
	protected void attached() {
		EventService.addListener(ApplicationUpdateEvent.class, onInputUpdateListener);
		EventService.addListener(ApplicationUpdateEvent.class, onThinkUpdateListener);
		EventService.addListener(ApplicationUpdateEvent.class, onPreRenderUpdateListener);
		EventService.addListener(ApplicationUpdateEvent.class, onRenderUpdateListener);
		EventService.addListener(ApplicationUpdateEvent.class, onDebugRenderUpdateListener);
		EventService.addListener(ApplicationUpdateEvent.class, onAfterRenderUpdateListener);
		EventService.addListener(ApplicationUpdateEvent.class, onCleanupUpdateListener);
		//getScene().addListener(scriptSceneListener);
		getScene().startSignal.addListener(sceneStartListener);
		getScene().stopSignal.addListener(sceneStopListener);
		//getScene().pauseSignal.addListener(pauseListener);
		//getScene().resumeSignal.addListener(resumeListener);
	}

	@Override
	protected void detached() {
		EventService.removeListener(ApplicationUpdateEvent.class, onInputUpdateListener);
		EventService.removeListener(ApplicationUpdateEvent.class, onThinkUpdateListener);
		EventService.removeListener(ApplicationUpdateEvent.class, onPreRenderUpdateListener);
		EventService.removeListener(ApplicationUpdateEvent.class, onRenderUpdateListener);
		EventService.removeListener(ApplicationUpdateEvent.class, onDebugRenderUpdateListener);
		EventService.removeListener(ApplicationUpdateEvent.class, onAfterRenderUpdateListener);
		EventService.removeListener(ApplicationUpdateEvent.class, onCleanupUpdateListener);
		//getScene().removeListener(scriptSceneListener);
		getScene().startSignal.removeListener(sceneStartListener);
		getScene().stopSignal.removeListener(sceneStopListener);
		//getScene().pauseSignal.removeListener(pauseListener);
		//getScene().resumeSignal.removeListener(resumeListener);

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
	public void componentAdded(SceneNodeComponent component) {
	}

	@Override
	public void componentRemoved(SceneNodeComponent component) {
	}

	@Override
	public void componentActivated(SceneNodeComponent component) {
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
		if (methodId == /*BehaviourEvents.nodeComponentAdded.id*/1) {
			NodeComponentAddedListener.obtain(behaviourComponent);
			return;
		} else if (methodId == 2) {
			NodeComponentAddedListener.obtain(behaviourComponent);
			return;
		} else if (methodId == 3) {
			NodeComponentRemovedListener.obtain(behaviourComponent);
			return;
		} else if (methodId == 4) {
			NodeComponentActivatedListener.obtain(behaviourComponent);
			return;
		} else if (methodId == 5) {
			NodeComponentDeactivatedListener.obtain(behaviourComponent);
			return;
		} else if (methodId == 6) {
			NodeParentChangedListener.obtain(behaviourComponent);
			return;
		} else if (methodId == 7) {
			NodeChildAddedListener.obtain(behaviourComponent);
			return;
		} else if (methodId == 8) {
			NodeChildRemovedListener.obtain(behaviourComponent);
			return;
		}
	}

	@Override
	public void componentDeactivated(SceneNodeComponent component) {
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

	public OrderedSet<BehaviourComponent> getScriptsByMethod(int methodId) {
		OrderedSet<BehaviourComponent> scripts = scriptsByMethod.get(methodId);
		if (scripts == null) {
			scripts = new OrderedSet<BehaviourComponent>();
			scriptsByMethod.put(methodId, scripts);
		}
		return scripts;
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

	private static class OverridenScriptMethods {
		IntSet methods = new IntSet();

		OverridenScriptMethods(Class<? extends BehaviourComponent> scriptComponentClass) {
			Class<?> tempClass = scriptComponentClass;
			while (tempClass != BehaviourComponent.class) {
				for (Method method : ClassReflection.getDeclaredMethods(tempClass)) {
				}

				tempClass = tempClass.getSuperclass();
			}
		}
	}

	private class OnInputUpdateListener implements ApplicationUpdateListener {
		@Override
		public int getPriority() {
			return CommonUpdatePriority.INPUT;
		}

		@Override
		public void update() {
			for (BehaviourComponent behaviourComponent : getScriptsByMethod(1)) {
				behaviourComponent.onInput();
			}
		}
	}

	private class OnThinkUpdateListener implements ApplicationUpdateListener {
		@Override
		public int getPriority() {
			return CommonUpdatePriority.THINK;
		}

		@Override
		public void update() {
			for (BehaviourComponent behaviourComponent : getScriptsByMethod(1)) {
				behaviourComponent.onThink();
			}
		}
	}

	private class OnPreRenderUpdateListener implements ApplicationUpdateListener {
		@Override
		public int getPriority() {
			return CommonUpdatePriority.PRE_RENDER;
		}

		@Override
		public void update() {
			for (BehaviourComponent behaviourComponent : getScriptsByMethod(1)) {
				behaviourComponent.onPreRender();
			}
		}
	}

	private class OnRenderUpdateListener implements ApplicationUpdateListener {
		@Override
		public int getPriority() {
			return CommonUpdatePriority.RENDER;
		}

		@Override
		public void update() {
			for (BehaviourComponent behaviourComponent : getScriptsByMethod(1)) {
				behaviourComponent.onRender();
			}
		}
	}

	private class OnDebugRenderUpdateListener implements ApplicationUpdateListener {
		@Override
		public int getPriority() {
			return CommonUpdatePriority.DEBUG_RENDER;
		}

		@Override
		public void update() {
			for (BehaviourComponent behaviourComponent : getScriptsByMethod(1)) {
				behaviourComponent.onDebugRender();
			}
		}
	}

	private class OnAfterRenderUpdateListener implements ApplicationUpdateListener {
		@Override
		public int getPriority() {
			return CommonUpdatePriority.POST_RENDER;
		}

		@Override
		public void update() {
			for (BehaviourComponent behaviourComponent : getScriptsByMethod(1)) {
				behaviourComponent.onAfterRender();
			}
		}
	}

	private class OnCleanupUpdateListener implements ApplicationUpdateListener {
		@Override
		public int getPriority() {
			return CommonUpdatePriority.CLEANUP;
		}

		@Override
		public void update() {
			for (BehaviourComponent behaviourComponent : getScriptsByMethod(1)) {
				behaviourComponent.onCleanup();
			}
		}
	}

	private class ScriptSceneListener implements SceneListener {
		@Override
		public void componentActivated(SceneNodeComponent component) {
			SceneNode node = component.getNode();
			for (BehaviourComponent behaviourComponent : getScriptsByMethod(1)) {
				behaviourComponent.componentActivated(node, component);
			}

			for (BehaviourComponent behaviourComponent : getScriptsByMethod(1)) {
				behaviourComponent.nodeComponentActivated(component);
			}
		}

		@Override
		public void componentDeactivated(SceneNodeComponent component) {
			SceneNode node = component.getNode();
			for (BehaviourComponent behaviourComponent : getScriptsByMethod(1)) {
				behaviourComponent.componentDeactivated(node, component);
			}
		}

		@Override
		public void componentAdded(SceneNodeComponent component) {
			SceneNode node = component.getNode();
			for (BehaviourComponent behaviourComponent : getScriptsByMethod(1)) {
				behaviourComponent.componentAdded(node, component);
			}
		}

		@Override
		public void componentRemoved(SceneNodeComponent component) {
			SceneNode node = component.getNode();
			for (BehaviourComponent behaviourComponent : getScriptsByMethod(1)) {
				behaviourComponent.componentRemoved(node, component);
			}
		}
	}

	private class SceneStartListener implements Listener0 {
		@Override
		public void handle() {
			for (BehaviourComponent behaviourComponent : getScriptsByMethod(1)) {
				behaviourComponent.onSceneStart();
			}
		}
	}

	private class SceneStopListener implements Listener0 {
		@Override
		public void handle() {
			for (BehaviourComponent behaviourComponent : getScriptsByMethod(1)) {
				behaviourComponent.onSceneStop();
			}
		}
	}

	private class PauseListener implements Listener0 {
		@Override
		public void handle() {
			for (BehaviourComponent behaviourComponent : getScriptsByMethod(1)) {
				behaviourComponent.onPause();
			}
		}
	}

	private class ResumeListener implements Listener0 {
		@Override
		public void handle() {
			for (BehaviourComponent behaviourComponent : getScriptsByMethod(1)) {
				behaviourComponent.onPause();
			}
		}
	}

	private static class NodeComponentAddedListener implements Listener1<SceneNodeComponent>, Listener0, Poolable {
		BehaviourComponent behaviourComponent;

		static NodeComponentAddedListener obtain(BehaviourComponent behaviourComponent) {
			NodeComponentAddedListener listener = PoolService.obtain(NodeComponentAddedListener.class);
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
			PoolService.free(this);
		}
	}

	private static class NodeComponentRemovedListener implements Listener1<SceneNodeComponent>, Listener0, Poolable {
		BehaviourComponent behaviourComponent;

		static NodeComponentRemovedListener obtain(BehaviourComponent behaviourComponent) {
			NodeComponentRemovedListener listener = PoolService.obtain(NodeComponentRemovedListener.class);
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
			PoolService.free(this);
		}
	}

	private static class NodeComponentActivatedListener implements Listener1<SceneNodeComponent>, Listener0, Poolable {
		BehaviourComponent behaviourComponent;

		static NodeComponentActivatedListener obtain(BehaviourComponent behaviourComponent) {
			NodeComponentActivatedListener listener = PoolService.obtain(NodeComponentActivatedListener.class);
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
			PoolService.free(this);
		}
	}

	private static class NodeComponentDeactivatedListener
			implements Listener1<SceneNodeComponent>, Listener0, Poolable {
		BehaviourComponent behaviourComponent;

		static NodeComponentDeactivatedListener obtain(BehaviourComponent behaviourComponent) {
			NodeComponentDeactivatedListener listener = PoolService
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
			PoolService.free(this);
		}
	}

	private static class NodeParentChangedListener implements Listener1<SceneNode>, Listener0, Poolable {
		BehaviourComponent behaviourComponent;

		static NodeParentChangedListener obtain(BehaviourComponent behaviourComponent) {
			NodeParentChangedListener listener = PoolService.obtain(NodeParentChangedListener.class);
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
			PoolService.free(this);
		}
	}

	private static class NodeChildAddedListener implements Listener1<SceneNode>, Listener0, Poolable {
		BehaviourComponent behaviourComponent;

		static NodeChildAddedListener obtain(BehaviourComponent behaviourComponent) {
			NodeChildAddedListener listener = PoolService.obtain(NodeChildAddedListener.class);
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
			PoolService.free(this);
		}
	}

	private static class NodeChildRemovedListener implements Listener1<SceneNode>, Listener0, Poolable {
		BehaviourComponent behaviourComponent;

		static NodeChildRemovedListener obtain(BehaviourComponent behaviourComponent) {
			NodeChildRemovedListener listener = PoolService.obtain(NodeChildRemovedListener.class);
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
			PoolService.free(this);
		}
	}
}
