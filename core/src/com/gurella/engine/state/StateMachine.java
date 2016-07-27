package com.gurella.engine.state;

import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.event.EventService;
import com.gurella.engine.event.Signal;
import com.gurella.engine.event.TypePriority;
import com.gurella.engine.subscriptions.application.ApplicationUpdateListener;
import com.gurella.engine.subscriptions.application.CommonUpdatePriority;

@TypePriority(priority = CommonUpdatePriority.logicPriority, type = ApplicationUpdateListener.class)
public class StateMachine<STATE> implements ApplicationUpdateListener {
	private StateChangedSignal signal = new StateChangedSignal();
	private ObjectMap<STATE, StateChangedSignal> stateListeners = new ObjectMap<STATE, StateChangedSignal>();

	private StateMachineContext<STATE> context;
	private STATE currentState;

	private StateTransition<STATE> currentTransition;
	private STATE destinationState;

	public StateMachine(StateMachineContext<STATE> context) {
		this.context = context;
		currentState = context.getInitialState();
	}

	public boolean apply(STATE newState) {
		StateTransition<STATE> stateTransition = getStateTransition();

		if (stateTransition == null) {
			return false;
		} else {
			currentTransition = stateTransition;
			destinationState = newState;
			processTransition();
			return true;
		}
	}

	private StateTransition<STATE> getStateTransition() {
		if (isInTransition()) {
			return null;
		} else {
			return context.getStateTransition(currentState, destinationState);
		}
	}

	private void processTransition() {
		if (currentTransition.process()) {
			endTransition();
		} else {
			EventService.subscribe(this);
		}
	}

	private void endTransition() {
		context.stateChanged(destinationState);
		dispatchStateChanged();
		currentState = destinationState;

		currentTransition = null;
		destinationState = null;
	}

	private void dispatchStateChanged() {
		signal.dispatch(currentState, destinationState);
		StateChangedSignal signal = stateListeners.get(destinationState);
		if (signal != null) {
			signal.dispatch(currentState, destinationState);
		}
	}

	public STATE getCurrentState() {
		return currentState;
	}

	public boolean isInState(STATE... states) {
		STATE currentState = getCurrentState();
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

	public boolean isInTransition() {
		return currentTransition != null;
	}

	@Override
	public void update() {
		if (currentTransition.process()) {
			endTransition();
			EventService.unsubscribe(this);
		}
	}

	public void addListener(StateChangedListener<STATE> listener) {
		if (listener != null) {
			signal.addListener(listener);
		}
	}

	public void addListener(StateChangedListener<STATE> listener, STATE... states) {
		if (listener != null) {
			for (STATE state : states) {
				StateMachine<STATE>.StateChangedSignal signal = stateListeners.get(state);
				if (signal != null) {
					signal = new StateChangedSignal();
					stateListeners.put(state, signal);
				}
				signal.addListener(listener);
			}
		}
	}

	public void removeListener(STATE state, StateChangedListener<STATE> listener) {
		if (listener != null && stateListeners.containsKey(state)) {
			stateListeners.get(state).removeListener(listener);
		}
	}

	public void reset() {
		stateListeners.clear();
		context.reset();
		currentState = context.getInitialState();
	}

	public interface StateChangedListener<STATE> {
		void stateChanged(STATE oldState, STATE newState);
	}

	private class StateChangedSignal extends Signal<StateChangedListener<STATE>> {
		private void dispatch(STATE oldState, STATE newState) {
			StateChangedListener<STATE>[] items = listeners.begin();
			for (int i = 0, n = listeners.size; i < n; i++) {
				items[i].stateChanged(oldState, newState);
			}
			listeners.end();
		}
	}
}
