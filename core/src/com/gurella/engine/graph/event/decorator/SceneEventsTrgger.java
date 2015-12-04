package com.gurella.engine.graph.event.decorator;

import com.gurella.engine.application.CommonUpdateOrder;
import com.gurella.engine.application.UpdateEvent;
import com.gurella.engine.application.UpdateListener;
import com.gurella.engine.event.EventService;
import com.gurella.engine.graph.SceneGraph;
import com.gurella.engine.graph.SceneGraphListener;
import com.gurella.engine.graph.SceneNode;
import com.gurella.engine.graph.SceneNodeComponent;
import com.gurella.engine.graph.behaviour.BehaviourComponent;
import com.gurella.engine.graph.behaviour.BehaviourEvents;
import com.gurella.engine.graph.event.EventTrigger;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.signal.Listener0;

public class SceneEventsTrgger extends EventTrigger {
	private final ScriptSceneGraphListener scriptSceneGraphListener = new ScriptSceneGraphListener();
	private final SceneStartListener sceneStartListener = new SceneStartListener();
	private final SceneStopListener sceneStopListener = new SceneStopListener();
	private final PauseListener pauseListener = new PauseListener();
	private final ResumeListener resumeListener = new ResumeListener();

	@Override
	protected void activated() {
		eventSystem.getGraph().addListener(scriptSceneGraphListener);
		Scene scene = eventSystem.getScene();
		eventSystem.getScene().startSignal.addListener(sceneStartListener);
		scene.stopSignal.addListener(sceneStopListener);
		scene.pauseSignal.addListener(pauseListener);
		scene.resumeSignal.addListener(resumeListener);
	}

	@Override
	protected void deactivated() {
		eventSystem.getGraph().removeListener(scriptSceneGraphListener);
		Scene scene = eventSystem.getScene();
		scene.startSignal.removeListener(sceneStartListener);
		scene.stopSignal.removeListener(sceneStopListener);
		scene.pauseSignal.removeListener(pauseListener);
		scene.resumeSignal.removeListener(resumeListener);
	}

	private class OnInputUpdateListener implements UpdateListener {
		@Override
		public int getOrdinal() {
			return CommonUpdateOrder.INPUT;
		}

		@Override
		public void update() {
			for (BehaviourComponent behaviourComponent : eventSystem.getListeners(BehaviourEvents.onInput)) {
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
			for (BehaviourComponent behaviourComponent : eventSystem.getListeners(BehaviourEvents.onThink)) {
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
			for (BehaviourComponent behaviourComponent : eventSystem.getListeners(BehaviourEvents.onPreRender)) {
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
			for (BehaviourComponent behaviourComponent : eventSystem.getListeners(BehaviourEvents.onRender)) {
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
			for (BehaviourComponent behaviourComponent : eventSystem.getListeners(BehaviourEvents.onDebugRender)) {
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
			for (BehaviourComponent behaviourComponent : eventSystem.getListeners(BehaviourEvents.onAfterRender)) {
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
			for (BehaviourComponent behaviourComponent : eventSystem.getListeners(BehaviourEvents.onCleanup)) {
				behaviourComponent.onCleanup();
			}
		}
	}

	private class ScriptSceneGraphListener implements SceneGraphListener {
		@Override
		public void componentActivated(SceneNodeComponent component) {
			SceneNode node = component.getNode();
			for (BehaviourComponent behaviourComponent : eventSystem.getListeners(BehaviourEvents.componentActivated)) {
				behaviourComponent.componentActivated(node, component);
			}

			for (BehaviourComponent behaviourComponent : eventSystem.getListeners(node,
					BehaviourEvents.nodeComponentActivated)) {
				behaviourComponent.nodeComponentActivated(component);
			}
		}

		@Override
		public void componentDeactivated(SceneNodeComponent component) {
			SceneNode node = component.getNode();
			for (BehaviourComponent behaviourComponent : eventSystem
					.getListeners(BehaviourEvents.componentDeactivated)) {
				behaviourComponent.componentDeactivated(node, component);
			}

			for (BehaviourComponent behaviourComponent : eventSystem.getListeners(node,
					BehaviourEvents.nodeComponentDeactivated)) {
				behaviourComponent.nodeComponentDeactivated(component);
			}
		}

		@Override
		public void componentAdded(SceneNodeComponent component) {
			SceneNode node = component.getNode();
			for (BehaviourComponent behaviourComponent : eventSystem.getListeners(BehaviourEvents.componentAdded)) {
				behaviourComponent.componentAdded(node, component);
			}

			for (BehaviourComponent behaviourComponent : eventSystem.getListeners(node,
					BehaviourEvents.nodeComponentAdded)) {
				behaviourComponent.nodeComponentAdded(component);
			}
		}

		@Override
		public void componentRemoved(SceneNodeComponent component) {
			SceneNode node = component.getNode();
			for (BehaviourComponent behaviourComponent : eventSystem.getListeners(BehaviourEvents.componentRemoved)) {
				behaviourComponent.componentRemoved(node, component);
			}

			for (BehaviourComponent behaviourComponent : eventSystem.getListeners(node,
					BehaviourEvents.nodeComponentRemoved)) {
				behaviourComponent.nodeComponentRemoved(component);
			}
		}
	}

	private class SceneStartListener implements Listener0 {
		@Override
		public void handle() {
			for (BehaviourComponent behaviourComponent : eventSystem.getListeners(BehaviourEvents.onSceneStart)) {
				behaviourComponent.onSceneStart();
			}
		}
	}

	private class SceneStopListener implements Listener0 {
		@Override
		public void handle() {
			for (BehaviourComponent behaviourComponent : eventSystem.getListeners(BehaviourEvents.onSceneStop)) {
				behaviourComponent.onSceneStop();
			}
		}
	}

	private class PauseListener implements Listener0 {
		@Override
		public void handle() {
			for (BehaviourComponent behaviourComponent : eventSystem.getListeners(BehaviourEvents.onPause)) {
				behaviourComponent.onPause();
			}
		}
	}

	private class ResumeListener implements Listener0 {
		@Override
		public void handle() {
			for (BehaviourComponent behaviourComponent : eventSystem.getListeners(BehaviourEvents.onResume)) {
				behaviourComponent.onPause();
			}
		}
	}
}
