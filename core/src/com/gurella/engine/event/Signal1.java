package com.gurella.engine.event;

public abstract class Signal1<LISTENER, EVENT> extends AbstractSignal<LISTENER> {
	public void dispatch(EVENT event) {
		for (LISTENER listener : listeners) {
			dispatch(listener, event);
		}
	}

	protected abstract void dispatch(LISTENER listener, EVENT event);
	
	public static class Signal1Impl<EVENT> extends Signal1<Listener1<EVENT>, EVENT> {
		@Override
		protected void dispatch(Listener1<EVENT> listener, EVENT event) {
			listener.handle(event);
		}
	}
}
