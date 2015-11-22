package com.gurella.engine.graph;

import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.ObjectIntMap;

public class SceneNodeFlags {
	private static ObjectIntMap<?> flagTypes = new ObjectIntMap<Object>();

	private static int FLAG_INDEX = 0;
	public static int ACTIVE_FLAG = nextFlag();

	private Bits flags = new Bits();

	private static int nextFlag() {
		return FLAG_INDEX++;
	}

	public static int getFlagFor(Object flagType) {
		return -1;//TODO  getFor(componentType).getIndex();
	}
}
