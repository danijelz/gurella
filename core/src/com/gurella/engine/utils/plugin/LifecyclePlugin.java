package com.gurella.engine.utils.plugin;

public interface LifecyclePlugin extends Plugin {
	void activate();

	void deactivate();
}
