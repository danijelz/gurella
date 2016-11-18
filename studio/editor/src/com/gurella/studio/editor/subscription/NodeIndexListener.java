package com.gurella.studio.editor.subscription;

import com.gurella.engine.event.EventSubscription;
import com.gurella.engine.scene.SceneNode2;

public interface NodeIndexListener extends EventSubscription {
	void nodeIndexChanged(SceneNode2 node, int newIndex);
}
