package com.gurella.engine.scene.tag;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.utils.ValueRegistry;

public final class Tag implements Comparable<Tag> {
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
		Tag tag = tagsByName.get(name);
		if (tag == null) {
			tag = new Tag(name);
			tagsByName.put(name, tag);
		}
		return tag;
	}

	public static Array<Tag> values() {
		return tagsByName.values().toArray();
	}

	@Override
	public int compareTo(Tag other) {
		return name.compareTo(other.name);
	}
}
