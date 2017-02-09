package com.gurella.studio.editor.history;

import com.gurella.engine.utils.plugin.Plugin;

public interface HistoryContributor extends Plugin {
	void setHistoryService(HistoryService historyService);
}
