package com.gurella.engine.scene.tag;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.utils.IndexedValue;

public final class Tag {
	private static final IndexedValue<Tag> INDEXER = new IndexedValue<Tag>();
	private static ObjectMap<String, Tag> tagsByName = new ObjectMap<String, Tag>();

	public final int id;
	public final String name;

	public Tag(String name) {
		if (tagsByName.containsKey(name)) {
			throw new GdxRuntimeException("Layer name duplicate");
		}

		id = INDEXER.getIndex(this);
		this.name = name;

		tagsByName.put(name, this);
	}

	public static Tag getTag(int id) {
		return INDEXER.getValueByIndex(id);
	}

	public static Tag getTag(String name) {
		return tagsByName.get(name);
	}
}
