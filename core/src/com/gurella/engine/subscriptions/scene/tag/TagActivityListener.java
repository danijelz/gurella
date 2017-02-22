package com.gurella.engine.subscriptions.scene.tag;

import com.gurella.engine.scene.tag.Tag;
import com.gurella.engine.scene.tag.TagComponent;
import com.gurella.engine.subscriptions.scene.SceneEventSubscription;

public interface TagActivityListener extends SceneEventSubscription {
	void onTagAdded(TagComponent component, Tag tag);

	void onTagRemoved(TagComponent component, Tag tag);
}
