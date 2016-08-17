package com.gurella.engine.scene.action;

public class ParallelAction extends CompositeAction {
	ParallelAction() {
	}

	public ParallelAction(SceneAction action1) {
		super(action1);
	}

	public ParallelAction(SceneAction action1, SceneAction action2) {
		super(action1, action2);
	}

	public ParallelAction(SceneAction action1, SceneAction action2, SceneAction action3) {
		super(action1, action2, action3);
	}

	public ParallelAction(SceneAction action1, SceneAction action2, SceneAction action3, SceneAction action4) {
		super(action1, action2, action3, action4);
	}

	public ParallelAction(SceneAction action1, SceneAction action2, SceneAction action3, SceneAction action4,
			SceneAction action5) {
		super(action1, action2, action3, action4, action5);
	}

	@Override
	public boolean doAct() {
		boolean complete = actions.size == 0;
		for (int i = 0, n = actions.size; i < n; i++) {
			SceneAction currentAction = actions.get(i);
			if (!currentAction.isComplete()) {
				complete |= currentAction.act();
			}
		}
		return complete;
	}
}
