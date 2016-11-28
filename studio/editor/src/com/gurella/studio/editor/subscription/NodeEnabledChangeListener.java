package com.gurella.studio.editor.subscription;

import com.gurella.engine.event.EventSubscription;
import com.gurella.engine.scene.SceneNode;

public interface NodeEnabledChangeListener extends EventSubscription {
	void nodeEnabledChanged(SceneNode node);
}
