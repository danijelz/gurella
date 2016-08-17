package com.gurella.engine.scene.action;

import com.gurella.engine.pool.PoolService;
import com.gurella.engine.utils.ArrayExt;

public abstract class CompositeAction extends Action {
	int currentAction = 0;
	final ArrayExt<Action> owned = new ArrayExt<Action>();
	final ArrayExt<Action> actions = new ArrayExt<Action>();

	CompositeAction() {
	}

	public CompositeAction(Action action1) {
		addAction(action1);
	}

	public CompositeAction(Action action1, Action action2) {
		addAction(action1);
		addAction(action2);
	}

	public CompositeAction(Action action1, Action action2, Action action3) {
		addAction(action1);
		addAction(action2);
		addAction(action3);
	}

	public CompositeAction(Action action1, Action action2, Action action3, Action action4) {
		addAction(action1);
		addAction(action2);
		addAction(action3);
		addAction(action4);
	}

	public CompositeAction(Action action1, Action action2, Action action3, Action action4,
			Action action5) {
		addAction(action1);
		addAction(action2);
		addAction(action3);
		addAction(action4);
		addAction(action5);
	}

	public void addAction(Action action) {
		actions.add(action);
	}

	public void addOwnedAction(Action action) {
		owned.add(action);
		actions.add(action);
	}

	@Override
	public void restart() {
		super.restart();
		for (int i = 0, n = actions.size; i < n; i++) {
			actions.get(i).restart();
		}
	}

	@Override
	public void reset() {
		super.reset();
		currentAction = 0;
		PoolService.freeAll(owned);
		owned.reset();
		actions.reset();
	}
}
