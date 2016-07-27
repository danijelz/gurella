package com.gurella.engine.state;

public abstract class BaseStateMachineContext<STATE> implements StateMachineContext<STATE> {
	private STATE initialState;

	public BaseStateMachineContext(STATE initialState) {
		this.initialState = initialState;
		stateChanged(initialState);
	}

	@Override
	public STATE getInitialState() {
		return initialState;
	}

	@Override
	public void stateChanged(STATE newState) {
	}

	@Override
	public void reset() {
	}
}