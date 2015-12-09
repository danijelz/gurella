package com.gurella.engine.event2;

import com.gurella.engine.application.Application;

public abstract class EventTrigger {
	Application application;
	
	public Application getApplication() {
		return application;
	}
	
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
