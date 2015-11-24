package com.gurella.engine.graphics.vector.sfnt.truetype.os2;

import com.badlogic.gdx.utils.IntMap;

public enum TransitionalSerifsFamilySubclass implements FamilySubclass {
	NoClassification(0), DirectLine(1), Script(2), Miscellaneous(15);

	private static IntMap<TransitionalSerifsFamilySubclass> valuesMap;

	private final int value;

	private TransitionalSerifsFamilySubclass(int value) {
		this.value = value;
		getValuesMap().put(value, this);
	}

	private static IntMap<TransitionalSerifsFamilySubclass> getValuesMap() {
		if (valuesMap == null) {
			valuesMap = new IntMap<TransitionalSerifsFamilySubclass>();
		}
		return valuesMap;
	}

	public int value() {
		return this.value;
	}

	public boolean equals(int value) {
		return value == this.value;
	}

	public static TransitionalSerifsFamilySubclass valueOf(int value) {
		return valuesMap.get(value);
	}
}