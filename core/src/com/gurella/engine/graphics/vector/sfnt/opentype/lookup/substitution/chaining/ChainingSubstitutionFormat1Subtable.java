package com.gurella.engine.graphics.vector.sfnt.opentype.lookup.substitution.chaining;

import com.gurella.engine.graphics.vector.sfnt.SfntDataType;
import com.gurella.engine.graphics.vector.sfnt.opentype.coverage.CoverageTable;

public class ChainingSubstitutionFormat1Subtable extends ChainingSubstitutionFormatSubtable {
	private CoverageTable coverageTable;

	public ChainingSubstitutionFormat1Subtable(ChainingSubstitutionSubtable parentTable, int offset) {
		super(parentTable, offset);
		coverageTable = new CoverageTable(raf, offset + getCoverage());
	}

	@Override
	public boolean isGlyphCovered(int lookupGlyphIndex, int... glyphIdSequence) {
		if(glyphIdSequence.length == 0) {
			return false;
		}
		
		int glyphId = glyphIdSequence[lookupGlyphIndex];
		int glyphIndex =  coverageTable.getGlyphIndex(glyphId);
		if (glyphIndex < 0 || glyphIndex >= getChainSubRuleSetCount()) {
			return false;
		}
		//TODO
		return false;
	}

	private int getCoverage() {
		return readUnsignedShort(ChainingSubstitutionFormat1Offsets.Coverage);
	}

	public int getChainSubRuleSetCount() {
		return readUnsignedShort(ChainingSubstitutionFormat1Offsets.ChainSubRuleSetCount);
	}

	@Override
	public int[] getSubstitutes(int lookupGlyphIndex, int... glyphIdSequence) {
		if(glyphIdSequence.length == 0) {
			return glyphIdSequence;
		}
		
		int glyphId = glyphIdSequence[lookupGlyphIndex];
		int glyphIndex = coverageTable.getGlyphIndex(glyphId);
		if (glyphIndex < 0 || glyphIndex >= getChainSubRuleSetCount()) {
			return glyphIdSequence;
		}
		
		int chainSubRuleSetOffset = readUnsignedShort(ChainingSubstitutionFormat1Offsets.ChainSubRuleSet.offset + (glyphIndex * SfntDataType.unsignedShortValue.size));
		/*TODO int glyphCount = readUnsignedShort(chainSubRuleSetOffset);
		if(glyphCount > 0) {
			return readUnsignedShortArray(chainSubRuleSetOffset + ChainSubRuleSetOffsets.ChainSubRule.offset, glyphCount);
		} else {
			return new int[]{glyphId};
		}*/
		
		return glyphIdSequence;
	}

	private enum ChainingSubstitutionFormat1Offsets implements Offset {
		SubstFormat(0), 
		Coverage(2), 
		ChainSubRuleSetCount(4),
		ChainSubRuleSet(6);

		private final int offset;

		private ChainingSubstitutionFormat1Offsets(int offset) {
			this.offset = offset;
		}

		@Override
		public int getOffset() {
			return offset;
		}
	}
	
	private enum ChainSubRuleSetOffsets implements Offset {
		ChainSubRuleCount(0), 
		ChainSubRule(2);
		
		private final int offset;
		
		private ChainSubRuleSetOffsets(int offset) {
			this.offset = offset;
		}
		
		@Override
		public int getOffset() {
			return offset;
		}
	}
}
