package com.gurella.engine.graphics.vector.sfnt.truetype.os2;

import java.util.EnumSet;

public enum CodePageRange {
	Latin1_1252,
	Latin2_1250,
	Cyrillic_1251,
	Greek_1253,
	Turkish_1254,
	Hebrew_1255,
	Arabic_1256,
	WindowsBaltic_1257,
	Vietnamese_1258,
	AlternateANSI9,
	AlternateANSI10,
	AlternateANSI11,
	AlternateANSI12,
	AlternateANSI13,
	AlternateANSI14,
	AlternateANSI15,
	Thai_874,
	JapanJIS_932,
	ChineseSimplified_936,
	KoreanWansung_949,
	ChineseTraditional_950,
	KoreanJohab_1361,
	AlternateANSI22,
	AlternateANSI23,
	AlternateANSI24,
	AlternateANSI25,
	AlternateANSI26,
	AlternateANSI27,
	AlternateANSI28,
	MacintoshCharacterSet,
	OEMCharacterSet,
	SymbolCharacterSet,
	ReservedForOEM32,
	ReservedForOEM33,
	ReservedForOEM34,
	ReservedForOEM35,
	ReservedForOEM36,
	ReservedForOEM37,
	ReservedForOEM38,
	ReservedForOEM39,
	ReservedForOEM40,
	ReservedForOEM41,
	ReservedForOEM42,
	ReservedForOEM43,
	ReservedForOEM44,
	ReservedForOEM45,
	ReservedForOEM46,
	ReservedForOEM47,
	IBMGreek_869,
	MSDOSRussion_866,
	MSDOSNordic_865,
	Arabic_864,
	MSDOSCanadianFrench_863,
	Hebrew_862,
	MSDOSIcelandic_861,
	MSDOSPortugese_860,
	IBMTurkish_857,
	IBMCyrillic_855,
	Latin2_852,
	MSDOSBaltic_775,
	Greek_737,
	Arabic_708,
	Latin1_850,
	US_437;

	public static UnicodeRange range(int bit) {
		if (bit > UnicodeRange.values().length) {
			return null;
		}
		return UnicodeRange.values()[bit];
	}

	public static EnumSet<CodePageRange> asSet(long range1, long range2) {
		EnumSet<CodePageRange> set = EnumSet.noneOf(CodePageRange.class);
		long[] range = { range1, range2 };
		int rangeBit = 0;
		int rangeIndex = -1;
		for (CodePageRange cpr : CodePageRange.values()) {
			if (cpr.ordinal() % 32 == 0) {
				rangeBit = 0;
				rangeIndex++;
			} else {
				rangeBit++;
			}
			if ((range[rangeIndex] & 1 << rangeBit) == 1 << rangeBit) {
				set.add(cpr);
			}
		}
		return set;
	}

	public static long[] asArray(EnumSet<CodePageRange> rangeSet) {
		long[] range = new long[4];
		for (CodePageRange ur : rangeSet) {
			int urSegment = ur.ordinal() / 32;
			long urFlag = 1 << (ur.ordinal() % 32);
			range[urSegment] |= urFlag;
		}
		return range;
	}
}