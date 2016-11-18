package com.gurella.studio.editor.subscription;

import com.gurella.engine.event.EventSubscription;
import com.gurella.engine.scene.SceneNodeComponent2;

public interface ComponentIndexListener extends EventSubscription {
	void componentIndexChanged(SceneNodeComponent2 component, int newIndex);
}
