package com.gurella.engine.graphics.vector.sfnt.opentype.lookup.substitution.single;

import com.gurella.engine.graphics.vector.sfnt.opentype.OpenTypeLookupTable;
import com.gurella.engine.graphics.vector.sfnt.opentype.lookup.LookupSubTable;

public class SingleSubstitutionSubtable extends LookupSubTable {
	private SingleSubstitutionFormatSubtable formatSubtable;
	
	public SingleSubstitutionSubtable(OpenTypeLookupTable parentTable, int offset) {
		super(parentTable, offset);
		formatSubtable = createPositioningFormatSubtable();
	}

	private SingleSubstitutionFormatSubtable createPositioningFormatSubtable() {
		int posFormat = readUnsignedShort(0);
		switch (posFormat) {
		case 1:
			return new SingleSubstitutionFormat1Subtable(this, offset);
		case 2:
			return new SingleSubstitutionFormat2Subtable(this, offset);
		default:
			return new SingleSubstitutionFormatSubtable(this, offset);
		}
	}

	public int getPosFormat() {
		return formatSubtable.getSubstFormat();
	}
	
	public boolean isGlyphCovered(int glyphId) {
		return formatSubtable.isGlyphCovered(glyphId);
	}
	
	public int getSubstitute(int glyphId) {
		return formatSubtable.getSubstitute(glyphId);
	}
}
