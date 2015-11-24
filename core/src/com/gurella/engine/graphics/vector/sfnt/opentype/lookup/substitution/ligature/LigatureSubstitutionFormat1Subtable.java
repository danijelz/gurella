package com.gurella.engine.graphics.vector.sfnt.opentype.lookup.substitution.ligature;

import com.gurella.engine.graphics.vector.sfnt.SfntDataType;
import com.gurella.engine.graphics.vector.sfnt.opentype.coverage.CoverageTable;

public class LigatureSubstitutionFormat1Subtable extends LigatureSubstitutionFormatSubtable {
	private CoverageTable coverageTable;

	public LigatureSubstitutionFormat1Subtable(LigatureSubstitutionSubtable parentTable, int offset) {
		super(parentTable, offset);
		coverageTable = new CoverageTable(raf, offset + getCoverage());
	}

	@Override
	public boolean isGlyphCovered(int glyphId) {
		return coverageTable.getGlyphIndex(glyphId) >= 0;
	}

	private int getCoverage() {
		return readUnsignedShort(LigatureSubstitutionFormat1Offsets.Coverage);
	}

	public int getLigSetCount() {
		return readUnsignedShort(LigatureSubstitutionFormat1Offsets.LigSetCount);
	}

	@Override
	public int getSubstitute(int glyphId, int... additionalGlyphIds) {
		int glyphIndex = coverageTable.getGlyphIndex(glyphId);
		if (glyphIndex < 0 || glyphIndex >= getLigSetCount()) {
			return glyphId;
		}
		
		int ligatureSetOffset = readUnsignedShort(LigatureSubstitutionFormat1Offsets.LigatureSet.offset + (glyphIndex * SfntDataType.unsignedShortValue.size));
		int ligatureCount = readUnsignedShort(ligatureSetOffset);
		if(ligatureCount <= 0) {
			return glyphId;
		} 

		for(int i = 0; i < ligatureCount; i++) {
			int ligatureOffset = ligatureSetOffset + readUnsignedShort(ligatureSetOffset + LigatureSetOffsets.Ligature.offset + (i * SfntDataType.unsignedShortValue.size));
			int compCount = readUnsignedShort(ligatureOffset + LigatureOffsets.CompCount.offset) - 1;
			if(compCount == additionalGlyphIds.length && isValidLigature(ligatureOffset, compCount, additionalGlyphIds)) {
				return readUnsignedShort(ligatureOffset + LigatureOffsets.LigGlyph.offset);
			}
		}
		
		return glyphId;
	}
	
	private boolean isValidLigature(int ligatureOffset, int compCount, int... additionalGlyphIds) {
		for(int j = 0; j < compCount; j++) {
			int component = readUnsignedShort(ligatureOffset + LigatureOffsets.Component.offset + (j * SfntDataType.unsignedShortValue.size));
			if(component != additionalGlyphIds[j]) {
				return false;
			}
		}
		
		return true;
	}

	private enum LigatureSubstitutionFormat1Offsets implements Offset {
		SubstFormat(0), 
		Coverage(2), 
		LigSetCount(4),
		LigatureSet(6);

		private final int offset;

		private LigatureSubstitutionFormat1Offsets(int offset) {
			this.offset = offset;
		}

		@Override
		public int getOffset() {
			return offset;
		}
	}
	
	private enum LigatureSetOffsets implements Offset {
		LigatureCount(0), 
		Ligature(2);
		
		private final int offset;
		
		private LigatureSetOffsets(int offset) {
			this.offset = offset;
		}
		
		@Override
		public int getOffset() {
			return offset;
		}
	}
	
	private enum LigatureOffsets implements Offset {
		LigGlyph(0), 
		CompCount(2),
		Component(4);
		
		private final int offset;
		
		private LigatureOffsets(int offset) {
			this.offset = offset;
		}
		
		@Override
		public int getOffset() {
			return offset;
		}
	}
}
