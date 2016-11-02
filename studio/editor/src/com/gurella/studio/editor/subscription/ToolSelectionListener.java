package com.gurella.studio.editor.subscription;

import com.gurella.engine.event.EventSubscription;
import com.gurella.studio.editor.tool.ToolType;

public interface ToolSelectionListener extends EventSubscription {
	void toolSelected(ToolType selectedTool);
}
