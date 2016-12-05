package com.gurella.engine.plugin;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.utils.IdentitySet;

public class Workbench {
	private static final ObjectMap<Application, Workbench> instances = new ObjectMap<Application, Workbench>();

	private final IdentitySet<Plugin> plugins = new IdentitySet<Plugin>();
	private final IdentitySet<PluginListener> listeners = new IdentitySet<PluginListener>();

	private Workbench() {
	}

	private static Workbench getInstance() {
		synchronized (instances) {
			Workbench input = instances.get(Gdx.app);
			if (input == null) {
				input = new Workbench();
				instances.put(Gdx.app, input);
			}
			return input;
		}
	}

	public static boolean activate(Plugin plugin) {
		return getInstance()._activate(plugin);
	}

	public static boolean deactivate(Plugin plugin) {
		return getInstance()._deactivate(plugin);
	}

	public static boolean addListener(PluginListener listener) {
		return getInstance()._addListener(listener);
	}

	public static boolean removeListener(PluginListener listener) {
		return getInstance()._removeListener(listener);
	}

	private boolean _activate(Plugin plugin) {
		if (!plugins.add(plugin)) {
			return false;
		}

		if (plugin instanceof LifecyclePlugin) {
			((LifecyclePlugin) plugin).activate();
		}

		for (PluginListener listener : listeners) {
			listener.activated(plugin);
		}

		if (plugin instanceof PluginListener && listeners.add((PluginListener) plugin)) {
			PluginListener listener = (PluginListener) plugin;
			for (Plugin active : plugins) {
				if (plugin != active) {
					listener.activated(active);
				}
			}
		}

		return true;
	}

	private boolean _deactivate(Plugin plugin) {
		if (!plugins.remove(plugin)) {
			return false;
		}

		if (plugin instanceof PluginListener && listeners.remove((PluginListener) plugin)) {
			PluginListener listener = (PluginListener) plugin;
			for (Plugin active : plugins) {
				listener.deactivated(active);
			}
		}

		for (PluginListener listener : listeners) {
			listener.deactivated(plugin);
		}

		if (plugin instanceof LifecyclePlugin) {
			((LifecyclePlugin) plugin).deactivate();
		}

		return true;
	}

	private boolean _addListener(PluginListener listener) {
		if (!listeners.add(listener)) {
			return false;
		}

		for (Plugin plugin : plugins) {
			listener.activated(plugin);
		}

		return true;
	}

	private boolean _removeListener(PluginListener listener) {
		if (!listeners.remove(listener)) {
			return false;
		}

		for (Plugin plugin : plugins) {
			listener.deactivated(plugin);
		}

		return true;
	}
}
