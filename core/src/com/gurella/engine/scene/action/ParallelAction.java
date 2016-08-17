package com.gurella.engine.scene.action;

public class ParallelAction extends CompositeAction {
	ParallelAction() {
	}

	public ParallelAction(Action action1) {
		super(action1);
	}

	public ParallelAction(Action action1, Action action2) {
		super(action1, action2);
	}

	public ParallelAction(Action action1, Action action2, Action action3) {
		super(action1, action2, action3);
	}

	public ParallelAction(Action action1, Action action2, Action action3, Action action4) {
		super(action1, action2, action3, action4);
	}

	public ParallelAction(Action action1, Action action2, Action action3, Action action4,
			Action action5) {
		super(action1, action2, action3, action4, action5);
	}

	@Override
	public boolean doAct() {
		boolean complete = actions.size == 0;
		for (int i = 0, n = actions.size; i < n; i++) {
			Action currentAction = actions.get(i);
			if (!currentAction.isComplete()) {
				complete |= currentAction.act();
			}
		}
		return complete;
	}
}
