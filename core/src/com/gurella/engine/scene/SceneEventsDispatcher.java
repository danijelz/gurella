package com.gurella.engine.scene;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.event.Event;
import com.gurella.engine.event.EventService;
import com.gurella.engine.event.TypePriority;
import com.gurella.engine.subscriptions.application.ApplicationUpdateListener;
import com.gurella.engine.subscriptions.application.CommonUpdatePriority;
import com.gurella.engine.subscriptions.scene.ComponentActivityListener;
import com.gurella.engine.subscriptions.scene.NodeComponentActivityListener;
import com.gurella.engine.subscriptions.scene.NodeRenamedListener;
import com.gurella.engine.subscriptions.scene.SceneActivityListener;
import com.gurella.engine.subscriptions.scene.update.CleanupUpdateListener;
import com.gurella.engine.subscriptions.scene.update.InputUpdateListener;
import com.gurella.engine.subscriptions.scene.update.IoUpdateListener;
import com.gurella.engine.subscriptions.scene.update.LogicUpdateListener;
import com.gurella.engine.subscriptions.scene.update.PhysicsUpdateListener;
import com.gurella.engine.subscriptions.scene.update.PostRenderUpdateListener;
import com.gurella.engine.subscriptions.scene.update.PreRenderUpdateListener;
import com.gurella.engine.subscriptions.scene.update.RenderUpdateListener;
import com.gurella.engine.subscriptions.scene.update.UpdateListener;
import com.gurella.engine.utils.Values;

@TypePriority(priority = CommonUpdatePriority.updatePriority, type = ApplicationUpdateListener.class)
class SceneEventsDispatcher implements ApplicationUpdateListener, Poolable {
	private static final SceneStartedEvent sceneStartedEvent = new SceneStartedEvent();
	private static final SceneStoppedEvent sceneStoppedEvent = new SceneStoppedEvent();

	private static final IoUpdateEvent ioUpdateEvent = new IoUpdateEvent();
	private static final InputUpdateEvent inputUpdateEvent = new InputUpdateEvent();
	private static final LogicUpdateEvent logicUpdateEvent = new LogicUpdateEvent();
	private static final PhysicsUpdateEvent physicsUpdateEvent = new PhysicsUpdateEvent();
	private static final UpdateEvent updateEvent = new UpdateEvent();
	private static final PreRenderUpdateEvent preRenderUpdateEvent = new PreRenderUpdateEvent();
	private static final RenderUpdateEvent renderUpdateEvent = new RenderUpdateEvent();
	private static final PostRenderUpdateEvent postRenderUpdateEvent = new PostRenderUpdateEvent();
	private static final CleanupUpdateEvent cleanupUpdateEvent = new CleanupUpdateEvent();

	private final Scene scene;
	private int sceneId;

	private final Array<Object> tempListeners = new Array<Object>(128);

	SceneEventsDispatcher(Scene scene) {
		this.scene = scene;
		sceneId = scene.getInstanceId();
	}

	void activate() {
		EventService.subscribe(this);
		EventService.post(sceneId, sceneStartedEvent);
	}

	void deactivate() {
		EventService.post(sceneId, sceneStoppedEvent);
		EventService.unsubscribe(this);
	}

	void componentActivated(SceneNodeComponent2 component) {
		Array<ComponentActivityListener> sceneListeners = Values.cast(tempListeners);
		EventService.getSubscribers(sceneId, ComponentActivityListener.class, sceneListeners);
		for (int i = 0; i < sceneListeners.size; i++) {
			sceneListeners.get(i).componentActivated(component);
		}
		tempListeners.clear();

		Array<NodeComponentActivityListener> listeners = Values.cast(tempListeners);
		EventService.getSubscribers(component.getNodeId(), NodeComponentActivityListener.class, listeners);
		for (int i = 0; i < listeners.size; i++) {
			listeners.get(i).nodeComponentActivated(component);
		}
		tempListeners.clear();
	}

	void componentDeactivated(SceneNodeComponent2 component) {
		Array<ComponentActivityListener> sceneListeners = Values.cast(tempListeners);
		EventService.getSubscribers(sceneId, ComponentActivityListener.class, sceneListeners);
		for (int i = 0; i < sceneListeners.size; i++) {
			sceneListeners.get(i).componentDeactivated(component);
		}
		tempListeners.clear();

		Array<NodeComponentActivityListener> listeners = Values.cast(tempListeners);
		EventService.getSubscribers(component.getNodeId(), NodeComponentActivityListener.class, listeners);
		for (int i = 0; i < listeners.size; i++) {
			listeners.get(i).nodeComponentDeactivated(component);
		}
		tempListeners.clear();
	}

