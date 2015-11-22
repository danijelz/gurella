package com.gurella.engine.state.transitionmanager;

import com.gurella.engine.state.StateTransition;

public interface StateMachineContext<STATE> {
	StateTransition<STATE> getStateTransition(STATE newState);

	void stateChanged(STATE newState);

	STATE getCurrentState();
}
