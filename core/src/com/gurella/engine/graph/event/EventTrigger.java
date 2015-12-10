package com.gurella.engine.graph.event;

public abstract class EventTrigger {
	protected EventManager eventManager;

	protected abstract void activated();

	protected abstract void deactivated();
	
	static final class NopEventTrigger extends EventTrigger {
		static final NopEventTrigger instance = new NopEventTrigger();
		
		@Override
		protected void activated() {
		}

		@Override
		protected void deactivated() {
		}
	}
}
