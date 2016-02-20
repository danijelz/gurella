package com.gurella.engine.state.transitionmanager;

import com.gurella.engine.state.StateTransition;

public abstract class LooseStateMachineContext<STATE> extends AbstractStateContext<STATE> {
	public LooseStateMachineContext(STATE initialState) {
		super(initialState);
	}

	@Override
	public StateTransition<STATE> getStateTransition(STATE newState) {
		return getStateTransition(getCurrentState(), newState);
	}

	protected abstract StateTransition<STATE> getStateTransition(STATE currentState, STATE newState);
}