package com.gurella.engine.plugin;

import com.badlogic.gdx.utils.IntMap;
import com.gurella.engine.utils.OrderedIdentitySet;

public class Workbench {
	private static final IntMap<Workbench> instances = new IntMap<Workbench>();

	private final OrderedIdentitySet<Plugin> plugins = new OrderedIdentitySet<Plugin>();
	private final OrderedIdentitySet<PluginListener> listeners = new OrderedIdentitySet<PluginListener>();

	private Workbench() {
	}

	public static Workbench newInstance(int channel) {
		if (channel < 0) {
			throw new IllegalArgumentException("channel must be positive int");
		}

		synchronized (instances) {
			Workbench instance = instances.get(channel);
			if (instance != null) {
				throw new IllegalStateException("Workbench for provided channel already exists.");
			}

			instance = new Workbench();
			instances.put(channel, instance);
			return instance;
		}
	}

	public static void close(Workbench workbench) {
		synchronized (instances) {
			int channel = instances.findKey(workbench, true, -1);
			if (channel < 0) {
				throw new IllegalStateException("Workbench for provided channel doesn't exists.");
			}

			instances.remove(channel);
		}
	}

	private static Workbench getInstance(int channel) {
		synchronized (instances) {
			Workbench instance = instances.get(channel);
			if (instance == null) {
				throw new IllegalStateException("Workbench for provided channel doesn't exists.");
			}
			return instance;
		}
	}

	public static boolean activate(int channel, Plugin plugin) {
		return getInstance(channel)._activate(plugin);
	}

	public static boolean deactivate(int channel, Plugin plugin) {
		return getInstance(channel)._deactivate(plugin);
	}

	public static boolean addListener(int channel, PluginListener listener) {
		return getInstance(channel)._addListener(listener);
	}

	public static boolean removeListener(int channel, PluginListener listener) {
		return getInstance(channel)._removeListener(listener);
	}

	private boolean _activate(Plugin plugin) {
		if (!plugins.add(plugin)) {
			return false;
		}

		if (plugin instanceof LifecyclePlugin) {
			((LifecyclePlugin) plugin).activate();
		}

		for (int i = 0; i < listeners.size; i++) {
			listeners.get(i).activated(plugin);
		}

		if (plugin instanceof PluginListener && listeners.add((PluginListener) plugin)) {
			PluginListener listener = (PluginListener) plugin;
			for (int i = 0; i < plugins.size; i++) {
				Plugin active = plugins.get(i);
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
			for (int i = 0; i < plugins.size; i++) {
				Plugin active = plugins.get(i);
				if (plugin != active) {
					listener.deactivated(active);
				}
			}
		}

		for (int i = 0; i < listeners.size; i++) {
			listeners.get(i).deactivated(plugin);
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

		for (int i = 0; i < plugins.size; i++) {
			listener.activated(plugins.get(i));
		}

		return true;
	}

	private boolean _removeListener(PluginListener listener) {
		if (!listeners.remove(listener)) {
			return false;
		}

		for (int i = 0; i < plugins.size; i++) {
			listener.deactivated(plugins.get(i));
		}

		return true;
	}
}
