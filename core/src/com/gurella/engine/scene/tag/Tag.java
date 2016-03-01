package com.gurella.engine.scene.tag;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.utils.ValueRegistry;

public final class Tag {
	private static final ValueRegistry<Tag> INDEXER = new ValueRegistry<Tag>();
	private static ObjectMap<String, Tag> tagsByName = new ObjectMap<String, Tag>();

	public final int id;
	public final String name;

	public Tag(String name) {
		if (tagsByName.containsKey(name)) {
			throw new GdxRuntimeException("Layer name duplicate");
		}

		id = INDEXER.getId(this);
		this.name = name;

		tagsByName.put(name, this);
	}

	public static Tag getTag(int id) {
		return INDEXER.getValue(id);
	}

	public static Tag getTag(String name) {
		return tagsByName.get(name);
	}
}
