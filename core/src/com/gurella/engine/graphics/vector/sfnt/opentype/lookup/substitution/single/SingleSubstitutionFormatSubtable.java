package com.gurella.engine.graphics.vector.sfnt.opentype.lookup.substitution.single;

import com.gurella.engine.graphics.vector.sfnt.SubTable;

public class SingleSubstitutionFormatSubtable extends SubTable<SingleSubstitutionSubtable> {
	public SingleSubstitutionFormatSubtable(SingleSubstitutionSubtable parentTable, int offset) {
		super(parentTable, offset);
	}
	
	public int getSubstFormat() {
		return readUnsignedShort(0);
	}
	
	public boolean isGlyphCovered(int glyphId) {
		return false;
	}
	
	public int getSubstitute(int glyphId) {
		return glyphId;
	}
}
