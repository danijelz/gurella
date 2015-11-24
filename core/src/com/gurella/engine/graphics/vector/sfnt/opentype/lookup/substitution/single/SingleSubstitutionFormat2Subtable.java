package com.gurella.engine.graphics.vector.sfnt.opentype.lookup.substitution.single;

import com.gurella.engine.graphics.vector.sfnt.SfntDataType;
import com.gurella.engine.graphics.vector.sfnt.opentype.coverage.CoverageTable;

public class SingleSubstitutionFormat2Subtable extends SingleSubstitutionFormatSubtable {
	private CoverageTable coverageTable;
	
	public SingleSubstitutionFormat2Subtable(SingleSubstitutionSubtable parentTable, int offset) {
		super(parentTable, offset);
		coverageTable = new CoverageTable(raf, offset + getCoverage());
	}
	
	@Override
	public boolean isGlyphCovered(int glyphId) {
		return coverageTable.getGlyphIndex(glyphId) >= 0;
	}
	
	private int getCoverage() {
		return readUnsignedShort(SingleSubstitutionFormat2Offsets.Coverage);
	}
	
	public int getGlyphCount() {
		return readUnsignedShort(SingleSubstitutionFormat2Offsets.GlyphCount);
	}
	
	@Override
	public int getSubstitute(int glyphId) {
		int glyphIndex = coverageTable.getGlyphIndex(glyphId);
		if(glyphIndex < 0) {
			return 0;
		}
		
		if(glyphIndex >= getGlyphCount()) {
			return 0;
		}
		
		int substituteOffset = SingleSubstitutionFormat2Offsets.Substitute.offset + (SfntDataType.unsignedShortValue.size * glyphIndex);
		return readUnsignedShort(substituteOffset);
	}
	
	private enum SingleSubstitutionFormat2Offsets implements Offset {
		SubstFormat(0), 
		Coverage(2), 
		GlyphCount(4), 
		Substitute(6);

		private final int offset;

		private SingleSubstitutionFormat2Offsets(int offset) {
			this.offset = offset;
		}

		@Override
		public int getOffset() {
			return offset;
		}
	}
}
