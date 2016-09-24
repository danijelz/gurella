package com.gurella.engine.subscriptions.scene.tag;

import com.gurella.engine.scene.tag.Tag;
import com.gurella.engine.scene.tag.TagComponent;
import com.gurella.engine.subscriptions.scene.SceneEventSubscription;

public interface TagActivityListener extends SceneEventSubscription {
	void tagAdded(TagComponent component, Tag tag);

	void tagRemoved(TagComponent component, Tag tag);
}
