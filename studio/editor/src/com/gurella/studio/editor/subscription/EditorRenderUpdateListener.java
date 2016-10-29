package com.gurella.studio.editor.subscription;

import com.gurella.engine.event.EventSubscription;

public interface EditorRenderUpdateListener extends EventSubscription {
	void onRenderUpdate();
}
