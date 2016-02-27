package com.gurella.engine.state.transitionmanager;

import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.event.EventService;
import com.gurella.engine.event.Signal;
import com.gurella.engine.event.TypePriorities;
import com.gurella.engine.event.TypePriority;
import com.gurella.engine.state.StateTransition;
import com.gurella.engine.subscriptions.application.ApplicationUpdateListener;
import com.gurella.engine.subscriptions.application.CommonUpdatePriority;

@TypePriorities({ @TypePriority(priority = CommonUpdatePriority.THINK, type = ApplicationUpdateListener.class) })
public class StateMachine<STATE> extends Signal<StateMachine.StateChangedListener<STATE>>
		implements ApplicationUpdateListener {
	private StateTransition<STATE> currentTransition;
	private ObjectMap<STATE, StateChangedSignal> stateListeners = new ObjectMap<STATE, StateChangedSignal>();
	private StateContext<STATE> context;

	public StateMachine(StateContext<STATE> stateTransitionManager) {
		this.context = stateTransitionManager;
		stateChanged(stateTransitionManager.getCurrentState());
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
		currentTransition.process();
		if (currentTransition.isFinished()) {
			endTransition();
		} else {
			EventService.subscribe(this);
		}
	}

	private void endTransition() {
		STATE currentState = currentTransition.getDestination();
		context.stateChanged(currentState);
		stateChanged(currentState);
		dispatchStateChanged(currentState);
		currentTransition = null;
	}

	protected void stateChanged(@SuppressWarnings("unused") STATE newState) {
	}

	private void dispatchStateChanged(STATE currentState) {
		dispatch(currentTransition.getSource(), currentState);
		StateChangedSignal signal = stateListeners.get(currentState);
		if (signal != null) {
			signal.dispatch(currentState);
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

	private void dispatch(STATE oldState, STATE newState) {
		StateChangedListener<STATE>[] items = listeners.begin();
		for (int i = 0, n = listeners.size; i < n; i++) {
			items[i].stateChanged(oldState, newState);
		}
		listeners.end();
	}

	@Override
	public void update() {
		if (currentTransition.isFinished()) {
			endTransition();
			EventService.unsubscribe(this);
		}
	}

	public void addListener(STATE state, StateListener<STATE> listener) {
		if (listener != null) {
			StateMachine<STATE>.StateChangedSignal signal = stateListeners.get(state);
			if (signal != null) {
				signal = new StateChangedSignal();
				stateListeners.put(state, signal);
			}
			signal.addListener(listener);
		}
	}

	public void removeListener(STATE state, StateListener<STATE> listener) {
		if (listener != null && stateListeners.containsKey(state)) {
			stateListeners.get(state).removeListener(listener);
		}
	}

	@Override
	public void clear() {
		super.clear();
		stateListeners.clear();
		context.stateChanged(context.getInitialState());
	}

	public interface StateChangedListener<STATE> {
		void stateChanged(STATE oldState, STATE newState);
	}

	private class StateChangedSignal extends Signal<StateListener<STATE>> {
		private void dispatch(STATE event) {
			StateListener<STATE>[] items = listeners.begin();
			for (int i = 0, n = listeners.size; i < n; i++) {
				items[i].stateChanged(event);
			}
			listeners.end();
		}
	}

	public interface StateListener<STATE> {
		void stateChanged(STATE newState);
	}
}
