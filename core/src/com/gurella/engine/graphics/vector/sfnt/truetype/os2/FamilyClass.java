package com.gurella.engine.graphics.vector.sfnt.truetype.os2;

public enum FamilyClass {
	NoClassification,
	OldStyleSerifs,
	TransitionalSerifs,
	ModernSerifs,
	ClarendonSerifs,
	SlabSerifs,
	Reserved1,
	FreeformSerifs,
	SansSerifs,
	Ornamentals,
	Scripts,
	Reserved2,
	Symbolic,
	Reserved3,
	Reserved4;
	
	static FamilyClass valueOf(int value) {
		FamilyClass[] values = values();
		return value < values.length ? values[value] : null;
	}
}