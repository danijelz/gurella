package com.gurella.engine.scene;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.event.Event0;
import com.gurella.engine.event.Event1;
import com.gurella.engine.event.Event3;
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

@TypePriority(priority = CommonUpdatePriority.updatePriority, type = ApplicationUpdateListener.class)
class SceneEventsDispatcher implements ApplicationUpdateListener, Poolable {
	private static final SceneStartedEvent sceneStartedEvent = new SceneStartedEvent();
	private static final SceneStoppedEvent sceneStoppedEvent = new SceneStoppedEvent();

	private static final ComponentActivatedEvent componentActivatedEvent = new ComponentActivatedEvent();
	private static final ComponentDeactivatedEvent componentDeactivatedEvent = new ComponentDeactivatedEvent();
	private static final NodeComponentActivatedEvent nodeComponentActivatedEvent = new NodeComponentActivatedEvent();
	private static final NodeComponentDeactivatedEvent nodeComponentDeactivatedEvent = new NodeComponentDeactivatedEvent();

	private static final NodeRenamedEvent modeRenamedEvent = new NodeRenamedEvent();

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
		EventService.post(sceneId, componentActivatedEvent, component);
		EventService.post(component.getNodeId(), nodeComponentActivatedEvent, component);
	}

	void componentDeactivated(SceneNodeComponent2 component) {
		EventService.post(sceneId, componentDeactivatedEvent, component);
		EventService.post(component.getNodeId(), nodeComponentDeactivatedEvent, component);
	}

	void nodeRenamed(SceneNode2 node, String oldName, String newName) {
		EventService.post(sceneId, modeRenamedEvent, node, oldName, newName);
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

	private static class SceneStartedEvent implements Event0<SceneActivityListener> {
		@Override
		public Class<SceneActivityListener> getSubscriptionType() {
			return SceneActivityListener.class;
		}

		@Override
		public void notify(SceneActivityListener listener) {
			listener.sceneStarted();
		}
	}

	private static class SceneStoppedEvent implements Event0<SceneActivityListener> {
		@Override
		public Class<SceneActivityListener> getSubscriptionType() {
			return SceneActivityListener.class;
		}

		@Override
		public void notify(SceneActivityListener listener) {
			listener.sceneStopped();
		}
	}

	private static class ComponentActivatedEvent implements Event1<ComponentActivityListener, SceneNodeComponent2> {
		@Override
		public Class<ComponentActivityListener> getSubscriptionType() {
			return ComponentActivityListener.class;
		}

		@Override
		public void notify(ComponentActivityListener listener, SceneNodeComponent2 component) {
			listener.componentActivated(component);
		}
	}

	private static class ComponentDeactivatedEvent implements Event1<ComponentActivityListener, SceneNodeComponent2> {
		@Override
		public Class<ComponentActivityListener> getSubscriptionType() {
			return ComponentActivityListener.class;
		}

		@Override
		public void notify(ComponentActivityListener listener, SceneNodeComponent2 component) {
			listener.componentDeactivated(component);
		}
	}

	private static class NodeComponentActivatedEvent
			implements Event1<NodeComponentActivityListener, SceneNodeComponent2> {
		@Override
		public Class<NodeComponentActivityListener> getSubscriptionType() {
			return NodeComponentActivityListener.class;
		}

		@Override
		public void notify(NodeComponentActivityListener listener, SceneNodeComponent2 component) {
			listener.nodeComponentActivated(component);
		}
	}

	private static class NodeComponentDeactivatedEvent
			implements Event1<NodeComponentActivityListener, SceneNodeComponent2> {
		@Override
		public Class<NodeComponentActivityListener> getSubscriptionType() {
			return NodeComponentActivityListener.class;
		}

		@Override
		public void notify(NodeComponentActivityListener listener, SceneNodeComponent2 component) {
			listener.nodeComponentDeactivated(component);
		}
	}

	private static class NodeRenamedEvent implements Event3<NodeRenamedListener, SceneNode2, String, String> {
		@Override
		public Class<NodeRenamedListener> getSubscriptionType() {
			return NodeRenamedListener.class;
		}

		@Override
		public void notify(NodeRenamedListener listener, SceneNode2 node, String oldName, String newName) {
			listener.nodeRenamed(node, oldName, newName);
		}
	}

	private static class IoUpdateEvent implements Event0<IoUpdateListener> {
		@Override
		public Class<IoUpdateListener> getSubscriptionType() {
			return IoUpdateListener.class;
		}

		@Override
		public void notify(IoUpdateListener listener) {
			listener.onIoUpdate();
		}
	}

	private static class InputUpdateEvent implements Event0<InputUpdateListener> {
		@Override
		public Class<InputUpdateListener> getSubscriptionType() {
			return InputUpdateListener.class;
		}

		@Override
		public void notify(InputUpdateListener listener) {
			listener.onInputUpdate();
		}
	}

	private static class LogicUpdateEvent implements Event0<LogicUpdateListener> {
		@Override
		public Class<LogicUpdateListener> getSubscriptionType() {
			return LogicUpdateListener.class;
		}

		@Override
		public void notify(LogicUpdateListener listener) {
			listener.onLogicUpdate();
		}
	}

	private static class PhysicsUpdateEvent implements Event0<PhysicsUpdateListener> {
		@Override
		public Class<PhysicsUpdateListener> getSubscriptionType() {
			return PhysicsUpdateListener.class;
		}

		@Override
		public void notify(PhysicsUpdateListener listener) {
			listener.onPhysicsUpdate();
		}
	}

	private static class UpdateEvent implements Event0<UpdateListener> {
		@Override
		public Class<UpdateListener> getSubscriptionType() {
			return UpdateListener.class;
		}

		@Override
		public void notify(UpdateListener listener) {
			listener.onUpdate();
		}
	}

	private static class PreRenderUpdateEvent implements Event0<PreRenderUpdateListener> {
		@Override
		public Class<PreRenderUpdateListener> getSubscriptionType() {
			return PreRenderUpdateListener.class;
		}

		@Override
		public void notify(PreRenderUpdateListener listener) {
			listener.onPreRenderUpdate();
		}
	}

	private static class RenderUpdateEvent implements Event0<RenderUpdateListener> {
		@Override
		public Class<RenderUpdateListener> getSubscriptionType() {
			return RenderUpdateListener.class;
		}

		@Override
		public void notify(RenderUpdateListener listener) {
			listener.onRenderUpdate();
		}
	}

	private static class PostRenderUpdateEvent implements Event0<PostRenderUpdateListener> {
		@Override
		public Class<PostRenderUpdateListener> getSubscriptionType() {
			return PostRenderUpdateListener.class;
		}

		@Override
		public void notify(PostRenderUpdateListener listener) {
			listener.onPostRenderUpdate();
		}
	}

	private static class CleanupUpdateEvent implements Event0<CleanupUpdateListener> {
		@Override
		public Class<CleanupUpdateListener> getSubscriptionType() {
			return CleanupUpdateListener.class;
		}

		@Override
		public void notify(CleanupUpdateListener listener) {
			listener.onCleanupUpdate();
		}
	}
}
