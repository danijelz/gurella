package com.gurella.engine.graphics.vector.sfnt.opentype.lookup.substitution.alternate;

import com.gurella.engine.graphics.vector.sfnt.SubTable;

public class AlternateSubstitutionFormatSubtable extends SubTable<AlternateSubstitutionSubtable> {
	public AlternateSubstitutionFormatSubtable(AlternateSubstitutionSubtable parentTable, int offset) {
		super(parentTable, offset);
	}
	
	public int getSubstFormat() {
		return readUnsignedShort(0);
	}
	
	public boolean isGlyphCovered(int glyphId) {
		return false;
	}
	
	public int[] getSubstitutes(int glyphId) {
		return new int[]{glyphId};
	}
}
