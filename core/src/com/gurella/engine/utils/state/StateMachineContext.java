package com.gurella.engine.utils.state;

public interface StateMachineContext<STATE> {
	STATE getInitialState();

	StateTransition<STATE> getTransition(STATE source, STATE destination);

	StateTransition<STATE> getInterruptTransition(STATE source, STATE currentDestination,
			StateTransition<STATE> currentTransition, STATE newDestination);

	void stateChanged(STATE newState);

	void reset();

	<V> V put(Object key, V value);

	<V> V get(Object key);

	<V> V remove(Object key);

	boolean containsKey(Object key);
}
