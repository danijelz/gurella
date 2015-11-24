package com.gurella.engine.graphics.vector.sfnt.truetype.os2;

import com.badlogic.gdx.utils.IntMap;

public enum SlabSerifsFamilySubclass implements FamilySubclass {
	NoClassification(0), Monotone(1), Humanist(2), Geometric(3), Swiss(4), Typewriter(5), Miscellaneous(15);

	private static IntMap<SlabSerifsFamilySubclass> valuesMap;

	private final int value;

	private SlabSerifsFamilySubclass(int value) {
		this.value = value;
		getValuesMap().put(value, this);
	}

	private static IntMap<SlabSerifsFamilySubclass> getValuesMap() {
		if (valuesMap == null) {
			valuesMap = new IntMap<SlabSerifsFamilySubclass>();
		}
		return valuesMap;
	}

	public int value() {
		return this.value;
	}

	public boolean equals(int value) {
		return value == this.value;
	}

	public static SlabSerifsFamilySubclass valueOf(int value) {
		return valuesMap.get(value);
	}
}