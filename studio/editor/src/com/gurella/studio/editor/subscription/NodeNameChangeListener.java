package com.gurella.studio.editor.subscription;

import com.gurella.engine.event.EventSubscription;
import com.gurella.engine.scene.SceneNode;

public interface NodeNameChangeListener extends EventSubscription {
	void nodeNameChanged(SceneNode node);
}
