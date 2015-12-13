package com.gurella.engine.scene.event;

import com.gurella.engine.scene.Scene;

public abstract class EventTrigger {
	protected Scene scene;
	protected EventManager eventManager;

	protected abstract void start();

	protected abstract void stop();

	static final class NopEventTrigger extends EventTrigger {
		static final NopEventTrigger instance = new NopEventTrigger();

		@Override
		protected void start() {
		}

		@Override
		protected void stop() {
		}
	}
}
