package com.gurella.engine.plugin;

public interface PluginListener {
	void activated(Plugin plugin);

	void deactivated(Plugin plugin);
}
