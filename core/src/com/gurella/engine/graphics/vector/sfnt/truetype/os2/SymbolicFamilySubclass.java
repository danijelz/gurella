package com.gurella.engine.graphics.vector.sfnt.truetype.os2;

import com.badlogic.gdx.utils.IntMap;

public enum SymbolicFamilySubclass implements FamilySubclass {
	NoClassification(0), MixedSerif(3), OldstyleSerif(6), NeoGrotesqueSansSerif(7), Miscellaneous(15);

	private static IntMap<SymbolicFamilySubclass> valuesMap;

	private final int value;

	private SymbolicFamilySubclass(int value) {
		this.value = value;
		getValuesMap().put(value, this);
	}

	private static IntMap<SymbolicFamilySubclass> getValuesMap() {
		if (valuesMap == null) {
			valuesMap = new IntMap<SymbolicFamilySubclass>();
		}
		return valuesMap;
	}

	public int value() {
		return this.value;
	}

	public boolean equals(int value) {
		return value == this.value;
	}

	public static SymbolicFamilySubclass valueOf(int value) {
		return valuesMap.get(value);
	}
}