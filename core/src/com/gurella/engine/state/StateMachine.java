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

	private StateContext<STATE> context;

	private StateTransition<STATE> currentTransition;

	public StateMachine(StateContext<STATE> stateTransitionManager) {
		this.context = stateTransitionManager;
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
		if (isInTransition()) {
			return null;
		} else {
			return context.getStateTransition(newState);
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
		STATE oldState = currentTransition.getSource();
		STATE newState = currentTransition.getDestination();
		context.stateChanged(newState);
		dispatchStateChanged(oldState, newState);
		currentTransition = null;
	}

	private void dispatchStateChanged(STATE oldState, STATE newState) {
		signal.dispatch(oldState, newState);
		StateChangedSignal signal = stateListeners.get(newState);
		if (signal != null) {
			signal.dispatch(oldState, newState);
		}
	}

	public STATE getCurrentState() {
		return context.getCurrentState();
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
