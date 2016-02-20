package com.gurella.engine.state.transitionmanager;

import com.gurella.engine.state.StateTransition;

public interface StateContext<STATE> {
	StateTransition<STATE> getStateTransition(STATE newState);

	void stateChanged(STATE newState);

	STATE getCurrentState();
	
	STATE getInitialState();
}
