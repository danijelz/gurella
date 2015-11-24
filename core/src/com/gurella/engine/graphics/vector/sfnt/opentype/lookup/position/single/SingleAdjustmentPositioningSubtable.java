package com.gurella.engine.graphics.vector.sfnt.opentype.lookup.position.single;

import com.gurella.engine.graphics.vector.sfnt.opentype.OpenTypeLookupTable;
import com.gurella.engine.graphics.vector.sfnt.opentype.ValueFormatType;
import com.gurella.engine.graphics.vector.sfnt.opentype.lookup.LookupSubTable;

public class SingleAdjustmentPositioningSubtable extends LookupSubTable {
	private SingleAdjustmentPositioningFormatSubtable positioningFormatSubtable;
	
	public SingleAdjustmentPositioningSubtable(OpenTypeLookupTable parentTable, int offset) {
		super(parentTable, offset);
		positioningFormatSubtable = createPositioningFormatSubtable();
	}

	private SingleAdjustmentPositioningFormatSubtable createPositioningFormatSubtable() {
		int posFormat = readUnsignedShort(0);
		switch (posFormat) {
		case 1:
			return new SingleAdjustmentPositioningFormat1Subtable(this, offset);
		case 2:
			return new SingleAdjustmentPositioningFormat2Subtable(this, offset);
		default:
			return new SingleAdjustmentPositioningFormatSubtable(this, offset);
		}
	}

	public int getPosFormat() {
		return positioningFormatSubtable.getPosFormat();
	}
	
	public boolean isGlyphCovered(int glyphId) {
		return positioningFormatSubtable.isGlyphCovered(glyphId);
	}
	
	public int getGlyphValue(int glyphId, ValueFormatType valueFormatType) {
		return positioningFormatSubtable.getGlyphValue(glyphId, valueFormatType);
	}
}
