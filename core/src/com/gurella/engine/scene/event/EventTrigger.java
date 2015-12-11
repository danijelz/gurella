package com.gurella.engine.scene.event;

import com.gurella.engine.scene.Scene;

public abstract class EventTrigger {
	protected Scene scene;
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
