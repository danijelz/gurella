package com.gurella.studio.editor.subscription;

import com.gurella.engine.event.EventSubscription;

public interface EditorClosingListener extends EventSubscription {
	void closing();
}
