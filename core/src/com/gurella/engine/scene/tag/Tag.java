package com.gurella.engine.scene.tag;

import com.gurella.engine.utils.ValueRegistry;

public final class Tag {
	private static final ValueRegistry<Tag> registry = new ValueRegistry<Tag>();

	final int id;
	public final String name;

	public Tag(String name) {
		id = registry.getId(this);
		this.name = name;
	}
}
