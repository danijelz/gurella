package com.gurella.engine.scene.action;

import com.gurella.engine.pool.PoolService;
import com.gurella.engine.utils.ArrayExt;

public abstract class CompositeAction extends SceneAction {
	int currentAction = 0;
	final ArrayExt<SceneAction> owned = new ArrayExt<SceneAction>();
	final ArrayExt<SceneAction> actions = new ArrayExt<SceneAction>();

	CompositeAction() {
	}

	public CompositeAction(SceneAction action1) {
		addAction(action1);
	}

	public CompositeAction(SceneAction action1, SceneAction action2) {
		addAction(action1);
		addAction(action2);
	}

	public CompositeAction(SceneAction action1, SceneAction action2, SceneAction action3) {
		addAction(action1);
		addAction(action2);
		addAction(action3);
	}

	public CompositeAction(SceneAction action1, SceneAction action2, SceneAction action3, SceneAction action4) {
		addAction(action1);
		addAction(action2);
		addAction(action3);
		addAction(action4);
	}

	public CompositeAction(SceneAction action1, SceneAction action2, SceneAction action3, SceneAction action4,
			SceneAction action5) {
		addAction(action1);
		addAction(action2);
		addAction(action3);
		addAction(action4);
		addAction(action5);
	}

	public void addAction(SceneAction action) {
		actions.add(action);
	}

	public void addOwnedAction(SceneAction action) {
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
