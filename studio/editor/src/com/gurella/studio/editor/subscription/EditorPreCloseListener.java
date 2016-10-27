package com.gurella.studio.editor.subscription;

import com.gurella.engine.event.EventSubscription;

public interface EditorPreCloseListener extends EventSubscription {
	void onEditorPreClose();
}
