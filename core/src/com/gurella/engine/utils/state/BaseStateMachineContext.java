package com.gurella.engine.utils.state;

import com.badlogic.gdx.utils.ObjectMap;

public abstract class BaseStateMachineContext<STATE> implements StateMachineContext<STATE> {
	private STATE initialState;
	private ObjectMap<Object, Object> data = new ObjectMap<Object, Object>();

	public BaseStateMachineContext(STATE initialState) {
		this.initialState = initialState;
	}

	@Override
	public STATE getInitialState() {
		return initialState;
	}

	@Override
	public StateTransition<STATE> getInterruptTransition(STATE source, STATE originalDestination,
			StateTransition<STATE> originalTransition, STATE newDestination) {
		return null;
	}

	@Override
	public void stateChanged(STATE newState) {
	}

	@Override
	public void reset() {
	}

	@Override
	public <V> V put(Object key, V value) {
		if (data == null) {
			data = new ObjectMap<Object, Object>();
		}
		@SuppressWarnings("unchecked")
		V casted = (V) data.put(key, value);
		return casted;
	}

	@Override
	public <V> V get(Object key) {
		if (data == null) {
			return null;
		}
		@SuppressWarnings("unchecked")
		V casted = (V) data.get(key);
		return casted;
	}

	@Override
	public <V> V remove(Object key) {
		if (data == null) {
			return null;
		}
		@SuppressWarnings("unchecked")
		V casted = (V) data.remove(key);
		return casted;
	}

	@Override
	public boolean containsKey(Object key) {
		return data != null && data.containsKey(key);
	}
}