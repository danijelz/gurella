package com.gurella.studio.editor.subscription;

import com.gurella.engine.event.EventSubscription;

public interface EditorSelectionListener extends EventSubscription {
	void selectionChanged(Object selection);
}
