package com.gurella.engine.graphics.vector.sfnt.opentype.lookup.substitution.chaining;

import com.gurella.engine.graphics.vector.sfnt.SubTable;

public class ChainingSubstitutionFormatSubtable extends SubTable<ChainingSubstitutionSubtable> {
	public ChainingSubstitutionFormatSubtable(ChainingSubstitutionSubtable parentTable, int offset) {
		super(parentTable, offset);
	}
	
	public int getSubstFormat() {
		return readUnsignedShort(0);
	}
	
	public boolean isGlyphCovered(int lookupGlyphIndex, int... glyphIdSequence) {
		return false;
	}
	
	public int[] getSubstitutes(int lookupGlyphIndex, int... glyphIdSequence) {
		return glyphIdSequence;
	}
}
