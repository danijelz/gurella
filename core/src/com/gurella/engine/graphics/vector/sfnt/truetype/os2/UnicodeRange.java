package com.gurella.engine.graphics.vector.sfnt.truetype.os2;

import java.util.EnumSet;

public enum UnicodeRange {
	// Do NOT reorder. This enumeration relies on the ordering of the data matching the
	// ordinal numbers of the properties
	BasicLatin,
	Latin1Supplement,
	LatinExtendedA,
	LatinExtendedB,
	IPAExtensions,
	SpacingModifierLetters,
	CombiningDiacriticalMarks,
	GreekAndCoptic,
	Coptic,
	Cyrillic,
	Armenian,
	Hebrew,
	Vai,
	Arabic,
	NKo,
	Devanagari,
	Bengali,
	Gurmukhi,
	Gujarati,
	Oriya,
	Tamil,
	Telugu,
	Kannada,
	Malayalam,
	Thai,
	Lao,
	Georgian,
	Balinese,
	HangulJamo,
	LatinExtendedAdditional,
	GreekExtended,
	GeneralPunctuation,
	SuperscriptsAndSubscripts,
	CurrencySymbols,
	CombiningDiacriticalMarksForSymbols,
	LetterlikeSymbols,
	NumberForms,
	Arrows,
	MathematicalOperators,
	MiscTechnical,
	ControlPictures,
	OpticalCharacterRecognition,
	EnclosedAlphanumerics,
	BoxDrawing,
	BlockElements,
	GeometricShapes,
	MiscSymbols,
	Dingbats,
	CJKSymbolsAndPunctuation,
	Hiragana,
	Katakana,
	Bopomofo,
	HangulCompatibilityJamo,
	Phagspa,
	EnclosedCJKLettersAndMonths,
	CJKCompatibility,
	HangulSyllables,
	NonPlane0,
	Phoenician,
	CJKUnifiedIdeographs,
	PrivateUseAreaPlane0,
	CJKStrokes,
	AlphabeticPresentationForms,
	ArabicPresentationFormsA,
	CombiningHalfMarks,
	VerticalForms,
	SmallFormVariants,
	ArabicPresentationFormsB,
	HalfwidthAndFullwidthForms,
	Specials,
	Tibetan,
	Syriac,
	Thaana,
	Sinhala,
	Myanmar,
	Ethiopic,
	Cherokee,
	UnifiedCanadianAboriginalSyllabics,
	Ogham,
	Runic,
	Khmer,
	Mongolian,
	BraillePatterns,
	YiSyllables,
	Tagalog,
	OldItalic,
	Gothic,
	Deseret,
	MusicalSymbols,
	MathematicalAlphanumericSymbols,
	PrivateUsePlane15And16,
	VariationSelectors,
	Tags,
	Limbu,
	TaiLe,
	NewTaiLue,
	Buginese,
	Glagolitic,
	Tifnagh,
	YijingHexagramSymbols,
	SylotiNagari,
	LinearB,
	AncientGreekNumbers,
	Ugaritic,
	OldPersian,
	Shavian,
	Osmanya,
	CypriotSyllabary,
	Kharoshthi,
	TaiXuanJingSymbols,
	Cuneiform,
	CountingRodNumerals,
	Sudanese,
	Lepcha,
	OlChiki,
	Saurashtra,
	KayahLi,
	Rejang,
	Charm,
	AncientSymbols,
	PhaistosDisc,
	Carian,
	DominoTiles,
	MahjongTiles,
	Reserved123,
	Reserved124,
	Reserved125,
	Reserved126,
	Reserved127;

	public static UnicodeRange range(int bit) {
		if (bit > UnicodeRange.values().length) {
			return null;
		}
		return UnicodeRange.values()[bit];
	}

	public static EnumSet<UnicodeRange> asSet(long range1, long range2, long range3, long range4) {
		EnumSet<UnicodeRange> set = EnumSet.noneOf(UnicodeRange.class);
		long[] range = { range1, range2, range3, range4 };
		int rangeBit = 0;
		int rangeIndex = -1;
		for (UnicodeRange ur : UnicodeRange.values()) {
			if (ur.ordinal() % 32 == 0) {
				rangeBit = 0;
				rangeIndex++;
			} else {
				rangeBit++;
			}
			if ((range[rangeIndex] & 1 << rangeBit) == 1 << rangeBit) {
				set.add(ur);
			}
		}
		return set;
	}
}