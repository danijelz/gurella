package com.gurella.engine.scene.event;

import com.gurella.engine.application.events.ApplicationActivityListener;
import com.gurella.engine.application.events.ApplicationResizeListener;
import com.gurella.engine.scene.SceneNodeComponent;
import com.gurella.engine.utils.ImmutableArray;

public final class SceneEventsSignal {
	private EventManager eventManager;

	public SceneEventsSignal(EventManager eventManager) {
		this.eventManager = eventManager;
	}

	public void sceneStarted() {
		ImmutableArray<SceneActivityListener> listeners = eventManager.getListeners(SceneActivityListener.class);
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).sceneStarted();
		}
	}

	public void sceneStopped() {
		ImmutableArray<SceneActivityListener> listeners = eventManager.getListeners(SceneActivityListener.class);
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).sceneStopped();
		}
	}

	public void componentActivated(SceneNodeComponent component) {
		ImmutableArray<ComponentActivityListener> listeners = eventManager
				.getListeners(ComponentActivityListener.class);
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).componentActivated(component);
		}

		ImmutableArray<NodeComponentActivityListener> nodeListeners = eventManager.getListeners(component.getNode(),
				NodeComponentActivityListener.class);
		for (int i = 0; i < nodeListeners.size(); i++) {
			nodeListeners.get(i).nodeComponentActivated(component);
		}
	}

	public void componentDeactivated(SceneNodeComponent component) {
		ImmutableArray<ComponentActivityListener> listeners = eventManager
				.getListeners(ComponentActivityListener.class);
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).componentDeactivated(component);
		}

		ImmutableArray<NodeComponentActivityListener> nodeListeners = eventManager.getListeners(component.getNode(),
				NodeComponentActivityListener.class);
		for (int i = 0; i < nodeListeners.size(); i++) {
			nodeListeners.get(i).nodeComponentDeactivated(component);
		}
	}

	public void componentAdded(SceneNodeComponent component) {
		ImmutableArray<ComponentAccumulationListener> listeners = eventManager
				.getListeners(ComponentAccumulationListener.class);
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).componentAdded(component);
		}

		ImmutableArray<NodeComponentAccumulationListener> nodeListeners = eventManager.getListeners(component.getNode(),
				NodeComponentAccumulationListener.class);
		for (int i = 0; i < nodeListeners.size(); i++) {
			nodeListeners.get(i).nodeComponentAdded(component);
		}
	}

	public void componentRemoved(SceneNodeComponent component) {
		ImmutableArray<ComponentAccumulationListener> listeners = eventManager
				.getListeners(ComponentAccumulationListener.class);
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).componentRemoved(component);
		}

		ImmutableArray<NodeComponentAccumulationListener> nodeListeners = eventManager.getListeners(component.getNode(),
				NodeComponentAccumulationListener.class);
		for (int i = 0; i < nodeListeners.size(); i++) {
			nodeListeners.get(i).nodeComponentRemoved(component);
		}
	}

	public void update() {
		ImmutableArray<InputUpdateListener> onInputListeners = eventManager.getListeners(InputUpdateListener.class);
		for (int i = 0; i < onInputListeners.size(); i++) {
			onInputListeners.get(i).onInputUpdate();
		}

		ImmutableArray<ThinkUpdateListener> onThinkListeners = eventManager.getListeners(ThinkUpdateListener.class);
		for (int i = 0; i < onThinkListeners.size(); i++) {
			onThinkListeners.get(i).onThinkUpdate();
		}

		ImmutableArray<PhysicsUpdateListener> onPhysicsListeners = eventManager
				.getListeners(PhysicsUpdateListener.class);
		for (int i = 0; i < onPhysicsListeners.size(); i++) {
			onPhysicsListeners.get(i).onPhysicsUpdate();
		}

		ImmutableArray<UpdateListener> updateListeners = eventManager.getListeners(UpdateListener.class);
		for (int i = 0; i < updateListeners.size(); i++) {
			updateListeners.get(i).onUpdate();
		}

		ImmutableArray<PreRenderUpdateListener> preRenderListeners = eventManager
				.getListeners(PreRenderUpdateListener.class);
		for (int i = 0; i < preRenderListeners.size(); i++) {
			preRenderListeners.get(i).onPreRenderUpdate();
		}

		ImmutableArray<RenderUpdateListener> renderListeners = eventManager.getListeners(RenderUpdateListener.class);
		for (int i = 0; i < renderListeners.size(); i++) {
			renderListeners.get(i).onRenderUpdate();
		}

		ImmutableArray<PostRenderUpdateListener> postRenderListeners = eventManager
				.getListeners(PostRenderUpdateListener.class);
		for (int i = 0; i < postRenderListeners.size(); i++) {
			postRenderListeners.get(i).onPostRenderUpdate();
		}

		ImmutableArray<CleanupUpdateListener> cleanupListeners = eventManager.getListeners(CleanupUpdateListener.class);
		for (int i = 0; i < cleanupListeners.size(); i++) {
			cleanupListeners.get(i).onCleanupUpdate();
		}
	}

	public void resize(int width, int height) {
		ImmutableArray<ApplicationResizeListener> listeners = eventManager
				.getListeners(ApplicationResizeListener.class);
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).onResize(width, height);
		}
	}

	public void pause(int width, int height) {
		ImmutableArray<ApplicationActivityListener> listeners = eventManager
				.getListeners(ApplicationActivityListener.class);
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).onPause();
		}
	}

	public void resume(int width, int height) {
		ImmutableArray<ApplicationActivityListener> listeners = eventManager
				.getListeners(ApplicationActivityListener.class);
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).onResume();
		}
	}

	public interface SceneActivityListener extends EventSubscription {
		void sceneStarted();

		void sceneStopped();
	}

	public interface ComponentActivityListener extends EventSubscription {
		void componentActivated(SceneNodeComponent component);

		void componentDeactivated(SceneNodeComponent component);
	}

	public interface ComponentAccumulationListener extends EventSubscription {
		void componentAdded(SceneNodeComponent component);

		void componentRemoved(SceneNodeComponent component);
	}

	public interface NodeComponentActivityListener extends EventSubscription {
		void nodeComponentActivated(SceneNodeComponent component);

		void nodeComponentDeactivated(SceneNodeComponent component);
	}

	public interface NodeComponentAccumulationListener extends EventSubscription {
		void nodeComponentAdded(SceneNodeComponent component);

		void nodeComponentRemoved(SceneNodeComponent component);
	}

	public interface InputUpdateListener extends EventSubscription {
		void onInputUpdate();
	}

	public interface ThinkUpdateListener extends EventSubscription {
		void onThinkUpdate();
	}

	public interface PhysicsUpdateListener extends EventSubscription {
		void onPhysicsUpdate();
	}

	public interface UpdateListener extends EventSubscription {
		void onUpdate();
	}

	public interface PreRenderUpdateListener extends EventSubscription {
		void onPreRenderUpdate();
	}

	public interface RenderUpdateListener extends EventSubscription {
		void onRenderUpdate();
	}

	public interface PostRenderUpdateListener extends EventSubscription {
		void onPostRenderUpdate();
	}

	public interface CleanupUpdateListener extends EventSubscription {
		void onCleanupUpdate();
	}
}
