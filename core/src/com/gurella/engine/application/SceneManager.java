package com.gurella.engine.application;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.event.EventService;
import com.gurella.engine.event.TypePriority;

import com.gurella.engine.scene.Scene;
import com.gurella.engine.state.StateMachine;
import com.gurella.engine.subscriptions.application.ApplicationUpdateListener;
import com.gurella.engine.subscriptions.application.CommonUpdatePriority;

public class SceneManager {
	private static final String DEFAULT_TRANSITION_GROUP = "Default";

	private static final SceneTransition defaultTransition = new SceneTransition();

	private Application application;
	private IntMap<Scene> scenes = new IntMap<Scene>();
	private ObjectMap<String, Array<Scene>> scenesByGroup = new ObjectMap<String, Array<Scene>>();

	private String currentSceneGroup = DEFAULT_TRANSITION_GROUP; // TODO groups
	private Scene currentScene;

	private TransitionWorker transitionWorker = new TransitionWorker();

	public SceneManager(Application application) {
		this.application = application;
	}

	public void addScene(Scene scene) {
		scenes.put(scene.getInstanceId(), scene);
		String group = getSceneGroup(scene);
		getGroupScenes(group).add(scene);
	}

	private Array<Scene> getGroupScenes(String group) {
		Array<Scene> groupScenes = scenesByGroup.get(group);
		if (groupScenes == null) {
			groupScenes = new Array<Scene>();
			scenesByGroup.put(group, groupScenes);
		}
		return groupScenes;
	}

	private static String getSceneGroup(Scene scene) {
		/*
		 * String group = scene.getGroup(); group = Values.isBlank(group) ? DEFAULT_TRANSITION_GROUP : group; return
		 * group;
		 */
		return null;
	}

	public IntMap<Scene> getScenes() {
		return scenes;
	}

	public void showScene(String sceneId) {
		showScene(sceneId, defaultTransition);
	}

	public synchronized void showScene(String sceneId, SceneTransition transition) {
		if (transitionWorker.active) {
			throw new IllegalStateException("Scene transition already in progress.");
		}

		Scene destinationScene = null;// scenes.get(sceneId);
		if (destinationScene == null) {
			throw new IllegalArgumentException("Invalid sceneId: " + sceneId);
		}

		if (destinationScene == currentScene) {
			throw new IllegalArgumentException("Scene : " + sceneId + "is already active.");
		}

		transitionWorker.startTransition(destinationScene, transition);
	}

	public Scene getCurrentScene() {
		return currentScene;
	}

	public String getCurrentSceneGroup() {
		return currentSceneGroup;
	}

	@TypePriority(priority = CommonUpdatePriority.ioPriority, type = ApplicationUpdateListener.class)
	private class TransitionWorker implements /*AsyncCallback<DependencyMap>,*/ ApplicationUpdateListener {
		private TransitionStateManager transitionStateManager = new TransitionStateManager();

		private boolean active;

		private Scene destinationScene;
		private SceneTransition transition;

		private float initializationProgress;
		private Throwable initializationException;
		//private DependencyMap destinationSceneResources;

		private final IntArray dependentResourceIds = new IntArray();

		private void startTransition(Scene destinationScene, SceneTransition transition) {
			active = true;
			this.destinationScene = destinationScene;
			this.transition = transition;
			this.transition.init(currentScene, destinationScene);
			// dependentResourceIds.addAll(destinationScene.getInitialSystems());
			// dependentResourceIds.addAll(destinationScene.getInitialNodes());
			// destinationScene.obtainResourcesAsync(dependentResourceIds, this);
			transition.beforeTransitionOut();
			EventService.subscribe(this);
		}

		/*@Override
		public void handleResource(DependencyMap resource) {
			destinationSceneResources = resource;
		}

		@Override
		public void handleException(Throwable exception) {
			cacheException(exception);
		}

		@Override
		public void handleProgress(float progress) {
			initializationProgress = progress;
		}*/

		@Override
		public void update() {
			switch (transitionStateManager.getCurrentState()) {
			case OUT:
				onTransitionOut();
				break;
			case HOLD:
				onTransitionHold();
				break;
			case IN:
				onTransitionIn();
				break;
			case EXCEPTION:
				onException();
				break;
			}
		}

		private void onTransitionOut() {
			try {
				if (transition.onTransitionOut()) {
					transition.afterTransitionOut();
					transition.beforeTransitionHold();
					stopCurrentScene();
					transitionStateManager.apply(SceneTransitionState.HOLD);
				}
			} catch (Exception exception) {
				cacheException(exception);
			}
		}

		private void stopCurrentScene() {
			if (currentScene != null) {
				currentScene.stop();
			}
		}

		private void onTransitionHold() {
			try {
//				if (transition.onTransitionHold(initializationProgress) && destinationSceneResources != null) {
//					transition.afterTransitionHold();
//					// destinationScene.start(destinationSceneResources);
//					transition.beforeTransitionIn();
//					transitionStateManager.apply(SceneTransitionState.IN);
//				}
			} catch (Exception exception) {
				cacheException(exception);
			}
		}

		private void onTransitionIn() {
			try {
				if (transition.onTransitionIn()) {
					transition.afterTransitionIn();
					transitionStateManager.apply(SceneTransitionState.OUT);
					currentScene = destinationScene;
					currentSceneGroup = getSceneGroup(currentScene);
					resetTransitionData();
				}
			} catch (Exception exception) {
				cacheException(exception);
			}
		}

		private void cacheException(Throwable exception) {
			initializationException = exception;
			transitionStateManager.apply(SceneTransitionState.EXCEPTION);
		}

		private void onException() {
			EventService.unsubscribe(this);
			try {
				transition.onTransitionException(initializationException);
				stopCurrentScene();

//				if (destinationSceneResources != null) {
//					// destinationScene.rollback(destinationSceneResources);
//					destinationSceneResources.free();
//					destinationSceneResources = null;
//				}
			} catch (Exception ignored) {
			}

			initializationException = null;
			destinationScene = null;
			transition = null;
			initializationProgress = 0;
			dependentResourceIds.clear();

			active = false;
		}

		private void resetTransitionData() {
			EventService.unsubscribe(this);

			destinationScene = null;
			transition = null;

			initializationProgress = 0;
			initializationException = null;
//			destinationSceneResources.free();
//			destinationSceneResources = null;

			dependentResourceIds.clear();

			active = false;
		}
	}

	private enum SceneTransitionState {
		OUT, HOLD, IN, EXCEPTION;
	}

	private static class TransitionStateManager extends StateMachine<SceneTransitionState> {
		public TransitionStateManager() {
			super(SceneTransitionState.OUT);

			put(SceneTransitionState.OUT, SceneTransitionState.HOLD);
			put(SceneTransitionState.HOLD, SceneTransitionState.IN);
			put(SceneTransitionState.IN, SceneTransitionState.OUT);

			put(SceneTransitionState.OUT, SceneTransitionState.EXCEPTION);
			put(SceneTransitionState.HOLD, SceneTransitionState.EXCEPTION);
			put(SceneTransitionState.IN, SceneTransitionState.EXCEPTION);

			put(SceneTransitionState.EXCEPTION, SceneTransitionState.OUT);
		}
	}
}
