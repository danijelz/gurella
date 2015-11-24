package com.gurella.engine.graphics.vector.sfnt.truetype.os2;

import java.util.EnumSet;

public enum EmbeddingFlags {
	Reserved0,
	RestrictedLicenseEmbedding,
	PreviewAndPrintEmbedding,
	EditableEmbedding,
	Reserved4,
	Reserved5,
	Reserved6,
	Reserved7,
	NoSubsetting,
	BitmapEmbeddingOnly,
	Reserved10,
	Reserved11,
	Reserved12,
	Reserved13,
	Reserved14,
	Reserved15;

	public int mask() {
		return 1 << this.ordinal();
	}

	public static EnumSet<EmbeddingFlags> asSet(int value) {
		EnumSet<EmbeddingFlags> set = EnumSet.noneOf(EmbeddingFlags.class);
		for (EmbeddingFlags flag : EmbeddingFlags.values()) {
			if ((value & flag.mask()) == flag.mask()) {
				set.add(flag);
			}
		}
		return set;
	}

	public static boolean isInstallableEditing(EnumSet<EmbeddingFlags> flagSet) {
		return flagSet.isEmpty();
	}

	public static boolean isInstallableEditing(int value) {
		return value == 0;
	}
}