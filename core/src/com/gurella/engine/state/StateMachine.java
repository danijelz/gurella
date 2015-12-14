package com.gurella.engine.state;

import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.application.CommonUpdateOrder;
import com.gurella.engine.application.events.UpdateEvent;
import com.gurella.engine.application.events.UpdateListener;
import com.gurella.engine.event.EventService;
import com.gurella.engine.event.Signal1;
import com.gurella.engine.event.Signal2;
import com.gurella.engine.state.StateTransition.SimpleStateTransition;

public class StateMachine<STATE> extends Signal2<StateMachine.StateChangedListener<STATE>, STATE, STATE> implements UpdateListener {
	private STATE previousState;
	private STATE currentState;
	private StateTransition<STATE> currentTransition;
	private ObjectMap<STATE, StateChangedSignal> stateListeners = new ObjectMap<STATE, StateChangedSignal>();

	private ObjectMap<STATE, ObjectMap<STATE, StateTransition<STATE>>> transitions = new ObjectMap<STATE, ObjectMap<STATE, StateTransition<STATE>>>();

	public StateMachine(STATE initialState) {
		currentState = initialState;
		stateChanged(currentState);
	}
	
	public StateMachine<STATE> put(STATE from, STATE to) {
		return put(new SimpleStateTransition<STATE>(from, to));
	}

	public StateMachine<STATE> put(StateTransition<STATE> transition) {
		STATE source = transition.getSource();

		if (!transitions.containsKey(source)) {
			transitions.put(source, new ObjectMap<STATE, StateTransition<STATE>>());
		}

		ObjectMap<STATE, StateTransition<STATE>> triggersMap = transitions.get(source);
		triggersMap.put(transition.getDestination(), transition);

		return this;
	}

	public boolean apply(STATE newState) {
		StateTransition<STATE> stateTransition = getStateTransition(newState);

		if (stateTransition == null) {
			return false;
		} else {
			currentTransition = stateTransition;
			processTransition();
			return true;
		}
	}

	private StateTransition<STATE> getStateTransition(STATE newState) {
		if (isInTransition() || !transitions.containsKey(currentState)) {
			return null;
		} else {
			return transitions.get(currentState).get(newState);
		}
	}

	private void processTransition() {
		currentTransition.process();
		if (currentTransition.isFinished()) {
			endTransition();
		} else {
			EventService.addListener(UpdateEvent.class, this);
		}
	}

	private void endTransition() {
		previousState = currentState;
		currentState = currentTransition.getDestination();
		dispatchStateChanged();
		currentTransition = null;
		stateChanged(currentState);
	}

	private void dispatchStateChanged() {
		dispatch(currentTransition.getSource(), currentTransition.getDestination());
		StateChangedSignal stateChangedSignal = stateListeners.get(currentState);
		if (stateChangedSignal != null) {
			stateChangedSignal.dispatch(currentState);
		}
	}
	
	protected void stateChanged(@SuppressWarnings("unused") STATE newState) {
	}

	public STATE getCurrentState() {
		return currentState;
	}

	public boolean isInState(STATE... states) {
		if (states == null || currentState == null) {
			return false;
		} else {
			for (STATE state : states) {
				if (currentState == state) {
					return true;
				}
			}

			return false;
		}
	}

	public STATE getPreviousState() {
		return previousState;
	}

	public boolean isInTransition() {
		return currentTransition != null;
	}

	@Override
	protected void dispatch(StateChangedListener<STATE> listener, STATE oldState, STATE newState) {
		listener.stateChanged(oldState, newState);
	}

	@Override
	public void update() {
		if (currentTransition.isFinished()) {
			endTransition();
			EventService.removeListener(UpdateEvent.class, this);
		}
	}

	@Override
	public int getPriority() {
		return CommonUpdateOrder.THINK;
	}

	public void addListener(STATE state, StateListener<STATE> listener) {
		if (listener != null) {
			if (!stateListeners.containsKey(state)) {
				stateListeners.put(state, new StateChangedSignal());
			}
			stateListeners.get(state).addListener(listener);
		}
	}

	public void removeListener(STATE state, StateListener<STATE> listener) {
		if (listener != null && stateListeners.containsKey(state)) {
			stateListeners.get(state).removeListener(listener);
		}
	}

	public interface StateChangedListener<STATE> {
		void stateChanged(STATE oldState, STATE newState);
	}

	public interface StateListener<STATE> {
		void stateChanged(STATE newState);
	}

	private class StateChangedSignal extends Signal1<StateListener<STATE>, STATE> {
		@Override
		protected void dispatch(StateListener<STATE> listener, STATE event) {
			listener.stateChanged(event);
		}
	}
}
