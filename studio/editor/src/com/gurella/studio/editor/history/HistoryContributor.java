package com.gurella.studio.editor.history;

import com.gurella.engine.plugin.Plugin;

public interface HistoryContributor extends Plugin {
	void setHistoryService(HistoryService historyService);
}
