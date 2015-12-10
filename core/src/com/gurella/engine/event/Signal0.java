package com.gurella.engine.event;

public abstract class Signal0<LISTENER> extends AbstractSignal<LISTENER> {
	public void dispatch() {
		for (LISTENER listener : listeners) {
			dispatch(listener);
		}
	}

	protected abstract void dispatch(LISTENER listener);
	
	public static class Signal0Impl extends Signal0<Listener0> {
		@Override
		protected void dispatch(Listener0 listener) {
			listener.handle();
		}
	}
}
