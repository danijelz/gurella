package com.gurella.engine.graphics.vector.sfnt.truetype.os2;

import com.badlogic.gdx.utils.IntMap;

public enum FreeformSerifsFamilySubclass implements FamilySubclass {
	NoClassification(0), Modern(1), Miscellaneous(15);

	private static IntMap<FreeformSerifsFamilySubclass> valuesMap;

	private final int value;

	private FreeformSerifsFamilySubclass(int value) {
		this.value = value;
		getValuesMap().put(value, this);
	}

	private static IntMap<FreeformSerifsFamilySubclass> getValuesMap() {
		if (valuesMap == null) {
			valuesMap = new IntMap<FreeformSerifsFamilySubclass>();
		}
		return valuesMap;
	}

	public int value() {
		return this.value;
	}

	public boolean equals(int value) {
		return value == this.value;
	}

	public static FreeformSerifsFamilySubclass valueOf(int value) {
		return valuesMap.get(value);
	}
}