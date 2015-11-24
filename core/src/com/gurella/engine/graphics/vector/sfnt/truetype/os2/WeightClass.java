package com.gurella.engine.graphics.vector.sfnt.truetype.os2;

import com.badlogic.gdx.utils.IntMap;

public enum WeightClass {
	Thin(100),
	ExtraLight_UltraLight(200),
	Light(300),
	Normal_Regular(400),
	Medium(500),
	SemiBold_DemiBold(600),
	Bold(700),
	ExtraBold_UltraBold(800),
	Black_Heavy(900);

	private static IntMap<WeightClass> tableTypesByValue;

	private final int value;

	private WeightClass(int value) {
		this.value = value;
		getTableTypesByValue().put(value, this);
	}

	private static IntMap<WeightClass> getTableTypesByValue() {
		if (tableTypesByValue == null) {
			tableTypesByValue = new IntMap<WeightClass>();
		}
		return tableTypesByValue;
	}

	public int value() {
		return this.value;
	}

	public boolean equals(int value) {
		return value == this.value;
	}

	public static WeightClass valueOf(int value) {
		return tableTypesByValue.get(value);
	}
}