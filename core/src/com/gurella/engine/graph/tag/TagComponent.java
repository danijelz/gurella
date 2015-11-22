package com.gurella.engine.graph.tag;

import com.badlogic.gdx.utils.ObjectSet;
import com.gurella.engine.graph.SceneNodeComponent;

public class TagComponent extends SceneNodeComponent {
	public final ObjectSet<Tag> tags = new ObjectSet<Tag>();

	public TagComponent(Tag... tags) {
		this.tags.addAll(tags);
	}
}
