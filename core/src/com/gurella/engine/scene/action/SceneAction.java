package com.gurella.engine.scene.action;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.scene.action.CoroutineAction.Coroutine;

public abstract class SceneAction implements Poolable {
	abstract public boolean act();

	abstract public boolean isComplete();

	public void restart() {
	}

	@Override
	public void reset() {
		restart();
	}

	public static ActionBuilder delay(float duration) {
		ActionBuilder builder = PoolService.obtain(ActionBuilder.class);
		builder.delay(duration);
		return builder;
	}

	public static ActionBuilder repeat(int repeatCount) {
		ActionBuilder builder = PoolService.obtain(ActionBuilder.class);
		builder.repeat(repeatCount);
		return builder;
	}

	public static ActionBuilder runnable(Runnable runnable) {
		ActionBuilder builder = PoolService.obtain(ActionBuilder.class);
		builder.runnable(runnable);
		return builder;
	}

	public static ActionBuilder coroutine(Coroutine coroutine) {
		ActionBuilder builder = PoolService.obtain(ActionBuilder.class);
		builder.coroutine(coroutine);
		return builder;
	}

	public static ActionBuilder parallel() {
		ActionBuilder builder = PoolService.obtain(ActionBuilder.class);
		builder.parallel();
		return builder;
	}

	public static ActionBuilder sequence() {
		ActionBuilder builder = PoolService.obtain(ActionBuilder.class);
		builder.sequence();
		return builder;
	}

	public static ActionBuilder tween(Tween tween, float duration) {
		ActionBuilder builder = PoolService.obtain(ActionBuilder.class);
		builder.tween(tween, duration);
		return builder;
	}

	public static ActionBuilder tween(Tween tween, float duration, Interpolation interpolation) {
		ActionBuilder builder = PoolService.obtain(ActionBuilder.class);
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
				SequenceAction sequenceAction = PoolService.obtain(SequenceAction.class);
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
			DelayAction action = PoolService.obtain(DelayAction.class);
			action.duration = duration;
			append(action);
			return this;
		}

		public ActionBuilder repeat(int repeatCount) {
			RepeatAction action = PoolService.obtain(RepeatAction.class);
			action.repeatCount = repeatCount;
			append(action);
			return this;
		}

		public ActionBuilder runnable(Runnable runnable) {
			RunnableAction action = PoolService.obtain(RunnableAction.class);
			action.runnable = runnable;
			append(action);
			return this;
		}

		public ActionBuilder coroutine(Coroutine coroutine) {
			CoroutineAction action = PoolService.obtain(CoroutineAction.class);
			action.coroutine = coroutine;
			append(action);
			return this;
		}

		public ActionBuilder tween(Tween tween, float duration) {
			return tween(tween, duration, null);
		}

		public ActionBuilder tween(Tween tween, float duration, Interpolation interpolation) {
			TweenAction action = PoolService.obtain(TweenAction.class);
			action.tween = tween;
			action.duration = duration;
			action.interpolation = interpolation;
			append(action);
			return this;
		}

		public ActionBuilder sequence() {
			append(PoolService.obtain(SequenceAction.class));
			return this;
		}

		public ActionBuilder parallel() {
			append(PoolService.obtain(ParallelAction.class));
			return this;
		}

		public ActionBuilder end() {
			if (stack.size > 1) {
				stack.pop();
			}
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
				.sequence().coroutine(null).delay(0).repeat(2).delay(0).end().delay(0).print();
	}
}
