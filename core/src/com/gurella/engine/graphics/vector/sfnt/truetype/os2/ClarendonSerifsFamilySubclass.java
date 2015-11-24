package com.gurella.engine.graphics.vector.sfnt.truetype.os2;

import com.badlogic.gdx.utils.IntMap;

public enum ClarendonSerifsFamilySubclass implements FamilySubclass {
	NoClassification(0), Clarendon(1), Modern(2), Traditional(3), Newspaper(4), StubSerif(5), Monotone(6), Typewriter(7), Miscellaneous(15);

	private static IntMap<ClarendonSerifsFamilySubclass> valuesMap;

	private final int value;

	private ClarendonSerifsFamilySubclass(int value) {
		this.value = value;
		getValuesMap().put(value, this);
	}

	private static IntMap<ClarendonSerifsFamilySubclass> getValuesMap() {
		if (valuesMap == null) {
			valuesMap = new IntMap<ClarendonSerifsFamilySubclass>();
		}
		return valuesMap;
	}

	public int value() {
		return this.value;
	}

	public boolean equals(int value) {
		return value == this.value;
	}

	public static ClarendonSerifsFamilySubclass valueOf(int value) {
		return valuesMap.get(value);
	}
}