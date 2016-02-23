package com.gurella.engine.state.transitionmanager;

public abstract class AbstractStateContext<STATE> implements StateContext<STATE> {
	private STATE initialState;
	private STATE currentState;

	public AbstractStateContext(STATE initialState) {
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
	public STATE getInitialState() {
		return initialState;
	}
}