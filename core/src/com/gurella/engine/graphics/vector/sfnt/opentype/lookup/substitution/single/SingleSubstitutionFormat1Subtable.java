package com.gurella.engine.graphics.vector.sfnt.opentype.lookup.substitution.single;

import com.gurella.engine.graphics.vector.sfnt.opentype.coverage.CoverageTable;

public class SingleSubstitutionFormat1Subtable extends SingleSubstitutionFormatSubtable {
	private CoverageTable coverageTable;

	public SingleSubstitutionFormat1Subtable(SingleSubstitutionSubtable parentTable, int offset) {
		super(parentTable, offset);
		coverageTable = new CoverageTable(raf, offset + getCoverage());
	}

	@Override
	public boolean isGlyphCovered(int glyphId) {
		return coverageTable.getGlyphIndex(glyphId) >= 0;
	}

	private int getCoverage() {
		return readUnsignedShort(SingleSubstitutionFormat1Offsets.Coverage);
	}

	public int getDeltaGlyphID() {
		return readUnsignedShort(SingleSubstitutionFormat1Offsets.DeltaGlyphID);
	}

	@Override
	public int getSubstitute(int glyphId) {
		int glyphIndex = coverageTable.getGlyphIndex(glyphId);
		if (glyphIndex < 0) {
			return 0;
		}

		return glyphId + getDeltaGlyphID();
	}

	private enum SingleSubstitutionFormat1Offsets implements Offset {
		SubstFormat(0), 
		Coverage(2), 
		DeltaGlyphID(4);

		private final int offset;

		private SingleSubstitutionFormat1Offsets(int offset) {
			this.offset = offset;
		}

		@Override
		public int getOffset() {
			return offset;
		}
	}
}
