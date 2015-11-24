package com.gurella.engine.graphics.vector.sfnt.opentype.lookup.substitution.chaining;

import com.gurella.engine.graphics.vector.sfnt.opentype.OpenTypeLookupTable;
import com.gurella.engine.graphics.vector.sfnt.opentype.lookup.LookupSubTable;

public class ChainingSubstitutionSubtable extends LookupSubTable {
	private ChainingSubstitutionFormatSubtable formatSubtable;
	
	public ChainingSubstitutionSubtable(OpenTypeLookupTable parentTable, int offset) {
		super(parentTable, offset);
		formatSubtable = createPositioningFormatSubtable();
	}

	private ChainingSubstitutionFormatSubtable createPositioningFormatSubtable() {
		int posFormat = readUnsignedShort(0);
		switch (posFormat) {
		case 1:
			return new ChainingSubstitutionFormat1Subtable(this, offset);
		default:
			return new ChainingSubstitutionFormatSubtable(this, offset);
		}
	}

	public int getPosFormat() {
		return formatSubtable.getSubstFormat();
	}
	
	public boolean isGlyphCovered(int lookupGlyphIndex, int... glyphIdSequence) {
		return formatSubtable.isGlyphCovered(lookupGlyphIndex, glyphIdSequence);
	}
	
	public int[] getSubstitutes(int lookupGlyphIndex, int... glyphIdSequence) {
		return formatSubtable.getSubstitutes(lookupGlyphIndex, glyphIdSequence);
	}
}
