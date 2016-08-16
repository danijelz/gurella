package com.gurella.engine.scene.action;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.base.object.Attachment;
import com.gurella.engine.event.EventService;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.scene.SceneElement2;
import com.gurella.engine.subscriptions.application.CommonUpdatePriority;
import com.gurella.engine.subscriptions.scene.update.CleanupUpdateListener;
import com.gurella.engine.subscriptions.scene.update.InputUpdateListener;
import com.gurella.engine.subscriptions.scene.update.IoUpdateListener;
import com.gurella.engine.subscriptions.scene.update.LogicUpdateListener;
import com.gurella.engine.subscriptions.scene.update.PhysicsUpdateListener;
import com.gurella.engine.subscriptions.scene.update.PostRenderUpdateListener;
import com.gurella.engine.subscriptions.scene.update.PreRenderUpdateListener;
import com.gurella.engine.subscriptions.scene.update.RenderUpdateListener;
import com.gurella.engine.subscriptions.scene.update.UpdateListener;

public abstract class ActionAttachment extends Attachment<SceneAction> implements Poolable {
	protected SceneElement2 owner;
	protected ActionFinishedStrategy actionFinishedStrategy;

	ActionAttachment() {
	}

	@Override
	protected void attach() {
		EventService.subscribe(owner.getScene().getInstanceId(), this);
	}

	@Override
	protected void detach() {
		EventService.unsubscribe(owner.getScene().getInstanceId(), this);
	}

	@Override
	public void reset() {
		PoolService.free(value);
		value = null;
		owner = null;
	}

	public static ActionAttachment obtain(SceneElement2 owner, CommonUpdatePriority updatePriority,
			SceneAction action) {
		return obtain(owner, updatePriority, action, DetachOnFinishedStrategy.instance);
	}

	public static ActionAttachment obtain(SceneElement2 owner, CommonUpdatePriority updatePriority, SceneAction action,
			ActionFinishedStrategy finishedStrategy) {
		ActionAttachment attachment = obtain(updatePriority);
		attachment.value = action;
		attachment.owner = owner;
		attachment.actionFinishedStrategy = finishedStrategy == null ? DetachOnFinishedStrategy.instance
				: finishedStrategy;
		return attachment;
	}

	private static ActionAttachment obtain(CommonUpdatePriority updatePriority) {
		switch (updatePriority) {
		case IO:
			return PoolService.obtain(IoActionAttachment.class);
		case INPUT:
			return PoolService.obtain(InputActionAttachment.class);
		case LOGIC:
			return PoolService.obtain(LogicActionAttachment.class);
		case PHYSICS:
			return PoolService.obtain(PhysicsActionAttachment.class);
		case UPDATE:
			return PoolService.obtain(UpdateActionAttachment.class);
		case PRE_RENDER:
			return PoolService.obtain(PreRenderActionAttachment.class);
		case RENDER:
			return PoolService.obtain(RenderActionAttachment.class);
		case POST_RENDER:
			return PoolService.obtain(PostRenderActionAttachment.class);
		case CLEANUP:
			return PoolService.obtain(CleanupActionAttachment.class);
		default:
			throw new IllegalArgumentException("Unhandled updatePriority.");
		}
	}

	private static class IoActionAttachment extends ActionAttachment implements IoUpdateListener {
		@Override
		public void onIoUpdate() {
			if (!value.isComplete() && value.act()) {
				actionFinishedStrategy.actionFinished(this);
			}
		}
	}

	private static class InputActionAttachment extends ActionAttachment implements InputUpdateListener {
		@Override
		public void onInputUpdate() {
			if (!value.isComplete() && value.act()) {
				actionFinishedStrategy.actionFinished(this);
			}
		}
	}

	private static class LogicActionAttachment extends ActionAttachment implements LogicUpdateListener {
		@Override
		public void onLogicUpdate() {
			if (!value.isComplete() && value.act()) {
				actionFinishedStrategy.actionFinished(this);
			}
		}
	}

	private static class PhysicsActionAttachment extends ActionAttachment implements PhysicsUpdateListener {
		@Override
		public void onPhysicsUpdate() {
			if (!value.isComplete() && value.act()) {
				actionFinishedStrategy.actionFinished(this);
			}
		}
	}

	private static class UpdateActionAttachment extends ActionAttachment implements UpdateListener {
		@Override
		public void onUpdate() {
			if (!value.isComplete() && value.act()) {
				actionFinishedStrategy.actionFinished(this);
			}
		}
	}

	private static class PreRenderActionAttachment extends ActionAttachment implements PreRenderUpdateListener {
		@Override
		public void onPreRenderUpdate() {
			if (!value.isComplete() && value.act()) {
				actionFinishedStrategy.actionFinished(this);
			}
		}
	}

	private static class RenderActionAttachment extends ActionAttachment implements RenderUpdateListener {
		@Override
		public void onRenderUpdate() {
			if (!value.isComplete() && value.act()) {
				actionFinishedStrategy.actionFinished(this);
			}
		}
	}

	private static class PostRenderActionAttachment extends ActionAttachment implements PostRenderUpdateListener {
		@Override
		public void onPostRenderUpdate() {
			if (!value.isComplete() && value.act()) {
				actionFinishedStrategy.actionFinished(this);
			}
		}
	}

	private static class CleanupActionAttachment extends ActionAttachment implements CleanupUpdateListener {
		@Override
		public void onCleanupUpdate() {
			if (!value.isComplete() && value.act()) {
				actionFinishedStrategy.actionFinished(this);
			}
		}
	}

	public interface ActionFinishedStrategy {
		void actionFinished(ActionAttachment actionAttachment);
	}

	public static final class DetachOnFinishedStrategy implements ActionFinishedStrategy {
		public static final DetachOnFinishedStrategy instance = new DetachOnFinishedStrategy();

		private DetachOnFinishedStrategy() {
		}

		@Override
		public void actionFinished(ActionAttachment actionAttachment) {
			actionAttachment.owner.detach(actionAttachment);
		}
	}

	public static final class RestartOnFinishedStrategy implements ActionFinishedStrategy {
		public static final RestartOnFinishedStrategy instance = new RestartOnFinishedStrategy();

		private RestartOnFinishedStrategy() {
		}

		@Override
		public void actionFinished(ActionAttachment actionAttachment) {
			actionAttachment.value.restart();
		}
	}

	public static final class NopFinishedStrategy implements ActionFinishedStrategy {
		public static final NopFinishedStrategy instance = new NopFinishedStrategy();

		private NopFinishedStrategy() {
		}

		@Override
		public void actionFinished(ActionAttachment actionAttachment) {
		}
	}
}
