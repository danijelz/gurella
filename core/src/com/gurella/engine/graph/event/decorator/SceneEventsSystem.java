package com.gurella.engine.graph.event.decorator;

import com.gurella.engine.application.CommonUpdateOrder;
import com.gurella.engine.application.UpdateEvent;
import com.gurella.engine.application.UpdateListener;
import com.gurella.engine.event.EventService;
import com.gurella.engine.graph.SceneGraph;
import com.gurella.engine.graph.SceneGraphListener;
import com.gurella.engine.graph.SceneNode;
import com.gurella.engine.graph.SceneNodeComponent;
import com.gurella.engine.graph.SceneSystem;
import com.gurella.engine.graph.behaviour.BehaviourEventCallbacks;
import com.gurella.engine.graph.behaviour.BehaviourComponent;
import com.gurella.engine.graph.event.EventSystem;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.signal.Listener0;

public class SceneEventsSystem extends SceneSystem {
	private final OnInputUpdateListener onInputUpdateListener = new OnInputUpdateListener();
	private final OnThinkUpdateListener onThinkUpdateListener = new OnThinkUpdateListener();
	private final OnPreRenderUpdateListener onPreRenderUpdateListener = new OnPreRenderUpdateListener();
	private final OnRenderUpdateListener onRenderUpdateListener = new OnRenderUpdateListener();
	private final OnDebugRenderUpdateListener onDebugRenderUpdateListener = new OnDebugRenderUpdateListener();
	private final OnAfterRenderUpdateListener onAfterRenderUpdateListener = new OnAfterRenderUpdateListener();
	private final OnCleanupUpdateListener onCleanupUpdateListener = new OnCleanupUpdateListener();
	private final ScriptSceneGraphListener scriptSceneGraphListener = new ScriptSceneGraphListener();
	private final SceneStartListener sceneStartListener = new SceneStartListener();
	private final SceneStopListener sceneStopListener = new SceneStopListener();
	private final PauseListener pauseListener = new PauseListener();
	private final ResumeListener resumeListener = new ResumeListener();

	private EventSystem eventSystem;

	@Override
	protected void activated() {
		SceneGraph graph = getGraph();
		eventSystem = graph.eventSystem;
		EventService.addListener(UpdateEvent.class, onInputUpdateListener);
		EventService.addListener(UpdateEvent.class, onThinkUpdateListener);
		EventService.addListener(UpdateEvent.class, onPreRenderUpdateListener);
		EventService.addListener(UpdateEvent.class, onRenderUpdateListener);
		EventService.addListener(UpdateEvent.class, onDebugRenderUpdateListener);
		EventService.addListener(UpdateEvent.class, onAfterRenderUpdateListener);
		EventService.addListener(UpdateEvent.class, onCleanupUpdateListener);
		graph.addListener(scriptSceneGraphListener);
		Scene scene = getScene();
		scene.startSignal.addListener(sceneStartListener);
		scene.stopSignal.addListener(sceneStopListener);
		scene.pauseSignal.addListener(pauseListener);
		scene.resumeSignal.addListener(resumeListener);
	}

	@Override
	protected void deactivated() {
		EventService.removeListener(UpdateEvent.class, onInputUpdateListener);
		EventService.removeListener(UpdateEvent.class, onThinkUpdateListener);
		EventService.removeListener(UpdateEvent.class, onPreRenderUpdateListener);
		EventService.removeListener(UpdateEvent.class, onRenderUpdateListener);
		EventService.removeListener(UpdateEvent.class, onDebugRenderUpdateListener);
		EventService.removeListener(UpdateEvent.class, onAfterRenderUpdateListener);
		EventService.removeListener(UpdateEvent.class, onCleanupUpdateListener);
		getGraph().removeListener(scriptSceneGraphListener);
		Scene scene = getScene();
		scene.startSignal.removeListener(sceneStartListener);
		scene.stopSignal.removeListener(sceneStopListener);
		scene.pauseSignal.removeListener(pauseListener);
		scene.resumeSignal.removeListener(resumeListener);
		eventSystem = null;
	}

	private class OnInputUpdateListener implements UpdateListener {
		@Override
		public int getOrdinal() {
			return CommonUpdateOrder.INPUT;
		}

