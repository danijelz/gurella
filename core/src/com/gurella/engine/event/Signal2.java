package com.gurella.engine.event;

public abstract class Signal2<LISTENER, ARG1, ARG2> extends AbstractSignal<LISTENER> {
	public void dispatch(ARG1 arg1, ARG2 arg2) {
		for (LISTENER listener : listeners) {
			dispatch(listener, arg1, arg2);
		}
	}

	protected abstract void dispatch(LISTENER listener, ARG1 arg1, ARG2 arg2);

	public static class Signal2Impl<ARG1, ARG2> extends Signal2<Listener2<ARG1, ARG2>, ARG1, ARG2> {
		@Override
		protected void dispatch(Listener2<ARG1, ARG2> listener, ARG1 arg1, ARG2 arg2) {
			listener.handle(arg1, arg2);
		}
	}
}
