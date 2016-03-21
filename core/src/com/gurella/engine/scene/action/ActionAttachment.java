package com.gurella.engine.scene.action;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.base.object.Attachment;
import com.gurella.engine.event.EventService;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.scene.SceneElement2;
import com.gurella.engine.subscriptions.application.CommonUpdatePriority;
import com.gurella.engine.subscriptions.scene.update.InputUpdateListener;
import com.gurella.engine.subscriptions.scene.update.IoUpdateListener;
import com.gurella.engine.subscriptions.scene.update.LogicUpdateListener;
import com.gurella.engine.subscriptions.scene.update.PhysicsUpdateListener;
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
		default:
			throw new IllegalArgumentException("Unhandled updatePriority.");
		}
	}

	private static class IoActionAttachment extends ActionAttachment implements IoUpdateListener {
		@Override
		public void onIoUpdate() {
			if (value.act()) {
				owner.detach(value);
			}
		}
	}

	private static class InputActionAttachment extends ActionAttachment implements InputUpdateListener {
		@Override
		public void onInputUpdate() {
			if (value.act()) {
				owner.detach(value);
			}
		}
	}

	private static class LogicActionAttachment extends ActionAttachment implements LogicUpdateListener {
		@Override
		public void onLogicUpdate() {
			if (value.act()) {
				owner.detach(value);
			}
		}
	}

	private static class PhysicsActionAttachment extends ActionAttachment implements PhysicsUpdateListener {
		@Override
		public void onPhysicsUpdate() {
			if (value.act()) {
				owner.detach(value);
			}
		}
	}

	private static class UpdateActionAttachment extends ActionAttachment implements UpdateListener {
		@Override
		public void onUpdate() {
			if (value.act()) {
				owner.detach(value);
			}
		}
	}
}
