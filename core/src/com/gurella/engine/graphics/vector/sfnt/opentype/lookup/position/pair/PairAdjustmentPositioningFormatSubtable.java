package com.gurella.engine.graphics.vector.sfnt.opentype.lookup.position.pair;

import com.gurella.engine.graphics.vector.sfnt.SubTable;
import com.gurella.engine.graphics.vector.sfnt.opentype.ValueFormatType;

class PairAdjustmentPositioningFormatSubtable extends SubTable<PairAdjustmentPositioningSubtable> {
	public PairAdjustmentPositioningFormatSubtable(PairAdjustmentPositioningSubtable positioningSubtable, int offset) {
		super(positioningSubtable, offset);
	}
	
	public int getPosFormat() {
		return readUnsignedShort(0);
	}
	
	public int getFirstGlyphValue(int firstGlyphId, int secondGlyphId, ValueFormatType valueFormatType) {
		return 0;
	}
	
	public int getSecondGlyphValue(int firstGlyphId, int secondGlyphId, ValueFormatType valueFormatType) {
		return 0;
	}

	public boolean isGlyphPairCovered(int firstGlyphId, int secondGlyphId) {
		return false;
	}
}