package com.gurella.engine.graphics.vector.sfnt.truetype.os2;

import com.badlogic.gdx.utils.IntMap;

public enum ModernSerifsFamilySubclass implements FamilySubclass {
	NoClassification(0), Italian(1), Script(2), Miscellaneous(15);

	private static IntMap<ModernSerifsFamilySubclass> valuesMap;

	private final int value;

	private ModernSerifsFamilySubclass(int value) {
		this.value = value;
		getValuesMap().put(value, this);
	}

	private static IntMap<ModernSerifsFamilySubclass> getValuesMap() {
		if (valuesMap == null) {
			valuesMap = new IntMap<ModernSerifsFamilySubclass>();
		}
		return valuesMap;
	}

	public int value() {
		return this.value;
	}

	public boolean equals(int value) {
		return value == this.value;
	}

	public static ModernSerifsFamilySubclass valueOf(int value) {
		return valuesMap.get(value);
	}
}