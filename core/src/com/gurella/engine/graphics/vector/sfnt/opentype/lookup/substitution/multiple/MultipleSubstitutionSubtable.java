package com.gurella.engine.graphics.vector.sfnt.opentype.lookup.substitution.multiple;

import com.gurella.engine.graphics.vector.sfnt.opentype.OpenTypeLookupTable;
import com.gurella.engine.graphics.vector.sfnt.opentype.lookup.LookupSubTable;

public class MultipleSubstitutionSubtable extends LookupSubTable {
	private MultipleSubstitutionFormatSubtable formatSubtable;
	
	public MultipleSubstitutionSubtable(OpenTypeLookupTable parentTable, int offset) {
		super(parentTable, offset);
		formatSubtable = createPositioningFormatSubtable();
	}

	private MultipleSubstitutionFormatSubtable createPositioningFormatSubtable() {
		int posFormat = readUnsignedShort(0);
		switch (posFormat) {
		case 1:
			return new MultipleSubstitutionFormat1Subtable(this, offset);
		default:
			return new MultipleSubstitutionFormatSubtable(this, offset);
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
