package com.gurella.engine.graphics.vector.sfnt.opentype.lookup.substitution.alternate;

import com.gurella.engine.graphics.vector.sfnt.opentype.OpenTypeLookupTable;
import com.gurella.engine.graphics.vector.sfnt.opentype.lookup.LookupSubTable;

public class AlternateSubstitutionSubtable extends LookupSubTable {
	private AlternateSubstitutionFormatSubtable formatSubtable;
	
	public AlternateSubstitutionSubtable(OpenTypeLookupTable parentTable, int offset) {
		super(parentTable, offset);
		formatSubtable = createPositioningFormatSubtable();
	}

	private AlternateSubstitutionFormatSubtable createPositioningFormatSubtable() {
		int posFormat = readUnsignedShort(0);
		switch (posFormat) {
		case 1:
			return new AlternateSubstitutionFormat1Subtable(this, offset);
		default:
			return new AlternateSubstitutionFormatSubtable(this, offset);
		}
	}

	public int getPosFormat() {
		return formatSubtable.getSubstFormat();
	}
	
	public boolean isGlyphCovered(int glyphId) {
		return formatSubtable.isGlyphCovered(glyphId);
	}
	
	public int[] getSubstitutes(int glyphId) {
		return formatSubtable.getSubstitutes(glyphId);
	}
}
