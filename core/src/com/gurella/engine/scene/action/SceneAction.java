package com.gurella.engine.scene.action;

import static com.gurella.engine.pool.PoolService.obtain;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.event.Listener0;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.scene.action.CoroutineAction.Coroutine;

public abstract class SceneAction implements Poolable {
	private boolean began;
	private boolean complete;

	private Listener0 beginListener;
	private Listener0 completeListener;

	public final boolean act() {
		if (complete) {
			return true;
		}

		if (!began) {
			if (beginListener != null) {
				beginListener.handle();
			}
			began = true;
		}

		complete = doAct();

		if (complete) {
			if (completeListener != null) {
				completeListener.handle();
			}
		}

		return complete;
	}

	abstract public boolean doAct();

	public final boolean isComplete() {
		return complete;
	}

	public void restart() {
		began = false;
		complete = false;
	}

	@Override
	public void reset() {
		restart();
	}

	public static ActionBuilder delay(float duration) {
		ActionBuilder builder = obtain(ActionBuilder.class);
		builder.delay(duration);
		return builder;
	}

	public static ActionBuilder repeat(int repeatCount) {
		ActionBuilder builder = obtain(ActionBuilder.class);
		builder.repeat(repeatCount);
		return builder;
	}

	public static ActionBuilder runnable(Runnable runnable) {
		ActionBuilder builder = obtain(ActionBuilder.class);
		builder.runnable(runnable);
		return builder;
	}

	public static ActionBuilder coroutine(Coroutine coroutine) {
		ActionBuilder builder = obtain(ActionBuilder.class);
		builder.coroutine(coroutine);
		return builder;
	}

	public static ActionBuilder parallel() {
		ActionBuilder builder = obtain(ActionBuilder.class);
		builder.parallel();
		return builder;
	}

	public static ActionBuilder sequence() {
		ActionBuilder builder = obtain(ActionBuilder.class);
		builder.sequence();
		return builder;
	}

	public static ActionBuilder tween(Tween tween, float duration) {
		ActionBuilder builder = obtain(ActionBuilder.class);
		builder.tween(tween, duration);
		return builder;
	}

	public static ActionBuilder tween(Tween tween, float duration, Interpolation interpolation) {
		ActionBuilder builder = obtain(ActionBuilder.class);
		builder.tween(tween, duration, interpolation);
		return builder;
	}

	public static class ActionBuilder implements Poolable {
		private Array<SceneAction> stack = new Array<SceneAction>();

		ActionBuilder() {
		}

		private void append(SceneAction action) {
			SceneAction current = stack.size > 0 ? stack.peek() : null;
			if (current == null) {
				stack.add(action);
				return;
			}

			if (current instanceof CompositeAction) {
				((CompositeAction) current).addOwnedAction(action);
			} else if (current instanceof RepeatAction && ((RepeatAction) current).delegate == null) {
				((RepeatAction) current).delegate = action;
				end();
			} else {
				SequenceAction sequenceAction = obtain(SequenceAction.class);
				sequenceAction.addOwnedAction(current);
				sequenceAction.addOwnedAction(action);
				stack.pop();
				stack.add(sequenceAction);
			}

			if (action instanceof CompositeAction || action instanceof RepeatAction) {
				stack.add(action);
			}
		}

		public ActionBuilder delay(float duration) {
			DelayAction action = obtain(DelayAction.class);
			action.duration = duration;
			append(action);
			return this;
		}

		public ActionBuilder repeat(int repeatCount) {
			RepeatAction action = obtain(RepeatAction.class);
			action.repeatCount = repeatCount;
			append(action);
			return this;
		}

		public ActionBuilder runnable(Runnable runnable) {
			RunnableAction action = obtain(RunnableAction.class);
			action.runnable = runnable;
			append(action);
			return this;
		}

		public ActionBuilder coroutine(Coroutine coroutine) {
			CoroutineAction action = obtain(CoroutineAction.class);
			action.coroutine = coroutine;
			append(action);
			return this;
		}

		public ActionBuilder tween(Tween tween, float duration) {
			return tween(tween, duration, null);
		}

		public ActionBuilder tween(Tween tween, float duration, Interpolation interpolation) {
			TweenAction action = obtain(TweenAction.class);
			action.tween = tween;
			action.duration = duration;
			action.interpolation = interpolation;
			append(action);
			return this;
		}

		public ActionBuilder sequence() {
			append(obtain(SequenceAction.class));
			return this;
		}

		public ActionBuilder parallel() {
			append(obtain(ParallelAction.class));
			return this;
		}

		public ActionBuilder end() {
			if (stack.size > 1) {
				stack.pop();
			}
			return this;
		}

		public ActionBuilder onBegin(Listener0 beginListener) {
			getLastAdded().beginListener = beginListener;
			return this;
		}

		private SceneAction getLastAdded() {
			SceneAction top = stack.peek();
			if (top instanceof CompositeAction) {
				CompositeAction composite = (CompositeAction) top;
				return composite.actions.size == 0 ? composite : composite.actions.peek();
			} else if (top instanceof RepeatAction && ((RepeatAction) top).delegate == null) {
				RepeatAction repeatAction = (RepeatAction) top;
				return repeatAction.delegate == null ? repeatAction : repeatAction.delegate;
			} else {
				return top;
			}
		}

		public ActionBuilder onComplete(Listener0 completeListener) {
			getLastAdded().completeListener = completeListener;
			return this;
		}

		public SceneAction build() {
			SceneAction action = stack.get(0);
			stack.clear();
			PoolService.free(this);
			return action;
		}

		private void print() {
			System.out.println(print(build(), 0));
		}

		private String print(SceneAction action, int level) {
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < level; i++) {
				builder.append('\t');
			}
			builder.append(action.getClass().getSimpleName());
			if (action instanceof CompositeAction) {
				CompositeAction compositeAction = (CompositeAction) action;
				for (int i = 0; i < compositeAction.actions.size; i++) {
					builder.append('\n');
					builder.append(print(compositeAction.actions.get(i), level + 1));
				}
			} else if (action instanceof RepeatAction) {
				RepeatAction repeatAction = (RepeatAction) action;
				builder.append('\n');
				builder.append(print(repeatAction.delegate, level + 1));
			}
			return builder.toString();
		}

		@Override
		public void reset() {
			if (stack.size > 0) {
				PoolService.freeAll(stack);
				stack.clear();
			}
		}
	}

	public static void main(String[] args) {
		SceneAction.sequence().delay(1).coroutine(null).parallel().delay(1).coroutine(null).end().delay(1).repeat(2)
				.sequence().coroutine(null).delay(0).repeat(2).delay(0).end().delay(0).repeat(2).parallel().delay(0)
				.delay(0).end().print();
	}
}