		@Override
		public void update() {
			for (BehaviourComponent behaviourComponent : eventSystem.getListeners(BehaviourEventCallbacks.onInput)) {
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
			for (BehaviourComponent behaviourComponent : eventSystem.getListeners(BehaviourEventCallbacks.onThink)) {
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
			for (BehaviourComponent behaviourComponent : eventSystem.getListeners(BehaviourEventCallbacks.onPreRender)) {
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
			for (BehaviourComponent behaviourComponent : eventSystem.getListeners(BehaviourEventCallbacks.onRender)) {
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
			for (BehaviourComponent behaviourComponent : eventSystem.getListeners(BehaviourEventCallbacks.onDebugRender)) {
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
			for (BehaviourComponent behaviourComponent : eventSystem.getListeners(BehaviourEventCallbacks.onAfterRender)) {
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
			for (BehaviourComponent behaviourComponent : eventSystem.getListeners(BehaviourEventCallbacks.onCleanup)) {
				behaviourComponent.onCleanup();
			}
		}
	}

	private class ScriptSceneGraphListener implements SceneGraphListener {
		@Override
		public void componentActivated(SceneNodeComponent component) {
			SceneNode node = component.getNode();
			for (BehaviourComponent behaviourComponent : eventSystem.getListeners(BehaviourEventCallbacks.componentActivated)) {
				behaviourComponent.componentActivated(node, component);
			}

			for (BehaviourComponent behaviourComponent : eventSystem.getListeners(node,
					BehaviourEventCallbacks.nodeComponentActivated)) {
				behaviourComponent.nodeComponentActivated(component);
			}
		}

		@Override
		public void componentDeactivated(SceneNodeComponent component) {
			SceneNode node = component.getNode();
			for (BehaviourComponent behaviourComponent : eventSystem.getListeners(BehaviourEventCallbacks.componentDeactivated)) {
				behaviourComponent.componentDeactivated(node, component);
			}

			for (BehaviourComponent behaviourComponent : eventSystem.getListeners(node,
					BehaviourEventCallbacks.nodeComponentDeactivated)) {
				behaviourComponent.nodeComponentDeactivated(component);
			}
		}

		@Override
		public void componentAdded(SceneNodeComponent component) {
			SceneNode node = component.getNode();
			for (BehaviourComponent behaviourComponent : eventSystem.getListeners(BehaviourEventCallbacks.componentAdded)) {
				behaviourComponent.componentAdded(node, component);
			}

			for (BehaviourComponent behaviourComponent : eventSystem.getListeners(node,
					BehaviourEventCallbacks.nodeComponentAdded)) {
				behaviourComponent.nodeComponentAdded(component);
			}
		}

		@Override
		public void componentRemoved(SceneNodeComponent component) {
			SceneNode node = component.getNode();
			for (BehaviourComponent behaviourComponent : eventSystem.getListeners(BehaviourEventCallbacks.componentRemoved)) {
				behaviourComponent.componentRemoved(node, component);
			}

			for (BehaviourComponent behaviourComponent : eventSystem.getListeners(node,
					BehaviourEventCallbacks.nodeComponentRemoved)) {
				behaviourComponent.nodeComponentRemoved(component);
			}
		}
	}

	private class SceneStartListener implements Listener0 {
		@Override
		public void handle() {
			for (BehaviourComponent behaviourComponent : eventSystem.getListeners(BehaviourEventCallbacks.onSceneStart)) {
				behaviourComponent.onSceneStart();
			}
		}
	}

	private class SceneStopListener implements Listener0 {
		@Override
		public void handle() {
			for (BehaviourComponent behaviourComponent : eventSystem.getListeners(BehaviourEventCallbacks.onSceneStop)) {
				behaviourComponent.onSceneStop();
			}
		}
	}

	private class PauseListener implements Listener0 {
		@Override
		public void handle() {
			for (BehaviourComponent behaviourComponent : eventSystem.getListeners(BehaviourEventCallbacks.onPause)) {
				behaviourComponent.onPause();
			}
		}
	}

	private class ResumeListener implements Listener0 {
		@Override
		public void handle() {
			for (BehaviourComponent behaviourComponent : eventSystem.getListeners(BehaviourEventCallbacks.onResume)) {
				behaviourComponent.onPause();
			}
		}
	}
}
