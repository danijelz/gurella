package com.gurella.engine.state;

import com.gurella.engine.event.EventService;
import com.gurella.engine.event.EventSubscription;
import com.gurella.engine.event.Signal;
import com.gurella.engine.event.SubscriptionEvent;
import com.gurella.engine.event.TypePriority;
import com.gurella.engine.subscriptions.application.ApplicationUpdateListener;
import com.gurella.engine.subscriptions.application.CommonUpdatePriority;

@TypePriority(priority = CommonUpdatePriority.logicPriority, type = ApplicationUpdateListener.class)
public class StateMachine<STATE> implements ApplicationUpdateListener {
	// private int instanceId;
	// private StateChangedEvent<STATE> stateChangedEvent = new StateChangedEvent<STATE>();
	private StateChangedSignal signal = new StateChangedSignal();

	private StateMachineContext<STATE> context;
	private STATE currentState;

	private StateTransition<STATE> currentTransition;
	private STATE destinationState;

	public StateMachine(StateMachineContext<STATE> context) {
		// instanceId = SequenceGenerator.next();
		this.context = context;
		currentState = context.getInitialState();
	}

	public boolean apply(STATE destination) {
		StateTransition<STATE> stateTransition = getStateTransition(destination);

		if (stateTransition == null) {
			return false;
		} else {
			currentTransition = stateTransition;
			destinationState = destination;
			processTransition();
			return true;
		}
	}

	private StateTransition<STATE> getStateTransition(STATE destination) {
		if (isInTransition()) {
			return context.getInterruptTransition(currentState, destinationState, currentTransition, destination);
		} else {
			return context.getTransition(currentState, destination);
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
		signal.dispatch(currentState, destinationState);
		currentState = destinationState;

		currentTransition = null;
		destinationState = null;
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

	public void removeListener(STATE state, StateChangedListener<STATE> listener) {
		signal.removeListener(listener);
	}

	public void reset() {
		signal.clear();
		context.reset();
		currentState = context.getInitialState();
	}

	public interface StateChangedListener<STATE> extends EventSubscription {
		void stateChanged(STATE sourceState, STATE destinationState);
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

	private static class StateChangedEvent<STATE> extends SubscriptionEvent<StateChangedListener> {
		STATE sourceState;
		STATE destinationState;

		private StateChangedEvent() {
			super(StateChangedListener.class);
		}

		@Override
		protected void notify(StateChangedListener listener) {
			listener.stateChanged(sourceState, destinationState);

		}
	}
}
