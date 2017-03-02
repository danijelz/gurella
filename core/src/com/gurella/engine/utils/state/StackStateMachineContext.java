package com.gurella.engine.utils.state;

import com.badlogic.gdx.utils.Array;

public class StackStateMachineContext<STATE> extends BaseStateMachineContext<STATE> {
	private StateMachineContext<STATE> delegate;
	private STATE popState;
	private StateTransition<STATE> defaultPopTransition;

	private Array<STATE> stateStack = new Array<STATE>();

	public StackStateMachineContext(STATE initialState, StateMachineContext<STATE> delegate, STATE popState,
			StateTransition<STATE> defaultPopTransition) {
		super(initialState);
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
	public final void stateChanged(STATE newState) {
		stateStack.add(newState);
		delegate.stateChanged(newState);
	}

	@Override
	public StateTransition<STATE> getTransition(STATE sourceState, STATE destinationState) {
		StateTransition<STATE> transition = delegate.getTransition(sourceState, destinationState);
		if (transition == null && destinationState == popState) {
			return defaultPopTransition;
		}
		return transition;
	}

	@Override
	public StateTransition<STATE> getInterruptTransition(STATE source, STATE originalDestination,
			StateTransition<STATE> originalTransition, STATE newDestination) {
		return delegate.getInterruptTransition(source, originalDestination, originalTransition, newDestination);
	}

	@Override
	public void reset() {
		delegate.reset();
		stateStack.clear();
		stateStack.add(getInitialState());
	}
}
