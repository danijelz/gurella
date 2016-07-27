package com.gurella.engine.state;

import com.badlogic.gdx.utils.Array;

public class StackStateTransitionManager<STATE> implements StateContext<STATE> {
	private StateContext<STATE> delegate;
	private STATE popState;
	private StateTransition<STATE> defaultPopTransition;

	private Array<STATE> stateStack = new Array<STATE>();

	public StackStateTransitionManager(StateContext<STATE> delegate, STATE popState,
			StateTransition<STATE> defaultPopTransition) {
		this.delegate = delegate;
		this.popState = popState;
		this.defaultPopTransition = defaultPopTransition;
	}

	@Override
	public void stateChanged(STATE newState) {
		stateStack.add(getCurrentState());
		delegate.stateChanged(newState);
	}

	@Override
	public StateTransition<STATE> getStateTransition(STATE newState) {
		StateTransition<STATE> transition = delegate.getStateTransition(newState);
		if (transition == null && newState == popState) {
			return defaultPopTransition;
		}
		return transition;
	}

	@Override
	public STATE getCurrentState() {
		return delegate.getCurrentState();
	}

	@Override
	public void reset() {
		delegate.reset();
		stateStack.clear();
	}
}
