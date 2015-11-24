package com.gurella.engine.graphics.vector.sfnt.truetype.os2;

import com.badlogic.gdx.utils.IntMap;

public enum OldStyleSerifsFamilySubclass implements FamilySubclass {
	NoClassification(0),
	IBMRoundedLegibility(1),
	Garalde(2),
	Venetian(3),
	ModifiedVenetian(4),
	DutchModern(5),
	DutchTraditional(6),
	Contemporary(7),
	Calligraphic(8),
	Miscellaneous(15);

	private static IntMap<OldStyleSerifsFamilySubclass> valuesMap;

	private final int value;

	private OldStyleSerifsFamilySubclass(int value) {
		this.value = value;
		getValuesMap().put(value, this);
	}

	private static IntMap<OldStyleSerifsFamilySubclass> getValuesMap() {
		if (valuesMap == null) {
			valuesMap = new IntMap<OldStyleSerifsFamilySubclass>();
		}
		return valuesMap;
	}

	public int value() {
		return this.value;
	}

	public boolean equals(int value) {
		return value == this.value;
	}

	public static OldStyleSerifsFamilySubclass valueOf(int value) {
		return valuesMap.get(value);
	}
}