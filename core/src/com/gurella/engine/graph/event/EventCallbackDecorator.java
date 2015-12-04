package com.gurella.engine.graph.event;

import com.gurella.engine.graph.SceneNodeComponent;

public interface EventCallbackDecorator {
	void componentActivated(SceneNodeComponent component);

	void componentDeactivated(SceneNodeComponent component);

	public static class NopEventCallbackDecorator implements EventCallbackDecorator {
		public static final NopEventCallbackDecorator instance = new NopEventCallbackDecorator();

		public NopEventCallbackDecorator() {
		}

		@Override
		public void componentActivated(SceneNodeComponent component) {
		}

		@Override
		public void componentDeactivated(SceneNodeComponent component) {
		}
	}
}
