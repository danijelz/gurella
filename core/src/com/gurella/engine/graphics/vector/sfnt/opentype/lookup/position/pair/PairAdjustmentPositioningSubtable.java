package com.gurella.engine.graphics.vector.sfnt.opentype.lookup.position.pair;

import com.gurella.engine.graphics.vector.sfnt.opentype.OpenTypeLookupTable;
import com.gurella.engine.graphics.vector.sfnt.opentype.ValueFormatType;
import com.gurella.engine.graphics.vector.sfnt.opentype.lookup.LookupSubTable;

public class PairAdjustmentPositioningSubtable extends LookupSubTable {
	private PairAdjustmentPositioningFormatSubtable positioningFormatSubtable;
	
	public PairAdjustmentPositioningSubtable(OpenTypeLookupTable parentTable, int offset) {
		super(parentTable, offset);
		positioningFormatSubtable = createPositioningFormatSubtable();
	}
	
	private PairAdjustmentPositioningFormatSubtable createPositioningFormatSubtable() {
		int posFormat = readUnsignedShort(0);
		switch (posFormat) {
		case 1:
			return new PairAdjustmentPositioningFormat1Subtable(this, offset);
		case 2:
			return new PairAdjustmentPositioningFormat2Subtable(this, offset);
		default:
			return new PairAdjustmentPositioningFormatSubtable(this, offset);
		}
	}
	
	public boolean isGlyphPairCovered(int firstGlyphId, int secondGlyphId) {
		return positioningFormatSubtable.isGlyphPairCovered(firstGlyphId, secondGlyphId);
	}
	
	public int getFirstGlyphValue(int firstGlyphId, int secondGlyphId, ValueFormatType valueFormatType) {
		return positioningFormatSubtable.getFirstGlyphValue(firstGlyphId, secondGlyphId, valueFormatType);
	}
	
	public int getSecondGlyphValue(int firstGlyphId, int secondGlyphId, ValueFormatType valueFormatType) {
		return positioningFormatSubtable.getSecondGlyphValue(firstGlyphId, secondGlyphId, valueFormatType);
	}
}
