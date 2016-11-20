package com.gurella.engine.scene.action;

import static com.gurella.engine.pool.PoolService.obtain;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.event.Listener0;
import com.gurella.engine.event.Listener1;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.scene.action.CoroutineAction.Coroutine;

public abstract class Action implements Poolable {
	private boolean began;
	private boolean complete;

	private Listener0 beginListener;
	private Listener1<Boolean> actListener;
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
		if (actListener != null) {
			actListener.handle(Boolean.valueOf(complete));
		}

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

	private static ActionBuilder obtainBuilder() {
		ActionBuilder builder = obtain(ActionBuilder.class);
		builder.free = true;
		return builder;
	}

	public static ActionBuilder delay(float duration) {
		ActionBuilder builder = obtainBuilder();
		builder.delay(duration);
		return builder;
	}

	public static ActionBuilder repeat(int repeatCount) {
		ActionBuilder builder = obtainBuilder();
		builder.repeat(repeatCount);
		return builder;
	}

	public static ActionBuilder runnable(Runnable runnable) {
		ActionBuilder builder = obtainBuilder();
		builder.runnable(runnable);
		return builder;
	}

	public static ActionBuilder coroutine(Coroutine coroutine) {
		ActionBuilder builder = obtainBuilder();
		builder.coroutine(coroutine);
		return builder;
	}

	public static ActionBuilder parallel() {
		ActionBuilder builder = obtainBuilder();
		builder.parallel();
		return builder;
	}

	public static ActionBuilder sequence() {
		ActionBuilder builder = obtainBuilder();
		builder.sequence();
		return builder;
	}

	public static ActionBuilder tween(Tween tween, float duration) {
		ActionBuilder builder = obtainBuilder();
		builder.tween(tween, duration);
		return builder;
	}

	public static ActionBuilder tween(Tween tween, float duration, Interpolation interpolation) {
		ActionBuilder builder = obtainBuilder();
		builder.tween(tween, duration, interpolation);
		return builder;
	}

	public static class ActionBuilder implements Poolable {
		boolean free;
		private Array<Action> stack = new Array<Action>();

		ActionBuilder() {
		}

		private void append(Action action) {
			Action current = stack.size > 0 ? stack.peek() : null;
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

		private Action getLastAdded() {
			Action top = stack.peek();
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

		public ActionBuilder onAct(Listener1<Boolean> actListener) {
			getLastAdded().actListener = actListener;
			return this;
		}

		public ActionBuilder onComplete(Listener0 completeListener) {
			getLastAdded().completeListener = completeListener;
			return this;
		}

		public Action build() {
			Action action = stack.get(0);
			stack.clear();
			if (free) {
				PoolService.free(this);
			}
			return action;
		}

		@Override
		public void reset() {
			free = false;
			if (stack.size > 0) {
				PoolService.freeAll(stack);
				stack.clear();
			}
		}
	}

	public static void main(String[] args) {
		Listener0 l = new Listener0() {
			@Override
			public void handle() {
			}
		};

		Action action = Action.sequence().onBegin(l).delay(1).onBegin(l).coroutine(null).onBegin(l).parallel()
				.onBegin(l).delay(1).onBegin(l).coroutine(null).onBegin(l).end().delay(1).onBegin(l).repeat(2)
				.onBegin(l).sequence().onBegin(l).coroutine(null).onBegin(l).delay(0).onBegin(l).repeat(2).onBegin(l)
				.delay(0).onBegin(l).end().delay(0).onBegin(l).repeat(2).onBegin(l).parallel().onBegin(l).delay(0)
				.onBegin(l).delay(0).onBegin(l).end().build();
		System.out.println(getDiagnostics(action, 0));
	}

	private static String getDiagnostics(Action action, int level) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < level; i++) {
			builder.append('\t');
		}
		builder.append(action.getClass().getSimpleName());
		if (action.beginListener != null) {
			builder.append('*');
		}
		if (action instanceof CompositeAction) {
			CompositeAction compositeAction = (CompositeAction) action;
			for (int i = 0; i < compositeAction.actions.size; i++) {
				builder.append('\n');
				builder.append(getDiagnostics(compositeAction.actions.get(i), level + 1));
			}
		} else if (action instanceof RepeatAction) {
			RepeatAction repeatAction = (RepeatAction) action;
			builder.append('\n');
			builder.append(getDiagnostics(repeatAction.delegate, level + 1));
		}
		return builder.toString();
	}
}
