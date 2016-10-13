package com.gurella.studio.editor.subscription;

import com.gurella.engine.event.EventSubscription;

public interface SceneChangedListener extends EventSubscription {
	void sceneChanged();
}
