package com.gurella.engine.scene.action;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.pool.PoolService;

public abstract class SceneAction implements Poolable {
	abstract public boolean act();

	abstract public boolean isComplete();

	public void restart() {
	}

	@Override
	public void reset() {
		restart();
	}

	public static class ActionBuilder {
		private Array<SceneAction> stack = new Array<SceneAction>();

		private void append(SceneAction action) {
			SceneAction current = stack.peek();
			if (current == null) {
				stack.add(action);
				return;
			}

			if (current instanceof CompositeAction) {
				((CompositeAction) current).addOwnedAction(action);
			} else {
				SequenceAction sequenceAction = PoolService.obtain(SequenceAction.class);
				sequenceAction.addOwnedAction(action);
			}
		}

		public ActionBuilder delay(float duration) {
			DelayAction action = PoolService.obtain(DelayAction.class);
			action.duration = duration;
			append(action);
			return this;
		}
	}
}
