package com.gurella.engine.state;

public interface StateMachineContext<STATE> {
	STATE getInitialState();

	StateTransition<STATE> getTransition(STATE source, STATE destination);

	StateTransition<STATE> getInterruptTransition(STATE source, STATE currentDestination,
			StateTransition<STATE> currentTransition, STATE newDestination);

	void stateChanged(STATE newState);

	void reset();
}
