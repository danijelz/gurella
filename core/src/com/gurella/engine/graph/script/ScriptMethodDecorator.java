package com.gurella.engine.graph.script;

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
