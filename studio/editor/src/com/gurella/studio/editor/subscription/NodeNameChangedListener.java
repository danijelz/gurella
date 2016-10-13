package com.gurella.studio.editor.subscription;

import com.gurella.engine.event.EventSubscription;
import com.gurella.engine.scene.SceneNode2;

public interface NodeNameChangedListener extends EventSubscription {
	void nodeNameChanged(SceneNode2 node);
}
