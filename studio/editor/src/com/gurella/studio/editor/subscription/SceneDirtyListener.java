package com.gurella.studio.editor.subscription;

import com.gurella.engine.event.EventSubscription;

public interface SceneDirtyListener extends EventSubscription {
	void sceneDirty();
}
