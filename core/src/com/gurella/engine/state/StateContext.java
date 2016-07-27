package com.gurella.engine.state;

public interface StateContext<STATE> {
	StateTransition<STATE> getStateTransition(STATE newState);

	void stateChanged(STATE newState);

	STATE getCurrentState();
	
	void reset();
}
