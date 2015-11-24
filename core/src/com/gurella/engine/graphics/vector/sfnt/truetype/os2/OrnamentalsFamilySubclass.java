package com.gurella.engine.graphics.vector.sfnt.truetype.os2;

import com.badlogic.gdx.utils.IntMap;

public enum OrnamentalsFamilySubclass implements FamilySubclass {
	NoClassification(0), Engraver(1), BlackLetter(2), Decorative(3), ThreeDimensional(4), Miscellaneous(15);

	private static IntMap<OrnamentalsFamilySubclass> valuesMap;

	private final int value;

	private OrnamentalsFamilySubclass(int value) {
		this.value = value;
		getValuesMap().put(value, this);
	}

	private static IntMap<OrnamentalsFamilySubclass> getValuesMap() {
		if (valuesMap == null) {
			valuesMap = new IntMap<OrnamentalsFamilySubclass>();
		}
		return valuesMap;
	}

	public int value() {
		return this.value;
	}

	public boolean equals(int value) {
		return value == this.value;
	}

	public static OrnamentalsFamilySubclass valueOf(int value) {
		return valuesMap.get(value);
	}
}