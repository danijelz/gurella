package com.gurella.engine.state;

public interface StateTransition<STATE> {
	STATE getSource();

	STATE getDestination();

	boolean process();

	public static class SimpleStateTransition<STATE> implements StateTransition<STATE> {
		private STATE source;
		private STATE destination;

		public SimpleStateTransition(STATE source, STATE destination) {
			this.source = source;
			this.destination = destination;
		}

		@Override
		public STATE getSource() {
			return source;
		}

		@Override
		public STATE getDestination() {
			return destination;
		}

		@Override
		public boolean process() {
			return true;
		}
	}
}
