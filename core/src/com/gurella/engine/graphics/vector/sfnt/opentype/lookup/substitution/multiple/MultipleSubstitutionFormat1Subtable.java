package com.gurella.engine.graphics.vector.sfnt.opentype.lookup.substitution.multiple;

import com.gurella.engine.graphics.vector.sfnt.SfntDataType;
import com.gurella.engine.graphics.vector.sfnt.opentype.coverage.CoverageTable;

public class MultipleSubstitutionFormat1Subtable extends MultipleSubstitutionFormatSubtable {
	private CoverageTable coverageTable;

	public MultipleSubstitutionFormat1Subtable(MultipleSubstitutionSubtable parentTable, int offset) {
		super(parentTable, offset);
		coverageTable = new CoverageTable(raf, offset + getCoverage());
	}

	@Override
	public boolean isGlyphCovered(int glyphId) {
		return coverageTable.getGlyphIndex(glyphId) >= 0;
	}

	private int getCoverage() {
		return readUnsignedShort(MultipleSubstitutionFormat1Offsets.Coverage);
	}

	public int getSequenceCount() {
		return readUnsignedShort(MultipleSubstitutionFormat1Offsets.SequenceCount);
	}

	@Override
	public int[] getSubstitutes(int glyphId) {
		int glyphIndex = coverageTable.getGlyphIndex(glyphId);
		if (glyphIndex < 0 || glyphIndex >= getSequenceCount()) {
			return new int[]{glyphId};
		}
		
		int sequenceOffset = readUnsignedShort(MultipleSubstitutionFormat1Offsets.Sequence.offset + (glyphIndex * SfntDataType.unsignedShortValue.size));
		int glyphCount = readUnsignedShort(sequenceOffset);
		if(glyphCount > 0) {
			return readUnsignedShortArray(sequenceOffset + SequenceOffsets.Substitute.offset, glyphCount);
		} else {
			return new int[]{glyphId};
		}
	}

	private enum MultipleSubstitutionFormat1Offsets implements Offset {
		SubstFormat(0), 
		Coverage(2), 
		SequenceCount(4),
		Sequence(6);

		private final int offset;

		private MultipleSubstitutionFormat1Offsets(int offset) {
			this.offset = offset;
		}

		@Override
		public int getOffset() {
			return offset;
		}
	}
	
	private enum SequenceOffsets implements Offset {
		GlyphCount(0), 
		Substitute(2);
		
		private final int offset;
		
		private SequenceOffsets(int offset) {
			this.offset = offset;
		}
		
		@Override
		public int getOffset() {
			return offset;
		}
	}
}
