package com.gurella.engine.graphics.vector.sfnt.truetype.os2;

import java.util.EnumSet;

public enum FsSelection {
	ITALIC, UNDERSCORE, NEGATIVE, OUTLINED, STRIKEOUT, BOLD, REGULAR, USE_TYPO_METRICS, WWS, OBLIQUE;

	public int mask() {
		return 1 << this.ordinal();
	}

	public static EnumSet<FsSelection> asSet(int value) {
		EnumSet<FsSelection> set = EnumSet.noneOf(FsSelection.class);
		for (FsSelection selection : FsSelection.values()) {
			if ((value & selection.mask()) == selection.mask()) {
				set.add(selection);
			}
		}
		return set;
	}
}