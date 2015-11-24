package com.gurella.engine.graphics.vector.sfnt.opentype.lookup.substitution.ligature;

import com.gurella.engine.graphics.vector.sfnt.opentype.OpenTypeLookupTable;
import com.gurella.engine.graphics.vector.sfnt.opentype.lookup.LookupSubTable;

public class LigatureSubstitutionSubtable extends LookupSubTable {
	private LigatureSubstitutionFormatSubtable formatSubtable;
	
	public LigatureSubstitutionSubtable(OpenTypeLookupTable parentTable, int offset) {
		super(parentTable, offset);
		formatSubtable = createPositioningFormatSubtable();
	}

	private LigatureSubstitutionFormatSubtable createPositioningFormatSubtable() {
		int posFormat = readUnsignedShort(0);
		switch (posFormat) {
		case 1:
			return new LigatureSubstitutionFormat1Subtable(this, offset);
		default:
			return new LigatureSubstitutionFormatSubtable(this, offset);
		}
	}

	public int getPosFormat() {
		return formatSubtable.getSubstFormat();
	}
	
	public boolean isGlyphCovered(int glyphId) {
		return formatSubtable.isGlyphCovered(glyphId);
	}
	
	public int getSubstitute(int startGlyphId, int... additionalGlyphIds) {
		return formatSubtable.getSubstitute(startGlyphId, additionalGlyphIds);
	}
}
