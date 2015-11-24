package com.gurella.engine.graphics.vector.sfnt.truetype.os2;

import com.badlogic.gdx.utils.IntMap;

public enum WidthClass {
	UltraCondensed(1),
	ExtraCondensed(2),
	Condensed(3),
	SemiCondensed(4),
	Medium_Normal(5),
	SemiExpanded(6),
	Expanded(7),
	ExtraExpanded(8),
	UltraExpanded(9);

	private static IntMap<WidthClass> valuesMap;

	private final int value;

	private WidthClass(int value) {
		this.value = value;
		getValuesMap().put(value, this);
	}

	private static IntMap<WidthClass> getValuesMap() {
		if (valuesMap == null) {
			valuesMap = new IntMap<WidthClass>();
		}
		return valuesMap;
	}

	public int value() {
		return this.value;
	}

	public boolean equals(int value) {
		return value == this.value;
	}

	public static WidthClass valueOf(int value) {
		return valuesMap.get(value);
	}
}