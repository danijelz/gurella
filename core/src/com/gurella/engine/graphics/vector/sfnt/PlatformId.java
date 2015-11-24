package com.gurella.engine.graphics.vector.sfnt;

import com.badlogic.gdx.utils.IntMap;

public enum PlatformId {
	Unknown(-1), 
	Unicode(0), 
	Macintosh(1), 
	ISO(2), 
	Windows(3), 
	Custom(4);
	
	private static IntMap<PlatformId> valuesById;

	public final int value;

	private PlatformId(int value) {
		this.value = value;
		getValuesById().put(value, this);
	}
	
	private static IntMap<PlatformId> getValuesById() {
		if(valuesById== null) {
			valuesById = new IntMap<PlatformId>();
		}
		return valuesById;
	}

	public int value() {
		return this.value;
	}

	public boolean equals(int value) {
		return value == this.value;
	}

	public static PlatformId valueOf(int value) {
		return valuesById.get(value, Unknown);
	}
}