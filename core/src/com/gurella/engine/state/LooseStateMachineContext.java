package com.gurella.engine.state;

public class LooseStateMachineContext<STATE> extends BaseStateMachineContext<STATE> {
	private StateTransition<STATE> defaultTransition;

	public LooseStateMachineContext(STATE initialState, StateTransition<STATE> defaultTransition) {
		super(initialState);
		this.defaultTransition = defaultTransition;
	}

	@Override
	public StateTransition<STATE> getStateTransition(STATE sourceState, STATE destinationState) {
		return defaultTransition;
	}
}