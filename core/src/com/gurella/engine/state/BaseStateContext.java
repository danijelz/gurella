package com.gurella.engine.state;

public abstract class BaseStateContext<STATE> implements StateContext<STATE> {
	private STATE initialState;
	private STATE currentState;

	public BaseStateContext(STATE initialState) {
		this.initialState = initialState;
		stateChanged(initialState);
	}

	@Override
	public STATE getCurrentState() {
		return currentState;
	}

	@Override
	public void stateChanged(STATE newState) {
		currentState = newState;
	}

	@Override
	public void reset() {
		stateChanged(initialState);
	}
}