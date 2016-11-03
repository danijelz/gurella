package com.gurella.engine.plugin;

public interface LifecyclePlugin extends Plugin {
	void activate();

	void deactivate();
}
