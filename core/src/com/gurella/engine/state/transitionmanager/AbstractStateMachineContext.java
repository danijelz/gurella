package com.gurella.engine.state.transitionmanager;

public abstract class AbstractStateMachineContext<STATE> implements StateMachineContext<STATE> {
	private STATE currentState;

	public AbstractStateMachineContext(STATE initialState) {
		stateChanged(initialState);
	}

	@Override
	public void stateChanged(STATE newState) {
		currentState = newState;
	}

	@Override
	public STATE getCurrentState() {
		return currentState;
	}
}