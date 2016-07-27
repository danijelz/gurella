package com.gurella.engine.state;

import com.badlogic.gdx.utils.Array;

public class StackStateMachineContext<STATE> implements StateMachineContext<STATE> {
	private StateMachineContext<STATE> delegate;
	private STATE popState;
	private StateTransition<STATE> defaultPopTransition;

	private Array<STATE> stateStack = new Array<STATE>();

	public StackStateMachineContext(StateMachineContext<STATE> delegate, STATE popState,
			StateTransition<STATE> defaultPopTransition) {
		this.delegate = delegate;
		this.popState = popState;
		this.defaultPopTransition = defaultPopTransition;
		stateStack.add(getInitialState());
	}

	@Override
	public STATE getInitialState() {
		return delegate.getInitialState();
	}

	@Override
	public void stateChanged(STATE newState) {
		stateStack.add(newState);
		delegate.stateChanged(newState);
	}

	@Override
	public StateTransition<STATE> getStateTransition(STATE sourceState, STATE destinationState) {
		StateTransition<STATE> transition = delegate.getStateTransition(sourceState, destinationState);
		if (transition == null && destinationState == popState) {
			return defaultPopTransition;
		}
		return transition;
	}

	@Override
	public void reset() {
		delegate.reset();
		stateStack.clear();
		stateStack.add(getInitialState());
	}
}
