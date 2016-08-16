package com.gurella.engine.scene.action;

public class SequenceAction extends CompositeAction {
	private int index;

	public SequenceAction() {
	}

	public SequenceAction(SceneAction action1) {
		super(action1);
	}

	public SequenceAction(SceneAction action1, SceneAction action2) {
		super(action1, action2);
	}

	public SequenceAction(SceneAction action1, SceneAction action2, SceneAction action3) {
		super(action1, action2, action3);
	}

	public SequenceAction(SceneAction action1, SceneAction action2, SceneAction action3, SceneAction action4) {
		super(action1, action2, action3, action4);
	}

	public SequenceAction(SceneAction action1, SceneAction action2, SceneAction action3, SceneAction action4,
			SceneAction action5) {
		super(action1, action2, action3, action4, action5);
	}

	@Override
	public boolean act() {
		if (index >= actions.size) {
			return true;
		}

		if (actions.get(index).act()) {
			index++;
		}

		return index >= actions.size;
	}

	@Override
	public boolean isComplete() {
		return index >= actions.size;
	}

	@Override
	public void restart() {
		super.restart();
		index = 0;
	}
}