	void nodeRenamed(SceneNode2 node, String oldName, String newName) {
		Array<NodeRenamedListener> sceneListeners = Values.cast(tempListeners);
		EventService.getSubscribers(sceneId, NodeRenamedListener.class, sceneListeners);
		for (int i = 0; i < sceneListeners.size; i++) {
			sceneListeners.get(i).nodeRenamed(node, oldName, newName);
		}
		tempListeners.clear();
	}

	@Override
	public void update() {
		EventService.post(sceneId, ioUpdateEvent);
		EventService.post(sceneId, inputUpdateEvent);
		EventService.post(sceneId, logicUpdateEvent);
		EventService.post(sceneId, physicsUpdateEvent);
		EventService.post(sceneId, updateEvent);
		EventService.post(sceneId, preRenderUpdateEvent);
		EventService.post(sceneId, renderUpdateEvent);
		EventService.post(sceneId, postRenderUpdateEvent);
		EventService.post(sceneId, cleanupUpdateEvent);
	}

	@Override
	public void reset() {
		sceneId = scene.getInstanceId();
	}

	private static class SceneStartedEvent implements Event<SceneActivityListener> {
		@Override
		public Class<SceneActivityListener> getSubscriptionType() {
			return SceneActivityListener.class;
		}

		@Override
		public void dispatch(SceneActivityListener listener) {
			listener.sceneStarted();
		}
	}

	private static class SceneStoppedEvent implements Event<SceneActivityListener> {
		@Override
		public Class<SceneActivityListener> getSubscriptionType() {
			return SceneActivityListener.class;
		}

		@Override
		public void dispatch(SceneActivityListener listener) {
			listener.sceneStopped();
		}
	}

	private static class IoUpdateEvent implements Event<IoUpdateListener> {
		@Override
		public Class<IoUpdateListener> getSubscriptionType() {
			return IoUpdateListener.class;
		}

		@Override
		public void dispatch(IoUpdateListener listener) {
			listener.onIoUpdate();
		}
	}

	private static class InputUpdateEvent implements Event<InputUpdateListener> {
		@Override
		public Class<InputUpdateListener> getSubscriptionType() {
			return InputUpdateListener.class;
		}

		@Override
		public void dispatch(InputUpdateListener listener) {
			listener.onInputUpdate();
		}
	}

	private static class LogicUpdateEvent implements Event<LogicUpdateListener> {
		@Override
		public Class<LogicUpdateListener> getSubscriptionType() {
			return LogicUpdateListener.class;
		}

		@Override
		public void dispatch(LogicUpdateListener listener) {
			listener.onLogicUpdate();
		}
	}

	private static class PhysicsUpdateEvent implements Event<PhysicsUpdateListener> {
		@Override
		public Class<PhysicsUpdateListener> getSubscriptionType() {
			return PhysicsUpdateListener.class;
		}

		@Override
		public void dispatch(PhysicsUpdateListener listener) {
			listener.onPhysicsUpdate();
		}
	}

	private static class UpdateEvent implements Event<UpdateListener> {
		@Override
		public Class<UpdateListener> getSubscriptionType() {
			return UpdateListener.class;
		}

		@Override
		public void dispatch(UpdateListener listener) {
			listener.onUpdate();
		}
	}

	private static class PreRenderUpdateEvent implements Event<PreRenderUpdateListener> {
		@Override
		public Class<PreRenderUpdateListener> getSubscriptionType() {
			return PreRenderUpdateListener.class;
		}

		@Override
		public void dispatch(PreRenderUpdateListener listener) {
			listener.onPreRenderUpdate();
		}
	}

	private static class RenderUpdateEvent implements Event<RenderUpdateListener> {
		@Override
		public Class<RenderUpdateListener> getSubscriptionType() {
			return RenderUpdateListener.class;
		}

		@Override
		public void dispatch(RenderUpdateListener listener) {
			listener.onRenderUpdate();
		}
	}

	private static class PostRenderUpdateEvent implements Event<PostRenderUpdateListener> {
		@Override
		public Class<PostRenderUpdateListener> getSubscriptionType() {
			return PostRenderUpdateListener.class;
		}

		@Override
		public void dispatch(PostRenderUpdateListener listener) {
			listener.onPostRenderUpdate();
		}
	}

	private static class CleanupUpdateEvent implements Event<CleanupUpdateListener> {
		@Override
		public Class<CleanupUpdateListener> getSubscriptionType() {
			return CleanupUpdateListener.class;
		}

		@Override
		public void dispatch(CleanupUpdateListener listener) {
			listener.onCleanupUpdate();
		}
	}
}
