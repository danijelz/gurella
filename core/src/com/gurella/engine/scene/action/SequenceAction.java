package com.gurella.engine.scene.action;

public class SequenceAction extends CompositeAction {
	private int index;

	SequenceAction() {
	}

	public SequenceAction(Action action1) {
		super(action1);
	}

	public SequenceAction(Action action1, Action action2) {
		super(action1, action2);
	}

	public SequenceAction(Action action1, Action action2, Action action3) {
		super(action1, action2, action3);
	}

	public SequenceAction(Action action1, Action action2, Action action3, Action action4) {
		super(action1, action2, action3, action4);
	}

	public SequenceAction(Action action1, Action action2, Action action3, Action action4,
			Action action5) {
		super(action1, action2, action3, action4, action5);
	}

	@Override
	public boolean doAct() {
		if (actions.get(index).act()) {
			index++;
		}

		return index >= actions.size;
	}

	@Override
	public void restart() {
		super.restart();
		index = 0;
	}
}
