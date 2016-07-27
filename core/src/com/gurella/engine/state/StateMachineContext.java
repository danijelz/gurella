package com.gurella.engine.state;

public interface StateMachineContext<STATE> {
	STATE getInitialState();

	StateTransition<STATE> getStateTransition(STATE sourceState, STATE destinationState);

	void stateChanged(STATE newState);

	void reset();
}
