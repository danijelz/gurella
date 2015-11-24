package com.gurella.engine.graphics.vector.sfnt.opentype.lookup.substitution.alternate;

import com.gurella.engine.graphics.vector.sfnt.SfntDataType;
import com.gurella.engine.graphics.vector.sfnt.opentype.coverage.CoverageTable;

public class AlternateSubstitutionFormat1Subtable extends AlternateSubstitutionFormatSubtable {
	private CoverageTable coverageTable;

	public AlternateSubstitutionFormat1Subtable(AlternateSubstitutionSubtable parentTable, int offset) {
		super(parentTable, offset);
		coverageTable = new CoverageTable(raf, offset + getCoverage());
	}

	@Override
	public boolean isGlyphCovered(int glyphId) {
		return coverageTable.getGlyphIndex(glyphId) >= 0;
	}

	private int getCoverage() {
		return readUnsignedShort(AlternateSubstitutionFormat1Offsets.Coverage);
	}

	public int getAlternateSetCount() {
		return readUnsignedShort(AlternateSubstitutionFormat1Offsets.AlternateSetCount);
	}

	@Override
	public int[] getSubstitutes(int glyphId) {
		int glyphIndex = coverageTable.getGlyphIndex(glyphId);
		if (glyphIndex < 0 || glyphIndex >= getAlternateSetCount()) {
			return new int[]{glyphId};
		}
		
		int alternateSetOffset = readUnsignedShort(AlternateSubstitutionFormat1Offsets.AlternateSet.offset + (glyphIndex * SfntDataType.unsignedShortValue.size));
		int glyphCount = readUnsignedShort(alternateSetOffset);
		if(glyphCount > 0) {
			return readUnsignedShortArray(alternateSetOffset + AlternateSetOffsets.Alternate.offset, glyphCount);
		} else {
			return new int[]{glyphId};
		}
	}

	private enum AlternateSubstitutionFormat1Offsets implements Offset {
		SubstFormat(0), 
		Coverage(2), 
		AlternateSetCount(4),
		AlternateSet(6);

		private final int offset;

		private AlternateSubstitutionFormat1Offsets(int offset) {
			this.offset = offset;
		}

		@Override
		public int getOffset() {
			return offset;
		}
	}
	
	private enum AlternateSetOffsets implements Offset {
		GlyphCount(0), 
		Alternate(2);
		
		private final int offset;
		
		private AlternateSetOffsets(int offset) {
			this.offset = offset;
		}
		
		@Override
		public int getOffset() {
			return offset;
		}
	}
}
