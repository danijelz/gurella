package com.gurella.studio.editor.history;

import java.util.HashSet;
import java.util.Set;

import com.gurella.engine.plugin.Plugin;
import com.gurella.engine.plugin.PluginListener;

class HistoryContributorRegistry implements PluginListener {
	private final HistoryService historyService;
	private final Set<HistoryContributor> extensions = new HashSet<>();

	HistoryContributorRegistry(HistoryService historyService) {
		this.historyService = historyService;
	}

	@Override
	public void activated(Plugin plugin) {
		if (plugin instanceof HistoryContributor) {
			HistoryContributor exstension = (HistoryContributor) plugin;
			if (extensions.add(exstension)) {
				exstension.setHistoryService(historyService);
			}
		}
	}

	@Override
	public void deactivated(Plugin plugin) {
		if (plugin instanceof HistoryContributor) {
			HistoryContributor exstension = (HistoryContributor) plugin;
			if (extensions.remove(exstension)) {
				exstension.setHistoryService(null);
			}
		}
	}
}
