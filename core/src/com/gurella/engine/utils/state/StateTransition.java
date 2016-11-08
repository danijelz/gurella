package com.gurella.engine.utils.state;

public interface StateTransition<STATE> {
	boolean process();

	public static class SimpleStateTransition<STATE> implements StateTransition<STATE> {
		private static final SimpleStateTransition<Object> instance = new SimpleStateTransition<Object>();

		@Override
		public boolean process() {
			return true;
		}

		public static <STATE> SimpleStateTransition<STATE> getInstance() {
			return (SimpleStateTransition<STATE>) instance;
		}
	}
}
