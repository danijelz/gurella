package com.gurella.studio.editor.menu;

import com.gurella.engine.utils.plugin.Plugin;

public interface EditorContextMenuContributor extends Plugin {
	void contribute(float x, float y, ContextMenuActions actions);
}
