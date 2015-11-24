package com.gurella.engine.graphics.vector.sfnt.opentype.lookup.substitution.ligature;

import com.gurella.engine.graphics.vector.sfnt.SubTable;

public class LigatureSubstitutionFormatSubtable extends SubTable<LigatureSubstitutionSubtable> {
	public LigatureSubstitutionFormatSubtable(LigatureSubstitutionSubtable parentTable, int offset) {
		super(parentTable, offset);
	}
	
	public int getSubstFormat() {
		return readUnsignedShort(0);
	}
	
	public boolean isGlyphCovered(int glyphId) {
		return false;
	}
	
	public int getSubstitute(int glyphId, int... additionalGlyphIds) {
		return glyphId;
	}
}
