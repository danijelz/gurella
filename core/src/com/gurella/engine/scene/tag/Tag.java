package com.gurella.engine.scene.tag;

import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.utils.ValueRegistry;

public final class Tag {
	private static final ValueRegistry<Tag> registry = new ValueRegistry<Tag>();
	private static ObjectMap<String, Tag> tagsByName = new ObjectMap<String, Tag>();

	final int id;
	public final String name;

	Tag(String name) {
		id = registry.getId(this);
		this.name = name;
	}

	public static Tag get(int id) {
		return registry.getValue(id);
	}

	public static Tag get(String name) {
		return tagsByName.get(name);
	}
}
