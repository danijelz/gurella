package com.gurella.studio.editor.subscription;

import com.gurella.engine.event.EventSubscription;

public interface EditorPreRenderUpdateListener extends EventSubscription {
	void onPreRenderUpdate();
}
