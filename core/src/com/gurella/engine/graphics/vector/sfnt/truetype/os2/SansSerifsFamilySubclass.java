package com.gurella.engine.graphics.vector.sfnt.truetype.os2;

import com.badlogic.gdx.utils.IntMap;

public enum SansSerifsFamilySubclass implements FamilySubclass {
	NoClassification(0),
	IBMNeoGrotesqueGothic(1),
	Humanist0(2),
	LowXRoundGeometric(3),
	HighXRoundGeometric(4),
	NeoGrotesqueGothic(5),
	ModifiedNeoGrotesqueGothic(6),
	TypewriterGothic(9),
	Matrix(10),
	Miscellaneous(15);

	private static IntMap<SansSerifsFamilySubclass> valuesMap;

	private final int value;

	private SansSerifsFamilySubclass(int value) {
		this.value = value;
		getValuesMap().put(value, this);
	}

	private static IntMap<SansSerifsFamilySubclass> getValuesMap() {
		if (valuesMap == null) {
			valuesMap = new IntMap<SansSerifsFamilySubclass>();
		}
		return valuesMap;
	}

	public int value() {
		return this.value;
	}

	public boolean equals(int value) {
		return value == this.value;
	}

	public static SansSerifsFamilySubclass valueOf(int value) {
		return valuesMap.get(value);
	}
}