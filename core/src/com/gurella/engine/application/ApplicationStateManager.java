package com.gurella.engine.application;

import com.gurella.engine.event.Event;
import com.gurella.engine.event.EventBus;
import com.gurella.engine.signal.Listener1;
import com.gurella.engine.state.StateMachine;

public class ApplicationStateManager extends StateMachine<ApplicationState> {
	public ApplicationStateManager() {
		super(ApplicationState.STOP);

		put(ApplicationState.STOP, ApplicationState.START);
		put(ApplicationState.PAUSE, ApplicationState.START);
		put(ApplicationState.START, ApplicationState.PAUSE);
		put(ApplicationState.PAUSE, ApplicationState.STOP);
	}

	@Override
	protected void stateChanged(ApplicationState newState) {
		super.stateChanged(newState);
		ApplicationStateChangedEvent applicationStateChangedEvent = ApplicationStateChangedEvent.instance;
		applicationStateChangedEvent.newState = newState;
		EventBus.GLOBAL.notify(applicationStateChangedEvent);
	}

	public static class ApplicationStateChangedEvent implements Event<Listener1<ApplicationState>> {
		static ApplicationStateChangedEvent instance = new ApplicationStateChangedEvent();

		ApplicationState newState;

		private ApplicationStateChangedEvent() {
		}

		@Override
		public void notify(Listener1<ApplicationState> listener) {
			listener.handle(newState);
		}
	}
}
