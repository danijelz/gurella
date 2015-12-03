package com.gurella.engine.graph.event;

import com.gurella.engine.graph.behaviour.ScriptComponent;

public interface ScriptMethodDecorator {
	void componentActivated(ScriptComponent component);

	void componentDeactivated(ScriptComponent component);

	public static class NopScriptMethodDecorator implements ScriptMethodDecorator {
		public static final NopScriptMethodDecorator instance = new NopScriptMethodDecorator();

		public NopScriptMethodDecorator() {
		}

		@Override
		public void componentActivated(ScriptComponent component) {
		}

		@Override
		public void componentDeactivated(ScriptComponent component) {
		}
	}
}
