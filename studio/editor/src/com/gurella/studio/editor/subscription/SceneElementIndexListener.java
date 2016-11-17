package com.gurella.studio.editor.subscription;

import com.gurella.engine.event.EventSubscription;
import com.gurella.engine.scene.SceneElement2;

public interface SceneElementIndexListener extends EventSubscription {
	void indexChanged(SceneElement2 element, int newIndex);
}
