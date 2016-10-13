package com.gurella.studio.editor.subscription;

import com.gurella.engine.event.EventSubscription;
import com.gurella.engine.scene.SceneNode2;

public interface NodeEnabledChangedListener extends EventSubscription {
	void nodeEnabledChanged(SceneNode2 node);
}
