package com.gurella.engine.scene.action;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.base.object.Attachment;
import com.gurella.engine.event.EventService;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.scene.SceneElement2;
import com.gurella.engine.subscriptions.application.CommonUpdatePriority;
import com.gurella.engine.subscriptions.scene.update.CleanupUpdateListener;
import com.gurella.engine.subscriptions.scene.update.DebugRenderUpdateListener;
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
		ActionAttachment attachment = obtain(updatePriority);
		attachment.value = action;
		attachment.owner = owner;
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
		case DEBUG_RENDER:
			return PoolService.obtain(DebugRenderActionAttachment.class);
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
			if (value.act()) {
				owner.detach(this);
			}
		}
	}

	private static class InputActionAttachment extends ActionAttachment implements InputUpdateListener {
		@Override
		public void onInputUpdate() {
			if (value.act()) {
				owner.detach(this);
			}
		}
	}

	private static class LogicActionAttachment extends ActionAttachment implements LogicUpdateListener {
		@Override
		public void onLogicUpdate() {
			if (value.act()) {
				owner.detach(this);
			}
		}
	}

	private static class PhysicsActionAttachment extends ActionAttachment implements PhysicsUpdateListener {
		@Override
		public void onPhysicsUpdate() {
			if (value.act()) {
				owner.detach(this);
			}
		}
	}

	private static class UpdateActionAttachment extends ActionAttachment implements UpdateListener {
		@Override
		public void onUpdate() {
			if (value.act()) {
				owner.detach(this);
			}
		}
	}

	private static class PreRenderActionAttachment extends ActionAttachment implements PreRenderUpdateListener {
		@Override
		public void onPreRenderUpdate() {
			if (value.act()) {
				owner.detach(this);
			}
		}
	}

	private static class RenderActionAttachment extends ActionAttachment implements RenderUpdateListener {
		@Override
		public void onRenderUpdate() {
			if (value.act()) {
				owner.detach(this);
			}
		}
	}

	private static class DebugRenderActionAttachment extends ActionAttachment implements DebugRenderUpdateListener {
		@Override
		public void onDebugRenderUpdate() {
			if (value.act()) {
				owner.detach(this);
			}
		}
	}

	private static class PostRenderActionAttachment extends ActionAttachment implements PostRenderUpdateListener {
		@Override
		public void onPostRenderUpdate() {
			if (value.act()) {
				owner.detach(this);
			}
		}
	}

	private static class CleanupActionAttachment extends ActionAttachment implements CleanupUpdateListener {
		@Override
		public void onCleanupUpdate() {
			if (value.act()) {
				owner.detach(this);
			}
		}
	}
}
