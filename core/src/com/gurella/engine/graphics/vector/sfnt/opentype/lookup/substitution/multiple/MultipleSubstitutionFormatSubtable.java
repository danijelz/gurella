package com.gurella.engine.graphics.vector.sfnt.opentype.lookup.substitution.multiple;

import com.gurella.engine.graphics.vector.sfnt.SubTable;

public class MultipleSubstitutionFormatSubtable extends SubTable<MultipleSubstitutionSubtable> {
	public MultipleSubstitutionFormatSubtable(MultipleSubstitutionSubtable parentTable, int offset) {
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
