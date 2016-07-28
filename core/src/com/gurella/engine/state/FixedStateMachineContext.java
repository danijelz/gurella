package com.gurella.engine.state;

import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.state.StateTransition.SimpleStateTransition;

public class FixedStateMachineContext<STATE> extends BaseStateMachineContext<STATE> {
	private ObjectMap<STATE, ObjectMap<STATE, StateTransition<STATE>>> validTransitions = new ObjectMap<STATE, ObjectMap<STATE, StateTransition<STATE>>>();

	public FixedStateMachineContext(STATE initialState) {
		super(initialState);
	}

	public FixedStateMachineContext<STATE> put(STATE source, STATE destination) {
		return put(SimpleStateTransition.<STATE> getInstance(), source, destination);
	}

	public FixedStateMachineContext<STATE> put(STATE source, STATE... destinations) {
		SimpleStateTransition<STATE> transition = SimpleStateTransition.<STATE> getInstance();
		for (STATE destination : destinations) {
			put(transition, source, destination);
		}
		return this;
	}

	public FixedStateMachineContext<STATE> put(StateTransition<STATE> transition, STATE source, STATE... destinations) {
		for (STATE destination : destinations) {
			put(transition, source, destination);
		}
		return this;
	}

	public FixedStateMachineContext<STATE> put(StateTransition<STATE> transition, STATE source, STATE destination) {
		ObjectMap<STATE, StateTransition<STATE>> triggersMap = validTransitions.get(source);

		if (triggersMap == null) {
			triggersMap = new ObjectMap<STATE, StateTransition<STATE>>();
			validTransitions.put(source, triggersMap);
		}

		triggersMap.put(destination, transition);
		return this;
	}

	@Override
	public StateTransition<STATE> getTransition(STATE sourceState, STATE destinationState) {
		ObjectMap<STATE, StateTransition<STATE>> stateTransitions = validTransitions.get(sourceState);
		return stateTransitions == null ? null : stateTransitions.get(destinationState);
	}
}