package com.gurella.engine.state.transitionmanager;

import com.gurella.engine.state.StateTransition;

public abstract class LooseStateMachineContext<STATE> extends AbstractStateMachineContext<STATE> {
	public LooseStateMachineContext(STATE initialState) {
		super(initialState);
	}

	@Override
	public StateTransition<STATE> getStateTransition(STATE newState) {
		return getStateTransition(getCurrentState(), newState);
	}

	protected abstract StateTransition<STATE> getStateTransition(STATE currentState, STATE newState);
}