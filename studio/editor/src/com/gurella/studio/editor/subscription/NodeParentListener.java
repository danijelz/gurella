package com.gurella.studio.editor.subscription;

import com.gurella.engine.event.EventSubscription;
import com.gurella.engine.scene.SceneNode2;

public interface NodeParentListener extends EventSubscription {
	void nodeParentChanged(SceneNode2 node, SceneNode2 newParent);
}
