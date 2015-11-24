package com.gurella.engine.graphics.vector.sfnt.truetype.os2;

import com.badlogic.gdx.utils.IntMap;

public enum ScriptsFamilySubclass implements FamilySubclass {
	NoClassification(0),
	Uncial(1),
	BrushJoined(2),
	FormalJoined(3),
	MonotoneJoined(4),
	Calligraphic(5),
	BrushUnjoined(6),
	FormalUnjoined(7),
	MonotoneUnjoined(8),
	Miscellaneous(15);

	private static IntMap<ScriptsFamilySubclass> valuesMap;

	private final int value;

	private ScriptsFamilySubclass(int value) {
		this.value = value;
		getValuesMap().put(value, this);
	}

	private static IntMap<ScriptsFamilySubclass> getValuesMap() {
		if (valuesMap == null) {
			valuesMap = new IntMap<ScriptsFamilySubclass>();
		}
		return valuesMap;
	}

	public int value() {
		return this.value;
	}

	public boolean equals(int value) {
		return value == this.value;
	}

	public static ScriptsFamilySubclass valueOf(int value) {
		return valuesMap.get(value);
	}
}