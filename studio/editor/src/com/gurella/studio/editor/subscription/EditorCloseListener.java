package com.gurella.studio.editor.subscription;

import com.gurella.engine.event.EventSubscription;

public interface EditorCloseListener extends EventSubscription {
	void onEditorClose();
}
