package com.gurella.engine.utils;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntMap.Values;

public class ImmutableIntMapValues<V> extends Values<V> {
	public static <V> ImmutableIntMapValues<V> with(IntMap<V> map) {
		return new ImmutableIntMapValues<V>(map);
	}

	public ImmutableIntMapValues(IntMap<V> map) {
		super(map);
	}

	@Override
	public void remove() {
		throw new GdxRuntimeException("Remove not allowed.");
	}
}
