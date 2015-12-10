package com.gurella.engine.graph.event;

import com.gurella.engine.graph.SceneGraph;

public abstract class EventTrigger {
	protected SceneGraph sceneGraph;
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
