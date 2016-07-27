package com.gurella.engine.state;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;

public abstract class HierarchicalStateMachineContext<STATE> extends BaseStateMachineContext<STATE> {
	private ObjectMap<STATE, ObjectSet<STATE>> validTransitions = new ObjectMap<STATE, ObjectSet<STATE>>();
	private ObjectMap<STATE, Array<STATE>> subsStates = new ObjectMap<STATE, Array<STATE>>();
	private ObjectMap<STATE, STATE> parentStates = new ObjectMap<STATE, STATE>();

	public HierarchicalStateMachineContext(STATE initialState) {
		super(initialState);
	}

	public HierarchicalStateMachineContext<STATE> addState(STATE state, STATE... substates) {
		subsStates.put(state, Array.with(substates));
		for (STATE substate : substates) {
			parentStates.put(state, substate);
		}
		return this;
	}

	public HierarchicalStateMachineContext<STATE> addTransition(STATE source, STATE... desinations) {
		for (STATE desination : desinations) {
			getOrCreateValidTransitionsSet(source).add(desination);
		}
		return this;
	}

	private ObjectSet<STATE> getOrCreateValidTransitionsSet(STATE source) {
		ObjectSet<STATE> validTransitionStates = validTransitions.get(source);
		if (validTransitionStates == null) {
			validTransitionStates = new ObjectSet<STATE>();
			validTransitions.put(source, validTransitionStates);
		}
		return validTransitionStates;
	}

	@Override
	public StateTransition<STATE> getStateTransition(STATE sourceState, STATE destinationState) {
		if (isValidTransition(sourceState, destinationState)) {
			return getStateTransition(sourceState, destinationState, areInSameBranch(sourceState, destinationState));
		} else {
			return null;
		}
	}

	private boolean isValidTransition(STATE currentState, STATE newState) {
		ObjectSet<STATE> transitionsByState = validTransitions.get(currentState);
		if (transitionsByState != null && transitionsByState.contains(newState)) {
			return true;
		}

		STATE parentState = parentStates.get(newState);
		if (parentState == null) {
			return false;
		} else {
			Array<STATE> substates = subsStates.get(parentState);
			if (substates != null && substates.contains(newState, true)) {
				return true;
			} else {
				return isValidTransition(currentState, parentState);
			}
		}
	}

	private boolean areInSameBranch(STATE currentState, STATE newState) {
		Array<STATE> currentStateParentHierarchy = getParentHierarchy(currentState);
		for (STATE newStateParent : getParentHierarchy(newState)) {
			if (currentStateParentHierarchy.contains(newStateParent, true)) {
				return true;
			}
		}
		return false;
	}

	private Array<STATE> getParentHierarchy(STATE state) {
		Array<STATE> parentHierarchy = new Array<STATE>();
		STATE parentState = parentStates.get(state);
		while (parentState != null) {
			parentHierarchy.add(parentState);
		}
		return parentHierarchy;
	}

	protected abstract StateTransition<STATE> getStateTransition(STATE currentState, STATE newState, boolean statesFromSameBranch);
}