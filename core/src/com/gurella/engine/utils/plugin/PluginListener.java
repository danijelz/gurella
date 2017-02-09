package com.gurella.engine.utils.plugin;

public interface PluginListener {
	void activated(Plugin plugin);

	void deactivated(Plugin plugin);
}
