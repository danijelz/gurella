package com.gurella.engine.utils;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntMap.Values;

public class ImmutableIntMapValues<V> extends Values<V>{
	public ImmutableIntMapValues(IntMap<V> map) {
		super(map);
	}

	@Override
	public void remove() {
		throw new GdxRuntimeException("Remove not allowed.");
	}
}
