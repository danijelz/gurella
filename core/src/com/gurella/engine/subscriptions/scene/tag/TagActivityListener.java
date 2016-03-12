package com.gurella.engine.subscriptions.scene.tag;

import com.gurella.engine.scene.tag.TagComponent;
import com.gurella.engine.subscriptions.scene.SceneEventSubscription;

public interface TagActivityListener extends SceneEventSubscription {
	void tagAdded(TagComponent component, int tagId);

	void tagRemoved(TagComponent component, int tagId);
}
